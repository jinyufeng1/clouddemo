package com.example.provider.service;

import com.example.provider.mapper.StudentMapper;
import jakarta.annotation.Resource;
import com.example.provider.entity.Student;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 学生表 服务类
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-06-02
 */
@Service
public class StudentService {

    @Resource
    private StudentMapper mapper;

//    **************************五大基础方法**************************
	public Student getById(Long id) {
        if (null == id) {
            throw new RuntimeException("查询失败，id为空！");
        }

        return mapper.getById(id);
    }

    public Student extractById(Long id) {
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

	public Boolean insert(Student entity) {
        if (null == entity) {
            throw new RuntimeException("插入失败，entity为空！");
        }

        // todo 必填字段判断

        long timestamp = System.currentTimeMillis() / 1000;
        entity.setCreateTime((int)timestamp);
        entity.setUpdateTime((int)timestamp);
        return 1 == mapper.insert(entity);
    }

    public Boolean update(Student entity) {
        if (null == entity) {
            throw new RuntimeException("更新失败，entity为空！");
        }

        if (null == entity.getId()) {
            throw new RuntimeException("更新失败，id为空！");
        }

        // todo 业务字段判断 另外还要考虑这张表有没有关联表要更新

        long timestamp = System.currentTimeMillis() / 1000;
        entity.setUpdateTime((int)timestamp);
        return 1 == mapper.update(entity);
    }

    public List<Student> getAll() {
        return mapper.getAll();
    }
}