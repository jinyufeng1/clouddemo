package com.example.file;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreateBeanConfig {
    @Value("${aliyun.accessKey.id}")
    private String accessKeyId;
    @Value("${aliyun.accessKey.secret}")
    private String accessKeySecret;
    @Value("${aliyun.endpoint}")
    private String endpoint;

    @Bean
    public OSS OssClient() {
        //创建实例的时候参数内容不能为空 随便补内容都可以，但是运行需要有效的内容
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
}
