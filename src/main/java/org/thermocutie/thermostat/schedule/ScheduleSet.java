package org.thermocutie.thermostat.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thermocutie.thermostat.core.TemperatureModeRegistry;
import org.thermocutie.thermostat.xml.IXmlFilePersistableHelper;
import org.w3c.dom.Element;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Registry of {@link ISchedule}
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class ScheduleSet implements IXmlFilePersistableHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleSet.class);

    private final TemperatureModeRegistry temperatureModeRegistry;
    private Map<String, ISchedule> scheduleMap = new HashMap<>();
    private File file;

    public ScheduleSet(TemperatureModeRegistry temperatureModeRegistry) {
        this.temperatureModeRegistry = temperatureModeRegistry;
    }

    public synchronized ISchedule getSchedule(String name) {
        return scheduleMap.get(name);
    }

    public synchronized void updateSchedule(ISchedule schedule) {
        scheduleMap.put(schedule.getName(), schedule);
    }

    @Override
    public String getRootTag() {
        return "Schedules";
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
    public void loadFromXml(Element element) {
        childElements(element, "Schedule").forEach(scheduleElement -> {
            String type = getAttribute(scheduleElement, "type");
            if (Objects.equals(type, "week")) {
                ISchedule schedule = new WeekSchedule(temperatureModeRegistry);
                schedule.loadFromXml(scheduleElement);
                scheduleMap.put(schedule.getName(), schedule);
            } else {
                LOGGER.error("Schedule type {} is not known", type);
            }
        });
    }

    @Override
    public void saveToXml(Element element) {
        saveChildren(element, "Schedule", scheduleMap.values());
    }

    public synchronized boolean isTemperatureModeUsed(String name) {
        for (ISchedule schedule : scheduleMap.values()) {
            if (schedule.isTemperatureModeUsed(name))
                return true;
        }
        return false;
    }

    public synchronized void renameTemperatureMode(String oldModeName, String newModeName) {
        for (ISchedule schedule : scheduleMap.values())
            schedule.renameTemperatureMode(oldModeName, newModeName);
    }
}
