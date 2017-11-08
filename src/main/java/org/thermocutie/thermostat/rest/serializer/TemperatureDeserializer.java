package org.thermocutie.thermostat.rest.serializer;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.thermocutie.thermostat.model.Temperature;

import java.io.IOException;

/**
 * Loads {@link Temperature} from JSON
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class TemperatureDeserializer extends JsonDeserializer<Temperature> {
    @Override
    public Temperature deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        double value = jp.getDoubleValue();
        return new Temperature(value);
    }
}
