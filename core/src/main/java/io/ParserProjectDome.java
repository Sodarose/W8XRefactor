package io;

import analysis.AbstractRuleVisitor;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.CollectionStrategy;
import com.github.javaparser.utils.ParserCollectionStrategy;
import com.github.javaparser.utils.SourceRoot;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 测试类
 * 主要功能:
 * 1. 生成文件树
 * 2. 生成javamodel列表
 */
public class ParserProjectDome {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParserProjectDome.class);

    private static final String FILE_SUFFIX = ".java";


    /**
     * 初始化
     */
    private static void init(String rootPath) {
        LOGGER.info("初始化库");
        Store.javaFiles = new ArrayList<>();
        Store.javaModelMap = new HashMap<>();
        Store.treeNodeMap = new HashMap<>();
        Store.jsonObjectList = Collections.synchronizedList(new ArrayList<JsonObject>());
        Store.issueContext = null;
        loadProject(rootPath);
        initTypeSolver();
        initRule();
    }

    /**
     *  初始化rule issue存储model
     * */
    private static void initRule(){
       for(Map.Entry<String, AbstractRuleVisitor> entry:Store.ruleMap.entrySet()){
            entry.getValue().getContext().getIssues().clear();
       }
    }

    /**
     * 读取项目
     */
    private static void loadProject(String path) {
        LOGGER.info("加载项目");
        CollectionStrategy collectionStrategy = new ParserCollectionStrategy();
        //获得项目
        Store.projectRoot = collectionStrategy.collect(Paths.get(path));
    }

    /**
     * 初始化解析器
     */
    private static void initTypeSolver() {
        LOGGER.info("初始化解析器");
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
        //初始化解析器
        Store.combinedTypeSolver = new CombinedTypeSolver();
        Store.combinedTypeSolver.add(reflectionTypeSolver);
        Store.javaParserFacade = JavaParserFacade.get(Store.combinedTypeSolver);
        for (SourceRoot sourceRoot : Store.projectRoot.getSourceRoots()) {
            Store.combinedTypeSolver.add(new JavaParserTypeSolver(sourceRoot.getRoot()));
        }
    }


    /**
     * 生成文件树以及索引表
     */
    private static void createFileTree(File file, TreeNode treeNode) throws FileNotFoundException {
        if(file.getName().startsWith(".")){
            return;
        }
        if (file.isFile() && file.getName().endsWith(FILE_SUFFIX)) {
            String fileContent = FileUlits.readFile(file);
            CompilationUnit unit = StaticJavaParser.parse(file);
            JavaModel javaModel = new JavaModel(file.getPath(), unit, fileContent, false, null);
            //加入索引表以及链表
            Store.javaFiles.add(unit);
            Store.javaModelMap.put(file.getPath(), javaModel);
            //构建子分支
            TreeNode node = new TreeNode(file.getPath(), file.getPath(), file.getName(), null, true);
            Store.treeNodeMap.put(node.getRealPath(), node);
            treeNode.getChildren().add(node);
        }
        if (file.isDirectory()) {
            TreeNode node = new TreeNode(file.getPath(), file.getPath(), file.getName(), new ArrayList<>(), false);
            treeNode.getChildren().add(node);
            Store.treeNodeMap.put(node.getRealPath(), node);
            for (File f : file.listFiles()) {
                createFileTree(f, node);
            }
        }
    }

    /**
     * 分析项目
     */
    public static List<JavaModel> parserProject(String path) throws FileNotFoundException {
        LOGGER.info("开始解析项目：" + path);
        //文件路径
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        Store.path = file.getPath();

        init(file.getPath());

        //如果路径是一个文件
        if (file.isFile() && file.getName().endsWith(FILE_SUFFIX)) {
            CompilationUnit unit = StaticJavaParser.parse(file);
            JavaModel javaModel = new JavaModel(file.getPath(), unit, FileUlits.readFile(file), false, null);
            Store.javaFiles.add(unit);
            TreeNode root = new TreeNode(file.getPath(), file.getPath(), file.getName(), null, true);
            Store.treeNodeMap.put(root.getRealPath(), root);
            Store.rootNode = root;
            Store.javaModelMap.put(file.getPath(), javaModel);
            return Store.javaModelMap.values().stream().collect(Collectors.toList());
        }

        // 生成文件树以及索引map
        TreeNode root = new TreeNode(file.getPath(), file.getPath(), file.getName(), new ArrayList<>(), false);
        Store.treeNodeMap.put(root.getRealPath(), root);
        Store.rootNode = root;
        createFileTree(file, root);
        return Store.javaModelMap.values().stream().collect(Collectors.toList());
    }

}
