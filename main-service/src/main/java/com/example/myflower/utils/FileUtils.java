package com.example.myflower.utils;

import io.jsonwebtoken.lang.Collections;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private FileUtils() {}

    public static File convertMultiPartFileToFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("Invalid file name");
        }

        File convertedFile = new File(originalFilename);

        // Use try-with-resources to ensure the stream is closed properly
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            // Handle exception (e.g., logging)
            throw new IOException("Failed to convert MultipartFile to File", e);
        }

        return convertedFile;
    }

    public static String generateFileName(MultipartFile file) {
        return System.currentTimeMillis() + "_" + file.getOriginalFilename();
    }

    public static List<MultipartFile> filterEmptyFiles(List<MultipartFile> files) {
        if (Collections.isEmpty(files)) {
            return new ArrayList<>();
        }
        return files.stream()
                .filter(file -> file != null && !file.isEmpty()) // Check if file is not null and not empty
                .toList();
    }
}
