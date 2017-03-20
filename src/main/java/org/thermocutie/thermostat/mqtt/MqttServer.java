package org.thermocutie.thermostat.mqtt;

import io.moquette.BrokerConstants;
import io.moquette.server.Server;
import io.moquette.server.config.MemoryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thermocutie.thermostat.xml.IXmlPersistable;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.Properties;

/**
 * Internal MQTT broker settings
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class MqttServer implements IXmlPersistable {
    private final static Logger LOGGER = LoggerFactory.getLogger(MqttServer.class);

    private boolean enabled = true;
    private int port = BrokerConstants.PORT;
    private Server server;

    @Override
    public void loadFromXml(Element element) {
        enabled = Boolean.parseBoolean(getAttribute(element, "enabled", "true"));
        port = Integer.parseInt(getAttribute(element, "port"));
    }

    @Override
    public void saveToXml(Element element) {
        element.setAttribute("enabled", String.valueOf(enabled));
        element.setAttribute("port", String.valueOf(port));
    }

    public void start() {
        try {
            if (enabled) {
                MemoryConfig config = new MemoryConfig(new Properties());
                config.setProperty(BrokerConstants.PORT_PROPERTY_NAME, String.valueOf(port));

                server = new Server();
                server.startServer(config);
            }
        } catch (IOException e) {
            LOGGER.error("Error starting internal MQTT server", e);
        }
    }

    public void stop() {
        if (server != null) {
            server.stopServer();
            server = null;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (enabled != this.enabled) {
            this.enabled = enabled;
            if (enabled)
                start();
            else
                stop();
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
