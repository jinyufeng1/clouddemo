package com.example.consumerconsole.controller;

import com.example.common.domain.Response;
import com.example.consumerconsole.feign.CoachFeign;
import com.example.objcoach.vo.console.CategoryTreeListVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CoachFeign coachFeign;

    @RequestMapping("/add")
    public Response<Long> addCategory(@RequestParam("name") String name, @RequestParam(value = "icon", required = false) String icon, @RequestParam(value = "parentId") Long parentId) {
        return coachFeign.addCategory(name, icon, parentId);
    }

    @RequestMapping("/del")
    public Response<Boolean> deleteCategory(@RequestParam("id") Long id) {
        return coachFeign.deleteCategory(id);
    }

    @RequestMapping("/update")
    public Response<Long> updateCategory(@RequestParam("id") Long id, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "icon", required = false) String icon, @RequestParam(value = "parentId", required = false) Long parentId) {
        return coachFeign.updateCategory(id, name, icon, parentId);
    }

    @RequestMapping("/ntree")
    public Response<CategoryTreeListVo> getCategoryTree() {
        return coachFeign.getCategoryTree();
    }
}
