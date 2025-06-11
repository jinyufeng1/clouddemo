package com.example.message.service;

import com.example.message.entity.MessageTask;
import com.example.message.mapper.MessageTaskMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 短信任务表表 服务类
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-05-07
 */
@Service
public class MessageTaskService {

    @Resource
    private MessageTaskMapper mapper;

    public List<MessageTask> getByStatus(Integer status) {
        if (null == status) {
            throw new RuntimeException("查询失败，status为空！");
        }

        return mapper.getByStatus(status);
    }

//    **************************五大基础方法**************************
	public MessageTask getById(Long id) {
        if (null == id) {
            throw new RuntimeException("查询失败，id为空！");
        }

        return mapper.getById(id);
    }

    public MessageTask extractById(Long id) {
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

    public Boolean opLock(Long id) {
        if (null == id) {
            throw new RuntimeException("检验乐观锁失败，id为空！");
        }

        long timestamp = System.currentTimeMillis() / 1000;
        return 1 == mapper.opLock(id, (int)timestamp);
    }

	public Boolean insert(MessageTask entity) {
        if (null == entity) {
            throw new RuntimeException("插入失败，entity为空！");
        }

        if (null == entity.getPhone()) {
            throw new RuntimeException("插入失败，phone必填！");
        }

        if (null == entity.getStatus()) {
            throw new RuntimeException("插入失败，status必填！");
        }

        long timestamp = System.currentTimeMillis() / 1000;
        entity.setCreateTime((int)timestamp);
        entity.setUpdateTime((int)timestamp);
        return 1 == mapper.insert(entity);
    }

    public Boolean update(MessageTask entity) {
        if (null == entity) {
            throw new RuntimeException("更新失败，entity为空！");
        }

        if (null == entity.getId()) {
            throw new RuntimeException("更新失败，id为空！");
        }

        long timestamp = System.currentTimeMillis() / 1000;
        entity.setUpdateTime((int)timestamp);
        return 1 == mapper.update(entity);
    }
}