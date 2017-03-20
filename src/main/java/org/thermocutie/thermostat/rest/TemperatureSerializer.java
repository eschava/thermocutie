package org.thermocutie.thermostat.rest;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.thermocutie.thermostat.model.Temperature;

import java.awt.*;
import java.io.IOException;

/**
 * Serializes {@link Temperature} to JSON
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class TemperatureSerializer extends SerializerBase<Temperature> {
    public TemperatureSerializer() {
        super(Temperature.class);
    }

    @Override
    public void serialize(Temperature temperature, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeNumber(temperature.getValue());
    }
}
