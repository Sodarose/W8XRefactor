package refactor.refactorimpl;

import com.github.javaparser.ast.stmt.*;
import model.Issue;
import refactor.AbstractRefactor;


public class CodeStyleRefactor extends AbstractRefactor {
    @Override
    public void refactor(Issue issue) {
        Statement stmt = (Statement) issue.getIssueNode();
        changeStmt(stmt);
    }

    private void changeStmt(Statement stmt) {
        if (stmt.isDoStmt()) {
            DoStmt doStmt = stmt.asDoStmt();
            Statement statement = doStmt.getBody();
            BlockStmt blockStmt = new BlockStmt();
            blockStmt.getStatements().add(statement);
            doStmt.setBody(blockStmt);
        }
        if (stmt.isIfStmt()) {
            IfStmt ifStmt = stmt.asIfStmt();
            if (!ifStmt.getThenStmt().isBlockStmt()) {
                Statement statement = ifStmt.getThenStmt();
                BlockStmt blockStmt = new BlockStmt();
                blockStmt.getStatements().add(statement);
                ifStmt.setThenStmt(blockStmt);
            }
            if (ifStmt.hasElseBranch() && !ifStmt.hasElseBlock()) {
                Statement statement = ifStmt.getElseStmt().get();
                BlockStmt blockStmt = new BlockStmt();
                blockStmt.getStatements().add(statement);
                ifStmt.setElseStmt(blockStmt);
            }
        }
        if (stmt.isForEachStmt()) {
            ForStmt forStmt = stmt.asForStmt();
            BlockStmt blockStmt = new BlockStmt();
            blockStmt.getStatements().add(forStmt.getBody());
            forStmt.setBody(blockStmt);
        }
        if (stmt.isForStmt()) {
            ForStmt forStmt = stmt.asForStmt();
            BlockStmt blockStmt = new BlockStmt();
            blockStmt.getStatements().add(forStmt.getBody());
            forStmt.setBody(blockStmt);
        }
        if (stmt.isWhileStmt()) {
            WhileStmt whileStmt = stmt.asWhileStmt();
            BlockStmt blockStmt = new BlockStmt();
            blockStmt.getStatements().add(whileStmt.getBody());
            whileStmt.setBody(blockStmt);
        }
    }
}
