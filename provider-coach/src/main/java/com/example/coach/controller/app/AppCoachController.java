package com.example.coach.controller.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.coach.service.CategoryService;
import com.example.coach.service.CoachService;
import com.example.coach.service.RelationTagCoachService;
import com.example.objcoach.vo.app.CoachDetailsVo;
import com.example.objcoach.vo.app.CoachItemListVo;
import com.example.objcoach.vo.app.CoachItemVo;
import com.example.objcoach.vo.app.WpVo;
import com.example.objcoach.dto.BlockDTO;
import com.example.objcoach.entity.Category;
import com.example.objcoach.entity.Coach;
import com.example.objcoach.entity.Tag;
import lombok.extern.slf4j.Slf4j;
import com.example.common.Constant;
import com.example.common.domain.Response;
import com.example.common.util.CustomUtil;
import com.example.common.util.ImageVoUtil;
import com.example.redis.RedisService;
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
@RequestMapping("/app")
public class AppCoachController {

    @Autowired
    private CoachService coachService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RelationTagCoachService relationTagCoachService;

    @Autowired
    private RedisService redisService;

    @RequestMapping("/coach/list")
    public Response<CoachItemListVo> getCoachList(@RequestParam(name = "wp", required = false) String wp,
                                                  @RequestParam(name = "keyword", required = false) String keyword) {

        WpVo wpVo = null;
        if (StringUtils.hasLength(wp)) {
            //Base64解码
            String decode = new String(Base64.getUrlDecoder().decode(wp));
            //获取json转实体
            wpVo = JSON.parseObject(decode, WpVo.class);
        }

        CoachItemListVo coachItemListVo = new CoachItemListVo();
        int page = null == wpVo ? 1 : wpVo.getPage();
        keyword = null == wpVo ? keyword : wpVo.getKeyword();
        String redisKey = "list_" + page + (null == keyword ? "" : "_" + keyword);
        List<CoachItemVo> list;
        // 判断缓存是否存在
        if (redisService.hasKey(redisKey)) {
            String jsonString = redisService.get(redisKey);
            list = JSONArray.parseArray(jsonString, CoachItemVo.class);
        }
        // 去数据库里取
        else {
            // 如果没有数据，getCoachList会拿到一个空的ArrayList对象
            List<Coach> pageList = coachService.getPageList(page, keyword);
            if (pageList.isEmpty()) {
                coachItemListVo.setIsEnd(true);
                return new Response<>(1001, coachItemListVo);
            }

            // 没有取全表，而是根据id进行in条件查询
            Set<Long> categoryIds = pageList.stream().map(Coach::getCategoryId).collect(Collectors.toSet());

            // 获取分类映射列表
            Map<Long, String> categoryMap = categoryService.getList(null, categoryIds, null).stream().collect(Collectors.toMap(Category::getId, Category::getName));

            // vo就是再controller层做转换
            list = new ArrayList<>();
            for (Coach coach : pageList) {
                String category = categoryMap.get(coach.getCategoryId());
                if (null == category) {
                    continue;
                }

                CoachItemVo coachItemVo = new CoachItemVo();
                coachItemVo.setCategory(category);
                BeanUtils.copyProperties(coach, coachItemVo);
                String pics = coach.getPics();
                //不需要判断是否包含split参数，没有就不切
                String pic = StringUtils.hasLength(pics) ? pics.split(Constant.PIC_SPLIT)[0] : null;
                coachItemVo.setPic(ImageVoUtil.transformObj(pic));
                list.add(coachItemVo);
            }

            // json存入redis 过期时间60秒
            String jsonString = JSON.toJSONString(list, SerializerFeature.IgnoreNonFieldGetter);
            redisService.setWithExpire(redisKey, jsonString, 60);
        }

        coachItemListVo.setList(list);
        coachItemListVo.setIsEnd(list.size() < Constant.PAGE_SIZE);

        // 构建下一页需要的wp
        if (null == wpVo) {
            //记录第一次进入接口的时间
            wpVo = new WpVo(2, keyword, CustomUtil.transformTimestamp(System.currentTimeMillis(), Constant.DATE_PATTERN_1), null);
        }
        else {
            wpVo.setPage(wpVo.getPage() + 1);
        }

        // 实体转json
        String jsonString = JSON.toJSONString(wpVo);
        // Base64编码
        String wpString = Base64.getUrlEncoder().encodeToString(jsonString.getBytes());
        coachItemListVo.setWp(wpString);

        return new Response<>(1001, coachItemListVo);
    }

    @RequestMapping("/coach/list2")
    public Response<CoachItemListVo> getCoachList2(@RequestParam(name = "wp", required = false) String wp,
                                         @RequestParam(name = "keyword", required = false) String keyword) {
        WpVo wpVo = null;
        if (StringUtils.hasLength(wp)) {
            //Base64解码
            String decode = new String(Base64.getUrlDecoder().decode(wp));
            //获取json转实体
            wpVo = JSON.parseObject(decode, WpVo.class);
        }

        CoachItemListVo coachItemListVo = new CoachItemListVo();
        List<CoachItemVo> list = coachService.getPageListLinkTable(null == wpVo ? 1 : wpVo.getPage(), null == wpVo ? keyword : wpVo.getKeyword())
                .stream()
                .map(e -> {
                    CoachItemVo coachItemVo = new CoachItemVo();
                    BeanUtils.copyProperties(e, coachItemVo);
                    String pics = e.getPics();
                    //不需要判断是否包含split参数，没有就不切
                    String pic = StringUtils.hasLength(pics) ? pics.split(Constant.PIC_SPLIT)[0] : null;
                    coachItemVo.setPic(ImageVoUtil.transformObj(pic));
                    return coachItemVo;
                }).collect(Collectors.toList());
        coachItemListVo.setList(list);
        coachItemListVo.setIsEnd(list.size() < Constant.PAGE_SIZE);

        // 构建下一页需要的wp
        if (null == wpVo) {
            //记录第一次进入接口的时间
            wpVo = new WpVo(2, keyword, CustomUtil.transformTimestamp(System.currentTimeMillis(), Constant.DATE_PATTERN_1), null);
        }
        else {
            wpVo.setPage(wpVo.getPage() + 1);
        }

        // 实体转json
        String jsonString = JSON.toJSONString(wpVo);
        // Base64编码
        String wpString = Base64.getUrlEncoder().encodeToString(jsonString.getBytes());
        coachItemListVo.setWp(wpString);

        return new Response<>(1001, coachItemListVo);
    }

    @RequestMapping("/coach/detail")
    public Response<CoachDetailsVo> getCoachDetail(@RequestParam(name = "id") Long id) {
        CoachDetailsVo coachDetailsVo = new CoachDetailsVo();
        Coach coachInfo = coachService.getById(id);
        //自己写方法判断
        if (ObjectUtils.isEmpty(coachInfo)) {
            // 不存在不是错误的，用info
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

        // json转成block列表
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
        return new Response<>(1001, coachDetailsVo);
    }
}
