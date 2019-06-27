package model;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import refactor.Refactor;

import java.util.List;
import java.util.Map;

/**
 * 坏味道模型数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Issue {
    private String description;
    private Node issueNode;
    private String className;
    private JavaModel javaModel;
    private String ruleName;
    private String refactorName;
    private Map<String, Object> data;
}
