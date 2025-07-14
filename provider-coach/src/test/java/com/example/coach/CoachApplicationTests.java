package com.example.coach;

import com.example.objcoach.entity.Coach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;

@SpringBootTest
class CoachApplicationTests {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    void contextLoads() {
        Coach coach = new Coach();
        coach.setId(1L);
        coach.setName("XSXR");
        coach.setPics("xxxxxxxxxx");
        coach.setIntro("xxxxxxx");
        Coach save = elasticsearchTemplate.save(coach);
        System.out.println(save);
    }

}
