package org.thermocutie.thermostat.mqtt;

/**
 * Interface for components listening for MQTT messages
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public interface IMqttListener {
    String getMqttBroker();
    String getMqttTopic();
    void onMessage(String payload);
}
