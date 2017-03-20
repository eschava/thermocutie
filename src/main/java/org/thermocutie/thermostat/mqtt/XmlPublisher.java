package org.thermocutie.thermostat.mqtt;

import org.thermocutie.thermostat.xml.IXmlPersistable;
import org.w3c.dom.Element;

/**
 * Base implementation of MQTT publishers loading configuration from XML
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class XmlPublisher implements IXmlPersistable, IMqttPublisher {
    private String mqttBroker;
    private String mqttTopic;
    private MqttClient mqttClient;

    @Override
    public void loadFromXml(Element element) {
        mqttBroker = getAttribute(element, "mqttBroker");
        mqttTopic = getAttribute(element, "mqttTopic");
    }

    @Override
    public void saveToXml(Element element) {
        element.setAttribute("mqttBroker", mqttBroker);
        element.setAttribute("mqttTopic", mqttTopic);
    }

    @Override
    public String getMqttBroker() {
        return mqttBroker;
    }

    @Override
    public void setMqttClient(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public String getMqttTopic() {
        return mqttTopic;
    }

    public MqttClient getMqttClient() {
        return mqttClient;
    }
}
