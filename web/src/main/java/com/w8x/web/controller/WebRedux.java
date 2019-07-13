package com.w8x.web.controller;

import com.w8x.web.BO.RulesConfig;
import com.w8x.web.Service.RefactCoreService;
import com.w8x.web.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/core")
public class WebRedux {

    @Autowired
    RefactCoreService refactCoreService;

    @Autowired
    ProjectConfig projectConfig;

    /**
     * 获得项目路径 并进行扫描
     */
    @RequestMapping(value = "/analysis", method = RequestMethod.POST)
    @ResponseBody
    public Code analysis(@RequestParam(value = "fileName") String fileName) throws FileNotFoundException, IOException {
        //System.out.println(fileName);
        Code code = refactCoreService.runAnalysis(fileName);
        return code;
    }

    @GetMapping("/projectTree")
    @ResponseBody
    public String javaFileTree() {
        return refactCoreService.getJavaFileTree();
    }

    @PostMapping("/javaFileDetail")
    @ResponseBody
    public CodeShown javaFileDetail(String filePath) throws UnsupportedEncodingException {
        return refactCoreService.getJavaFileDetail(filePath);
    }


    @GetMapping("/analysisagin")
    @ResponseBody
    Code<String> analysisAgin() throws FileNotFoundException, IOException {
        return refactCoreService.analysisAgin();
    }

    @PostMapping("/analysisByGitHub")
    @ResponseBody
    Code<String> analysisByGitHub(String gitPath, String branch) throws IOException {
        return refactCoreService.analysisByGithub(gitPath, branch);
    }


    @GetMapping("/analysisRules")
    @ResponseBody
    Code<List<RuleModelVo>> getRules() {
        return refactCoreService.getRuleByMap();
    }

    @PostMapping("/setAnalysisRules")
    @ResponseBody
    Code<String> setRules(@RequestBody RulesConfig[] rules) throws IOException {
        Map<String, Integer> ruleMap = new HashMap<>();
        for (RulesConfig rulesConfig : rules) {
            ruleMap.put(rulesConfig.getRuleName(), rulesConfig.getStatus());
        }
        return refactCoreService.setRuleByMap(ruleMap);
    }


    @PostMapping("/project/saveModify")
    @ResponseBody
    public boolean saveModify() throws IOException {
        return refactCoreService.saveModify();
    }

    @GetMapping("/getCodeStyles")
    @ResponseBody
    public Code<List<CodeStyle>> getCodeStyleList() {
        if (projectConfig.getCodeStyles() == null || projectConfig.getCodeStyles().size() == 0) {
            return Code.createCode(403, null, "没有配置文件");
        }
        return Code.createCode(200, projectConfig.getCodeStyles(), "得到配置信息");
    }

    @PostMapping("/uploadCodeStyle")
    @ResponseBody
    public Code<String> uploadCodeStyle(MultipartFile file, @RequestParam("codename") String codeName, HttpServletRequest request) {
        return refactCoreService.uploadCodeStyle(file, codeName, request);
    }

    @PostMapping("/updateCodeStyleStatus")
    @ResponseBody
    public Code<String> updateCodeStyleStatus(@RequestParam(name = "codename") String codeName) {
        return refactCoreService.updateCodeStyleStatus(codeName);
    }

    @PostMapping("/deleteCodeStyleStatus")
    @ResponseBody
    public Code<String> deleteCodeStyleStatus(@RequestParam(name = "codename") String codeName) {
        return refactCoreService.deleteCodeStyle(codeName);
    }

}
