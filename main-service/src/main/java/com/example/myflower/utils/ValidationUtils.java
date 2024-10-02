package com.example.myflower.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ValidationUtils {
    private ValidationUtils() {}

    public static boolean validateImage(MultipartFile file) {
        try {
            return !file.isEmpty() &&
                    ("image/png".equals(file.getContentType()) || "image/jpg".equals(file.getContentType()) || "image/jpeg".equals(file.getContentType()));
        }
        catch (Exception e) {
            return false;
        }
    }
}
