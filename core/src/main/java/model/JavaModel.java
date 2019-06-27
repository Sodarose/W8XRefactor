package model;

import com.github.javaparser.ast.CompilationUnit;
import lombok.Data;

import java.util.List;

/**
 *  java类模型
 * */
@Data
public class JavaModel {
    private String readPath;
    private CompilationUnit unit;
    private CompilationUnit copyUnit;
    private boolean hasIssue;
    List<Issue> issues;

}
