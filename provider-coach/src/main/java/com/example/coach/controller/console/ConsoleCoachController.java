package com.example.coach.controller.console;

import com.alibaba.fastjson.JSON;
import com.example.coach.service.CategoryService;
import com.example.coach.service.CoachService;
import com.example.coach.service.RelationTagCoachService;
import com.example.common.Constant;
import com.example.common.domain.Response;
import com.example.common.util.CustomUtil;
import com.example.objcoach.dto.BlockDTO;
import com.example.objcoach.dto.EditCoachDTO;
import com.example.objcoach.entity.Category;
import com.example.objcoach.entity.Coach;
import com.example.objcoach.entity.Tag;
import com.example.objcoach.vo.console.CoachDetailsVo;
import com.example.objcoach.vo.console.CoachItemListVo;
import com.example.objcoach.vo.console.CoachItemVo;
import com.example.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/console")
public class ConsoleCoachController {

    @Autowired
    private CoachService coachService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RelationTagCoachService relationTagCoachService;

    @Autowired
    private RedisService redisService;


    //新增教练信息
    @RequestMapping("/coach/add")
    public Response<Long> addCoach(@RequestParam(name = "pics", required = false) String pics,
                                   @RequestParam("name") String name,
                                   @RequestParam("categoryId") Long categoryId,
                                   @RequestParam(name = "speciality", required = false) String speciality,
                                   @RequestParam(name = "intro", required = false) String intro,
                                   @RequestParam(name = "tags", required = false) String tags) {
        EditCoachDTO editCoachDTO = new EditCoachDTO(null, pics, name.trim(), speciality, intro, categoryId, tags);
        Long coachId = coachService.edit(editCoachDTO);
        // 为保证一致性而删除
        if (null != coachId) {
            redisService.deleteKeysWithPrefix("list_");
        }
        return new Response<>(1001, coachId);
    }

    //删除教练信息
    @RequestMapping("/coach/del")
    public Response<Boolean> delCoach(@RequestParam("id") Long id) {
        Boolean result = coachService.delete(id);
        // 为保证一致性而删除
        if (result) {
            redisService.deleteKeysWithPrefix("list_");
        }
        return new Response<>(1001, result);
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
        Long coachId = coachService.edit(new EditCoachDTO(id, pics, null == name ? null : name.trim(), speciality, intro, categoryId, tags));
        return new Response<>(1001, coachId);
    }

    @RequestMapping("/coach/list")
    public Response<CoachItemListVo> getCoachList(@RequestParam("page") Integer page,
                                                  @RequestParam(name = "keyword", required = false) String keyword) {
        CoachItemListVo coachItemListVo = new CoachItemListVo();
        coachItemListVo.setPageSize(Constant.PAGE_SIZE);

        int coachTotal = coachService.count(keyword);
        coachItemListVo.setTotal(coachTotal);
        if (0 == coachTotal) {
            log.info("总数都为0就不用查了，节约数据库访问");
            return new Response<>(1001, coachItemListVo);
        }

        //如果没有数据，getCoachList会拿到一个空的ArrayList对象
        List<Coach> pageList = coachService.getPageList(page, keyword);

        // 没有取全表，而是根据id进行in条件查询
        Set<Long> categoryIds = pageList.stream().map(Coach::getCategoryId).collect(Collectors.toSet());

        // 获取分类映射列表
        Map<Long, String> categoryMap = categoryService.getList(null, categoryIds, null).stream().collect(Collectors.toMap(Category::getId, Category::getName));

        // vo就是再controller层做转换
        List<CoachItemVo> list = new ArrayList<>();
        for (Coach coach : pageList) {
            String category = categoryMap.get(coach.getCategoryId());
            if (null == category) {
                continue;
            }

            CoachItemVo coachItemVo = new CoachItemVo();
            coachItemVo.setId(coach.getId());
            coachItemVo.setName(coach.getName());
            String pics = coach.getPics();
            //不需要判断是否包含split参数，没有就不切
            String pic = StringUtils.hasLength(pics) ? pics.split(Constant.PIC_SPLIT)[0] : null;
            coachItemVo.setPic(pic);
            coachItemVo.setSpeciality(coach.getSpeciality());
            coachItemVo.setCategory(category);
            list.add(coachItemVo);
        }
        coachItemListVo.setList(list);

        return new Response<>(1001, coachItemListVo);
    }

    @RequestMapping("/coach/list2")
    public Response<CoachItemListVo> getCoachList2(@RequestParam("page") Integer page,
                                         @RequestParam(name = "keyword", required = false) String keyword) {
        CoachItemListVo coachItemListVo = new CoachItemListVo();
        coachItemListVo.setPageSize(Constant.PAGE_SIZE);

        int coachTotal = coachService.count(keyword);
        coachItemListVo.setTotal(coachTotal);
        if (0 == coachTotal) {
            log.info("总数都为0就不用查了，节约数据库访问");
            return new Response<>(1001, coachItemListVo);
        }

        List<CoachItemVo> list = coachService.getPageListLinkTable(page, keyword)
                .stream()
                .map(e -> {
                    CoachItemVo coachItemVo = new CoachItemVo();
                    BeanUtils.copyProperties(e, coachItemVo);
                    return coachItemVo;
                }).collect(Collectors.toList());
        coachItemListVo.setList(list);
        return new Response<>(1001, coachItemListVo);
    }

    @RequestMapping("/coach/detail")
    public Response<CoachDetailsVo> getCoachDetail(@RequestParam(name = "id") Long id) {
        CoachDetailsVo coachDetailsVo = new CoachDetailsVo();
        Coach coachInfo = coachService.getById(id);
        //自己写方法判断
        if (ObjectUtils.isEmpty(coachInfo)) {
            log.info("教练id：{}不存在", id);
            return new Response<>(1001, coachDetailsVo);
        }

        Long categoryId = coachInfo.getCategoryId();
        // 获取分类信息
        Category category = categoryService.getById(categoryId);
        if (ObjectUtils.isEmpty(category)) {
            log.info("分类id：{}不存在", categoryId);
            return new Response<>(1001, coachDetailsVo);
        }

        List<BlockDTO> contents = JSON.parseArray(coachInfo.getIntro(), BlockDTO.class);
        coachDetailsVo.setIntro(contents);
        String pics = coachInfo.getPics();
        if (StringUtils.hasLength(pics)) {
            //不需要判断是否包含split参数，没有就不切
            String[] split = pics.split(Constant.PIC_SPLIT);
            coachDetailsVo.setPics(Arrays.asList(split));
        }

        coachDetailsVo.setCategory(category.getName());
        coachDetailsVo.setIcon(category.getPic());
        List<String> tags = relationTagCoachService.getTagByCoachId(coachInfo.getId())
                .stream().map(Tag::getName).collect(Collectors.toList());
        coachDetailsVo.setTags(tags);

        coachDetailsVo.setCreateTime(CustomUtil.transformTimestamp(coachInfo.getCreateTime() * 1000L, Constant.DATE_PATTERN_1));
        coachDetailsVo.setUpdateTime(CustomUtil.transformTimestamp(coachInfo.getUpdateTime() * 1000L, Constant.DATE_PATTERN_1));
        return new Response<>(1001, coachDetailsVo);
    }
}
