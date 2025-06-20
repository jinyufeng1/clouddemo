package com.example.consumerconsole.feign;

import com.example.common.domain.Response;
import com.example.objcoach.entity.Category;
import com.example.objcoach.entity.Coach;
import com.example.objcoach.vo.console.CategoryTreeListVo;
import com.example.objcoach.vo.console.CoachDetailsVo;
import com.example.objcoach.vo.console.CoachItemListVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("coach")
public interface CoachFeign {
    @RequestMapping("/console/category/add")
    Response<Long> addCategory(@RequestParam("name") String name, @RequestParam(value = "icon", required = false) String icon, @RequestParam(value = "parentId") Long parentId);

    @RequestMapping("/console/category/del")
    Response<Boolean> deleteCategory(@RequestParam("id") Long id);

    @RequestMapping("/console/category/update")
    Response<Long> updateCategory(@RequestParam("id") Long id, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "icon", required = false) String icon, @RequestParam(value = "parentId", required = false) Long parentId);

    @RequestMapping("/console/category/ntree")
    Response<CategoryTreeListVo> getCategoryTree();

    @RequestMapping("/console/coach/add")
    Response<Long> addCoach(@RequestParam(name = "pics", required = false) String pics,
                            @RequestParam("name") String name,
                            @RequestParam("categoryId") Long categoryId,
                            @RequestParam(name = "speciality", required = false) String speciality,
                            @RequestParam(name = "intro", required = false) String intro,
                            @RequestParam(name = "tags", required = false) String tags);

    @RequestMapping("/console/coach/del")
    Response<Boolean> delCoach(@RequestParam("id") Long id);

    @RequestMapping("/console/coach/update")
    Response<Long> updateCoach(@RequestParam(name = "id") Long id,
                               @RequestParam(name = "categoryId", required = false) Long categoryId,
                               @RequestParam(name = "pics", required = false) String pics,
                               @RequestParam(name = "name", required = false) String name,
                               @RequestParam(name = "speciality", required = false) String speciality,
                               @RequestParam(name = "intro", required = false) String intro,
                               @RequestParam(name = "tags", required = false) String tags);

    @RequestMapping("/console/coach/list")
    Response<CoachItemListVo> getCoachList(@RequestParam("page") Integer page,
                                           @RequestParam(name = "keyword", required = false) String keyword);

    @RequestMapping("/console/coach/list2")
    Response<CoachItemListVo> getCoachList2(@RequestParam("page") Integer page,
            @RequestParam(name = "keyword", required = false) String keyword);

    @RequestMapping("/console/coach/detail")
    Response<CoachDetailsVo> getCoachDetail(@RequestParam(name = "id") Long id);

    @RequestMapping("/middle/category/info")
    Response<List<Category>> getCategoryList();

    @RequestMapping("/middle/coach/info")
    Response<List<Coach>> getCoachList(@RequestParam Integer page);
}
