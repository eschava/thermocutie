package org.thermocutie.thermostat.web;

import org.thermocutie.thermostat.core.ThermoSystem;
import org.thermocutie.thermostat.listener.ITemperatureListener;
import org.thermocutie.thermostat.listener.ITemperatureModeListener;

/**
 * {@link CurrentState}observer
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class CurrentStateObserver {
    private final ThermoSystem thermoSystem;
    private final ITemperatureListener currentTemperatureListener;
    private final ITemperatureModeListener targetModeListener;

    public CurrentStateObserver(ThermoSystem thermoSystem, ICurrentStateListener currentStateListener) {
        this.thermoSystem = thermoSystem;
        CurrentState currentState = new CurrentState();

        currentTemperatureListener = temperature -> {
            currentState.setCurrentTemperature(temperature.getValue());
            currentStateListener.onCurrentStateChanged(currentState);
        };
        thermoSystem.addCurrentTemperatureListener(currentTemperatureListener);

        targetModeListener = mode -> {
            currentState.setTargetMode(mode);
            currentStateListener.onCurrentStateChanged(currentState);
        };
        thermoSystem.addTargetTemperatureModeListener(targetModeListener);
    }

    public void unsubscribe() {
        thermoSystem.removeCurrentTemperatureListener(currentTemperatureListener);
        thermoSystem.removeTargetTemperatureModeListener(targetModeListener);
    }
}
