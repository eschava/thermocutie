package org.thermocutie.thermostat.mqtt;

/**
 * Interface for components publishing MQTT messages
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public interface IMqttPublisher {
    String getMqttBroker();
    void setMqttClient(MqttClient mqttClient);
}
