package org.thermocutie.thermostat.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thermocutie.thermostat.device.DeviceSet;
import org.thermocutie.thermostat.device.HVAC;
import org.thermocutie.thermostat.device.TemperatureSensor;
import org.thermocutie.thermostat.listener.ITemperatureListener;
import org.thermocutie.thermostat.listener.ITemperatureModeListener;
import org.thermocutie.thermostat.listener.composite.TemperatureCompositeListener;
import org.thermocutie.thermostat.listener.composite.TemperatureModeCompositeListener;
import org.thermocutie.thermostat.schedule.ISchedule;
import org.thermocutie.thermostat.schedule.ScheduleSet;

import java.io.File;
import java.util.Collection;

/**
 * God object
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class ThermoSystem {
    private final static Logger LOGGER = LoggerFactory.getLogger(ThermoSystem.class);

    private File folder;
    private ThermoSystemSettings settings = new ThermoSystemSettings();
    private DeviceSet deviceSet = new DeviceSet();
    private TemperatureModeRegistry temperatureModeRegistry = new TemperatureModeRegistry();
    private TemperatureSensor defaultTemperatureSensor; // TODO: there could be several sensors producing current temperature

    private ScheduleSet scheduleSet = new ScheduleSet(temperatureModeRegistry);
    private ISchedule schedule;
    private Thermostat thermostat;
    private final TemperatureCompositeListener currentTemperatureListener = new TemperatureCompositeListener(true);
    private final TemperatureModeCompositeListener targetTemperatureModeListener = new TemperatureModeCompositeListener(true);
//    private final TemperatureCompositeListener targetTemperatureListener = new TemperatureCompositeListener(true);

    public void loadFromFolder(File folder) {
        this.folder = folder;

        settings.setFile(new File(folder, "settings.xml"));
        deviceSet.setFile(new File(folder, "devices.xml"));
        temperatureModeRegistry.setFile(new File(folder, "temperaturemodes.xml"));
        scheduleSet.setFile(new File(folder, "schedules.xml"));

        try {
            settings.loadFromFile();
            deviceSet.loadFromFile();
            temperatureModeRegistry.loadFromFile();
            scheduleSet.loadFromFile();
        } catch (Exception e) {
            LOGGER.error("Thermo system loading error", e);
        }
    }

    public void start() {
        Collection<TemperatureSensor> temperatureSensors = deviceSet.getDevices(TemperatureSensor.class);
        if (temperatureSensors.size() == 1) {
            TemperatureSensor temperatureSensor = temperatureSensors.iterator().next();
            setDefaultTemperatureSensor(temperatureSensor);
        } else {
            LOGGER.warn("Thermo system should have exactly one temperature sensor");
        }

        setThermostat(new Thermostat());
        setSchedule(scheduleSet.getSchedule("default"));
    }

    public ThermoSystemSettings getSettings() {
        return settings;
    }

    public DeviceSet getDeviceSet() {
        return deviceSet;
    }

    public TemperatureModeRegistry getTemperatureModeRegistry() {
        return temperatureModeRegistry;
    }

    public ScheduleSet getScheduleSet() {
        return scheduleSet;
    }

    public void setSchedule(ISchedule schedule) {
        if (this.schedule != null)
            this.schedule.removeTargetTemperatureModeListener(targetTemperatureModeListener);
        schedule.addTargetTemperatureModeListener(targetTemperatureModeListener);
        this.schedule = schedule;
    }

    public void updateSchedule(ISchedule schedule) {
        schedule.setTemperatureModeRegistry(temperatureModeRegistry); // to have correct references to temperature modes

        scheduleSet.updateSchedule(schedule);
        scheduleSet.saveToFile();

        if (schedule.getName().equals(this.schedule.getName())) { // active schedule updated
            setSchedule(schedule);
        }
    }

    public void setDefaultTemperatureSensor(TemperatureSensor defaultTemperatureSensor) {
        this.defaultTemperatureSensor = defaultTemperatureSensor;
        defaultTemperatureSensor.addListener(currentTemperatureListener); // TODO: current is not always default
    }

    public void setThermostat(Thermostat thermostat) {
        this.thermostat = thermostat;

        Collection<HVAC> hvacs = deviceSet.getDevices(HVAC.class);
        if (!hvacs.isEmpty())
            thermostat.setHvac(hvacs.iterator().next());
        addCurrentTemperatureListener(thermostat::setCurrentTemperature);
        addTargetTemperatureModeListener(mode -> thermostat.setTargetTemperature(mode.getTemperature()));
    }

    public void addCurrentTemperatureListener(ITemperatureListener listener) {
        currentTemperatureListener.addListener(listener);
    }

    public void removeCurrentTemperatureListener(ITemperatureListener listener) {
        currentTemperatureListener.removeListener(listener);
    }

    public void addTargetTemperatureModeListener(ITemperatureModeListener listener) {
        targetTemperatureModeListener.addListener(listener);
    }

    public void removeTargetTemperatureModeListener(ITemperatureModeListener listener) {
        targetTemperatureModeListener.removeListener(listener);
    }

    public Collection<TemperatureMode> getTemperatureModes() {
        return temperatureModeRegistry.getModes();
    }

    public void addTemperatureMode(TemperatureMode mode) {
        temperatureModeRegistry.add(mode);
        temperatureModeRegistry.saveToFile();
    }

    public void updateTemperatureMode(TemperatureMode mode) {
        temperatureModeRegistry.update(mode);
        temperatureModeRegistry.saveToFile();

        if (targetTemperatureModeListener.getCurrentState().getName().equals(mode.getName())) {
            targetTemperatureModeListener.onTemperatureModeChanged(mode);
        }
    }

    public void renameTemperatureMode(String oldModeName, String newModeName) {
        temperatureModeRegistry.rename(oldModeName, newModeName);
        temperatureModeRegistry.saveToFile();

        scheduleSet.renameTemperatureMode(oldModeName, newModeName);

        if (targetTemperatureModeListener.getCurrentState().getName().equals(newModeName)) {  // current state already uses new name
            TemperatureMode mode = temperatureModeRegistry.getMode(newModeName);
            targetTemperatureModeListener.onTemperatureModeChanged(mode);
        }
    }

    public void removeTemperatureMode(String name) throws Exception {
        if (scheduleSet.isTemperatureModeUsed(name))
            throw new Exception("Mode is used and cannot be deleted");
        temperatureModeRegistry.remove(name);
        temperatureModeRegistry.saveToFile();
    }

//    public void addTargetTemperatureListener(ITemperatureListener listener) {
//        targetTemperatureListener.addListener(listener);
//    }
//
//    public void removeTargetTemperatureListener(ITemperatureListener listener) {
//        targetTemperatureListener.removeListener(listener);
//    }
}
