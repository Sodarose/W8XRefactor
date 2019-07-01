/*******************************************************************************
 * Copyright (c) 2014, 2018 Mateusz Matela and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Mateusz Matela <mateusz.matela@gmail.com> - [formatter] Formatter does not format Java code correctly, especially when max line width is set - https://bugs.eclipse.org/303519
 *     Mateusz Matela <mateusz.matela@gmail.com> - [formatter] IndexOutOfBoundsException in TokenManager - https://bugs.eclipse.org/462945
 *     Mateusz Matela <mateusz.matela@gmail.com> - [formatter] follow up bug for comments - https://bugs.eclipse.org/458208
 *******************************************************************************/
package formatter;

import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameAT;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameCOLON;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameCOMMA;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameCOMMENT_JAVADOC;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameLBRACE;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameRBRACE;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameSEMICOLON;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNameelse;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNamefinally;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNamepackage;
import static org.eclipse.jdt.internal.compiler.parser.TerminalTokens.TokenNamewhile;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.ModuleDeclaration;
import org.eclipse.jdt.core.dom.ModuleDirective;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatterOptions;
import org.eclipse.jdt.internal.formatter.Token;
import org.eclipse.jdt.internal.formatter.TokenManager;

public class LineBreaksPreparator extends ASTVisitor {
    final private TokenManager tm;
    final private DefaultCodeFormatterOptions options;

    private boolean declarationModifierVisited;

    public LineBreaksPreparator(TokenManager tokenManager, DefaultCodeFormatterOptions options) {
        this.tm = tokenManager;
        this.options = options;
    }

    //前置 MALFORMED malformed
    @Override
    public boolean preVisit2(ASTNode node) {
        boolean isMalformed = (node.getFlags() & ASTNode.MALFORMED) != 0;
        return !isMalformed;
    }

    //node
    @Override
    public boolean visit(CompilationUnit node) {
        //导入
        List<ImportDeclaration> imports = node.imports();

        //如果 导入不为空 找到第一个import 在他上面加一行空行
        if (!imports.isEmpty()) {
            int index = this.tm.firstIndexIn(imports.get(0), -1);
            if (index > 0)
                this.tm.get(index).putLineBreaksBefore(this.options.blank_lines_before_imports + 1);
        }

        //注解
        List<AnnotationTypeDeclaration> types = node.types();

        //
        if (!types.isEmpty()) {
            if (!imports.isEmpty())
                //空一行
                putBlankLinesBefore(types.get(0), this.options.blank_lines_after_imports);

            for (int i = 1; i < types.size(); i++)
                putBlankLinesBefore(types.get(i), this.options.blank_lines_between_type_declarations);
        }

        return true;
    }

    @Override
    public boolean visit(PackageDeclaration node) {
        //语句前的空白行
        int blanks = this.options.blank_lines_before_package;
        if (blanks > 0) {
            List<Annotation> annotations = node.annotations();
            int firstTokenIndex = annotations.isEmpty() ? this.tm.firstIndexBefore(node.getName(), TokenNamepackage)
                    : this.tm.firstIndexIn(annotations.get(0), -1);
            this.tm.get(firstTokenIndex).putLineBreaksBefore(blanks + 1);
        }
        //最后的分号 语句后的空白行
        this.tm.lastTokenIn(node, TokenNameSEMICOLON).putLineBreaksAfter(this.options.blank_lines_after_package + 1);
        this.declarationModifierVisited = false;
        return true;
    }

    @Override
    public boolean visit(ImportDeclaration node) {
        breakLineBefore(node);
        return true;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        handleBodyDeclarations(node.bodyDeclarations());

        if (node.getName().getStartPosition() == -1)
            return true; // this is a fake type created by parsing in class body mode

        breakLineBefore(node);

        handleBracedCode(node, node.getName(), this.options.brace_position_for_type_declaration,
                this.options.indent_body_declarations_compare_to_type_header);

        this.declarationModifierVisited = false;
        return true;
    }

