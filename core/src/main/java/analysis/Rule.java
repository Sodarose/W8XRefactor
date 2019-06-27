package analysis;

import com.github.javaparser.ast.CompilationUnit;
import model.Issue;
import model.IssueContext;
import model.JavaModel;

import java.util.List;

/**
 * 扫描规则文件
 * */
public interface Rule {
    /**
     * 扫描文件接口
     * */
    IssueContext apply(List<JavaModel> javaModels);

    boolean isRun();
}
