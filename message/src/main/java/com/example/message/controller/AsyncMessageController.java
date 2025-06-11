package com.example.message.controller;

import com.example.common.domain.Response;
import com.example.message.crond.AsyncMessageTaskCrond;
import com.example.message.entity.MessageTask;
import com.example.message.service.MessageTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/async")
public class AsyncMessageController {
    @Autowired
    private MessageTaskService messageTaskService;

    /**
     * 异步发送一个验证码
     * @param phone
     */
    @RequestMapping("/send/code/single")
    public Response<Boolean> asynSendCodeSingle(@RequestParam("phone") String phone) {
        MessageTask messageTask = new MessageTask();
        messageTask.setPhone(phone);
        messageTask.setStatus(AsyncMessageTaskCrond.TaskStatus.UNTREATED.getStatus());
        messageTaskService.insert(messageTask);
        return new Response<>(1001, true);
    }

    @RequestMapping("/test")
    public Response<Boolean> test() {
        return new Response<>(1001, true);
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

        for (String phone : phones) {
            executorService.execute(() -> {
                MessageTask messageTask = new MessageTask();
                messageTask.setPhone(phone);
                messageTask.setStatus(AsyncMessageTaskCrond.TaskStatus.UNTREATED.getStatus());
                messageTaskService.insert(messageTask);
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

        return new Response<>(1001, true);
    }
}