    private void handleBodyDeclarations(List<BodyDeclaration> bodyDeclarations) {
        BodyDeclaration previous = null;
        for (BodyDeclaration bodyDeclaration : bodyDeclarations) {
            if (previous == null) {
                putBlankLinesBefore(bodyDeclaration, this.options.blank_lines_before_first_class_body_declaration);
            } else {
                int blankLines = 0;
                if (bodyDeclaration instanceof FieldDeclaration) {
                    blankLines = this.options.blank_lines_before_field;
                } else if (bodyDeclaration instanceof AbstractTypeDeclaration) {
                    blankLines = this.options.blank_lines_before_member_type;
                } else if (bodyDeclaration instanceof MethodDeclaration
                        || bodyDeclaration instanceof AnnotationTypeMemberDeclaration) {
                    blankLines = this.options.blank_lines_before_method;
                }

                if (!sameChunk(previous, bodyDeclaration))
                    blankLines = Math.max(blankLines, this.options.blank_lines_before_new_chunk);

                putBlankLinesBefore(bodyDeclaration, blankLines);
            }
            previous = bodyDeclaration;
        }
    }

    private boolean sameChunk(BodyDeclaration bd1, BodyDeclaration bd2) {
        if (bd1.getClass().equals(bd2.getClass()))
            return true;
        if (bd1 instanceof AbstractTypeDeclaration && bd2 instanceof AbstractTypeDeclaration)
            return true;
        if ((bd1 instanceof FieldDeclaration || bd1 instanceof Initializer)
                && (bd2 instanceof FieldDeclaration || bd2 instanceof Initializer))
            return true;
        return false;
    }

    private void putBlankLinesBefore(ASTNode node, int linesCount) {
        int index = this.tm.firstIndexIn(node, -1);
        while (index > 0 && this.tm.get(index - 1).tokenType == TokenNameCOMMENT_JAVADOC)
            index--;
        this.tm.get(index).putLineBreaksBefore(linesCount + 1);
    }

    @Override
    public boolean visit(EnumDeclaration node) {
        handleBracedCode(node, node.getName(), this.options.brace_position_for_enum_declaration,
                this.options.indent_body_declarations_compare_to_enum_declaration_header);
        handleBodyDeclarations(node.bodyDeclarations());

        List<EnumConstantDeclaration> enumConstants = node.enumConstants();
        for (int i = 0; i < enumConstants.size(); i++) {
            EnumConstantDeclaration declaration = enumConstants.get(i);
            if (declaration.getJavadoc() != null)
                this.tm.firstTokenIn(declaration, TokenNameCOMMENT_JAVADOC).breakBefore();
            if (declaration.getAnonymousClassDeclaration() != null && i < enumConstants.size() - 1)
                this.tm.firstTokenAfter(declaration, TokenNameCOMMA).breakAfter();
        }

        // put breaks after semicolons
        int index = enumConstants.isEmpty() ? this.tm.firstIndexAfter(node.getName(), TokenNameLBRACE) + 1
                : this.tm.firstIndexAfter(enumConstants.get(enumConstants.size() - 1), -1);
        for (; ; index++) {
            Token token = this.tm.get(index);
            if (token.isComment())
                continue;
            if (token.tokenType == TokenNameSEMICOLON)
                token.breakAfter();
            else
                break;
        }

        this.declarationModifierVisited = false;
        return true;
    }

    @Override
    public boolean visit(AnnotationTypeDeclaration node) {
        handleBracedCode(node, node.getName(), this.options.brace_position_for_annotation_type_declaration,
                this.options.indent_body_declarations_compare_to_annotation_declaration_header);

        handleBodyDeclarations(node.bodyDeclarations());
        if (node.getModifiers() == 0)
            this.tm.firstTokenBefore(node.getName(), TokenNameAT).breakBefore();

        this.declarationModifierVisited = false;
        return true;
    }

