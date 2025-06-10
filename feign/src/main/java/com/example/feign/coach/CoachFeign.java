package com.example.feign.coach;

import com.example.common.domain.Response;
import com.example.feign.coach.domain.Category;
import com.example.feign.coach.domain.Coach;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("coach")
public interface CoachFeign {
    @RequestMapping("/feign/category/info")
    Response<List<Category>> getCategory();

    @RequestMapping("/feign/coach/info")
    Response<List<Coach>> getCoach(@RequestParam Integer page);
}
