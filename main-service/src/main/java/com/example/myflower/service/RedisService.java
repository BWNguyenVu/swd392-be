package com.example.myflower.service;

import java.util.List;
import java.util.Set;

public interface RedisService {
    String getStringValueByKey(String key);

    void setStringValueByKey(String key, String value);

    List<String> getListStringValueByKey(String key);

    void deleteStringValueByKey(String key);

    Set<String> getKeysByPattern(String pattern);
}