package com.example.excel.controller;

import com.alibaba.excel.EasyExcel;
import com.example.common.domain.CustomException;
import com.example.common.domain.Response;
import com.example.excel.ExcelDataListener;
import com.example.feign.coach.CoachFeign;
import com.example.feign.coach.domain.Category;
import com.example.feign.coach.domain.Coach;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Slf4j
@RestController
@RequestMapping("/excel")
public class ExcelController {
    @Autowired
    private CoachFeign coachFeign;

    @RequestMapping("/test")
    public Integer test() {
        return 1;
    }

    @RequestMapping("/export")
    public void exportData(HttpServletResponse response) {
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
        String fileName;
        fileName = URLEncoder.encode("分类表", StandardCharsets.UTF_8);
        // Content-Disposition主要用于指示浏览器如何处理响应内容
        // Content-Disposition: [disposition-type]; [parameters]
        // disposition-type : 1 inline（默认值） 表示内容应该在浏览器中直接显示    2 attachment 表示内容应该作为文件下载
        // parameters filename="filename.ext" 指定下载文件的文件名
        response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xlsx");

        // 获取输出流
        ServletOutputStream outputStream;
        try {
            outputStream = response.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // 查数据 远程调用
        Response<List<Category>> category = coachFeign.getCategory();
        Integer code = category.getCode();
        if (1001 != code) {
            throw new CustomException(code);
        }
        EasyExcel.write(outputStream, Category.class).sheet("分类信息").doWrite(category.getData());

        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequestMapping("/parse")
    public Response<List<Category>> parseData(MultipartFile file) {
        String contentType = file.getContentType();
        if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)) {
            return new Response<>(5001);
        }

        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return new Response<>(3001);
        }

        ExcelDataListener<Category> listener = new ExcelDataListener<>();
        EasyExcel.read(inputStream, Category.class, listener).sheet().doRead();
        List<Category> dataList = listener.getDataList();

        return new Response<>(1001, dataList);
    }

    private void zipFolder(File file, ZipOutputStream zos, String parentFolder) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null == files) {
                return;
            }

            for (File f : files) {
                zipFolder(f, zos, parentFolder  + file.getName() + "/");
            }
        }
        else {
            // 添加文件到ZIP
            FileInputStream fis = new FileInputStream(file);
            String entryName = parentFolder + file.getName();
            ZipEntry zipEntry = new ZipEntry(entryName);
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            fis.close();
        }
    }

    @RequestMapping("/exportMul")
    public void exportDataMul(HttpServletResponse response) {
        // 使用多线程读数据库写文件
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch countDownLatch = new CountDownLatch(2);

        // 线程1
        File file = new File("分类表.xlsx");
        AtomicReference<Integer> code = new AtomicReference<>(1001);
        executorService.execute(() -> {
            // 查数据
            Response<List<Category>> category = coachFeign.getCategory();
            code.set(category.getCode());
            if (1001 != code.get()) {
                countDownLatch.countDown();
                return;
            }
            //写excel文件
            EasyExcel.write(file, Category.class).sheet("分类信息").doWrite(category.getData());
            countDownLatch.countDown();
        });

//        // 线程2
        File file1 = new File("教练表.xlsx");
        AtomicReference<Integer> code2 = new AtomicReference<>(1001);
        executorService.execute(() -> {
//            // 查数据
            Response<List<Coach>> coach = coachFeign.getCoach(1);
            code2.set(coach.getCode());
            if (1001 != code2.get()) {
                countDownLatch.countDown();
                return;
            }
//            //写excel文件
            EasyExcel.write(file1, Coach.class).sheet("教练信息").doWrite(coach.getData());
            countDownLatch.countDown();
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 当前线程无法捕获另外一个线程抛出的异常，放到当前线程里抛
        if (1001 != code.get()) {
            throw new CustomException(code.get());
        }

        if (1001 != code2.get()) {
            throw new CustomException(code2.get());
        }

        // 设置响应头
        response.setContentType("application/zip;charset=UTF-8");
        String zipName;
        zipName = URLEncoder.encode("压缩包", StandardCharsets.UTF_8);
        response.setHeader("Content-disposition", "attachment; filename=" + zipName + ".zip");

        // 获取输出流
        ZipOutputStream zos;
        try {
            // response.getOutputStream() 一旦获取成功就必须要 flush close
            zos = new ZipOutputStream(response.getOutputStream());
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return;
        }

        // 打包1 打包2
        try {
            zipFolder(file, zos, "");
            zipFolder(file1, zos, "");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        finally {
            try {
                zos.flush();
                zos.close();
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}
