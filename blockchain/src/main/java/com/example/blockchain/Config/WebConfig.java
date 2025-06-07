package com.example.blockchain.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "/uploads/**"로 들어오는 요청을 "C:/upload/" 디렉토리에서 찾도록 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/upload/");
    }
}
