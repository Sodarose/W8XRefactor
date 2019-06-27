package io;

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
import model.JavaModel;
import model.Store;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 项目扫描具体执行类
 *
 * @author Administrator
 */
public class ParserProject {

    /**
     * 生成整个项目的javaModel
     * */
    public static List<JavaModel> parserProject(String path) {
        loadProject(path);
        if (Store.projectRoot == null) {
            throw new RuntimeException("读取项目失败");
        }
        if (Store.projectRoot.getSourceRoots() == null || Store.projectRoot.getSourceRoots().size() == 0) {

            throw new RuntimeException("读取项目失败");
        }

        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
        //初始化解析器
        Store.combinedTypeSolver = new CombinedTypeSolver();
        Store.combinedTypeSolver.add(reflectionTypeSolver);
        Store.javaParserFacade = JavaParserFacade.get(Store.combinedTypeSolver);
        //这个只是方便遍历
        List<CompilationUnit> javaFiles = new ArrayList<>();
        Map<String, JavaModel> javaModelMap = new HashMap<>(100);
        try {
            List<SourceRoot> sourceRoots = Store.projectRoot.getSourceRoots();
            Iterator<SourceRoot> itRoots = sourceRoots.iterator();
            while (itRoots.hasNext()) {
                SourceRoot sourceRoot = itRoots.next();
                Store.combinedTypeSolver.add(new JavaParserTypeSolver(sourceRoot.getRoot()));
                Files.walkFileTree(sourceRoot.getRoot(),new SimpleFileVisitor<Path>(){
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if(!attrs.isDirectory() && file.toString().endsWith(".java")){
                            CompilationUnit unit = StaticJavaParser.parse(file);
                            JavaModel javaModel = new JavaModel();
                            javaModel.setUnit(unit);
                            String realPath = file.toFile().getPath();
                            sourceRoot.add(unit);
                            javaModel.setReadPath(realPath);
                            javaModelMap.put(realPath, javaModel);
                            javaFiles.add(unit);
                        }
                        return super.visitFile(file, attrs);
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //建立索引表
        Store.javaModelMap = javaModelMap;
        Store.path = path;
        Store.javaFiles = javaFiles;
        return javaModelMap.values().stream().collect(Collectors.toList());
    }

    /**
     * 读取项目
     */
    private static void loadProject(String path) {
        CollectionStrategy collectionStrategy = new ParserCollectionStrategy();
        //获得项目
        Store.projectRoot = collectionStrategy.collect(Paths.get(path));
    }

    public static void saveAll() {
        for (SourceRoot sourceRoot : Store.projectRoot.getSourceRoots()) {
            sourceRoot.saveAll();
        }
    }



}
