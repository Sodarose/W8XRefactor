package com.w8x.web.controller;

import analysis.AbstractRuleVisitor;
import com.w8x.web.Service.RefactCoreService;
import com.w8x.web.model.Code;
import com.w8x.web.model.CodeShown;
import com.w8x.web.model.RuleModelVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/core")
public class WebRedux {

    @Autowired
    RefactCoreService refactCoreService;

    /**
     * 获得项目路径 并进行扫描
     */
    @RequestMapping(value = "/analysis", method = RequestMethod.POST)
    @ResponseBody
    public Code analysis(@RequestParam(value = "fileName") String fileName) throws FileNotFoundException {
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

    @GetMapping("/refactorAll")
    @ResponseBody
    Code<String> refactorAll() {
        return refactCoreService.refactorAll();
    }

    @GetMapping("/analysisagin")
    @ResponseBody
    Code<String> analysisAgin() throws FileNotFoundException {
        return refactCoreService.analysisAgin();
    }

    @PostMapping("/analysisByGitHub")
    @ResponseBody
    Code<String> analysisByGitHub(String gitPath, String branch) throws IOException {
        return refactCoreService.analysisByGithub(gitPath,branch);
    }


    @GetMapping("/analysisRules")
    @ResponseBody
    Code<List<RuleModelVo>> getRules() {
        return refactCoreService.getRuleByMap();
    }

    @PostMapping("/setAnalysisRules")
    @ResponseBody
    Code<String> setRules(Map<String, Integer> rules) throws IOException {
        System.out.println(rules);
        return null;
    }


}
