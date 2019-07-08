package com.w8x.web;

import com.w8x.web.api.GithubDataGrabber;
import com.w8x.web.config.GitConfigDev;
import com.w8x.web.dao.CodeStyleRepository;
import com.w8x.web.pojo.CodeStyleConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebApplicationTests {
    @Autowired
    GitConfigDev gitConfigDev;


    @Autowired
    GithubDataGrabber githubDataGrabber;

    @Autowired
    CodeStyleRepository codeStyleRepository;

    @Test
    public void contextLoads() throws IOException {
        CodeStyleConfig codeStyleConfig = new CodeStyleConfig();
        codeStyleConfig.setId(1L);
        codeStyleConfig.setCodeName("各个风格校验");
        codeStyleConfig.setPath("");
        codeStyleConfig.setUse(true);
        codeStyleRepository.save(codeStyleConfig);
        CodeStyleConfig t = codeStyleRepository.findCodeStyleConfigByCodeNameAndUse("各个风格校验",true);
        System.out.println("啊哈哈哈哈哈"+t);
    }

}
