package com.example.message.crond;

import com.example.message.entity.MessageTask;
import com.example.message.service.MessageService;
import com.example.message.service.MessageTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class AsyncMessageTaskCrond {
    @Autowired
    private MessageTaskService messageTaskService;

    @Autowired
    private MessageService messageService;

    public enum TaskStatus {
        // 定义任务状态常量
        UNTREATED(0), // 等待处理的任务
        SUCCEED(1), // 处理成功的任务
        FAIL(2); // 处理失败的任务

        // 成员变量，用于存储状态的描述
        private final Integer status;

        // 构造函数，私有化构造函数，防止外部实例化
        TaskStatus(Integer status) {
            this.status = status;
        }

        // 提供一个方法来获取状态的描述
        public Integer getStatus() {
            return status;
        }
    }

    private void doTask(MessageTask task) {
        String code = "";
        try {
            code = messageService.sendCode(task.getPhone());
        }
        catch (Exception e) {
            e.printStackTrace();
            task.setStatus(TaskStatus.FAIL.getStatus());
        }

        // 发送成功
        if ("OK".equals(code)) {
            task.setStatus(TaskStatus.SUCCEED.getStatus());
        }
        // 发送失败
        else {
            task.setStatus(TaskStatus.FAIL.getStatus());
        }
        messageTaskService.update(task);
    }

    // 每10秒执行一次
    @Scheduled(fixedDelay = 10000)
    public void fixedDelayJob() {
        List<MessageTask> tasks = messageTaskService.getByStatus(TaskStatus.UNTREATED.getStatus());
        int size = tasks.size();
        if (0 == size) {
            return;
        }

        // 大于等于5个的时候使用多线程
        if (5 <= size) {
            ExecutorService executorService = Executors.newCachedThreadPool();
            for (MessageTask task : tasks) {
                executorService.execute(() -> doTask(task));
            }
        }
        else {
            for (MessageTask task : tasks) {
                doTask(task);
            }
        }

    }

}
