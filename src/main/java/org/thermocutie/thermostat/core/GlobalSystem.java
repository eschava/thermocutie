package org.thermocutie.thermostat.core;

import org.thermocutie.thermostat.core.ThermoSystemSettings.TemperaturePublisher;
import org.thermocutie.thermostat.mqtt.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;

/**
 * @author Eugene Schava <eschava@gmail.com>
 */
public class GlobalSystem {
//    private final static Logger LOGGER = LoggerFactory.getLogger(GlobalSystem.class);

    public static final GlobalSystem INSTANCE = new GlobalSystem();

    private GlobalSystem() {
    }

    private MqttSystem mqttSystem;

    public void load() throws ParserConfigurationException {
        File configFolder = new File("conf");

        ThermoSystemRegistry.loadFromFolder(configFolder);

        mqttSystem = new MqttSystem();
        mqttSystem.setFile(new File(configFolder, "mqtt.xml"));
        mqttSystem.loadFromFile();
    }

    public void start() {
        mqttSystem.start();

        ThermoSystemRegistry.getNames().stream()
                .map(ThermoSystemRegistry::get)
                .forEach(thermoSystem -> {
                    thermoSystem.start();
                    thermoSystem.getDeviceSet()
                            .getDevices(IMqttListener.class)
                            .forEach(mqttSystem::addListener);

                    thermoSystem.getDeviceSet()
                            .getDevices(IMqttPublisher.class)
                            .forEach(mqttSystem::addPublisher);

                    ThermoSystemSettings settings = thermoSystem.getSettings();

                    // add MQTT publishers
                    TemperaturePublisher currentTemperaturePublisher = settings.getCurrentTemperaturePublisher();
                    if (currentTemperaturePublisher != null) {
                        mqttSystem.addPublisher(currentTemperaturePublisher);
                        thermoSystem.addCurrentTemperatureListener(currentTemperaturePublisher);
                    }

                    TemperaturePublisher targetTemperaturePublisher = settings.getTargetTemperaturePublisher();
                    if (targetTemperaturePublisher != null) {
                        mqttSystem.addPublisher(targetTemperaturePublisher);
                        thermoSystem.addTargetTemperatureModeListener(mode -> targetTemperaturePublisher.onTemperatureChanged(mode.getTemperature()));
                    }

                    XmlPublisher targetModePublisher = settings.getTargetModePublisher();
                    if (targetModePublisher != null) {
                        mqttSystem.addPublisher(targetModePublisher);
                        thermoSystem.addTargetTemperatureModeListener(mode ->
                                targetModePublisher.getMqttClient().publish(targetModePublisher.getMqttTopic(), mode.getName()
                        ));
                    }
                });
    }

    public MqttSystem getMqttSystem() {
        return mqttSystem;
    }

    public static void main(String[] args) throws ParserConfigurationException {
        GlobalSystem globalSystem = new GlobalSystem();
        globalSystem.load();
        globalSystem.start();
    }
}
