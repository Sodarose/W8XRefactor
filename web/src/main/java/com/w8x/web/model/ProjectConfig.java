package com.w8x.web.model;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.w8x.web.ulits.FileUlits;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Data
public class ProjectConfig {
    @JsonIgnore
    private static final String PROJECT_CONFIG = "configuration.json";
    @JsonIgnore
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectConfig.class);

    @JSONField(name = "clonepath")
    String clonePath;
    @JSONField(name = "codestyles")
    List<CodeStyle> codeStyles;
    @JsonIgnore
    CodeStyle codeStyle;

    public static ProjectConfig buildProjectConfig() {
        String json = FileUlits.readFile(FileUlits.readFileByJarContents(PROJECT_CONFIG));
        ProjectConfig projectConfig = JSON.parseObject(json, ProjectConfig.class);
        if (projectConfig == null) {
            LOGGER.warn("读取配置文件失败");
        }
        return projectConfig;
    }
}
