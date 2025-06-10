package com.example.file.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Autowired
    private OSS ossClient;

    @Value("${oss.bucketName}")
    private String bucketName;

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "文件为空，请选择一个文件";
        }

        // 设置文件保存路径
        String uploadDir = "d:/uploads/";
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();// 如果目录不存在，创建目录
        }

        try {
            // 保存文件
            String fileName = file.getOriginalFilename();
            file.transferTo(new File(uploadDir + fileName));
            return "文件上传成功: " + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "文件上传失败: " + e.getMessage();
        }
    }

    @PostMapping("/uploadOss")
    public String uploadFileOss(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "文件为空，请选择一个文件";
        }

        String contentType = file.getContentType();
        if (!StringUtils.hasLength(contentType)) {
            return "无法获取文件类型";
        }

        String objectName = file.getOriginalFilename();
        if (!StringUtils.hasLength(objectName)) {
            return "文件名为空，请选择一个文件";
        }

        String filePrefix; // 文件路径名前缀
        Date date = new Date(System.currentTimeMillis());
        String fileMiddle = new SimpleDateFormat("yyyyMM/dd/").format(date) + UUID.randomUUID(); // 文件路径名中部

        // 判断文件类型
        if (contentType.contains("image")) {
            BufferedImage image;
            try{
                image = ImageIO.read(file.getInputStream());
            }
            catch (IOException e) {
                e.printStackTrace();
                return "文件读取失败";
            }
            filePrefix = "image/";
            // 获取图片的宽度和高度
            int width = image.getWidth();
            int height = image.getHeight();
            fileMiddle = fileMiddle + "_" + width + "x" + height;
        } else if (contentType.contains("video")) {
            filePrefix = "video/";
        } else {
            filePrefix = "file/";
        }

        String fileSuffix = objectName.substring(objectName.lastIndexOf(".")); // 文件路径名后缀
        objectName = filePrefix + fileMiddle + fileSuffix; // 前中后组合

        try {
            // 保存文件
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(file.getBytes()));
            // 生成预签名URL
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectName);
            // 指定生成的预签名URL过期时间，单位为毫秒。本示例以设置过期时间为1小时为例。
            Date expiration = new Date(new Date().getTime() + 3600 * 1000L);
            // 设置过期时间。
            request.setExpiration(expiration);
            URL signedUrl = ossClient.generatePresignedUrl(request);
            return "文件上传成功, url: " + signedUrl;
        } catch (IOException e) {
            e.printStackTrace();
            return "文件上传失败: " + e.getMessage();
        }
    }
}
