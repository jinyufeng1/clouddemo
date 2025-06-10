package com.example.message.controller;

import com.example.common.domain.Response;
import com.example.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/sync")
public class SyncMessageController {
    @Autowired
    private MessageService messageService;

    /**
     * 同步发送一个验证码
     * @param phone
     */
    @RequestMapping("/send/code/single")
    public Response<Boolean> sendCodeSingle(@RequestParam("phone") String phone) {
        try {
            String code = messageService.sendCode(phone);
            if (!"OK".equals(code)) {
                return new Response<>(2003);
            }
            return new Response<>(1001, true);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Response<>(2003);
        }
    }

    /**
     * 同步发送多个验证码
     * @param phones
     */
    @RequestMapping("/send/code/multi")
    public Response<Boolean> sendCodeMulti(@RequestParam("phones") List<String> phones) {
        // 使用多线程
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch countDownLatch = new CountDownLatch(phones.size());

        AtomicBoolean ret = new AtomicBoolean(false);
        for (String phone : phones) {
            executorService.execute(() -> {
                try {
                    String code = messageService.sendCode(phone);
                    if (!"OK".equals(code)) {
                        // 目前只要有一个成功就算发送成功
                        ret.set(true);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        executorService.shutdown();

        if (!ret.get()) {
            return new Response<>(2003);
        }

        return new Response<>(1001, true);
    }
}
