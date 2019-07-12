package com.w8x.web.Service.impl;

import analysis.AbstractRuleVisitor;
import api.AnalysisApi;
import com.w8x.web.Service.RefactCoreService;
import com.w8x.web.api.GithubDataGrabber;
import com.w8x.web.model.Code;
import com.w8x.web.model.CodeShown;
import com.w8x.web.model.IssueShow;
import com.w8x.web.model.RuleModelVo;
import com.w8x.web.ulits.RuleLink;
import model.Issue;
import model.JavaModel;
import model.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ulits.JsonUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 调用Core服务
 *
 * @author Administrator
 */
@Service
public class RefactCoreServiceImpl implements RefactCoreService {

    private AnalysisApi analysisApi = AnalysisApi.getInstance();
    @Autowired
    GithubDataGrabber githubDataGrabber;

    /**
     * 进行 分析
     */
    @Override
    public Code runAnalysis(String filePath) throws FileNotFoundException {
        if (analysisApi.analysis(filePath)) {
            return Code.createCode(200, null, "扫描成功");
        }
        return Code.createCode(404, null, "扫描失败");
    }

    @Override
    public String getJavaFileTree() {
        return analysisApi.getJavaFileTree();
    }

    @Override
    public CodeShown getJavaFileDetail(String filePath) throws UnsupportedEncodingException {
        JavaModel vo = analysisApi.getJavaModelVo(filePath);
        if (vo == null) {
            System.out.println("有为空的代码片段");
            return null;
        }
        CodeShown codeShown = new CodeShown();
        codeShown.setRefactCode(vo.getUnit().toString());
        codeShown.setOriginalCode(vo.getFileContent());
        if (vo.getIssues() != null && vo.getIssues().size() != 0) {
            List<IssueShow> issueShows = new ArrayList<>();
            for (Issue issue : vo.getIssues()) {
                IssueShow issueShow = new IssueShow();
                if (!issue.getIssueNode().getRange().isPresent()) {
                    continue;
                }
                issueShow.setBeginLine(issue.getIssueNode().getRange().get().begin.line);
                issueShow.setEndLine(issue.getIssueNode().getRange().get().end.line);
                issueShow.setIssueMessage(issue.getDescription());
                issueShow.setRuleName(issue.getRuleName());
                issueShows.add(issueShow);
            }
            codeShown.setIssueShows(issueShows);
        }
        return codeShown;
    }


    @Override
    public Code<String> analysisAgin() throws FileNotFoundException {
        if (analysisApi.analysisagin()) {
            return Code.createCode(200, null, "扫描成功");
        }
        return Code.createCode(404, null, "扫描失败");
    }

    @Override
    public Code<String> analysisByGithub(String gitPath, String branch) throws IOException {
        String projectPath = githubDataGrabber.gitCloneRepository(gitPath, branch);
        if (projectPath == null) {
            return Code.createCode(404, "仓库克隆失败", "仓库克隆失败");
        }
        return runAnalysis(projectPath);
    }

    @Override
    public Code<List<RuleModelVo>> getRuleByMap() {
        Map<String, AbstractRuleVisitor> ruleVisitorMap = Store.ruleMap;
        if (ruleVisitorMap == null) {
            return Code.createCode(404, null, "数据获取失败");
        }
        List<RuleModelVo> ruleModelVos = new ArrayList<>();
        for (Map.Entry<String, AbstractRuleVisitor> entry : ruleVisitorMap.entrySet()) {
            AbstractRuleVisitor ruleVisitor = entry.getValue();
            ruleModelVos.add(new RuleModelVo(ruleVisitor.getRuleName(), ruleVisitor.getDescription()
                    , ruleVisitor.isRuleStatus(), ruleVisitor.getMessage(), ruleVisitor.getExample()));
        }
        return Code.createCode(200, ruleModelVos, "获取数据成功");
    }

    @Override
    public Code<String> setRuleByMap(Map<String, Integer> rules) throws IOException {
        for (Map.Entry<String, Integer> entry : rules.entrySet()) {
            AbstractRuleVisitor rule = Store.ruleMap.get(entry.getKey());
            if (rule == null) {
                continue;
            }
            if (entry.getValue() == 1) {
                rule.setRuleStatus(true);
            } else {
                rule.setRuleStatus(false);
            }
        }
        //修改xml内容
        RuleLink.newInstance().changeRuleXMLByMap(rules);
        return Code.createCode(200, "", "设置成功");
    }


    @Override
    public boolean saveModify() throws IOException {
        boolean ModifyFlag = JsonUtil.savemodify(Store.modifyPath);
        return ModifyFlag;
    }
}
