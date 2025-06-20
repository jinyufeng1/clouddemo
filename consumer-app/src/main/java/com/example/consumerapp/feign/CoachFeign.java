package com.example.consumerapp.feign;


import com.example.common.domain.Response;
import com.example.objcoach.vo.app.CategoryItemListVo;
import com.example.objcoach.vo.app.CoachDetailsVo;
import com.example.objcoach.vo.app.CoachItemListVo;
import com.example.objcoach.vo.app.LevelThreeAboveVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("coach")
public interface CoachFeign {
    @RequestMapping("/app/category/list")
    Response<CategoryItemListVo> getCategoryList(@RequestParam(name = "keyword", required = false) String keyword);

    @RequestMapping("/app/category/nlist")
    Response<LevelThreeAboveVo> getLevelThreeAboveList(@RequestParam(name = "wp", required = false) String wp,
                                                       @RequestParam(name = "parentId", required = false) Long parentId);
    @RequestMapping("/app/coach/list")
    Response<CoachItemListVo> getCoachList(@RequestParam(name = "wp", required = false) String wp,
                                           @RequestParam(name = "keyword", required = false) String keyword);
    @RequestMapping("/app/coach/list2")
    Response<CoachItemListVo> getCoachList2(@RequestParam(name = "wp", required = false) String wp,
                                            @RequestParam(name = "keyword", required = false) String keyword);
    @RequestMapping("/app/coach/detail")
    Response<CoachDetailsVo> getCoachDetail(@RequestParam(name = "id") Long id);
}
