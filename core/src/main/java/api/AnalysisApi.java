package api;

import analysis.process.Analysis;
import com.alibaba.fastjson.JSON;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import model.*;
import ulits.ThreadPoolUtils;

import java.io.File;
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
    public boolean analysis(String path) {
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
    public boolean analysisagin() {
        return analysis(Store.path);
    }

    /**
     * 数据处理
     */
    private void organizeData() {
        //得到文件树 //这个功能可以和parser操作合并
        TreeNode root = new TreeNode();
        ProjectRoot projectRoot = Store.projectRoot;
        root.setFile(false);
        String rootName = projectRoot.getRoot().toFile().getName();
        root.setFileName(rootName);
        root.setRealPath(projectRoot.getRoot().toFile().getPath());
        root.setChildren(new ArrayList<>());
        for (SourceRoot sourceRoot : projectRoot.getSourceRoots()) {
            String path = sourceRoot.getRoot().toFile().getPath();
            String name = path.substring(path.indexOf(rootName) + rootName.length() + 1);
            TreeNode sourceTree = new TreeNode();
            root.getChildren().add(sourceTree);
            sourceTree.setRealPath(path);
            sourceTree.setFileName(name);
            sourceTree.setFile(false);
            sourceTree.setHasIssue(false);
            sourceTree.setChildren(new ArrayList<>());
            createFileTree(sourceRoot.getRoot().toFile(), sourceTree);
        }
        Store.rootNode = root;
    }

    private void createFileTree(File file, TreeNode treeNode) {
        if (file.isFile() && file.getName().endsWith(".java")) {
            TreeNode fileNode = new TreeNode();
            fileNode.setRealPath(file.getPath());
            fileNode.setFileName(file.getName());
            fileNode.setFile(true);
            JavaModel javaModel = Store.javaModelMap.get(file.getPath());
            if (javaModel != null && javaModel.getIssues() != null && javaModel.getIssues().size() != 0) {
                fileNode.setHasIssue(true);
            }
            treeNode.getChildren().add(fileNode);
        }
        if (file.isDirectory()) {
            TreeNode dirNode = new TreeNode();
            dirNode.setHasIssue(false);
            dirNode.setFile(false);
            dirNode.setFileName(file.getName());
            dirNode.setRealPath(file.getPath());
            dirNode.setChildren(new ArrayList<>());
            treeNode.getChildren().add(dirNode);
            for (File f : file.listFiles()) {
                createFileTree(f, dirNode);
            }
        }
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
