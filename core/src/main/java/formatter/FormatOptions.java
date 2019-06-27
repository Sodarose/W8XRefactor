package formatter;

import io.FileUlits;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;

public class FormatOptions {

    private static Map<String, Object> options;
    private static final String defaultOptions = "/static/options.xml";

    public static Map<String, Object> getOptions() {
        if (options == null) {
            options = options(FormatOptions.class.getResource(defaultOptions).toString());
        }
        return options;
    }

    public static Map<String, Object> options(String filePath) {
        Map<String, Object> options = new HashMap<String, Object>();
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(filePath);
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
        return DefaultCodeFormatterConstants.getEclipseDefaultSettings();
    }

}
