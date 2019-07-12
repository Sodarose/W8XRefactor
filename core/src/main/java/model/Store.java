package model;

import analysis.AbstractRuleVisitor;
import analysis.Rule;
import com.github.javaparser.ast.CompilationUnit;
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
    /**
     * 项目路径
     */
    public static String path;

    /**
     * 保存项目路径
     */
    public static String savePath;

    /**
     * 文件树
     */
    public static TreeNode rootNode;

    /**
     * 项目根路径
     */
    public static ProjectRoot projectRoot;

    /**
     * 项目解析器
     */
    public static JavaParserFacade javaParserFacade;

    /**
     * 编译解析器
     */
    public static CombinedTypeSolver combinedTypeSolver;

    /**
     * 问题代码
     */
    public static IssueContext issueContext;

    /**
     * model索引表
     */
    public static Map<String, JavaModel> javaModelMap;

    public static List<CompilationUnit> javaFiles;

    public static List<Rule> rules;

    /**
     * 为了更方便的更改文件树的数据
     */
    public static Map<String, TreeNode> treeNodeMap;

    /**
     * 规则索引
     */
    public static Map<String, AbstractRuleVisitor> ruleMap;

    public static List<JsonObject> jsonObjectList;

    public static String modifyPath;

    /**
     * 格式化配置
     */
    public static Map<String, Object> options;

    /**
     *
     * */

}
