package com.w8x.web;

import com.w8x.web.api.GithubDataGrabber;
import com.w8x.web.config.GitConfig;
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
    GitConfig gitConfig;


    @Autowired
    GithubDataGrabber githubDataGrabber;


    @Test
    public void contextLoads() throws IOException {
       // githubDataGrabber.gitCloneRepository("https://github.com/Sodarose/W8XRefactor.git","master");
    }

}
