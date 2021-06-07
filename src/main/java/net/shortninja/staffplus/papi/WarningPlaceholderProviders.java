package net.shortninja.staffplus.papi;

import net.shortninja.staffplusplus.IStaffPlus;
import net.shortninja.staffplusplus.warnings.IWarning;
import net.shortninja.staffplusplus.warnings.WarningFilters;
import net.shortninja.staffplusplus.warnings.WarningFilters.WarningFiltersBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class WarningPlaceholderProviders {

    public static final BiFunction<String, IStaffPlus, String> SCORE = (placeholder, iStaffPlus) -> {
        Map<String, String> filters = PlaceholderUtil.getFilters(placeholder);
        if(!filters.containsKey("player")) {
            return "";
        }

        return String.valueOf(iStaffPlus.getWarningService().getTotalScore(filters.get("player")));
    };

    static final BiFunction<String, IStaffPlus, String> WARN_COUNT = (placeholder, iStaffPlus) -> {
        Map<String, String> filters = PlaceholderUtil.getFilters(placeholder);

        return String.valueOf(iStaffPlus.getWarningService().getWarnCount(getWarningFiltersBuilderFromParams(filters)));
    };

    static final BiFunction<String, IStaffPlus, String> NEWEST_WARNINGS = (placeholder, iStaffPlus) -> {
        try {
            Map<String, String> filters = PlaceholderUtil.getFilters(placeholder);

            String withoutPrefix = placeholder.replace("warnings_newest_", "");
            int index = Integer.parseInt(withoutPrefix.split("_")[0]);
            String placeholderMethod = withoutPrefix.split("_")[1];

            List<? extends IWarning> warnings = iStaffPlus.getWarningService().findWarnings(getWarningFiltersBuilderFromParams(filters), 0, index);
            if (index > warnings.size()) {
                return "";
            }

            IWarning warning = warnings.get(index - 1);
            String methodName = "get" + placeholderMethod.substring(0, 1).toUpperCase() + placeholderMethod.substring(1);
            Method fieldGetter = warning.getClass().getMethod(methodName);

            return String.valueOf(fieldGetter.invoke(warning));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    };

    static WarningFilters getWarningFiltersBuilderFromParams(Map<String, String> filters) {
        WarningFiltersBuilder warningFiltersBuilder = new WarningFiltersBuilder();

        if (filters.containsKey("severity")) {
            List<String> severities = Arrays.stream(filters.get("severity").split(";")).collect(Collectors.toList());
            warningFiltersBuilder.anyOfSeverity(severities);
        }

        Long timestamp = null;
        if (filters.containsKey("period")) {
            String period = filters.get("period");
            if (period.equalsIgnoreCase("month")) {
                timestamp = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
            } else if (period.equalsIgnoreCase("week")) {
                timestamp = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
            } else if (period.equalsIgnoreCase("year")) {
                timestamp = LocalDateTime.now().with(TemporalAdjusters.firstDayOfYear()).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
            } else if (period.equalsIgnoreCase("day")) {
                timestamp = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
            }
            warningFiltersBuilder.createdAfter(timestamp);
        }

        if (filters.containsKey("reason")) warningFiltersBuilder.reason(filters.get("reason"));
        if (filters.containsKey("expired")) warningFiltersBuilder.expired(Boolean.parseBoolean(filters.get("expired")));
        if (filters.containsKey("issuer")) warningFiltersBuilder.warnerName(filters.get("issuer"));
        if (filters.containsKey("server")) warningFiltersBuilder.server(filters.get("server"));
        if (filters.containsKey("culprit")) warningFiltersBuilder.culpritName(filters.get("culprit"));
        return warningFiltersBuilder.build();
    }
}
