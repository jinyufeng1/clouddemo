package com.example.coach;

import com.example.coach.entity.Coach;
import com.example.coach.service.CoachService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 监听队列 接收信息后进行处理
 */
@Component
public class MqListener {
    @Autowired
    @Lazy
    private CoachService coachService;

    @RabbitListener(queues = "delete_coach_by_catagoryId")
    public void receiveMessage(Long id) {
        System.out.println("catagoryId:" + id);
        Coach entity = new Coach();
        entity.setCategoryId(id);
        if (coachService.deleteByProperty(entity)) {
            System.out.println("aim is deleted!");
        }
    }
}
