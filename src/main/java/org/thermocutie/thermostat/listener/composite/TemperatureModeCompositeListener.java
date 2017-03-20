package org.thermocutie.thermostat.listener.composite;

import org.thermocutie.thermostat.core.TemperatureMode;
import org.thermocutie.thermostat.listener.ITemperatureListener;
import org.thermocutie.thermostat.listener.ITemperatureModeListener;
import org.thermocutie.thermostat.model.Temperature;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite implementation of {@link ITemperatureModeListener}
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class TemperatureModeCompositeListener implements ITemperatureModeListener {
    private boolean withState;
    private List<ITemperatureModeListener> listenerList = new ArrayList<>();
    private TemperatureMode currentState;

    public TemperatureModeCompositeListener() {
    }

    public TemperatureModeCompositeListener(boolean withState) {
        this.withState = withState;
    }

    public synchronized int size() {
        return listenerList.size();
    }

    public synchronized void addListener(ITemperatureModeListener listener) {
        listenerList.add(listener);
        if (withState && currentState != null)
            listener.onTemperatureModeChanged(currentState);
    }

    public synchronized void removeListener(ITemperatureModeListener listener){
        listenerList.remove(listener);
    }

    @Override
    public synchronized void onTemperatureModeChanged(TemperatureMode mode) {
        if (withState)
            currentState = mode;
        listenerList.forEach(listener -> listener.onTemperatureModeChanged(mode));
    }

    public TemperatureMode getCurrentState() {
        return currentState;
    }
}
