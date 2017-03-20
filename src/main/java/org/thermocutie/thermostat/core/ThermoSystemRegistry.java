package org.thermocutie.thermostat.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry of {@link ThermoSystem}
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class ThermoSystemRegistry {
    private static final ThermoSystemRegistry INSTANCE = new ThermoSystemRegistry();

    private final Map<String, ThermoSystem> thermoSystemMap = new LinkedHashMap<>();

    private ThermoSystemRegistry() {
    }

    public static ThermoSystemRegistry getInstance() {
        return INSTANCE;
    }

    public static void loadFromFolder(File folder) {
        for (File thermoFolder : folder.listFiles()) {
            if (thermoFolder.isDirectory()) {
                ThermoSystem thermoSystem = new ThermoSystem();
                thermoSystem.loadFromFolder(thermoFolder);
                register(thermoFolder.getName(), thermoSystem);
            }
        }
    }

    public static synchronized void register(String name, ThermoSystem thermoSystem) {
        INSTANCE.thermoSystemMap.put(name, thermoSystem);
    }

    public static synchronized Collection<String> getNames() {
        return new ArrayList<>(INSTANCE.thermoSystemMap.keySet());
    }

    public static synchronized ThermoSystem get(String name) {
        ThermoSystem thermoSystem = INSTANCE.thermoSystemMap.get(name);
        if (thermoSystem == null)
            thermoSystem = INSTANCE.thermoSystemMap.values().iterator().next(); // TODO: temp
        return thermoSystem;
    }
}
