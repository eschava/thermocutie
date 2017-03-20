package org.thermocutie.thermostat.device;

import org.thermocutie.thermostat.mqtt.XmlPublisher;
import org.w3c.dom.Element;

/**
 * HVAC device
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class HVAC extends XmlPublisher implements IDevice {
    private String name;
    private String mqttOnCommand;
    private String mqttOffCommand;
    private boolean heating = true;
    private boolean on;

    @Override
    public String getName() {
        return name;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;

        getMqttClient().publish(getMqttTopic(), on ? mqttOnCommand : mqttOffCommand);
    }

    public boolean isHeating() {
        return heating;
    }

    @Override
    public void loadFromXml(Element element) {
        super.loadFromXml(element);
        name = getAttribute(element,"name");
        mqttOnCommand = getAttribute(element,"mqttOnCommand");
        mqttOffCommand = getAttribute(element,"mqttOffCommand");
    }

    @Override
    public void saveToXml(Element element) {
        super.saveToXml(element);
        element.setAttribute("name", name);
        element.setAttribute("mqttOnCommand", mqttOnCommand);
        element.setAttribute("mqttOffCommand", mqttOffCommand);
    }
}
