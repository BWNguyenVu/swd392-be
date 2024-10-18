package com.example.myflower.service;

import java.util.List;
import java.util.Set;

public interface RedisService {
    String getStringValueByKey(String key);

    void setStringValueByKey(String key, String value);

    void setStringValueByKeyExpire(String key, String value, long expirationTime);

    List<String> getListStringValueByKey(String key);

    void deleteStringValueByKey(String key);

    void deleteListStringValueByKey(Set<String> keys);

    Set<String> getKeysByPattern(String pattern);
}