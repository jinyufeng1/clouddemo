package com.example.coach.service;

import com.example.coach.mapper.CategoryMapper;
import com.example.objcoach.dto.EditCategoryDTO;
import com.example.objcoach.entity.Category;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 分类信息表 服务类
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-03-30
 */
@Slf4j
@Service
public class CategoryService {

    @Resource
    private CategoryMapper mapper;

//    **************************五大基础方法**************************
	public Category getById(Long id) {
        if (null == id) {
            throw new RuntimeException("查询失败，id为空！");
        }

        return mapper.getById(id);
    }

    public Category extractById(Long id) {
        if (null == id) {
            throw new RuntimeException("查询失败，id为空！");
        }

        return mapper.extractById(id);
    }

    public Boolean delete(Long id) {
        if (null == id) {
            throw new RuntimeException("软删除失败，id为空！");
        }

        long timestamp = System.currentTimeMillis() / 1000;
        return 1 == mapper.delete(id, (int)timestamp);
    }

	public Boolean insert(Category entity) {
        if (null == entity) {
            throw new RuntimeException("插入失败，entity为空！");
        }

        if (null == entity.getName()) {
            throw new RuntimeException("插入失败，name必填！");
        }

        long timestamp = System.currentTimeMillis() / 1000;
        entity.setCreateTime((int)timestamp);
        entity.setUpdateTime((int)timestamp);
        return 1 == mapper.insert(entity);
    }

    public Boolean update(Category entity) {
        if (null == entity) {
            throw new RuntimeException("更新失败，entity为空！");
        }

        if (null == entity.getId()) {
            throw new RuntimeException("更新失败，id为空！");
        }

        // 失去修改的意义
        if (null == entity.getName()
                && null == entity.getPic()
                && null == entity.getParentId()) {
            throw new RuntimeException("更新失败，业务字段全为空！");
        }

        long timestamp = System.currentTimeMillis() / 1000;
        entity.setUpdateTime((int)timestamp);
        return 1 == mapper.update(entity);
    }

    public List<Category> getList(String keyword, Set<Long> ids, Boolean limit) {
        return mapper.getList(keyword, ids, limit);
    }

    public List<Category> getListByParent(String keyword, List<Long> parentIds) {
        return mapper.getListByParent(keyword, parentIds);
    }

    public List<Category> getFirstList(String keyword) {
        return mapper.getFirstList(keyword);
    }

    /**
     * 合并 insert & update
     * @param dto
     * @return
     */
    public Long edit(EditCategoryDTO dto) {
        if (ObjectUtils.isEmpty(dto)) {
            throw new RuntimeException("dto对象为空");
        }

        // 父级校验
        Long parentId = dto.getParentId();
        if (null != parentId) {
            Category category = this.getById(parentId);
            if (ObjectUtils.isEmpty(category)) {
                throw new RuntimeException("parentId：" + parentId + "不存在");
            }
        }

        // copy
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        Boolean result;
        // id校验
        if (ObjectUtils.isEmpty(category.getId())) {
            result = insert(category);
        }
        else {
            result = update(category);
        }
        return result ? category.getId() : null;
    }

    public Boolean deleteHierarchy(Long id) {
        long timestamp = System.currentTimeMillis() / 1000;
        return 0 < mapper.deleteHierarchy(id, (int)timestamp);
    }

    public void collectLeafItemIds(Long parentId, List<Long> leafItemIds) {
        List<Category> children = getListByParent(null , Collections.singletonList(parentId));

        // 判断是否为叶子节点
        if (0 != children.size()) {
            for (Category child : children) {
                collectLeafItemIds(child.getId(), leafItemIds);
            }
        }
        else {
            leafItemIds.add(parentId);
        }
    }

    public List<Category> getAll() {
        return mapper.getAll();
    }
}