package com.example.consumerapp.controller;

import com.example.common.domain.Response;
import com.example.consumerapp.feign.CoachFeign;
import com.example.consumerapp.verification.login.annotations.VerifiedUser;
import com.example.consumerapp.verification.login.entity.CurrentUser;
import com.example.objcoach.vo.app.CategoryItemListVo;
import com.example.objcoach.vo.app.LevelThreeAboveVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CategoryController {

    @Autowired
    @Lazy
    private CoachFeign coachFeign;

    @RequestMapping("/category/list")
    public Response<CategoryItemListVo> getCategoryList(@VerifiedUser CurrentUser currentUser, @RequestParam(name = "keyword", required = false) String keyword) {
        Integer code = currentUser.getCode();
        if (1001 != code) {
            return new Response<>(code);
        }

        return coachFeign.getCategoryList(keyword);
    }

    @RequestMapping("/category/nlist")
    public Response<LevelThreeAboveVo> getLevelThreeAboveList(@VerifiedUser CurrentUser currentUser, @RequestParam(name = "wp", required = false) String wp,
                                                              @RequestParam(name = "parentId", required = false) Long parentId) {
        Integer code = currentUser.getCode();
        if (1001 != code) {
            return new Response<>(code);
        }

        return coachFeign.getLevelThreeAboveList(wp, parentId);
    }
}
