package com.example.message;

import com.aliyun.dysmsapi20170525.Client;
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
    public Client client() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 私密信息不能上传到github
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint(endpoint);
        return new Client(config);
    }
}
