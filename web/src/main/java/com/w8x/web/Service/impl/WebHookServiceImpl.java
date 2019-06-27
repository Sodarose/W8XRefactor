package com.w8x.web.Service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.w8x.web.Service.WebHookService;
import com.w8x.web.api.GithubDataGrabber;
import com.w8x.web.model.CommitMsg;
import com.w8x.web.model.Commits;
import com.w8x.web.model.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于接受来自github的webhook请求
 * */
@Service
public class WebHookServiceImpl implements WebHookService {

    private static Logger LOGGER = LoggerFactory.getLogger(WebHookServiceImpl.class);

    @Autowired
    GithubDataGrabber git;

    @Override
    public int webHookSolve(String payload) {
        LOGGER.info("开始");
        CommitMsg commitMsg = solveJson(payload);
        if(commitMsg==null){
            return solveFristRequest(payload);
        }
        LOGGER.info("开始处理信息");
        if(git.checkLocalRepository(commitMsg.getRepository().getFull_name())){
            git.gitPullRepository(commitMsg);
        }else {
            git.gitCloneRepository(commitMsg);
        }
        List<String> javaFiles = git.collectJavaFile(commitMsg);
        return 0;
    }

    /**
     * */
    private CommitMsg solveJson(String payload){
        try {
            LOGGER.info("解析json");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(payload);
            String zen = root.findPath("zen").asText();
            if(zen!=""){
                return null;
            }
            JsonNode rep = root.findPath("repository");
            JsonNode commit = root.findPath("commits");
            String ref = root.findPath("ref").asText();
            String before = root.findPath("before").asText();
            String after = root.findPath("after").asText();
            List<Commits> commits = objectMapper.readValue(commit.toString(),objectMapper
                .getTypeFactory()
                .constructCollectionType(ArrayList.class,Commits.class));
            Repository repository = objectMapper.readValue(rep.toString(), Repository.class);
            CommitMsg commitMsg = new CommitMsg(ref,before,after,commits,repository);
            return commitMsg;
        }catch (IOException e){
            LOGGER.info("解析失败");
            e.printStackTrace();
        }
        LOGGER.info("解析失败");
        return null;
    }

    /**
     * 处理github 的第一次请求
     * */
    private int solveFristRequest(String json){
        return 0;
    }
}
