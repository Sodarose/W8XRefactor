package com.w8x.web.Service;

import analysis.AbstractRuleVisitor;
import com.w8x.web.model.Code;
import com.w8x.web.model.CodeShown;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 核心服务
 * */
public interface RefactCoreService {
    Code runAnalysis(String filePath) throws FileNotFoundException;
    String getJavaFileTree();
    CodeShown getJavaFileDetail(String filePath) throws UnsupportedEncodingException;
    Code<String> refactorAll();

    Code<String> analysisAgin() throws FileNotFoundException;

    Code<String> analysisByGithub(String gitPath,String branch) throws IOException;

    Code<Map<String, AbstractRuleVisitor>> getRuleByMap();

    Code<String> setRuleByMap(Map<String,Integer> rules) throws IOException;
}
