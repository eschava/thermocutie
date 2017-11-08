package org.thermocutie.thermostat.rest.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO for internal MQTT broker settings
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
@XmlRootElement
public class MqttServer {
    private boolean enabled;
    private int port;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
