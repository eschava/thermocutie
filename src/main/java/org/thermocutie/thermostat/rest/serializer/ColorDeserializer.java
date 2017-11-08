package org.thermocutie.thermostat.rest.serializer;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.thermocutie.thermostat.util.StringConverterUtil;

import java.awt.*;
import java.io.IOException;

/**
 * Loads {@link Color} from JSON
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class ColorDeserializer extends JsonDeserializer<Color> {
    @Override
    public Color deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return StringConverterUtil.colorFromString(jp.getText());
    }
}
