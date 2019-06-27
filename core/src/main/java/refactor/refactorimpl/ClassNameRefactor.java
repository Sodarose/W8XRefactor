package refactor.refactorimpl;

import analysis.rule.ClassNamingShouldBeCamelRule;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import io.FileUlits;
import model.Issue;
import model.JavaModel;
import refactor.AbstractRefactor;
import ulits.ClassReferUtil;
import ulits.FilesNameRename;
import ulits.SplitName;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClassNameRefactor extends AbstractRefactor {
    @Override
    public void refactor(Issue issue) {
        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) issue.getIssueNode();
        try {
            classNameRefactor(classOrInterfaceDeclaration, issue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void classNameRefactor(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, Issue issue) throws IOException {
        String newName = "";
        String oldName = classOrInterfaceDeclaration.getNameAsString();
        List<String> nameList = SplitName.split(classOrInterfaceDeclaration.getNameAsString());
        if (nameList == null) {
            return;
        }
        for (String data : nameList) {
            data = data.substring(0, 1).toUpperCase() + data.substring(1);

            newName = newName + data;
        }
            classOrInterfaceDeclaration.setName(newName);
            ClassReferUtil.referUtil(oldName, newName);
        JavaModel javaModel = issue.getJavaModel();
        Optional<String> primaryTypeName = javaModel.getUnit().getPrimaryTypeName();
        if (primaryTypeName.isPresent() && primaryTypeName != null) {
            if (primaryTypeName.get().equals(oldName)) {
                FilesNameRename.nameRename(issue, oldName, newName);
            }
        }
    }
}
