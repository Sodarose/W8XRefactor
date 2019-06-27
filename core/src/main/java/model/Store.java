package model;

import analysis.AbstractRuleVisitor;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.utils.ProjectRoot;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 数据仓库
 *
 * @author Administrator
 */
@Data
public class Store {
    //项目路径
    public static String path;
    //状态
    public static boolean run = false;
    public static TreeNode rootNode;
    //项目
    public static ProjectRoot projectRoot;
    //解析器
    public static JavaParserFacade javaParserFacade;
    public static CombinedTypeSolver combinedTypeSolver;
    //整个项目的问题代码片段
    public static IssueContext issueContext;
    //索引列表
    public static Map<String, JavaModel> javaModelMap;

    public static List<CompilationUnit> javaFiles;

    public static List<AbstractRuleVisitor> rules;

}
