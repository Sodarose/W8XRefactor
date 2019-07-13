package com.w8x.web.Service.impl;

import analysis.AbstractRuleVisitor;
import api.AnalysisApi;
import com.alibaba.fastjson.JSON;
import com.w8x.web.Service.RefactCoreService;
import com.w8x.web.api.GithubDataGrabber;
import com.w8x.web.model.*;
import com.w8x.web.ulits.FileUlits;
import com.w8x.web.ulits.RuleLink;
import model.Issue;
import model.JavaModel;
import model.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ulits.JsonUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(RefactCoreService.class);

    private AnalysisApi analysisApi = AnalysisApi.getInstance();
    @Autowired
    GithubDataGrabber githubDataGrabber;

    @Autowired
    ProjectConfig projectConfig;

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

    /**
     * 上传配置文件
     */
    @Override
    public Code<String> uploadCodeStyle(MultipartFile file, String codeName, HttpServletRequest request) {
        CodeStyle codeStyle = null;
        // 将文件下载到文件夹
        try {
           /* if (file.isEmpty()) {
                LOGGER.info(file.getOriginalFilename());
                return Code.createCode(403, "", "上传失败");
            }*/
            LOGGER.info(file.getOriginalFilename());
            String fileName = file.getOriginalFilename();
            String path = "codestyle" + File.separator + fileName;
            codeStyle = new CodeStyle();
            codeStyle.setCodeName(codeName);
            codeStyle.setFilename(path);
            codeStyle.setStatus(false);
            File doFile = FileUlits.readFileByJarContents(path);
            LOGGER.info(doFile.exists() ? "文件存在" : "文件不存在");
            file.transferTo(doFile);
        } catch (IOException e) {
            e.printStackTrace();
            return Code.createCode(403, "", "上传失败");
        }

        //更新内存状态
        if (codeStyle == null) {
            return Code.createCode(403, "", "上传失败");
        }
        List<CodeStyle> list = projectConfig.getCodeStyles();
        int i = -1;
        for (CodeStyle code : list) {
            if (codeStyle.getCodeName().equals(code.getCodeName())) {
                i = list.indexOf(code);
                break;
            }
        }
        if (i >= 0) {
            projectConfig.getCodeStyles().remove(i);
        }
        projectConfig.getCodeStyles().add(codeStyle);

        //更新json配置
        File jsonFile = FileUlits.readFileByJarContents("configuration.json");
        if (!jsonFile.exists()) {
            return Code.createCode(403, "", "上传失败");
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonFile)));
            writer.write(JSON.toJSONString(projectConfig));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {

                }
            }
        }
        LOGGER.info("配置文件修改完毕");
        return Code.createCode(200, "", "上传成功");
    }

    /**
     * 更新配置文件
     */
    @Override
    public Code<String> updateCodeStyleStatus(CodeStyle codeStyle) {

        CodeStyle code = null;
        //找到code
        for (CodeStyle codeStyleItem : projectConfig.getCodeStyles()) {
            if (codeStyle.getCodeName().equals(codeStyle.getCodeName())) {
                code = codeStyleItem;
                break;
            }
        }

        if(code==null){
            Code.createCode(403, "", "更改错误");
        }

        //清除其余配置的状态
        projectConfig.getCodeStyles().stream().forEach(cxf -> {
            cxf.setStatus(false);
        });

        code.setStatus(true);

        //更新json配置
        File jsonFile = FileUlits.readFileByJarContents("configuration.json");
        if (!jsonFile.exists()) {
            return Code.createCode(403, "", "上传失败");
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonFile)));
            writer.write(JSON.toJSONString(projectConfig));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {

                }
            }
        }
        LOGGER.info("配置文件修改完毕");
        return Code.createCode(200, "", "更改配置");
    }

    /**
     * 删除配置文件
     */
    @Override
    public Code<String> deleteCodeStyle(CodeStyle codeStyle) {
        if("默认的谷歌代码风格".equals(codeStyle.getCodeName())){
            return Code.createCode(403,"","不允许删除");
        }

        CodeStyle code = null;
        for(CodeStyle codeStyleItem:projectConfig.getCodeStyles()){
            if(codeStyle.getCodeName().equals(codeStyleItem.getCodeName())){
                code = codeStyleItem;
                break;
            }
        }

        if(code==null){
            Code.createCode(403, "", "删除错误");
        }

        boolean result =  projectConfig.getCodeStyles().remove(code);
        if(!result){
            return Code.createCode(403,"","删除错误");
        }

        //更新json配置
        File jsonFile = FileUlits.readFileByJarContents("configuration.json");
        if (!jsonFile.exists()) {
            return Code.createCode(403, "", "上传失败");
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonFile)));
            writer.write(JSON.toJSONString(projectConfig));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {

                }
            }
        }
        LOGGER.info("配置文件修改完毕");
        return Code.createCode(200, "", "删除成功");
    }

}
