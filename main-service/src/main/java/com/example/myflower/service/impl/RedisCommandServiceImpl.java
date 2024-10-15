package com.example.myflower.service.impl;

import com.example.myflower.dto.auth.responses.FlowerListingResponseDTO;
import com.example.myflower.dto.flowercategogy.response.FlowerCategoryResponseDTO;
import com.example.myflower.exception.ErrorCode;
import com.example.myflower.exception.flowers.FlowerCategoryException;
import com.example.myflower.exception.flowers.FlowerListingException;
import com.example.myflower.service.RedisCommandService;
import com.example.myflower.service.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisCommandServiceImpl implements RedisCommandService {
    private static final Logger LOG = LogManager.getLogger(RedisCommandServiceImpl.class);
    @NonNull
    private RedisService redisService;
    @NonNull
    private ObjectMapper objectMapper;

    @Override
    public List<FlowerCategoryResponseDTO> getAllFlowerCategoriesWithDeleteStatusFalse() {
        try {
            String pattern = "flowerCategories:*";
            Set<String> keys = redisService.getKeysByPattern(pattern);
            return keys.stream()
                    .map(key -> redisService.getStringValueByKey(key))
                    .map(value -> objectMapper.convertValue(value, FlowerCategoryResponseDTO.class))
                    .toList();
        }
        catch (Exception e) {
            LOG.error("[getAllFlowerCategoriesWithDeleteStatusFalse] Has exception: ", e);
            return new ArrayList<>();
        }
    }

    @Override
    public FlowerCategoryResponseDTO getFlowerCategoryById(Integer id) {
        try {
            String key = String.format("flowerCategory:%s", id);
            String value = redisService.getStringValueByKey(key);
            return objectMapper.convertValue(value, FlowerCategoryResponseDTO.class);
        }
        catch (Exception e) {
            LOG.error("[getFlowerCategoryById] Has exception: ", e);
            return null;
        }
    }

    @Override
    public void setAllFlowerCategoriesWithDeleteStatusFalse(List<FlowerCategoryResponseDTO> list) {
        try {
            for (FlowerCategoryResponseDTO flowerCategoryResponseDTO : list) {
                String key = String.format("flowerCategory:%s", flowerCategoryResponseDTO.getId());
                String value = objectMapper.writeValueAsString(flowerCategoryResponseDTO);
                redisService.setStringValueByKey(key, value);
            }
        }
        catch (JsonProcessingException e) {
            throw new FlowerCategoryException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e) {
            LOG.error("[setAllFlowerCategoriesWithDeleteStatusFalse] Has exception: ", e);
        }
    }

    @Override
    public void setFlowerCategoryById(FlowerCategoryResponseDTO responseDTO) {
        try {
            String key = String.format("flowerCategory:%s", responseDTO.getId());
            String value = objectMapper.writeValueAsString(responseDTO);
            redisService.setStringValueByKey(key, value);
        }
        catch (JsonProcessingException e) {
            throw new FlowerCategoryException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e) {
            LOG.error("[setFlowerCategoryById] Has exception: ", e);
        }
    }

    @Override
    public void deleteFlowerCategoryById(Integer id) {
        try {
            String key = String.format("flowerCategory:%s", id);
            redisService.deleteStringValueByKey(key);
        }
        catch (Exception e) {
            LOG.error("[deleteFlowerCategoryById] Has exception: ", e);
        }
    }

    @Override
    public FlowerListingResponseDTO getFlowerById(Integer id) {
        try {
            String key = String.format("flowers:%s", id);
            String value = redisService.getStringValueByKey(key);
            return objectMapper.readValue(value, FlowerListingResponseDTO.class);
        }
        catch (Exception e) {
            LOG.error("[getFlowerById] Has exception: ", e);
            return null;
        }
    }

    @Override
    public void setFlowerById(FlowerListingResponseDTO responseDTO) {
        try {
            String key = String.format("flowers:%s", responseDTO.getId());
            String value = objectMapper.writeValueAsString(responseDTO);
            redisService.setStringValueByKey(key, value);
        } catch (JsonProcessingException e) {
            throw new FlowerListingException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e) {
            LOG.error("[setFlowerById] Has exception: ", e);
        }
    }

    @Override
    public void deleteFlowerById(Integer id) {
        try {
            String key = String.format("flowers:%s", id);
            redisService.deleteStringValueByKey(key);
        }
        catch (Exception e) {
            LOG.error("[deleteFlowerById] Has exception: ", e);
        }
    }

    @Override
    public void storeRefreshToken(Integer userId, String refreshToken) {
        try {
            String checkSumRefreshToken = DigestUtils.md5Hex(refreshToken);
            String key = String.format("refreshTokens:%s:%s", userId, checkSumRefreshToken);
            redisService.setStringValueByKey(key, refreshToken);
        }
        catch (Exception e) {
            LOG.error("[storeRefreshToken] Has exception: ", e);
        }
    }

    @Override
    public boolean isRefreshTokenExisted(Integer userId, String refreshToken) {
        try {
            String checkSumRefreshToken = DigestUtils.md5Hex(refreshToken);
            String pattern = String.format("refreshTokens:%s:%s", userId, checkSumRefreshToken);
            Set<String> keySet = redisService.getKeysByPattern(pattern);
            return !keySet.isEmpty();
        }
        catch (Exception e) {
            LOG.error("[isRefreshTokenExisted] Has exception: ", e);
            return false;
        }
    }

    @Override
    public String getValidRefreshTokenByUserId(Integer userId) {
        try {
            String refreshTokenPattern = String.format("refreshTokens:%s:*", userId);
            Set<String> refreshTokenKeySet = redisService.getKeysByPattern(refreshTokenPattern);
            String revokedTokenPattern = String.format("revokedRefreshTokens:%s:*", userId);
            Set<String> revokedRefreshTokenKeySet = redisService.getKeysByPattern(revokedTokenPattern);
            refreshTokenKeySet.removeAll(revokedRefreshTokenKeySet);
            return refreshTokenKeySet.stream()
                    .map(key -> redisService.getStringValueByKey(key))
                    .findFirst()
                    .orElse(null);
        }
        catch (Exception e) {
            LOG.error("[getValidRefreshTokenByUserId] Has exception: ", e);
            return null;
        }
    }

    @Override
    public void revokeRefreshToken(Integer userId) {
        try {
            String refreshTokenPattern = String.format("refreshTokens:%s:*", userId);
            Set<String> refreshTokenKeySet = redisService.getKeysByPattern(refreshTokenPattern);
            String revokedTokenPattern = String.format("revokedRefreshTokens:%s:*", userId);
            Set<String> revokedRefreshTokenKeySet = redisService.getKeysByPattern(revokedTokenPattern);
            refreshTokenKeySet.removeAll(revokedRefreshTokenKeySet);

            for (String refreshToken : refreshTokenKeySet) {
                String checkSumRefreshToken = DigestUtils.md5Hex(refreshToken);
                String key = String.format("revokedRefreshTokens:%s:%s", userId, checkSumRefreshToken);
                redisService.setStringValueByKey(key, refreshToken);
            }
        }
        catch (Exception e) {
            LOG.error("[revokeRefreshToken] Has exception: ", e);
        }
    }

    @Override
    public boolean isRevokedTokenExist(Integer userId, String refreshToken) {
        try {
            String checkSumRefreshToken = DigestUtils.md5Hex(refreshToken);
            String pattern = String.format("revokedRefreshTokens:%s:%s", userId, checkSumRefreshToken);
            Set<String> keySet = redisService.getKeysByPattern(pattern);
            return !keySet.isEmpty();
        }
        catch (Exception e) {
            LOG.error("[isRevokedTokenExist] Has exception: ", e);
            return false;
        }
    }

    @Override
    public void storeOtpChangeEmail(Integer userId, String newEmail, String changeEmail) {
        String key = String.format("otp:%s:%s", userId, changeEmail);
        redisService.setStringValueByKeyExpire(key, newEmail, 300);
    }

    @Override
    public String getOtpChangeEmail(Integer userId, String changeEmail) {
        String key = String.format("otp:%s:%s", userId, changeEmail);
        String email = redisService.getStringValueByKey(key);
        return email.isEmpty() ? null : email;
    }

    @Override
    public void deleteOtp(Integer userId, String newEmail, String changeEmail) {
        try {
            String key = String.format("otp:%s:%s", userId, changeEmail);
            redisService.deleteStringValueByKey(key);
            String secondKey = String.format("otp:%s:%s", userId, newEmail);
            redisService.deleteStringValueByKey(secondKey);
        } catch (Exception e) {
            LOG.error("[deleteOtp] Has exception: ", e);
        }
    }

    @Override
    public void clearFlowerCache() {
        try {
            String pattern = "flowers:*";
            Set<String> keySet = redisService.getKeysByPattern(pattern);
            redisService.deleteListStringValueByKey(keySet);
        }
        catch (Exception e) {
            LOG.error("[clearFlowerCache] Has exception: ", e);
        }
    }
}