package com.w8x.web.config;

import com.w8x.web.model.CodeStyle;
import com.w8x.web.model.ProjectConfig;
import com.w8x.web.ulits.FormatOptions;
import com.w8x.web.ulits.RuleLink;
import model.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 监听器
 * */
@Component
public class ProjectCommandLineRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectCommandLineRunner.class);

    @Autowired
    ProjectConfig projectConfig;

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("初始化配置");

        LOGGER.info("读取重构规则");
        Store.rules= RuleLink.newInstance().readRuleLinkByXML();

        LOGGER.info("代码风格配置");
        Store.codestyle = initCodestyle();
        LOGGER.info("初始化完成");
    }

    private Map<String, Object> initCodestyle() {
        for(CodeStyle codeStyle:projectConfig.getCodeStyles()){
            if(codeStyle.isStatus()){
                projectConfig.setCodeStyle(codeStyle);
            }
        }
        if(projectConfig.getCodeStyle()==null){
            LOGGER.error("配置文件读取失败");
        }
        String name = projectConfig.getCodeStyle().getCodeName();
        LOGGER.info("加载"+name+"风格配置");
        String path = projectConfig.getCodeStyle().getFilename();
        return FormatOptions.newInstance().options(path);
    }

}
