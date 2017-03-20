package org.thermocutie.thermostat.listener;

import org.thermocutie.thermostat.model.Temperature;

/**
 * Temperature change listener
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public interface ITemperatureListener {
    void onTemperatureChanged(Temperature temperature);
}
