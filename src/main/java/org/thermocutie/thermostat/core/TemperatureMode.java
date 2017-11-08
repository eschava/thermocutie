package org.thermocutie.thermostat.core;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.thermocutie.thermostat.model.Temperature;
import org.thermocutie.thermostat.rest.serializer.ColorDeserializer;
import org.thermocutie.thermostat.rest.serializer.ColorSerializer;
import org.thermocutie.thermostat.rest.serializer.TemperatureDeserializer;
import org.thermocutie.thermostat.rest.serializer.TemperatureSerializer;
import org.thermocutie.thermostat.xml.IXmlPersistable;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlRootElement;
import java.awt.*;

/**
 * @author Eugene Schava <eschava@gmail.com>
 */
@XmlRootElement
public class TemperatureMode implements IXmlPersistable {
    private String name;
    private Temperature temperature;
    private Color color;
    private String icon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonSerialize(using = TemperatureSerializer.class)
    @JsonDeserialize(using = TemperatureDeserializer.class)
    public Temperature getTemperature() {
        return temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    @JsonSerialize(using = ColorSerializer.class)
    @JsonDeserialize(using = ColorDeserializer.class)
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public void loadFromXml(Element element) {
        name = getAttribute(element, "name");
        temperature = new Temperature(Double.parseDouble(getAttribute(element, "temperature")));
        color = Color.decode(getAttribute(element, "color"));
        icon = getAttribute(element, "icon");
    }

    @Override
    public void saveToXml(Element element) {
        element.setAttribute("name", name);
        element.setAttribute("temperature", String.valueOf(temperature.getValue()));
        if (color != null)
            element.setAttribute("color", "#"+Integer.toHexString(color.getRGB()).substring(2));
        element.setAttribute("icon", icon);
    }
}
