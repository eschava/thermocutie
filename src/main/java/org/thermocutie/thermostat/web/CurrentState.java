package org.thermocutie.thermostat.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.thermocutie.thermostat.core.TemperatureMode;

import java.io.IOException;

/**
 * Current thermostat state reported via web-socket
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class CurrentState {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private double currentTemperature;
    private TemperatureMode targetMode;

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public TemperatureMode getTargetMode() {
        return targetMode;
    }

    public void setTargetMode(TemperatureMode targetMode) {
        this.targetMode = targetMode;
    }

    public String toJson()
    {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args)
    {
        CurrentState state = new CurrentState();
        state.setCurrentTemperature(50);
        System.out.println(state.toJson());
    }
}
