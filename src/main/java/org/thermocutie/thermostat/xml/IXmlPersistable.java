package org.thermocutie.thermostat.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Interface for objects that are persistable to XML element
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public interface IXmlPersistable {
    void loadFromXml(Element element);
    void saveToXml(Element element);

    default String getAttribute(Element element, String name) {
        String value = element.getAttribute(name);
        if (value.isEmpty())
            throw new IllegalArgumentException("Attribute " + name + " should be present in element " + element.getTagName());
        return value;
    }

    default String getAttribute(Element element, String name, String defaultValue) {
        String value = element.getAttribute(name);
        if (value.isEmpty())
            value = defaultValue;
        return value;
    }

    default Stream<Element> childElements(Element element) {
        NodeList childNodes = element.getChildNodes();
        return IntStream.range(0, childNodes.getLength())
                .mapToObj(childNodes::item)
                .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                .map(node -> (Element)node);
    }

    default Stream<Element> childElements(Element element, String tagName) {
        return childElements(element)
                .filter(childElement -> {
                    if (!childElement.getTagName().equals(tagName)) {
                        Logger logger = LoggerFactory.getLogger(getClass());
                        logger.error("Element " + childElement.getTagName() + " cannot be child for " + element.getTagName());
                        return false;
                    }
                    return true;
                });
    }

    default <T extends IXmlPersistable> void loadChildren(Element element, String tag, Collection<T> children, Function<Element, T> childFactory) {
        children.clear();
        childElements(element, tag).forEach(childElement -> {
            T child = childFactory.apply(childElement);
            child.loadFromXml(childElement);
            children.add(child);
        });
    }

    default void saveChildren(Element element, String tag, Collection<? extends IXmlPersistable> children) {
        Iterator<? extends IXmlPersistable> iterator = children.iterator();
        List<Element> elementsToRemove = new ArrayList<>();
        childElements(element, tag).forEach(child -> {
            if (iterator.hasNext())
                iterator.next().saveToXml(child);
            else
                elementsToRemove.add(child);
        });

        elementsToRemove.forEach(element::removeChild);

        iterator.forEachRemaining(mode -> {
            Element child = element.getOwnerDocument().createElement(tag);
            mode.saveToXml(child);
            element.appendChild(child);
        });
    }
}