    @Override
    public boolean visit(AnonymousClassDeclaration node) {
        if (node.getParent() instanceof EnumConstantDeclaration) {
            handleBracedCode(node, null, this.options.brace_position_for_enum_constant,
                    this.options.indent_body_declarations_compare_to_enum_constant_header);
        } else {
            handleBracedCode(node, null, this.options.brace_position_for_anonymous_type_declaration,
                    this.options.indent_body_declarations_compare_to_type_header);
        }
        handleBodyDeclarations(node.bodyDeclarations());
        return true;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        this.declarationModifierVisited = false;
        if (node.getBody() == null)
            return true;

        String bracePosition = node.isConstructor() ? this.options.brace_position_for_constructor_declaration
                : this.options.brace_position_for_method_declaration;
        handleBracedCode(node.getBody(), null, bracePosition, this.options.indent_statements_compare_to_body);
        Token openBrace = this.tm.firstTokenIn(node.getBody(), TokenNameLBRACE);
        if (openBrace.getLineBreaksAfter() > 0) // if not, these are empty braces
            openBrace.putLineBreaksAfter(this.options.blank_lines_at_beginning_of_method_body + 1);
        return true;
    }

    @Override
    public boolean visit(Block node) {
        List<Statement> statements = node.statements();
        for (Statement statement : statements) {
            if (this.options.put_empty_statement_on_new_line || !(statement instanceof EmptyStatement))
                breakLineBefore(statement);
        }
        ASTNode parent = node.getParent();
        if (parent.getLength() == 0)
            return true; // this is a fake block created by parsing in statements mode
        if (parent instanceof MethodDeclaration)
            return true; // braces have been handled in #visit(MethodDeclaration)

        String bracePosition = this.options.brace_position_for_block;
        if (parent instanceof SwitchStatement) {
            List<Statement> siblings = ((SwitchStatement) parent).statements();
            int blockPosition = siblings.indexOf(node);
            boolean isFirstInCase = blockPosition > 0 && (siblings.get(blockPosition - 1) instanceof SwitchCase);
            if (isFirstInCase)
                bracePosition = this.options.brace_position_for_block_in_case;
        } else if (parent instanceof LambdaExpression) {
            bracePosition = this.options.brace_position_for_lambda_body;
        }
        handleBracedCode(node, null, bracePosition, this.options.indent_statements_compare_to_block);

        return true;
    }

    @Override
    public boolean visit(SwitchStatement node) {
        handleBracedCode(node, node.getExpression(), this.options.brace_position_for_switch,
                this.options.indent_switchstatements_compare_to_switch);

        List<Statement> statements = node.statements();
        if (this.options.indent_switchstatements_compare_to_cases) {
            int nonBreakStatementEnd = -1;
            for (Statement statement : statements) {
                boolean isBreaking = statement instanceof BreakStatement || statement instanceof ReturnStatement
                        || statement instanceof ContinueStatement || statement instanceof Block;
                if (isBreaking && !(statement instanceof Block))
                    adjustEmptyLineAfter(this.tm.lastIndexIn(statement, -1), -1);
                if (statement instanceof SwitchCase) {
                    if (nonBreakStatementEnd >= 0) {
                        // indent only comments between previous and current statement
                        this.tm.get(nonBreakStatementEnd + 1).indent();
                        this.tm.firstTokenIn(statement, -1).unindent();
                    }
                } else if (!(statement instanceof BreakStatement || statement instanceof Block)) {
                    indent(statement);
                }
                nonBreakStatementEnd = isBreaking ? -1 : this.tm.lastIndexIn(statement, -1);
            }
            if (nonBreakStatementEnd >= 0) {
                // indent comments between last statement and closing brace
                this.tm.get(nonBreakStatementEnd + 1).indent();
                this.tm.lastTokenIn(node, TokenNameRBRACE).unindent();
            }
        }
        if (this.options.indent_breaks_compare_to_cases) {
            for (Statement statement : statements) {
                if (statement instanceof BreakStatement)
                    indent(statement);
            }
        }

        for (Statement statement : statements) {
            if (statement instanceof Block)
                continue; // will add break in visit(Block) if necessary
            if (this.options.put_empty_statement_on_new_line || !(statement instanceof EmptyStatement))
                breakLineBefore(statement);
        }

        return true;
    }

