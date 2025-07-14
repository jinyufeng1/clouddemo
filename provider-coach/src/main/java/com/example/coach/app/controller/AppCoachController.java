package com.example.coach.app.controller;

import com.example.coach.app.service.AppCoachService;
import com.example.common.domain.Response;
import com.example.objcoach.vo.app.CoachDetailsVo;
import com.example.objcoach.vo.app.CoachItemListVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/app")
public class AppCoachController {

    @Autowired
    private AppCoachService appCoachService;

    @RequestMapping("/coach/list")
    public Response<CoachItemListVo> getCoachList(@RequestParam(name = "wp", required = false) String wp,
                                                  @RequestParam(name = "keyword", required = false) String keyword) {
        return appCoachService.getCoachList(wp, keyword);
    }

    @RequestMapping("/coach/list2")
    public Response<CoachItemListVo> getCoachList2(@RequestParam(name = "wp", required = false) String wp,
                                         @RequestParam(name = "keyword", required = false) String keyword) {
        return appCoachService.getCoachList2(wp, keyword);
    }

    @RequestMapping("/coach/listes")
    public Response<CoachItemListVo> getCoachListEs(@RequestParam(name = "wp", required = false) String wp,
                                                   @RequestParam(name = "keyword", required = false) String keyword) {
        return appCoachService.getCoachListEs(wp, keyword);
    }

    @RequestMapping("/coach/detail")
    public Response<CoachDetailsVo> getCoachDetail(@RequestParam(name = "id") Long id) {
        return appCoachService.getCoachDetail(id);
    }
}
