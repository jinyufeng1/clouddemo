package com.example.coach.middle;

import com.example.coach.service.CategoryService;
import com.example.coach.service.CoachService;
import com.example.common.domain.Response;
import com.example.objcoach.entity.Category;
import com.example.objcoach.entity.Coach;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/middle")
public class MiddleController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CoachService coachService;

    @RequestMapping("/category/info")
    public Response<List<Category>> getCategoryList() {
        return new Response<>(1001, categoryService.getList(null, null, null));
    }

    @RequestMapping("/coach/info")
    public Response<List<Coach>> getCoachList(@RequestParam Integer page) {
        return new Response<>(1001, coachService.getPageList(page, null, null));
    }
}
