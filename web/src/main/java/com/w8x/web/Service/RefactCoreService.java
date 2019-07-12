package com.w8x.web.Service;

import analysis.AbstractRuleVisitor;
import com.w8x.web.model.Code;
import com.w8x.web.model.CodeShown;
import com.w8x.web.model.RuleModelVo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * 核心服务
 */
public interface RefactCoreService {
    Code runAnalysis(String filePath) throws FileNotFoundException, IOException;

    String getJavaFileTree();

    CodeShown getJavaFileDetail(String filePath) throws UnsupportedEncodingException;

    Code<String> analysisAgin() throws FileNotFoundException, IOException;

    Code<String> analysisByGithub(String gitPath, String branch) throws IOException;

    Code<List<RuleModelVo>> getRuleByMap();

    Code<String> setRuleByMap(Map<String, Integer> rules) throws IOException;

    boolean saveModify() throws IOException;
}
