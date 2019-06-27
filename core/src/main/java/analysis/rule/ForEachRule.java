package analysis.rule;

import analysis.AbstractRuleVisitor;
import api.AnalysisApi;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import io.FileUlits;
import model.Issue;
import model.IssueContext;
import model.JavaModel;
import model.Store;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ForEachRule extends AbstractRuleVisitor {
    final String FOREACH_WARN = "不要在 foreach 循环里进行元素的 remove/add 操作";
    final List<String> FOREACH_OPERATIONG = Arrays.asList("add", "remove", "addAll", "replaceAll",
            "removeAll", "clear", "sort", "set", "replace");

    @Override
    public IssueContext apply(List<JavaModel> javaModels) {
        for (JavaModel javaModel : javaModels) {
            writeTip(javaModel);
        }
        return getContext();
    }

    private void writeTip(JavaModel javaModel) {
        List<ForEachStmt> forEachStmts = javaModel.getUnit().findAll(ForEachStmt.class);
        for (ForEachStmt forEachStmt : forEachStmts) {
            Expression iterable = forEachStmt.getIterable();
            List<MethodCallExpr> methodCallExprs = forEachStmt.findAll(MethodCallExpr.class);
            //遍历foreach中的调用方法 判断是否
            List<MethodCallExpr> changeMethods = new ArrayList<>();
            //递归查找更改内容
            searchChangeMethod(methodCallExprs, changeMethods, iterable);
            List<Issue> issues = changeMethods.stream().map(
                    methodCallExpr -> {
                        LineComment lineComment = new LineComment();
                        lineComment.setContent(FOREACH_WARN);
                        methodCallExpr.setComment(lineComment);
                        return createIssue(javaModel, methodCallExpr);
                    }
            ).collect(Collectors.toList());
            getContext().getIssues().addAll(issues);
        }
    }

    private void searchChangeMethod(List<MethodCallExpr> methodCallExprs, List<MethodCallExpr> changeMethods, Expression iterable) {
        List<MethodCallExpr> methods = methodCallExprs.stream().filter(
                methodCallExpr -> {
                    if (!methodCallExpr.getScope().isPresent()) {
                        return false;
                    }
                    Expression left = methodCallExpr.getScope().get();
                    if (!left.equals(iterable)) {
                        return false;
                    }
                    String name = methodCallExpr.getName().getIdentifier();
                    //判断该语句是否会更改列表内容
                    if (!FOREACH_OPERATIONG.contains(name)) {
                        return false;
                    }
                    return true;
                }
        ).collect(Collectors.toList());
        changeMethods.addAll(methods);
        List<MethodCallExpr> iterableArgument = methodCallExprs.stream().filter(
                methodCallExpr -> {
                    if(methodCallExpr.getArguments().contains(iterable)){
                        return true;
                    }
                    return false;
                }
        ).collect(Collectors.toList());
        for(MethodCallExpr methodCallExpr:iterableArgument){
            SymbolReference<ResolvedMethodDeclaration> reference =  Store.javaParserFacade.solve(methodCallExpr);
            if(!reference.isSolved()){
                continue;
            }
            JavaParserMethodDeclaration javaParserMethodDeclaration = (JavaParserMethodDeclaration) reference.
                    getCorrespondingDeclaration();
            MethodDeclaration methodDeclaration = javaParserMethodDeclaration.getWrappedNode();
            List<MethodCallExpr> methodCalls = methodDeclaration.findAll(MethodCallExpr.class);
            searchChangeMethod(methodCalls,changeMethods,iterable);
        }
    }


    private Issue createIssue(JavaModel javaModel, Node node) {
        Issue issue = new Issue();
        issue.setRuleName(getRuleName());
        issue.setJavaModel(javaModel);
        issue.setClassName(getClassName());
        issue.setIssueNode(node);
        issue.setDescription(getDescription());
        issue.setRefactorName(getSolutionClassName());
        return issue;
    }
}
