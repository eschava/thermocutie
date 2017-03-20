package org.thermocutie.thermostat.core;

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
    private File file;

    private TemperaturePublisher currentTemperaturePublisher;
    private TemperaturePublisher targetTemperaturePublisher;
    private XmlPublisher targetModePublisher;

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
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
        throw new UnsupportedOperationException();
    }

    public static class TemperaturePublisher extends XmlPublisher implements ITemperatureListener {
        @Override
        public void onTemperatureChanged(Temperature temperature) {
            getMqttClient().publish(getMqttTopic(), String.valueOf(temperature.getValue()));
        }
    }
}
