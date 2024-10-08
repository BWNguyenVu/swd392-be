package com.example.myflower.service.impl;

import com.example.myflower.service.RedisService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    @NonNull
    private StringRedisTemplate redisTemplate;

    @Override
    public String getStringValueByKey(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void setStringValueByKey(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public List<String> getListStringValueByKey(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    @Override
    public void deleteStringValueByKey(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public Set<String> getKeysByPattern(String pattern) {
        return redisTemplate.keys(pattern);
    }
}