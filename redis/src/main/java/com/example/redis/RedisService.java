package com.example.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 设置键值对
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    // 获取键值对
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    // 删除键
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    // 删除以指定字符串开头的所有键
    public void deleteKeysWithPrefix(String prefix) {
        // 使用 keys 命令查找所有匹配的键
        Set<String> keys = stringRedisTemplate.keys(prefix + "*");

        if (keys != null && !keys.isEmpty()) {
            // 批量删除匹配的键
            stringRedisTemplate.delete(keys);
            System.out.println("Deleted keys: " + keys);
        } else {
            System.out.println("No keys found with prefix: " + prefix);
        }
    }

    // 设置键值对并设置过期时间（秒）
    public void setWithExpire(String key, String value, long timeout) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    // 判断键是否存在
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    // 增加计数器（原子操作）
    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    // 减少计数器（原子操作）
    public Long decrement(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, -delta);
    }
}