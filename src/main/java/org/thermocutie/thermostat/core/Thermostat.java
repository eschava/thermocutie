package org.thermocutie.thermostat.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thermocutie.thermostat.device.HVAC;
import org.thermocutie.thermostat.model.Temperature;
import org.thermocutie.thermostat.schedule.WeekSchedule;

/**
 * Termostat logic
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class Thermostat {
    private final static Logger LOGGER = LoggerFactory.getLogger(Thermostat.class);

    private Temperature currentTemperature;
    private Temperature targetTemperature;
    private HVAC hvac;

    private double delta;

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public void setHvac(HVAC hvac) {
        this.hvac = hvac;
        update();
    }

    public void setCurrentTemperature(Temperature currentTemperature) {
        LOGGER.debug("Current temperature set to {}", currentTemperature);
        this.currentTemperature = currentTemperature;
        update();
    }

    public void setTargetTemperature(Temperature targetTemperature) {
        LOGGER.debug("Target temperature set to {}", targetTemperature);
        this.targetTemperature = targetTemperature;
        update();
    }

    private void update() {
        if (hvac == null || currentTemperature == null || targetTemperature == null)
            return;

        if (hvac.isHeating()) {
            if (hvac.isOn()) {
                if (currentTemperature.getValue() >= targetTemperature.getValue() + delta) {
                    LOGGER.debug("Current temperature {} is greater than {}, disabling HVAC", currentTemperature, targetTemperature.getValue() + delta);
                    hvac.setOn(false);
                }
            } else {
                if (currentTemperature.getValue() <= targetTemperature.getValue() - delta) {
                    hvac.setOn(true);
                    LOGGER.debug("Current temperature {} is less than {}, enabling HVAC", currentTemperature, targetTemperature.getValue() - delta);
                }
            }
        } else {
            if (hvac.isOn()) {
                if (currentTemperature.getValue() <= targetTemperature.getValue() - delta) {
                    hvac.setOn(false);
                    LOGGER.debug("Current temperature {} is less than {}, disabling HVAC", currentTemperature, targetTemperature.getValue() - delta);
                }
            } else {
                if (currentTemperature.getValue() >= targetTemperature.getValue() + delta) {
                    hvac.setOn(true);
                    LOGGER.debug("Current temperature {} is greater than {}, enabling HVAC", currentTemperature, targetTemperature.getValue() + delta);
                }
            }
        }
    }
}
