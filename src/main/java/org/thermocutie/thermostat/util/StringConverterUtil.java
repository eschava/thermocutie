package org.thermocutie.thermostat.util;

import java.awt.*;
import java.text.DateFormatSymbols;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

/**
 * Utility functions for converting objects to/from string
 * Is used for XML and JSON serialization
 *
 * @author Eugene Schava <eschava@gmail.com>
 */
public class StringConverterUtil {
    private static DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.US);
    private static List<String> shortWeekDays = Arrays.asList(dateFormatSymbols.getShortWeekdays());

    public static String colorToString(Color color) {
        return "#"+Integer.toHexString(color.getRGB()).substring(2);
    }

    public static Color colorFromString(String value) {
        return Color.decode(value);
    }

//    public static String minutesToString(long value) {
//        int startMinutes = (int) (value / 60_000);
//        int minutes = startMinutes % 60;
//        return startMinutes / 60 + ":" + (minutes > 10 ? minutes : "0" + minutes);
//    }
//
//    public static long minutesFromString(String value) {
//        String[] start = value.split(":", 2);
//        return (Integer.parseInt(start[0]) * 60 + Integer.parseInt(start[1])) * 60_000;
//    }

    public static String localTimeToString(LocalTime value) {
        return value.toString();
    }

    public static LocalTime localTimeFromString(String value) {
        return LocalTime.parse(value);
    }

    public static DayOfWeek dayOfWeekFromString(String value) {
        int index = shortWeekDays.indexOf(value);
        if (index < 0) index = 1;
        else {
            index--;
            if (index == 0) index = 7;
        }
        return DayOfWeek.of(index);
    }

    public static String dayOfWeekToString(DayOfWeek day) {
        int index = day.getValue() + 1;
        if (index > 7) index -= 7;
        return shortWeekDays.get(index);
    }

    public static String dayOfWeekSetToString(Set<DayOfWeek> set) {
        StringBuilder result = new StringBuilder();
        DayOfWeek intervalStartDay = null;
        DayOfWeek lastDay = null;

        for (DayOfWeek day : set) {
            if (lastDay == null || lastDay.getValue() < day.getValue() - 1) {
                if (lastDay != null) {
                    if (result.length() > 0)
                        result.append(",");
                    if (intervalStartDay != lastDay)
                        result.append(dayOfWeekToString(intervalStartDay)).append("-");
                    result.append(dayOfWeekToString(lastDay));
                }
                intervalStartDay = day;
            }

            lastDay = day;
        }

        if (result.length() > 0)
            result.append(",");
        if (intervalStartDay != lastDay)
            result.append(dayOfWeekToString(intervalStartDay)).append("-");
        result.append(dayOfWeekToString(lastDay));

        return result.toString();
    }

    public static Set<DayOfWeek> dayOfWeekSetFromString(String value) {
        EnumSet<DayOfWeek> result = EnumSet.noneOf(DayOfWeek.class);
        for (String part : value.split(",")) {
            String[] pair = part.split("-", 2);
            DayOfWeek startDay = dayOfWeekFromString(pair[0]);
            DayOfWeek endDay = pair.length > 1 ? dayOfWeekFromString(pair[1]) : startDay;
            result.addAll(EnumSet.range(startDay, endDay));
        }
        return result;
    }
}
