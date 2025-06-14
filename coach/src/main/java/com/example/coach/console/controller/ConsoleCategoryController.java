package com.example.coach.console.controller;

import com.example.coach.console.CategoryTree;
import com.example.coach.console.vo.CategoryItemListVo;
import com.example.coach.dto.EditCategoryDTO;
import com.example.coach.entity.Category;
import com.example.coach.service.CategoryService;
import com.example.common.domain.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/console")
public class ConsoleCategoryController {

//    @Autowired
//    private CoachService coachService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("/category/add")
    public Response<Long> addCategory(@RequestParam("name") String name, @RequestParam(value = "icon", required = false) String icon, @RequestParam(value = "parentId") Long parentId) {
        Long categoryId = categoryService.edit(new EditCategoryDTO(null, name, icon, parentId));
        return new Response<>(1001, categoryId);
    }

    @RequestMapping("/category/del")
    public Response<Boolean> deleteCategory(@RequestParam("id") Long id) {
        Boolean ret = categoryService.deleteHierarchy(id);
        if (ret) {
            //为解决循环依赖，将coachService提升一级，删除依赖这个分类的教练数据
//            Coach entity = new Coach();
//            entity.setCategoryId(id);
//            coachService.deleteByProperty(entity);

            //为解决循环依赖，将要删除的目标id发送给mq异步处理
            rabbitTemplate.convertAndSend("delete_coach_by_catagoryId", id);
        }
        return new Response<>(1001, ret);
    }

    @RequestMapping("/category/update")
    public Response<Long> updateCategory(@RequestParam("id") Long id, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "icon", required = false) String icon, @RequestParam(value = "parentId", required = false) Long parentId) {
        Long categoryId = categoryService.edit(new EditCategoryDTO(id, name, icon, parentId));
        return new Response<>(1001, categoryId);
    }

    @RequestMapping("/category/ntree")
    public Response<CategoryItemListVo> getCategoryTree() {
        List<Category> list = categoryService.getList(null, null, null);
        return new Response<>(1001, CategoryTree.getCategoryTree(list));
    }

    @RequestMapping("/rabbitMQ/test")
    public void test() {
        rabbitTemplate.convertAndSend("delete_coach_by_catagoryId", 10L); // 直接发送到队列
    }
}
