package formatter;

import com.github.javaparser.JavaToken;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.printer.PrettyPrintVisitor;
import com.github.javaparser.printer.PrettyPrinter;
import com.github.javaparser.printer.PrettyPrinterConfiguration;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

public class JavaParserFormatter {
    public static void main(String[] args) throws FileNotFoundException {
        String path = "D:\\IdeaProjects\\W8XRefactor\\core\\src\\main\\java\\formatter\\Test.java";
        File file = new File(path);
        CompilationUnit unit = StaticJavaParser.parse(file);
        PrettyPrinterConfiguration conf = new PrettyPrinterConfiguration();
        //UNPARSABLE unparsavle
        conf.setVisitorFactory(prettyPrinterConfiguration -> new UserPrettyPrintVisitor(conf));
        PrettyPrinter prettyPrinter = new PrettyPrinter(conf);
        //prettyPrinter.print(unit);
        System.out.println(prettyPrinter.print(unit));
    }
}
