package com.w8x.web.config;

import api.AnalysisApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 监听器
 * */
@Component
public class ProjectCommandLineRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectCommandLineRunner.class);
    private AnalysisApi analysisApi = AnalysisApi.getInstance();

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("初始化配置");
        //扫描配置初始化
        analysisApi.init();
        //数据库初始化

        //代码风格初始化

        //其余配置初始化
        
        LOGGER.info("初始化完成");
    }
}