    @Override
    public boolean visit(DoStatement node) {
        Statement body = node.getBody();
        boolean sameLine = this.options.keep_simple_do_while_body_on_same_line;
        if (!sameLine)
            handleLoopBody(body);
        if (this.options.insert_new_line_before_while_in_do_statement
                || (!(body instanceof Block) && !(body instanceof EmptyStatement) && !sameLine)) {
            Token whileToken = this.tm.firstTokenBefore(node.getExpression(), TokenNamewhile);
            whileToken.breakBefore();
        }
        return true;
    }

    @Override
    public boolean visit(LabeledStatement node) {
        if (this.options.insert_new_line_after_label)
            this.tm.firstTokenIn(node, TokenNameCOLON).breakAfter();
        return true;
    }

    @Override
    public boolean visit(ArrayInitializer node) {
        int openBraceIndex = this.tm.firstIndexIn(node, TokenNameLBRACE);
        int closeBraceIndex = this.tm.lastIndexIn(node, TokenNameRBRACE);

        boolean isEmpty = openBraceIndex + 1 == closeBraceIndex;
        if (isEmpty) {
            adjustEmptyLineAfter(openBraceIndex, this.options.continuation_indentation_for_array_initializer);
            closeBraceIndex = this.tm.lastIndexIn(node, TokenNameRBRACE);
        }

        Token openBraceToken = this.tm.get(openBraceIndex);
        Token closeBraceToken = this.tm.get(closeBraceIndex);

        if (!(node.getParent() instanceof ArrayInitializer)) {
            Token afterOpenBraceToken = this.tm.get(openBraceIndex + 1);
            for (int i = 0; i < this.options.continuation_indentation_for_array_initializer; i++) {
                afterOpenBraceToken.indent();
                closeBraceToken.unindent();
            }
        }

        if (!isEmpty || !this.options.keep_empty_array_initializer_on_one_line)
            handleBracePosition(openBraceToken, closeBraceIndex, this.options.brace_position_for_array_initializer);

        if (!isEmpty) {
            if (this.options.insert_new_line_after_opening_brace_in_array_initializer)
                openBraceToken.breakAfter();
            if (this.options.insert_new_line_before_closing_brace_in_array_initializer)
                closeBraceToken.breakBefore();
        }
        return true;
    }

    @Override
    public boolean visit(NormalAnnotation node) {
        handleAnnotation(node);
        return true;
    }

    @Override
    public boolean visit(SingleMemberAnnotation node) {
        handleAnnotation(node);
        return true;
    }

    @Override
    public boolean visit(MarkerAnnotation node) {
        handleAnnotation(node);
        return true;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        this.declarationModifierVisited = false;
        return true;
    }

    @Override
    public boolean visit(SingleVariableDeclaration node) {
        this.declarationModifierVisited = false;
        if (node.getParent() instanceof MethodDeclaration) {
            // special case: annotations on parameters without modifiers should not be treated as type annotations
            this.declarationModifierVisited = (node.getModifiers() == 0);
        }
        return true;
    }

    @Override
    public boolean visit(VariableDeclarationExpression node) {
        this.declarationModifierVisited = false;
        return true;
    }

    @Override
    public boolean visit(FieldDeclaration node) {
        this.declarationModifierVisited = false;
        return true;
    }

    @Override
    public boolean visit(AnnotationTypeMemberDeclaration node) {
        this.declarationModifierVisited = false;
        return true;
    }

    @Override
    public boolean visit(EnumConstantDeclaration node) {
        this.declarationModifierVisited = false;
        return true;
    }

    @Override
    public boolean visit(Modifier node) {
        this.declarationModifierVisited = true;
        return true;
    }

