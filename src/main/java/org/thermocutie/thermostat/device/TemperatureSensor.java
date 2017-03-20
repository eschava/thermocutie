package org.thermocutie.thermostat.device;

import org.thermocutie.thermostat.listener.composite.TemperatureCompositeListener;
import org.thermocutie.thermostat.model.Temperature;
import org.thermocutie.thermostat.mqtt.IMqttListener;
import org.w3c.dom.Element;

/**
 * Temperature sensor device
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class TemperatureSensor extends TemperatureCompositeListener
        implements IDevice, IMqttListener {
    private String name;
    private String mqttBroker; // TODO: maybe move all MQTT-related stuff to another class?
    private String mqttTopic; // TODO: maybe move all MQTT-related stuff to another class?

    public TemperatureSensor() {
        super(true);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setMqttTopic(String mqttTopic) {
        this.mqttTopic = mqttTopic;
    }

    @Override
    public String getMqttTopic() {
        return mqttTopic;
    }

    @Override
    public String getMqttBroker() {
        return mqttBroker;
    }

    @Override
    public void onMessage(String payload) {
        Temperature temperature = new Temperature();
        temperature.setValue(Double.parseDouble(payload));

        onTemperatureChanged(temperature);
    }

    @Override
    public void loadFromXml(Element element) {
        name = getAttribute(element,"name");
        mqttBroker = getAttribute(element,"mqttBroker");
        mqttTopic = getAttribute(element,"mqttTopic");
    }

    @Override
    public void saveToXml(Element element) {
        element.setAttribute("name", name);
        element.setAttribute("mqttBroker", mqttBroker);
        element.setAttribute("mqttTopic", mqttTopic);
    }
}
