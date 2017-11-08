package org.thermocutie.thermostat.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thermocutie.thermostat.listener.ITemperatureListener;
import org.thermocutie.thermostat.model.Temperature;
import org.thermocutie.thermostat.mqtt.XmlPublisher;
import org.thermocutie.thermostat.xml.IXmlFilePersistableHelper;
import org.w3c.dom.Element;

import java.io.File;

/**
 * Settings of {@link ThermoSystem}
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class ThermoSystemSettings implements IXmlFilePersistableHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(ThermoSystemSettings.class);

    private File file;
    private String title;

    private TemperaturePublisher currentTemperaturePublisher;
    private TemperaturePublisher targetTemperaturePublisher;
    private XmlPublisher targetModePublisher;

    @Override
    public String getRootTag() {
        return "Settings";
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TemperaturePublisher getCurrentTemperaturePublisher() {
        return currentTemperaturePublisher;
    }

    public TemperaturePublisher getTargetTemperaturePublisher() {
        return targetTemperaturePublisher;
    }

    public XmlPublisher getTargetModePublisher() {
        return targetModePublisher;
    }

    @Override
    public void loadFromXml(Element element) {
        title = element.getAttribute("title");

        childElements(element).forEach(childElement -> {
            switch (childElement.getTagName()) {
                case "CurrentTemperature":
                    currentTemperaturePublisher = new TemperaturePublisher();
                    currentTemperaturePublisher.loadFromXml(childElement);
                    break;
                case "TargetTemperature":
                    targetTemperaturePublisher = new TemperaturePublisher();
                    targetTemperaturePublisher.loadFromXml(childElement);
                    break;
                case "TargetMode":
                    targetModePublisher = new XmlPublisher();
                    targetModePublisher.loadFromXml(childElement);
                    break;

            }
        });
    }

    @Override
    public void saveToXml(Element element) {
        element.setAttribute("title", title);

        childElements(element).forEach(childElement -> {
            String tagName = childElement.getTagName();
            switch (tagName) {
                case "CurrentTemperature":
                    currentTemperaturePublisher.saveToXml(childElement);
                    break;
                case "TargetTemperature":
                    targetTemperaturePublisher.saveToXml(childElement);
                    break;
                case "TargetMode":
                    targetModePublisher.saveToXml(childElement);
                    break;
                default:
                    LOGGER.error("Element " + tagName + " cannot be child for " + element.getTagName());
                    break;
            }
        });
    }

    public static class TemperaturePublisher extends XmlPublisher implements ITemperatureListener {
        @Override
        public void onTemperatureChanged(Temperature temperature) {
            getMqttClient().publish(getMqttTopic(), String.valueOf(temperature.getValue()));
        }
    }
}
