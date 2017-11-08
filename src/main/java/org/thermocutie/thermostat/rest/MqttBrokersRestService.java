package org.thermocutie.thermostat.rest;

import org.thermocutie.thermostat.core.GlobalSystem;
import org.thermocutie.thermostat.mqtt.MqttClient;
import org.thermocutie.thermostat.mqtt.MqttSystem;
import org.thermocutie.thermostat.rest.dto.MqttBroker;
import org.thermocutie.thermostat.rest.dto.MqttServer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * REST service for MQTT brokers management
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
@Path("/mqttbrokers")
public class MqttBrokersRestService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<MqttBroker> getBrokers() {
        MqttSystem mqttSystem = GlobalSystem.INSTANCE.getMqttSystem();
        List<MqttBroker> result = new ArrayList<>();
        for (String name : mqttSystem.getClientNames()) {
            MqttClient client = mqttSystem.getClient(name);
            MqttBroker broker = new MqttBroker(name, client.getClientId(), client.getUri());
            result.add(broker);
        }
        return result;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/internal")
    public MqttServer getServer() {
        MqttSystem mqttSystem = GlobalSystem.INSTANCE.getMqttSystem();

        MqttServer mqttServer = new MqttServer();
        mqttServer.setEnabled(mqttSystem.isServerEnabled());
        mqttServer.setPort(mqttSystem.getServerPort());
        return mqttServer;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/internal")
    public void updateServer(MqttServer mqttServer) {
        MqttSystem mqttSystem = GlobalSystem.INSTANCE.getMqttSystem();
        mqttSystem.updateServer(mqttServer.isEnabled(), mqttServer.getPort());
    }
}
