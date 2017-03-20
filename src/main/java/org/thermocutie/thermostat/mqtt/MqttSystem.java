package org.thermocutie.thermostat.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thermocutie.thermostat.xml.IXmlFilePersistableHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MQTT system
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class MqttSystem implements IXmlFilePersistableHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(MqttSystem.class);

    private File file;
    private MqttServer server = new MqttServer();
    private Map<String, MqttClient> clientMap = new LinkedHashMap<>();

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public synchronized void loadFromXml(Element element) {
        childElements(element).forEach(childElement -> {
            String tagName = childElement.getTagName();
            switch (tagName) {
                case "Server":
                    server.loadFromXml(childElement);
                    break;
                case "Clients":
                    loadClientsFromXml(childElement);
                    break;
                default:
                    LOGGER.error("Element " + tagName + " cannot be child for " + element.getTagName());
                    break;
            }
        });
    }

    private void loadClientsFromXml(Element element) {
        childElements(element, "Client").forEach(clientElement -> {
            String name = getAttribute(clientElement, "name");
            MqttClient client = new MqttClient();
            client.loadFromXml(clientElement);
            clientMap.put(name, client);
        });
    }

    @Override
    public synchronized void saveToXml(Element element) {
        childElements(element).forEach(childElement -> {
            String tagName = childElement.getTagName();
            switch (tagName) {
                case "Server":
                    server.saveToXml(childElement);
                    break;
                case "Clients":
//                    loadClientsFromXml(childElement); // TODO
                    break;
                default:
                    LOGGER.error("Element " + tagName + " cannot be child for " + element.getTagName());
                    break;
            }
        });
    }

    public void start() {
        server.start();

        clientMap.values().forEach(MqttClient::start);
    }

    public synchronized void addListener(IMqttListener listener) {
        try {
            MqttClient client = clientMap.get(listener.getMqttBroker());
            client.addListener(listener);
        } catch (MqttException e) {
            LOGGER.error("Cannot add listener {} to client {}", listener, listener.getMqttBroker(), e);
        }
    }

    public synchronized void addPublisher(IMqttPublisher publisher) {
        String mqttBroker = publisher.getMqttBroker();
        MqttClient client = clientMap.get(mqttBroker);
        publisher.setMqttClient(client);
    }

    public boolean isServerEnabled() {
        return server.isEnabled();
    }

    public int getServerPort() {
        return server.getPort();
    }

    public void updateServer(boolean enabled, int port) {
        // TODO: change port of embedded client
        if (port != server.getPort()) {
            server.setPort(port);
            // port was changed and server is enabled - need to stop server to
            // re-start with new port later
            if (enabled)
                server.setEnabled(false);
        }
        server.setEnabled(enabled);

        // TODO: review saving settings to file
        File configFolder = new File("conf");
        File mqttFile = new File(configFolder, "mqtt.xml");

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            try (FileInputStream is = new FileInputStream(mqttFile)) {
                Document doc = builder.parse(is);
                Element root = doc.getDocumentElement();
                saveToXml(root);

                // Use a Transformer for output
                TransformerFactory tFactory = TransformerFactory.newInstance();
                Transformer transformer = tFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                try (FileOutputStream os = new FileOutputStream(mqttFile)) {
                    StreamResult result = new StreamResult(os);
                    DOMSource source = new DOMSource(doc);
                    transformer.transform(source, result);
                }
            }
        } catch (Exception e) {
            LOGGER.error("mqtt.xml file loading error", e);
        }
    }

    public synchronized Collection<String> getClientNames() {
        return new ArrayList<>(clientMap.keySet());
    }

    public synchronized MqttClient getClient(String name) {
        return clientMap.get(name);
    }
}
