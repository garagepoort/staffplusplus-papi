package net.shortninja.staffplus.papi.common;

import net.shortninja.staffplus.papi.StaffPlusPlusPapiException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FilterUtil {

    public static Map<String, String> getFilters(String placeholder) {
        Map<String, String> result = new HashMap<>();
        String[] split = placeholder.split("_@");
        if (split.length == 1) {
            return result;
        }

        String[] filters = Arrays.copyOfRange(split, 1, split.length);
        for (String filter : filters) {
            String[] filterSplit = filter.split("=");
            if (filterSplit.length != 2) {
                throw new StaffPlusPlusPapiException("Invalid filter provided in placeholder");
            }
            String key = filterSplit[0];
            String value = filterSplit[1];
            result.put(key, value);
        }
        return result;
    }

    public static long mapPeriodToTimestamp(String period) {
        if (period.equalsIgnoreCase("month")) {
            return LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
        }
        if (period.equalsIgnoreCase("week")) {
            return LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
        }
        if (period.equalsIgnoreCase("year")) {
            return LocalDateTime.now().with(TemporalAdjusters.firstDayOfYear()).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
        }
        if (period.equalsIgnoreCase("day")) {
            return LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
        }
        return 0;
    }

}
