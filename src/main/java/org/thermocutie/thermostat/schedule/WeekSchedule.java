package org.thermocutie.thermostat.schedule;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.SimpleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thermocutie.thermostat.core.TemperatureMode;
import org.thermocutie.thermostat.core.TemperatureModeRegistry;
import org.thermocutie.thermostat.listener.ITemperatureModeListener;
import org.thermocutie.thermostat.listener.composite.TemperatureModeCompositeListener;
import org.thermocutie.thermostat.model.Temperature;
import org.thermocutie.thermostat.util.StringConverterUtil;
import org.thermocutie.thermostat.xml.IXmlPersistable;
import org.w3c.dom.Element;

import java.io.IOException;
import java.time.*;
import java.util.*;

/**
 * Week schedule
 * Has different schedule for days of week
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class WeekSchedule implements ISchedule {
    private final static Logger LOGGER = LoggerFactory.getLogger(WeekSchedule.class);

    private TemperatureModeRegistry temperatureModeRegistry;
    private Timer timer;

    private String name;
    private List<DaySet> daySets = new ArrayList<>();
    private TemperatureModeCompositeListener targetTemperatureModeListener = new TemperatureModeCompositeListener(true);

    public WeekSchedule(TemperatureModeRegistry temperatureModeRegistry) {
        this.temperatureModeRegistry = temperatureModeRegistry;
    }

    public WeekSchedule() {
    }

    @Override
    public void setTemperatureModeRegistry(TemperatureModeRegistry temperatureModeRegistry) {
        this.temperatureModeRegistry = temperatureModeRegistry;
    }

    @Override
    public synchronized void addTargetTemperatureModeListener(ITemperatureModeListener listener) {
        if (targetTemperatureModeListener.size() == 0) {
            startCalculation();
        }
        targetTemperatureModeListener.addListener(listener);
    }

    @Override
    public synchronized void removeTargetTemperatureModeListener(ITemperatureModeListener listener) {
        targetTemperatureModeListener.removeListener(listener);
        if (targetTemperatureModeListener.size() == 0) {
            stopCalculation();
        }
    }

    @Override
    public synchronized boolean isTemperatureModeUsed(String name) {
        for (DaySet daySet : daySets)
            if (daySet.isTemperatureModeUsed(name))
                return true;
        return false;
    }

    @Override
    public synchronized void renameTemperatureMode(String oldModeName, String newModeName) {
        for (DaySet daySet : daySets)
            daySet.renameTemperatureMode(oldModeName, newModeName);
    }

    private void startCalculation() {
        timer = new Timer("Week schedule '" + name + "' timer", true);
        recalculate();

        LOGGER.info("Started calculation of '{}' week schedule", name);
    }

    private void stopCalculation() {
        timer.cancel();
        timer = null;

        LOGGER.info("Stopped calculation of '{}' week schedule", name);
    }

    private synchronized void recalculate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate date = now.toLocalDate();
        LocalTime time = now.toLocalTime();

        DaySet daySet = findDaySet(date);
        if (daySet == null)
            return;

        Period period = daySet.findPeriod(time);
        if (period == null) {
            LocalDate previousDate = date.minusDays(1);
            DaySet previousDaySet = findDaySet(previousDate);
            if (previousDaySet == null)
                return;
            period = previousDaySet.getLastPeriod();
        }

        // update listeners
        String modeName = period.getMode();
        TemperatureMode temperatureMode = temperatureModeRegistry.getMode(modeName);
        Temperature targetTemperature = temperatureMode.getTemperature();
        targetTemperatureModeListener.onTemperatureModeChanged(temperatureMode);
        LOGGER.info("Mode of '{}' week schedule set to '{}' (temperature {})", name, temperatureMode.getName(), targetTemperature.getValue());

        // find next period (could be at next day)
        Period nextPeriod = daySet.findNextPeriod(time);
        if (nextPeriod == null) {
            date = date.plusDays(1);
            daySet = findDaySet(date);
            if (daySet == null)
                return;
            nextPeriod = daySet.getFirstPeriod();
        }

        // schedule next calculation
        LocalDateTime nextRecalculationTime = LocalDateTime.of(date, nextPeriod.getStart());
        Instant nextRecalculationTimeInstant = nextRecalculationTime.atZone(ZoneId.systemDefault())
                .toInstant();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                recalculate();
            }
        }, Date.from(nextRecalculationTimeInstant));
    }

    //@CheckForNull
    private DaySet findDaySet(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        for (DaySet daySet : daySets) {
            if (daySet.contains(dayOfWeek)) {
                return daySet;
            }
        }
        LOGGER.error("Week schedule '{}' doesn't contain '{}' day of week", name, dayOfWeek);
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DaySet> getDaySets() {
        return daySets;
    }

    public void setDaySets(List<DaySet> daySets) {
        this.daySets = daySets;
    }

    @Override
    public void loadFromXml(Element element) {
        name = getAttribute(element, "name");
        loadChildren(element, "DaySet", daySets, el -> new DaySet(temperatureModeRegistry));
    }

    @Override
    public void saveToXml(Element element) {
        element.setAttribute("name", name);
        saveChildren(element, "DaySet", daySets);
    }

    public static class DaySet implements IXmlPersistable {
        private TemperatureModeRegistry temperatureModeRegistry;
        private String name;
        private Set<DayOfWeek> days;
        private List<Period> periods = new ArrayList<>();

        public DaySet(TemperatureModeRegistry temperatureModeRegistry) {
            this.temperatureModeRegistry = temperatureModeRegistry;
        }

        public DaySet() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @JsonSerialize(using = DayOfWeekSetSerializer.class)
        @JsonDeserialize(using = DayOfWeekSetDeserializer.class)
        public Set<DayOfWeek> getDays() {
            return days;
        }

        public void setDays(Set<DayOfWeek> days) {
            this.days = days;
        }

        public List<Period> getPeriods() {
            return periods;
        }

        public void setPeriods(List<Period> periods) {
            this.periods = periods;
        }

        @Override
        public void loadFromXml(Element element) {
            name = getAttribute(element, "name");
            days = StringConverterUtil.dayOfWeekSetFromString(getAttribute(element, "days"));
            loadChildren(element, "Period", periods, el -> new Period(temperatureModeRegistry));
        }

        @Override
        public void saveToXml(Element element) {
            element.setAttribute("name", name);
            element.setAttribute("days", StringConverterUtil.dayOfWeekSetToString(days));
            saveChildren(element, "Period", periods);
        }

        public boolean contains(DayOfWeek dayOfWeek) {
            return days.contains(dayOfWeek);
        }

        // returns null if time belongs to previous day
        public Period findPeriod(LocalTime time) {
            for (ListIterator<Period> iterator = periods.listIterator(periods.size()); iterator.hasPrevious(); ) {
                Period period = iterator.previous();
                if (!period.getStart().isAfter(time))
                    return period;
            }
            return null;
        }

        public Period findNextPeriod(LocalTime time) {
            Period prevPeriod = null;
            for (ListIterator<Period> iterator = periods.listIterator(periods.size()); iterator.hasPrevious(); ) {
                Period period = iterator.previous();
                if (!period.getStart().isAfter(time))
                    return prevPeriod;
                prevPeriod = period;
            }
            return prevPeriod;
        }

        @JsonIgnore
        public Period getFirstPeriod() {
            return periods.get(0);
        }

        @JsonIgnore
        public Period getLastPeriod() {
            return periods.get(periods.size() - 1);
        }

        public boolean isTemperatureModeUsed(String name) {
            for (Period period : periods)
                if (period.isTemperatureModeUsed(name))
                    return true;
            return false;
        }

        public void renameTemperatureMode(String oldModeName, String newModeName) {
            for (Period period : periods)
                period.renameTemperatureMode(oldModeName, newModeName);
        }
    }

    public static class Period implements IXmlPersistable {
        private TemperatureModeRegistry temperatureModeRegistry;
        private LocalTime start;
        private String mode;

        public Period(TemperatureModeRegistry temperatureModeRegistry) {
            this.temperatureModeRegistry = temperatureModeRegistry;
        }

        @SuppressWarnings("unused")
        public Period() {
        }

        @JsonSerialize(using = LocalTimeSerializer.class)
        @JsonDeserialize(using = LocalTimeDeserializer.class)
        public LocalTime getStart() {
            return start;
        }

        public void setStart(LocalTime start) {
            this.start = start;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        @Override
        public void loadFromXml(Element element) {
            this.start = StringConverterUtil.localTimeFromString(getAttribute(element, "start"));
            this.mode = getAttribute(element, "mode");
        }

        @Override
        public void saveToXml(Element element) {
            element.setAttribute("start", StringConverterUtil.localTimeToString(start));
            element.setAttribute("mode", mode);
        }

        public boolean isTemperatureModeUsed(String name) {
            return mode.equals(name);
        }

        public void renameTemperatureMode(String oldModeName, String newModeName) {
            if (mode.equals(oldModeName))
                mode = newModeName;
        }
    }

    public static class DayOfWeekSetSerializer extends SerializerBase<Set<DayOfWeek>> {
        public DayOfWeekSetSerializer() {
            super(CollectionType.construct(Set.class, SimpleType.construct(DayOfWeek.class)));
        }

        @Override
        public void serialize(Set<DayOfWeek> value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(StringConverterUtil.dayOfWeekSetToString(value));
        }
    }

    public static class DayOfWeekSetDeserializer extends JsonDeserializer<Set<DayOfWeek>> {
        @Override
        public Set<DayOfWeek> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return StringConverterUtil.dayOfWeekSetFromString(jp.getText());
        }
    }

    public static class LocalTimeSerializer extends SerializerBase<LocalTime> {
        public LocalTimeSerializer() {
            super(LocalTime.class);
        }

        @Override
        public void serialize(LocalTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(StringConverterUtil.localTimeToString(value));
        }
    }

    public static class LocalTimeDeserializer extends JsonDeserializer<LocalTime > {
        @Override
        public LocalTime  deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            return StringConverterUtil.localTimeFromString(jp.getText());
        }
    }

    /*
    public static class TemperatureModeSerializer extends SerializerBase<TemperatureMode> {
        public TemperatureModeSerializer() {
            super(TemperatureMode.class);
        }

        @Override
        public void serialize(TemperatureMode mode, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeString(mode.getName());
        }
    }

    public static class TemperatureModeDeserializer extends JsonDeserializer<TemperatureMode> {
        @Override
        public TemperatureMode deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            String name = jp.getText();
            TemperatureMode temperatureMode = new TemperatureMode(); // TODO: load from registry
            temperatureMode.setName(name);
            return temperatureMode;
        }
    }
    */
}
