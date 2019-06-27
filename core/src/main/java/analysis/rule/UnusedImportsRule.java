package analysis.rule;

import analysis.AbstractRuleVisitor;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import model.Issue;
import model.IssueContext;
import model.JavaModel;

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
        List<ImportDeclaration> importDeclarations = javaModel.getUnit().getImports();
        List<SimpleName> unitSimpleNames = javaModel.getUnit().findAll(SimpleName.class);
        javaModel.getUnit().getTypes();
        unitSimpleNames.addAll(javaModel.getUnit().getTypes().stream().map(typeDeclaration -> {
            return typeDeclaration.getName();
        }).collect(Collectors.toList()));

        CompilationUnit unit = javaModel.getUnit();
        List<String> simpleNames = new ArrayList<>();
        for (SimpleName simpleName : unitSimpleNames) {
            simpleNames.add(simpleName.getIdentifier());
        }
        for (ImportDeclaration declaration : importDeclarations) {
            String fullName = declaration.getName().asString();
            String name = fullName.substring(fullName.lastIndexOf(".") + 1);
            if(declaration.toString().contains("*")){
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