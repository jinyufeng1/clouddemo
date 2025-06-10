package com.example.coach.service;

import com.example.coach.entity.Tag;
import com.example.coach.mapper.TagMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 标签信息表 服务类
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-04-28
 */
@Service
public class TagService {

    @Resource
    private TagMapper mapper;

    public List<Tag> getByNames(List<String> names) {
        return mapper.getByNames(names);
    }

//    **************************五大基础方法**************************
	public Tag getById(Long id) {
        if (null == id) {
            throw new RuntimeException("查询失败，id为空！");
        }

        return mapper.getById(id);
    }

    public Tag extractById(Long id) {
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

	public Boolean insert(Tag entity) {
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

    public Boolean update(Tag entity) {
        if (null == entity) {
            throw new RuntimeException("更新失败，entity为空！");
        }

        if (null == entity.getId()) {
            throw new RuntimeException("更新失败，id为空！");
        }

        if (null == entity.getName()) {
            throw new RuntimeException("更新失败，业务字段全为空！");
        }

        long timestamp = System.currentTimeMillis() / 1000;
        entity.setUpdateTime((int)timestamp);
        return 1 == mapper.update(entity);
    }
}