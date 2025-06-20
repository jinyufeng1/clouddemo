package com.example.file;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FileApplication {

    @Value("${oss.accessKey.id}")
    private String accessKeyId;
    @Value("${oss.accessKey.secret}")
    private String accessKeySecret;
    @Value("${oss.endpoint}")
    private String endpoint;

    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class, args);
    }

    @Bean
    public OSS OssClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
}
