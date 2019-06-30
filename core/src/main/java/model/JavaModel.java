package model;

import com.github.javaparser.ast.CompilationUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *  java类模型
 * */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JavaModel {
    private String readPath;
    private CompilationUnit unit;
    private String fileContent;
    private boolean hasIssue;
    List<Issue> issues;

    public JavaModel(CompilationUnit unit){
        this.unit = unit;
    }
}
