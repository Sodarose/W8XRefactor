package com.w8x.web.Service.impl;

import api.AnalysisApi;
import com.w8x.web.Service.RefactCoreService;
import com.w8x.web.model.Code;
import com.w8x.web.model.CodeShown;
import com.w8x.web.model.IssueShow;
import com.w8x.web.model.Overview;
import model.Issue;
import model.JavaModel;
import model.Store;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 调用Core服务
 *
 * @author Administrator
 */
@Service
public class RefactCoreServiceImpl implements RefactCoreService {

    private AnalysisApi analysisApi = AnalysisApi.getInstance();

    /**
     * 进行 分析
     */
    @Override
    public Code runAnalysis(String filePath) {
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
            return null;
        }
        CodeShown codeShown = new CodeShown();
        codeShown.setOriginalCode(vo.getUnit().toString());
        //判断是否重构了
        if (vo.getCopyUnit() != null) {
            codeShown.setRefactCode(vo.getUnit().toString());
            codeShown.setOriginalCode(vo.getCopyUnit().toString());
        }
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
    public Code<Overview> getOverview() {
        Overview overview = new Overview();
        if (!Store.run) {
            return Code.createCode(404, null, "获取信息失败");
        }
        overview.setProjectName(Store.projectRoot.getRoot().toFile().getName());
        overview.setIssueCount(Store.issueContext.getIssues().size());
        overview.setJavaCount(Store.javaModelMap.size());
        overview.setRule(Store.rules.size());
        overview.setRealPath(Store.projectRoot.getRoot().toFile().getPath());
        int badCount = 0;
        badCount = (int) Store.javaModelMap.values().stream().filter(javaModelVo ->
                javaModelVo.getIssues() != null && javaModelVo.getIssues().size() != 0).count();
        overview.setBadFileCount(badCount);
        return Code.createCode(200, overview, "获取信息成功");
    }

    @Override
    public Code<String> refactorAll() {
        if (!Store.run) {
            return Code.createCode(404, null, "操作失败");
        }
        return Code.createCode(200, null, "操作成功");
    }

    @Override
    public Code<String> analysisAgin() {
        if (analysisApi.analysisagin()) {
            return Code.createCode(200, null, "扫描成功");
        }
        return Code.createCode(404, null, "扫描失败");
    }


}