    private void handleAnnotation(Annotation node) {
        ASTNode parentNode = node.getParent();
        boolean breakAfter = false;
        boolean isTypeAnnotation = this.declarationModifierVisited;
        if (isTypeAnnotation) {
            breakAfter = this.options.insert_new_line_after_type_annotation;
        } else if (parentNode instanceof PackageDeclaration) {
            breakAfter = this.options.insert_new_line_after_annotation_on_package;
        } else if (parentNode instanceof AbstractTypeDeclaration) {
            breakAfter = this.options.insert_new_line_after_annotation_on_type;
        } else if (parentNode instanceof EnumConstantDeclaration) {
            breakAfter = this.options.insert_new_line_after_annotation_on_enum_constant;
        } else if (parentNode instanceof FieldDeclaration) {
            breakAfter = this.options.insert_new_line_after_annotation_on_field;
        } else if (parentNode instanceof MethodDeclaration) {
            breakAfter = this.options.insert_new_line_after_annotation_on_method;
        } else if (parentNode instanceof AnnotationTypeMemberDeclaration) {
            breakAfter = this.options.insert_new_line_after_annotation_on_method
                    && ((AnnotationTypeMemberDeclaration) parentNode).getDefault() != node;
        } else if (parentNode instanceof VariableDeclarationStatement
                || parentNode instanceof VariableDeclarationExpression) {
            breakAfter = this.options.insert_new_line_after_annotation_on_local_variable;
        } else if (parentNode instanceof SingleVariableDeclaration) {
            breakAfter = this.options.insert_new_line_after_annotation_on_parameter;
            if ((parentNode.getParent()) instanceof EnhancedForStatement)
                breakAfter = this.options.insert_new_line_after_annotation_on_local_variable;
        }
        if (breakAfter)
            this.tm.lastTokenIn(node, -1).breakAfter();
    }

    @Override
    public boolean visit(WhileStatement node) {
        if (!this.options.keep_simple_while_body_on_same_line)
            handleLoopBody(node.getBody());
        return true;
    }

    @Override
    public boolean visit(ForStatement node) {
        if (!this.options.keep_simple_for_body_on_same_line)
            handleLoopBody(node.getBody());
        return true;
    }

    @Override
    public boolean visit(EnhancedForStatement node) {
        if (!this.options.keep_simple_for_body_on_same_line)
            handleLoopBody(node.getBody());
        return true;
    }

    private void handleLoopBody(Statement body) {
        if (body instanceof Block)
            return;
        if (body instanceof EmptyStatement && !this.options.put_empty_statement_on_new_line
                && !(body.getParent() instanceof IfStatement))
            return;
        breakLineBefore(body);
        adjustEmptyLineAfter(this.tm.lastIndexIn(body, -1), -1);
        indent(body);
    }

    @Override
    public boolean visit(IfStatement node) {
        Statement elseNode = node.getElseStatement();
        Statement thenNode = node.getThenStatement();
        if (elseNode != null) {
            if (this.options.insert_new_line_before_else_in_if_statement || !(thenNode instanceof Block))
                this.tm.firstTokenBefore(elseNode, TokenNameelse).breakBefore();

            boolean keepElseOnSameLine = (this.options.keep_else_statement_on_same_line)
                    || (this.options.compact_else_if && (elseNode instanceof IfStatement));
            if (!keepElseOnSameLine)
                handleLoopBody(elseNode);
        }

        boolean keepThenOnSameLine = this.options.keep_then_statement_on_same_line
                || (this.options.keep_simple_if_on_one_line && elseNode == null);
        if (!keepThenOnSameLine)
            handleLoopBody(thenNode);

        return true;
    }

    @Override
    public boolean visit(TryStatement node) {
        if (node.getFinally() != null && this.options.insert_new_line_before_finally_in_try_statement) {
            this.tm.firstTokenBefore(node.getFinally(), TokenNamefinally).breakBefore();
        }
        return true;
    }

    @Override
    public boolean visit(CatchClause node) {
        if (this.options.insert_new_line_before_catch_in_try_statement)
            breakLineBefore(node);
        return true;
    }

