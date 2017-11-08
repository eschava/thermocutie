package org.thermocutie.thermostat.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Helper implementation of {@link IXmlFilePersistable}
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public interface IXmlFilePersistableHelper extends IXmlFilePersistable {
    DocumentBuilder DOCUMENT_BUILDER = createDocumentBuilder();
    Transformer TRANSFORMER = createTransformer();

    File getFile();

    String getRootTag();

    @Override
    default void loadFromFile() {
        File file = getFile();

        if (file.exists()) {
            try (FileInputStream is = new FileInputStream(file)) {
                Document doc = DOCUMENT_BUILDER.parse(is);
                Element root = doc.getDocumentElement();
                // validation
                if (!root.getTagName().equals(getRootTag()))
                    getLogger().warn("Root element of file {} should be {}", file, getRootTag());

                loadFromXml(root);
            } catch (Exception e) {
                getLogger().error("File {} loading error", file, e);
            }
        } else {
            getLogger().debug("File {} doesn't exist", file);
        }
    }

    @Override
    default void saveToFile() {
        File file = getFile();

        try {
            Document doc;

            if (file.exists()) {
                try (FileInputStream is = new FileInputStream(file)) {
                    doc = DOCUMENT_BUILDER.parse(is);

                    // validation
                    if (!doc.getDocumentElement().getTagName().equals(getRootTag()))
                        getLogger().warn("Root element of file {} should be {}", file, getRootTag());
                }
            } else {
                doc = DOCUMENT_BUILDER.newDocument();
                doc.appendChild(doc.createElement(getRootTag()));
            }
            saveToXml(doc.getDocumentElement());

            try (FileOutputStream os = new FileOutputStream(file)) {
                StreamResult result = new StreamResult(os);
                DOMSource source = new DOMSource(doc);
                TRANSFORMER.transform(source, result);
            }
        } catch (Exception e) {
            getLogger().error("{} file saving error", file, e);
        }
    }

    static DocumentBuilder createDocumentBuilder() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            return factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            LoggerFactory.getLogger(IXmlFilePersistable.class).error("XML error", e);
            return null;
        }
    }

    static Transformer createTransformer() {
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            return transformer;
        } catch (TransformerConfigurationException e) {
            LoggerFactory.getLogger(IXmlFilePersistable.class).error("XML error", e);
            return null;
        }
    }

    default Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }
}
