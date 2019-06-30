package analysis.rule;

import analysis.AbstractRuleVisitor;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.Type;
import model.Issue;
import model.IssueContext;
import model.JavaModel;
import refactor.refactorimpl.UnusedImportsRefactor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 检测为使用的导入
 */
public class UnusedImportsRule extends AbstractRuleVisitor {

    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for (JavaModel javaModel : javaModels) {
            checkUnusedImport(javaModel);
        }
        return getContext();
    }

    private void checkUnusedImport(JavaModel javaModel) {
        //得到所有导入
        List<ImportDeclaration> importDeclarations = javaModel.getUnit().getImports();
        //找到所有的使用的命名名称
        List<SimpleName> unitSimpleNames = javaModel.getUnit().findAll(SimpleName.class);
        //
        unitSimpleNames.addAll(javaModel.getUnit().getTypes().stream().map(typeDeclaration -> {
            return typeDeclaration.getName();
        }).collect(Collectors.toList()));

        List<String> simpleNames = new ArrayList<>();
        for (SimpleName simpleName : unitSimpleNames) {
            simpleNames.add(simpleName.getIdentifier());
        }
        List<String> commitName = javaModel.getUnit().findAll(AnnotationExpr.class)
                .stream().map(declaration -> {
                    return declaration.getName().getIdentifier();
                }).collect(Collectors.toList());
        System.out.println(commitName.size());
        simpleNames.addAll(commitName);
        for (ImportDeclaration declaration : importDeclarations) {
            String fullName = declaration.getName().asString();
            String name = fullName.substring(fullName.lastIndexOf(".") + 1);
            if (declaration.toString().contains("*")) {
                continue;
            }
            if (!simpleNames.contains(name) && !simpleNames.contains(fullName)) {
                getContext().getIssues().add(createIssue(declaration, javaModel));
            }
        }
    }

    private Issue createIssue(ImportDeclaration declaration, JavaModel javaModel) {
        Issue issue = new Issue();
        issue.setIssueNode(declaration);
        issue.setJavaModel(javaModel);
        issue.setRuleName(getRuleName());
        issue.setDescription(getDescription());
        issue.setRefactorName(getSolutionClassName());
        return issue;
    }

}