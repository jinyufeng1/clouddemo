package com.example.message.service;

import com.example.message.entity.MessageRecord;
import com.example.message.mapper.MessageRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 短信记录表 服务类
 * </p>
 *
 * @author 我叫小呆呆
 * @since 2025-05-07
 */
@Service
public class MessageRecordService {

    @Resource
    private MessageRecordMapper mapper;

//    **************************五大基础方法**************************
	public MessageRecord getById(Long id) {
        if (null == id) {
            throw new RuntimeException("查询失败，id为空！");
        }

        return mapper.getById(id);
    }

    public MessageRecord extractById(Long id) {
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

	public Boolean insert(MessageRecord entity) {
        if (null == entity) {
            throw new RuntimeException("插入失败，entity为空！");
        }

        if (null == entity.getPhone()) {
            throw new RuntimeException("插入失败，phone必填！");
        }

        if (null == entity.getContent()) {
            throw new RuntimeException("插入失败，content必填！");
        }

        if (null == entity.getCode()) {
            throw new RuntimeException("插入失败，code必填！");
        }

        long timestamp = System.currentTimeMillis() / 1000;
        entity.setCreateTime((int)timestamp);
        entity.setUpdateTime((int)timestamp);
        return 1 == mapper.insert(entity);
    }

    public Boolean update(MessageRecord entity) {
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