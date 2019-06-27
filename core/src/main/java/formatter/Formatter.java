package formatter;

import java.text.Normalizer;
import java.util.Map;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

public class Formatter {

    public static String format(String source, Map<String, Object> options) {
        CodeFormatter formatter = ToolFactory.createCodeFormatter(options, ToolFactory.M_FORMAT_EXISTING);
        TextEdit textEdit = formatter.format(CodeFormatter.K_COMPILATION_UNIT
                | CodeFormatter.F_INCLUDE_COMMENTS, source, 0, source.length(), 0, System.lineSeparator());
        IDocument document = new Document(source);
        try {
            textEdit.apply(document);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return document.get();
    }

    public static String format(CompilationUnit unit, Map<String, Object> options) {
        IDocument document = new Document(unit.toString());
        TextEdit textEdit = unit.rewrite(document, options);
        try {
            textEdit.apply(document);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return document.get();
    }
}
