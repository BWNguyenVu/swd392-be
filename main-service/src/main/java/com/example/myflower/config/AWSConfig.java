package com.example.myflower.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String accessSecret;
    @Value("${cloud.aws.region.static}")
    private String region;

    public AWSCredentials getAWSCredentials() {
        return new BasicAWSCredentials(accessKey, accessSecret);
    }

    @Bean
    public AmazonS3 generateS3Client() {
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(getAWSCredentials()))
                .withRegion(region)
                .build();
    }
}