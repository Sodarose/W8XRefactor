package com.w8x.web.config;

import api.AnalysisApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProjectCommandLineRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectCommandLineRunner.class);
    private AnalysisApi analysisApi = AnalysisApi.getInstance();

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("初始化配置");
        analysisApi.init();
        LOGGER.info("初始化完成");
    }
}
