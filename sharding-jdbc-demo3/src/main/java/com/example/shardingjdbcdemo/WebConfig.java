package com.example.shardingjdbcdemo;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

// @EnableWebMvc = 手动接管 Spring MVC ，Spring Boot 项目默认已帮你做好了，除非你要“全手动”，否则不要用它
// 完全接管 MVC 配置，才用 @EnableWebMvc + WebMvcConfigurer
// 加了 @EnableWebMvc 后，Spring Boot 的 MVC 自动配置 WebMvcAutoConfiguration 会完全失效，你需要自己配静态资源、消息转换器、参数解析器等
@Configuration
public class WebConfig implements WebMvcConfigurer {

//    extendMessageConverters：在原有列表基础上修改，更适合微调
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 移除所有 XML 转换器
        // ShardingSphere 把 MappingJackson2XmlHttpMessageConverter 注册到了 Spring 的消息转换器列表，并且它的优先级高于 MappingJackson2HttpMessageConverter
//        converters.removeIf(c -> c instanceof MappingJackson2XmlHttpMessageConverter);
//        converters.removeIf(c -> MappingJackson2XmlHttpMessageConverter.class.isInstance(c));
        converters.removeIf(MappingJackson2XmlHttpMessageConverter.class::isInstance);
    }
}
