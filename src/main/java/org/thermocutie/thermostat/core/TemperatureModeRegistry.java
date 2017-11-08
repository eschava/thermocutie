package org.thermocutie.thermostat.core;

import org.thermocutie.thermostat.xml.IXmlFilePersistableHelper;
import org.w3c.dom.Element;

import java.io.File;
import java.util.*;

/**
 * Registry of {@link TemperatureMode}
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class TemperatureModeRegistry implements IXmlFilePersistableHelper {
    private Map<String, TemperatureMode> temperatureModeMap = new LinkedHashMap<>();
    private File file;

    public synchronized TemperatureMode getMode(String name) {
        return temperatureModeMap.get(name);
    }

    public synchronized Collection<TemperatureMode> getModes() {
        return new ArrayList<>(temperatureModeMap.values());
    }

    @Override
    public String getRootTag() {
        return "TemperatureModes";
    }

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
        temperatureModeMap.clear();

        childElements(element, "Mode").forEach(child -> {
            TemperatureMode mode = new TemperatureMode();
            mode.loadFromXml(child);
            temperatureModeMap.put(mode.getName(), mode);
        });
    }

    @Override
    public synchronized void saveToXml(Element element) {
        saveChildren(element, "Mode", temperatureModeMap.values());
    }

    public synchronized void remove(String name) {
        temperatureModeMap.remove(name);
    }

    public synchronized void add(TemperatureMode mode) {
        temperatureModeMap.put(mode.getName(), mode);
    }

    public synchronized void update(TemperatureMode mode) {
        temperatureModeMap.put(mode.getName(), mode);
    }

    public synchronized void rename(String oldName, String newName) {
        TemperatureMode mode = temperatureModeMap.remove(oldName);
        temperatureModeMap.put(newName, mode);
        mode.setName(newName);
    }
}
