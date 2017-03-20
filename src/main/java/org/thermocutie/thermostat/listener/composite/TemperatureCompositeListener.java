package org.thermocutie.thermostat.listener.composite;

import org.thermocutie.thermostat.listener.ITemperatureListener;
import org.thermocutie.thermostat.model.Temperature;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite implementation of {@link ITemperatureListener}
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class TemperatureCompositeListener implements ITemperatureListener {
    private boolean withState;
    private List<ITemperatureListener> listenerList = new ArrayList<>();
    private Temperature currentState;

    public TemperatureCompositeListener() {
    }

    public TemperatureCompositeListener(boolean withState) {
        this.withState = withState;
    }

    public synchronized int size() {
        return listenerList.size();
    }

    public synchronized void addListener(ITemperatureListener listener) {
        listenerList.add(listener);
        if (withState && currentState != null)
            listener.onTemperatureChanged(currentState);
    }

    public synchronized void removeListener(ITemperatureListener listener){
        listenerList.remove(listener);
    }

    @Override
    public synchronized void onTemperatureChanged(Temperature temperature) {
        if (withState)
            currentState = temperature;
        listenerList.forEach(listener -> listener.onTemperatureChanged(temperature));
    }

    public Temperature getCurrentState() {
        return currentState;
    }
}
