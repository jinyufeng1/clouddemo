package com.example.objcoach;

import com.example.objcoach.entity.Category;
import com.example.objcoach.vo.console.CategoryTreeListVo;
import com.example.objcoach.vo.console.CategoryItemTreeVo;
import lombok.extern.slf4j.Slf4j;
import com.example.common.Constant;
import com.example.common.util.CustomUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class CategoryListToTree {
    private static CategoryItemTreeVo shift(Category category, Map<Long, List<Category>> map) {
        CategoryItemTreeVo categoryItemTreeVo = new CategoryItemTreeVo();
        categoryItemTreeVo.setId(category.getId());
        categoryItemTreeVo.setName(category.getName());
        categoryItemTreeVo.setIcon(category.getPic());
        categoryItemTreeVo.setCreateTime(CustomUtil.transformTimestamp(category.getCreateTime() * 1000L, Constant.DATE_PATTERN_1));
        categoryItemTreeVo.setUpdateTime(CustomUtil.transformTimestamp(category.getUpdateTime() * 1000L, Constant.DATE_PATTERN_1));
        List<Category> children = map.get(category.getId());
        if (null == children || children.isEmpty()) {
            return categoryItemTreeVo;
        }

        //添加子节点
        categoryItemTreeVo.setChildren(new ArrayList<>());
        for (Category child : children) {
            categoryItemTreeVo.getChildren().add(shift(child, map));
        }

        return categoryItemTreeVo;
    }

    public static CategoryTreeListVo getCategoryTreeList(List<Category> list) {
        CategoryTreeListVo categoryTreeListVo = new CategoryTreeListVo();
        if (list.isEmpty()) {
            log.info("无分类信息");
            return categoryTreeListVo;
        }

        // 一级分类
        List<Category> firstList = new ArrayList<>();
        //分类所属关系 所有子节点
        Map<Long, List<Category>> map = new HashMap<>();
        for (Category category : list) {
            Long parentId = category.getParentId();
            if (null == parentId) {
                firstList.add(category);
            } else {
                List<Category> categories = map.computeIfAbsent(parentId, e -> new ArrayList<>());
                categories.add(category);
            }
        }

        if (firstList.isEmpty()) {
            log.info("无一级分类信息");
            return categoryTreeListVo;
        }

        //构建层级列表
        List<CategoryItemTreeVo> retList = firstList.stream().map(e -> shift(e, map)).collect(Collectors.toList());

        categoryTreeListVo.setList(retList);
        return categoryTreeListVo;
    }
}
