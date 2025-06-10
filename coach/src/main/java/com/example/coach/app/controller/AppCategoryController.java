package com.example.coach.app.controller;

import com.alibaba.fastjson.JSON;
import com.example.coach.app.vo.*;
import com.example.coach.entity.Category;
import com.example.coach.service.CategoryService;
import com.example.coach.service.CoachService;
import lombok.extern.slf4j.Slf4j;
import com.example.common.Constant;
import com.example.common.domain.Response;
import com.example.common.util.ImageVoUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("app")
public class AppCategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CoachService coachService;

    @RequestMapping("/category/list")
    public Response<CategoryItemListVo> getCategoryList(@RequestParam(name = "keyword", required = false) String keyword) {
        CategoryItemListVo categoryItemListVo = new CategoryItemListVo();
        List<Category> firstList = categoryService.getFirstList(keyword); // 一级分类

        if (firstList.isEmpty()) {
            log.info("无一级分类信息");
            return new Response<>(1001, categoryItemListVo);
        }
        List<Long> parentIds = firstList.stream().map(Category::getId).collect(Collectors.toList());

        List<Category> nextList = categoryService.getListByParent(null, parentIds); // 二级分类
        //分类所属关系
        Map<Long, List<Category>> map = new HashMap<>();
        for (Category category : nextList) {
            List<Category> categories = map.computeIfAbsent(category.getParentId(), e -> new ArrayList<>());
            categories.add(category);
        }

        //构建层级列表
        List<CategoryItemVo> retList = firstList.stream().map(e -> {
            CategoryItemVo categoryItemVo = new CategoryItemVo();
            categoryItemVo.setId(e.getId());
            categoryItemVo.setName(e.getName());
            categoryItemVo.setIcon(e.getPic());
            List<Category> categories = map.get(e.getId());
            if (!CollectionUtils.isEmpty(categories)) {
                categoryItemVo.setChildren(new ArrayList<>());

                for (Category category : categories) {
                    CategoryItemVo child = new CategoryItemVo();
                    child.setId(category.getId());
                    child.setName(category.getName());
                    child.setIcon(category.getPic());
                    categoryItemVo.getChildren().add(child);
                }
            }

            return categoryItemVo;
        }).collect(Collectors.toList());

        categoryItemListVo.setList(retList);
        return new Response<>(1001, categoryItemListVo);
    }

    @RequestMapping("/category/nlist")
    public Response<LevelThreeAboveVo> getLevelThreeAboveList(@RequestParam(name = "wp", required = false) String wp,
                                                              @RequestParam(name = "parentId", required = false) Long parentId) {
        int page;
        List<Long> leafCategoryIds = null;
        WpVo wpVo = null;
        if (StringUtils.hasLength(wp)) {
            //Base64解码
            String decode = new String(Base64.getUrlDecoder().decode(wp));
            //获取json转实体
            wpVo = JSON.parseObject(decode, WpVo.class);

            page = wpVo.getPage();
            leafCategoryIds = wpVo.getLeafCategoryIds();
        }
        else {
            page = 1;
        }

        LevelThreeAboveVo levelThreeAboveVo = new LevelThreeAboveVo();
        // 获取下级类目列表
        if (1 == page) {
            // 获取下级类目列表
            List<Category> list = categoryService.getListByParent(null , Collections.singletonList(parentId));
            //如果没有后面都不用做了
            if (list.isEmpty()) {
                log.info("分类id：{}无下级分类", parentId);
                levelThreeAboveVo.setIsEnd(true);
                return new Response<>(1001, levelThreeAboveVo);
            }

            List<CategoryItemVo> categoryItems = list.stream().map(e -> {
                CategoryItemVo categoryItemVo = new CategoryItemVo();
                categoryItemVo.setId(e.getId());
                categoryItemVo.setName(e.getName());
                categoryItemVo.setIcon(e.getPic());
                return categoryItemVo;
            }).collect(Collectors.toList());
            levelThreeAboveVo.setCategoryItems(categoryItems);

            // 向下递归，收集叶子节点类目id
            leafCategoryIds = new ArrayList<>();
            categoryService.collectLeafItemIds(parentId, leafCategoryIds);
        }

        // 获取推荐列表
        List<CoachItemVo> coachItemVos = coachService.getPageListLinkTable2(page, leafCategoryIds)
                .stream().map(e -> {
                    CoachItemVo coachItemVo = new CoachItemVo();
                    BeanUtils.copyProperties(e, coachItemVo);
                    String pics = e.getPics();
                    //不需要判断是否包含split参数，没有就不切
                    String pic = StringUtils.hasLength(pics) ? pics.split(Constant.PIC_SPLIT)[0] : null;
                    coachItemVo.setPic(ImageVoUtil.transformObj(pic));
                    return coachItemVo;
                }).collect(Collectors.toList());

        if (coachItemVos.isEmpty()) {
            levelThreeAboveVo.setIsEnd(true);
            return new Response<>(1001, levelThreeAboveVo);
        }

        levelThreeAboveVo.setCoachItems(coachItemVos);
        boolean isEnd = coachItemVos.size() < Constant.PAGE_SIZE;
        levelThreeAboveVo.setIsEnd(isEnd);

        if (isEnd) {
            return new Response<>(1001, levelThreeAboveVo);
        }

        if (1 == page) {
            wpVo = new WpVo(2, null, null, leafCategoryIds);
        }
        else {
            wpVo.setPage(wpVo.getPage() + 1);
        }

        // 实体转json
        String jsonString = JSON.toJSONString(wpVo);
        // Base64编码
        String wpString = Base64.getUrlEncoder().encodeToString(jsonString.getBytes());
        levelThreeAboveVo.setWp(wpString);

        return new Response<>(1001, levelThreeAboveVo);
    }
}
