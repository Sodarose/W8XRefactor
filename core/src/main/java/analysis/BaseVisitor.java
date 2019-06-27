package analysis;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *  带存储库的观察者
 * */
@Data
public class BaseVisitor<T extends Node> extends VoidVisitorAdapter {
    private Node caller = null;
    private List<T> list = new ArrayList<>();
}