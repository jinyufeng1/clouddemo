package com.example.consumerconsole.controller;

import com.example.common.domain.Response;
import com.example.consumerconsole.feign.CoachFeign;
import com.example.objcoach.vo.console.CoachDetailsVo;
import com.example.objcoach.vo.console.CoachItemListVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/coach")
public class CoachController {

    @Autowired
    private CoachFeign coachFeign;

    //新增教练信息
    @RequestMapping("/add")
    public Response<Long> addCoach(@RequestParam(name = "pics", required = false) String pics,
                                   @RequestParam("name") String name,
                                   @RequestParam("categoryId") Long categoryId,
                                   @RequestParam(name = "speciality", required = false) String speciality,
                                   @RequestParam(name = "intro", required = false) String intro,
                                   @RequestParam(name = "tags", required = false) String tags) {
        return coachFeign.addCoach(pics, name, categoryId, speciality, intro, tags);
    }

    //删除教练信息
    @RequestMapping("/del")
    public Response<Boolean> delCoach(@RequestParam("id") Long id) {
        return coachFeign.delCoach(id);
    }

    //修改教练信息
    @RequestMapping("/update")
    public Response<Long> updateCoach(@RequestParam(name = "id") Long id,
                              @RequestParam(name = "categoryId", required = false) Long categoryId,
                              @RequestParam(name = "pics", required = false) String pics,
                              @RequestParam(name = "name", required = false) String name,
                              @RequestParam(name = "speciality", required = false) String speciality,
                              @RequestParam(name = "intro", required = false) String intro,
                              @RequestParam(name = "tags", required = false) String tags) {
        return coachFeign.updateCoach(id, categoryId, pics, name, speciality, intro, tags);
    }

    @RequestMapping("/list")
    public Response<CoachItemListVo> getCoachList(@RequestParam("page") Integer page,
                                                  @RequestParam(name = "keyword", required = false) String keyword) {
        return coachFeign.getCoachList(page, keyword);
    }

    @RequestMapping("/list2")
    public Response<CoachItemListVo> getCoachList2(@RequestParam("page") Integer page,
                                         @RequestParam(name = "keyword", required = false) String keyword) {
        return coachFeign.getCoachList2(page, keyword);
    }

    @RequestMapping("/detail")
    public Response<CoachDetailsVo> getCoachDetail(@RequestParam(name = "id") Long id) {
        return coachFeign.getCoachDetail(id);
    }
}
