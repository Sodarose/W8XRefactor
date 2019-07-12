package com.w8x.web.ulits;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 代码格式化选项
 */
public class FormatOptions {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormatOptions.class);
    private static final String PATH = "codestyle";

    private static FormatOptions formatOptions;

    public static FormatOptions newInstance() {
        if (formatOptions == null) {
            formatOptions = new FormatOptions();
        }
        return formatOptions;
    }

    public Map<String, Object> options(String fileName) {
        Map<String, Object> options = new HashMap<String, Object>();
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(FileUlits.
                    readFileByJarContents(PATH + File.separator + fileName));
            Element root = document.getRootElement();
            Element el = root.element("profile");
            Iterator<Element> it = el.elementIterator();
            while (it.hasNext()) {
                el = it.next();
                String key = el.attributeValue("id");
                String value = el.attributeValue("value");
                options.put(key, value);
            }
            return options;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        LOGGER.warn("文件读取失败，即将采用eclipse默认风格配置");
        return DefaultCodeFormatterConstants.getEclipseDefaultSettings();
    }

}
