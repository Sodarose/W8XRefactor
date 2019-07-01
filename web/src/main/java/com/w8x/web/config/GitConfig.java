package com.w8x.web.config;


import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 *  针对于git的配置
 * */
@Data
@Component
@PropertySource("classpath:application-dev.properties")
@ConfigurationProperties(prefix = "git")
public class GitConfig {
    private String path;
    private File repository;
}
