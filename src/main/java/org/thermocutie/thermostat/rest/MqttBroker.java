package org.thermocutie.thermostat.rest;

/**
 * DTO for MQTT connection settings
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class MqttBroker {
    private final String name;
    private final String clientId;
    private final String uri;

    public MqttBroker(String name, String clientId, String uri) {
        this.name = name;
        this.clientId = clientId;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public String getClientId() {
        return clientId;
    }

    public String getUri() {
        return uri;
    }
}
