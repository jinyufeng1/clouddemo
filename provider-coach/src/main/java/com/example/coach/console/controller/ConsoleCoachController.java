package com.example.coach.console.controller;

import com.example.coach.console.service.ConsoleCoachService;
import com.example.common.domain.Response;
import com.example.objcoach.vo.console.CoachDetailsVo;
import com.example.objcoach.vo.console.CoachItemListVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/console")
public class ConsoleCoachController {

    @Autowired
    private ConsoleCoachService consoleCoachService;

    //新增教练信息
    @RequestMapping("/coach/add")
    public Response<Long> addCoach(@RequestParam(name = "pics", required = false) String pics,
                                   @RequestParam("name") String name,
                                   @RequestParam("categoryId") Long categoryId,
                                   @RequestParam(name = "speciality", required = false) String speciality,
                                   @RequestParam(name = "intro", required = false) String intro,
                                   @RequestParam(name = "tags", required = false) String tags) {
        return consoleCoachService.addCoach(pics, name, categoryId, speciality, intro, tags);
    }

    //删除教练信息
    @RequestMapping("/coach/del")
    public Response<Boolean> delCoach(@RequestParam("id") Long id) {
        return consoleCoachService.delCoach(id);
    }

    //修改教练信息
    @RequestMapping("/coach/update")
    public Response<Long> updateCoach(@RequestParam(name = "id") Long id,
                              @RequestParam(name = "categoryId", required = false) Long categoryId,
                              @RequestParam(name = "pics", required = false) String pics,
                              @RequestParam(name = "name", required = false) String name,
                              @RequestParam(name = "speciality", required = false) String speciality,
                              @RequestParam(name = "intro", required = false) String intro,
                              @RequestParam(name = "tags", required = false) String tags) {
        return consoleCoachService.updateCoach(id, categoryId, pics, name, speciality, intro, tags);
    }

    @RequestMapping("/coach/list")
    public Response<CoachItemListVo> getCoachList(@RequestParam("page") Integer page,
                                                  @RequestParam(name = "keyword", required = false) String keyword) {
        return consoleCoachService.getCoachList(page, keyword);
    }

    @RequestMapping("/coach/list2")
    public Response<CoachItemListVo> getCoachList2(@RequestParam("page") Integer page,
                                         @RequestParam(name = "keyword", required = false) String keyword) {
        return consoleCoachService.getCoachList2(page, keyword);
    }

    @RequestMapping("/coach/detail")
    public Response<CoachDetailsVo> getCoachDetail(@RequestParam(name = "id") Long id) {
        return consoleCoachService.getCoachDetail(id);
    }

    @RequestMapping("/init/esdb")
    public Response<Boolean> initEsDb() {
        return consoleCoachService.initEsDb();
    }
}
