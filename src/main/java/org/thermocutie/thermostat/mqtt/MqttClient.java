package org.thermocutie.thermostat.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thermocutie.thermostat.xml.IXmlPersistable;
import org.w3c.dom.Element;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

/**
 * MQTT broker connection
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class MqttClient implements IXmlPersistable, MqttCallback {
    private final static Logger LOGGER = LoggerFactory.getLogger(MqttClient.class);

    private org.eclipse.paho.client.mqttv3.MqttClient mqttClient;
    private Map<String, IMqttListener> sensorMap = new LinkedHashMap<>();
    private String uri;
    private String clientId;

    public String getUri() {
        return uri;
    }

    public String getClientId() {
        return clientId;
    }

    public void addListener(IMqttListener sensor) throws MqttException {
        mqttClient.subscribe(sensor.getMqttTopic());
        sensorMap.put(sensor.getMqttTopic(), sensor);
    }

    @Override
    public void connectionLost(Throwable throwable) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        IMqttListener mqttSensor = sensorMap.get(topic);
        if (mqttSensor != null)
            mqttSensor.onMessage(new String(mqttMessage.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    @Override
    public void loadFromXml(Element element) {
        uri = getAttribute(element, "uri");
        clientId = getAttribute(element, "clientId");
    }

    @Override
    public void saveToXml(Element element) {
        throw new UnsupportedOperationException();
    }

    public void start() {
        try {
            mqttClient = new org.eclipse.paho.client.mqttv3.MqttClient(uri, clientId);
            mqttClient.setCallback(this);
            mqttClient.connect();
        } catch (MqttException e) {
            LOGGER.error("Error connecting MQTT client to " + uri, e);
        }
    }

    public void publish(String topic, String payload) {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setRetained(false);
        message.setQos(0);

        if (Thread.currentThread().getName().startsWith("MQTT Call:")) // MQTT receiver thread, need to move publishing to another thread
            ForkJoinPool.commonPool().execute(() -> publishImpl(topic, message));
        else
            publishImpl(topic, message);
    }

    private void publishImpl(String topic, MqttMessage message) {
        try {
            LOGGER.debug("Sending MQTT message '{}' to topic '{}'", message.toString(), topic);
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
            LOGGER.error("MQTT publishing exception", e);
        }
    }
}
