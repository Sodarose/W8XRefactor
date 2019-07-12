package com.w8x.web.ulits;

import analysis.AbstractRuleVisitor;
import analysis.Rule;
import model.Store;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * 读取xml中的信息
 */
public class RuleLink {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleLink.class);

    private static final String RULE_XML_PATH = "rule.xml";

    private static RuleLink ruleLink;


    public static RuleLink newInstance() {
        if (ruleLink == null) {
            ruleLink = new RuleLink();
        }
        return ruleLink;
    }


    /**
     * 读取xml规则
     */
    public List<Rule> readRuleLinkByXML() {
        List<Rule> rules = new ArrayList<>();
        LOGGER.info("读取规则配置文件");
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(FileUlits.readFileByJarContents(RULE_XML_PATH));
            Element root = document.getRootElement();
            Iterator<Element> it = root.elementIterator();
            Store.ruleMap = new HashMap<>();
            while (it.hasNext()) {
                AbstractRuleVisitor rule = (AbstractRuleVisitor) createRule(it.next());
                rules.add(rule);
                Store.ruleMap.put(rule.getRuleName(), rule);
            }
            rules.sort(new Comparator<Rule>() {
                @Override
                public int compare(Rule o1, Rule o2) {
                    AbstractRuleVisitor r1 = (AbstractRuleVisitor) o1;
                    AbstractRuleVisitor r2 = (AbstractRuleVisitor) o2;
                    return r1.getLevel() - r2.getLevel();
                }
            });
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        if (rules.size() == 0) {
            LOGGER.info("读取失败");
        } else {
            LOGGER.info("读取成功");
        }
        return rules;
    }

    /**
     * 生成规则
     */
    private Rule createRule(Element element) {
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
            rule.setExample(example);
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


    /**
     * 保存配置修改
     */
    public void changeRuleXMLByMap(Map<String, Integer> rules) throws IOException {
        //文档
        Document document = null;
        //索引表
        Map<String, Element> elementMap = null;
        try {
            SAXReader reader = new SAXReader();
            document = reader.read(FileUlits.readFileByJarContents(RULE_XML_PATH));
            Element root = document.getRootElement();
            Iterator<Element> it = root.elementIterator();
            elementMap = new HashMap<>();
            while (it.hasNext()) {
                Element element = it.next();
                String ruleName = element.elementText("rule-name");
                elementMap.put(ruleName, element);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        if (document == null) {
            LOGGER.error("配置写入失败");
            return;
        }

        //更改值
        for (Map.Entry<String, Integer> entry : rules.entrySet()) {

            Element element = elementMap.get(entry.getKey());
            if (element == null) {
                return;
            }
            Element ruleStatus = element.element("rule-status");
            if (ruleStatus == null) {
                return;
            }
            if (entry.getValue() == 1) {
                ruleStatus.setText("true");
            } else {
                ruleStatus.setText("false");
            }
        }
        writeRuleXML(document);
    }


    public void writeRuleXML(Document document) throws IOException {
        XMLWriter xmlWriter = null;
        try {
            xmlWriter = new XMLWriter(new FileOutputStream(FileUlits.readFileByJarContents(RULE_XML_PATH)));
            xmlWriter.write(document);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (xmlWriter != null) {
                LOGGER.warn("关闭写入流");
                xmlWriter.close();
            }
        }
    }
}