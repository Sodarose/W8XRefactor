package api;

import analysis.process.Analysis;
import com.alibaba.fastjson.JSON;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ulits.DirCopy;
import ulits.SaveJson;
import ulits.ThreadPoolUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;


public class AnalysisApi {

    private static Logger LOGGER = LoggerFactory.getLogger(AnalysisApi.class);

    private static AnalysisApi instance;

    public static AnalysisApi getInstance() {
        if (instance == null) {
            instance = new AnalysisApi();
        }
        return instance;
    }

    /**
     * 初始化一些配置
     */

    /**
     * 项目扫描接口
     */
    public boolean analysis(String path) throws FileNotFoundException {
        DirCopy.dirCopy(path);
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        //分析
        Analysis analysis = new Analysis();
        //扫描
        analysis.analysis(path);
        //数据处理
        organizeData();
        saveProject();
        return Store.javaModelMap != null;
    }

    /**
     * 重新扫描
     */
    public boolean analysisagin() throws FileNotFoundException {
        if (Store.path == null) {
            return false;
        }
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
            //这里可以改写写入路径
            TransmissionThread transmissionThread = new TransmissionThread(entry.getValue());
            ThreadPoolUtils.execute(transmissionThread);
        }
        try {
            SaveJson.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