    @Override
    public boolean visit(ModuleDeclaration node) {
        // using settings for type declaration and fields for now, add new settings if necessary
        breakLineBefore(node);
        handleBracedCode(node, node.getName(), this.options.brace_position_for_type_declaration,
                this.options.indent_body_declarations_compare_to_type_header);

        List<ModuleDirective> statements = node.moduleStatements();
        ModuleDirective previous = null;
        for (ModuleDirective statement : statements) {
            int blankLines = previous == null ? this.options.blank_lines_before_first_class_body_declaration
                    : previous.getClass().equals(statement.getClass()) ? this.options.blank_lines_before_field
                    : this.options.blank_lines_before_new_chunk;
            putBlankLinesBefore(statement, blankLines);
            previous = statement;
        }

        this.declarationModifierVisited = false;
        return true;
    }

    private void breakLineBefore(ASTNode node) {
        this.tm.firstTokenIn(node, -1).breakBefore();
    }

    private void handleBracedCode(ASTNode node, ASTNode nodeBeforeOpenBrace, String bracePosition, boolean indentBody) {
        int openBraceIndex = nodeBeforeOpenBrace == null
                ? this.tm.firstIndexIn(node, TokenNameLBRACE)
                : this.tm.firstIndexAfter(nodeBeforeOpenBrace, TokenNameLBRACE);
        int closeBraceIndex = this.tm.lastIndexIn(node, TokenNameRBRACE);
        Token openBraceToken = this.tm.get(openBraceIndex);
        Token closeBraceToken = this.tm.get(closeBraceIndex);
        handleBracePosition(openBraceToken, closeBraceIndex, bracePosition);

        openBraceToken.breakAfter();
        closeBraceToken.breakBefore();

        if (indentBody) {
            adjustEmptyLineAfter(openBraceIndex, 1);
            this.tm.get(openBraceIndex + 1).indent();
            closeBraceToken.unindent();
        }
    }

    private void handleBracePosition(Token openBraceToken, int closeBraceIndex, String bracePosition) {
        if (bracePosition.equals(DefaultCodeFormatterConstants.NEXT_LINE)) {
            openBraceToken.breakBefore();
        } else if (bracePosition.equals(DefaultCodeFormatterConstants.NEXT_LINE_SHIFTED)) {
            openBraceToken.breakBefore();
            openBraceToken.indent();
            if (closeBraceIndex + 1 < this.tm.size())
                this.tm.get(closeBraceIndex + 1).unindent();
        } else if (bracePosition.equals(DefaultCodeFormatterConstants.NEXT_LINE_ON_WRAP)) {
            openBraceToken.setNextLineOnWrap();
        }
    }

    private void adjustEmptyLineAfter(int tokenIndex, int indentationAdjustment) {
        if (tokenIndex + 1 >= this.tm.size())
            return;
        Token token = this.tm.get(tokenIndex);
        Token next = this.tm.get(tokenIndex + 1);
        if (this.tm.countLineBreaksBetween(token, next) < 2 || !this.options.indent_empty_lines)
            return;

        next.setEmptyLineIndentAdjustment(indentationAdjustment * this.options.indentation_size);
    }

    private void indent(ASTNode node) {
        int startIndex = this.tm.firstIndexIn(node, -1);
        while (startIndex > 0 && this.tm.get(startIndex - 1).isComment())
            startIndex--;
        this.tm.get(startIndex).indent();
        int lastIndex = this.tm.lastIndexIn(node, -1);
        if (lastIndex + 1 < this.tm.size())
            this.tm.get(lastIndex + 1).unindent();
    }

    public void finishUp() {
        // the visits only noted where indents increase and decrease,
        // now prepare actual indent values
        int currentIndent = this.options.initial_indentation_level;
        for (Token token : this.tm) {
            currentIndent += token.getIndent();
            token.setIndent(currentIndent * this.options.indentation_size);
        }
    }
}
