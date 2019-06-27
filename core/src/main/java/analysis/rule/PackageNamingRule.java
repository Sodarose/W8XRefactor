package analysis.rule;

import analysis.AbstractRuleVisitor;
import analysis.BaseVisitor;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import io.FileUlits;
import model.Issue;
import model.IssueContext;
import model.JavaModel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PackageNamingRule extends AbstractRuleVisitor {
    private final Pattern PATTERN=Pattern.compile("^[a-z0-9]+(\\.[a-z][a-z0-9]*)*$");
    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for (JavaModel javaModel:javaModels){
            checkPackageName(javaModel);
        }
        return getContext();
    }

    public void checkPackageName(JavaModel javaModel){
        List<PackageDeclaration> packageDeclarationList=javaModel.getUnit().findAll(PackageDeclaration.class);
        for(PackageDeclaration packageDeclaration:packageDeclarationList){
            String name=packageDeclaration.getNameAsString();
            Matcher matcher=PATTERN.matcher(name);
            boolean nameFlag=matcher.matches();
            if(!nameFlag){
                Issue issue=new Issue();
                issue.setIssueNode(packageDeclaration);
                issue.setJavaModel(javaModel);
                issue.setRefactorName(getSolutionClassName());
                issue.setDescription(getDescription());
                issue.setRuleName(getRuleName());
                getContext().getIssues().add(issue);
            }
        }
    }
}
