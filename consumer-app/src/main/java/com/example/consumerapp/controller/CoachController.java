package com.example.consumerapp.controller;

import com.example.common.domain.Response;
import com.example.consumerapp.feign.CoachFeign;
import com.example.consumerapp.verification.login.annotations.VerifiedUser;
import com.example.consumerapp.verification.login.entity.CurrentUser;
import com.example.objcoach.vo.app.CoachDetailsVo;
import com.example.objcoach.vo.app.CoachItemListVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CoachController {

    @Autowired
    @Lazy
    private CoachFeign coachFeign;


    @RequestMapping("/coach/list")
    public Response<CoachItemListVo> getCoachList(@VerifiedUser CurrentUser currentUser, @RequestParam(name = "wp", required = false) String wp,
                                                  @RequestParam(name = "keyword", required = false) String keyword) {
        Integer code = currentUser.getCode();
        if (1001 != code) {
            return new Response<>(code);
        }
        return coachFeign.getCoachList(wp, keyword);
    }

    @RequestMapping("/coach/detail")
    public Response<CoachDetailsVo> getCoachDetail(@VerifiedUser CurrentUser currentUser, @RequestParam(name = "id") Long id) {
        Integer code = currentUser.getCode();
        if (1001 != code) {
            return new Response<>(code);
        }

        return coachFeign.getCoachDetail(id);
    }
}
