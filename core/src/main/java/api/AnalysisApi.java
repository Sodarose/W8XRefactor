package api;

import analysis.process.Analysis;
import com.alibaba.fastjson.JSON;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import model.*;
import ulits.ThreadPoolUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

public class AnalysisApi {

    private static AnalysisApi instance;

    public static AnalysisApi getInstance() {
        if (instance == null) {
            instance = new AnalysisApi();
        }
        return instance;
    }

    /**
     * 项目扫描接口
     */
    public boolean analysis(String path) throws FileNotFoundException {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        Analysis analysis = new Analysis();
        analysis.analysis(path);
        //数据处理
        organizeData();
        Store.run = true;
        saveProject();
        return Store.javaModelMap != null;
    }

    /**
     * 重新扫描
     */
    public boolean analysisagin() throws FileNotFoundException {
        return analysis(Store.path);
    }

    /**
     * 数据处理
     */
    private void organizeData() {
        Store.javaModelMap.entrySet().stream().forEach(entry -> {
            if (entry.getValue().getIssues() != null && entry.getValue().getIssues().size() != 0) {
                TreeNode treeNode = Store.treeNodeMap.get(entry.getKey());
                if (treeNode != null) {
                    treeNode.setHasIssue(true);
                }
            }
        });
    }


    public String getJavaFileTree() {
        return JSON.toJSONString(Store.rootNode);
    }

    public JavaModel getJavaModelVo(String filePath) {
        return Store.javaModelMap.get(filePath);
    }

    public void saveProject() {
        for (Map.Entry<String, JavaModel> entry : Store.javaModelMap.entrySet()) {
            TransmissionThread transmissionThread = new TransmissionThread(entry.getValue());
            ThreadPoolUtils.execute(transmissionThread);
        }
    }
}
