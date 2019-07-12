package com.w8x.web;

import com.w8x.web.model.ProjectConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

    /**
     * 设置一些配置
     */
    @Bean
    public ProjectConfig projectConfig() {
        return ProjectConfig.buildProjectConfig();
    }
}
