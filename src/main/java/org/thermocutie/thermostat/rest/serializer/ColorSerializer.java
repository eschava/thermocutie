package org.thermocutie.thermostat.rest.serializer;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.thermocutie.thermostat.util.StringConverterUtil;

import java.awt.*;
import java.io.IOException;

/**
 * Serializes {@link Color} to JSON
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class ColorSerializer extends SerializerBase<Color> {
    public ColorSerializer() {
        super(Color.class);
    }

    @Override
    public void serialize(Color color, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(StringConverterUtil.colorToString(color));
    }
}
