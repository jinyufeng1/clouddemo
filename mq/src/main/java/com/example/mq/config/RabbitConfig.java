package com.example.mq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public Queue myQueue() {
        return new Queue("delete_coach_by_catagoryId", true); // 创建一个持久化的队列
    }

// 只用一个固定队列，暂时不用交换机，只关心最本质的东西
//    @Bean
//    public DirectExchange myExchange() {
//        return new DirectExchange("myExchange");
//    }
//
//    @Bean
//    public Binding myBinding(Queue myQueue, DirectExchange myExchange) {
//        return BindingBuilder.bind(myQueue).to(myExchange).with("myRoutingKey");
//    }
}
