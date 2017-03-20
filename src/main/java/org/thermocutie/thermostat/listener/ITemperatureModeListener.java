package org.thermocutie.thermostat.listener;

import org.thermocutie.thermostat.core.TemperatureMode;

/**
 * Temperature mode change listener
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public interface ITemperatureModeListener {
    void onTemperatureModeChanged(TemperatureMode mode);
}
