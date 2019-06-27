package analysis;

import model.Store;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 读取xml中的信息 然后根据反射生成对象 生成规则链
 */
public class RuleLink {
    private static final String RULE_XML_PATH = "/static/rule.xml";

    private static RuleLink ruleLink = null;

    public static RuleLink newInstance() {
        if (ruleLink == null) {
            ruleLink = new RuleLink();
        }
        return ruleLink;
    }

    public List<Rule> readRuleLinkByXML() {
        List<Rule> rules = new ArrayList<>();
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(RuleLink.class.getResource(RULE_XML_PATH).toString());
            Element root = document.getRootElement();
            Iterator<Element> it = root.elementIterator();
            Store.rules = new ArrayList<>();
            while (it.hasNext()) {
                AbstractRuleVisitor rule = createRule(it.next());
                Store.rules.add(rule);
                it.remove();
            }
            Store.rules.sort(new Comparator<AbstractRuleVisitor>() {
                @Override
                public int compare(AbstractRuleVisitor o1, AbstractRuleVisitor o2) {
                    return o1.getLevel() - o2.getLevel();
                }
            });
            rules.addAll(Store.rules);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return rules;
    }

    private AbstractRuleVisitor createRule(Element element) {
        AbstractRuleVisitor rule = null;
        String ruleName = element.elementText("rule-name");
        String description = element.elementText("description");
        String className = element.elementText("className");
        String status = element.elementText("rule-status");
        String message = element.elementText("rule-message");
        String example = element.elementText("example");
        String solutionClassName = element.elementText("solutionClassName");
        int level = Integer.parseInt(element.elementText("rule-level"));
        try {
            rule = (AbstractRuleVisitor) Class.forName(className).newInstance();
            rule.setClassName(className);
            rule.setDescription(description);
            rule.setRuleName(ruleName);
            rule.setRuleStatus(Boolean.parseBoolean(status));
            rule.setMessage(message);
            rule.setMessage(example);
            rule.setSolutionClassName(solutionClassName);
            rule.setLevel(level);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return rule;
    }


}
