package org.thermocutie.thermostat.schedule;

import org.thermocutie.thermostat.core.TemperatureModeRegistry;
import org.thermocutie.thermostat.listener.ITemperatureModeListener;
import org.thermocutie.thermostat.xml.IXmlPersistable;

/**
 * Abstract schedule for changing temperature mode
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public interface ISchedule extends IXmlPersistable {
    String getName();

    void setTemperatureModeRegistry(TemperatureModeRegistry temperatureModeRegistry);

    void addTargetTemperatureModeListener(ITemperatureModeListener listener);
    void removeTargetTemperatureModeListener(ITemperatureModeListener listener);

    boolean isTemperatureModeUsed(String name);
    void renameTemperatureMode(String oldModeName, String newModeName);
}
