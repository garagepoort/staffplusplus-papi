package net.shortninja.staffplus.papi.providers;

import net.shortninja.staffplus.papi.common.FilterUtil;
import net.shortninja.staffplusplus.IStaffPlus;
import net.shortninja.staffplusplus.warnings.IWarning;
import net.shortninja.staffplusplus.warnings.WarningFilters;
import net.shortninja.staffplusplus.warnings.WarningFilters.WarningFiltersBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static net.shortninja.staffplus.papi.common.FilterUtil.mapPeriodToTimestamp;
import static net.shortninja.staffplus.papi.common.ReflectionUtil.getMethodValue;

public class WarningPlaceholderProviders {

    public static final BiFunction<String, IStaffPlus, String> SCORE = (placeholder, iStaffPlus) -> {
        String playerName = placeholder.replace("warnings_score_", "");
        return String.valueOf(iStaffPlus.getWarningService().getTotalScore(playerName));
    };

    public static final BiFunction<String, IStaffPlus, String> WARN_COUNT = (placeholder, iStaffPlus) -> {
        Map<String, String> filters = FilterUtil.getFilters(placeholder);

        return String.valueOf(iStaffPlus.getWarningService().getWarnCount(getWarningFiltersBuilderFromParams(filters)));
    };

    public static final BiFunction<String, IStaffPlus, String> NEWEST_WARNINGS = (placeholder, iStaffPlus) -> {
        try {
            Map<String, String> filters = FilterUtil.getFilters(placeholder);

            String withoutPrefix = placeholder.replace("warnings_newest_", "");
            int index = Integer.parseInt(withoutPrefix.split("_")[0]);
            String placeholderMethod = withoutPrefix.split("_")[1];

            List<? extends IWarning> warnings = iStaffPlus.getWarningService().findWarnings(getWarningFiltersBuilderFromParams(filters), 0, index);
            if (index > warnings.size()) {
                return "";
            }

            return getMethodValue(placeholderMethod, warnings.get(index - 1));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    };

    private static WarningFilters getWarningFiltersBuilderFromParams(Map<String, String> filters) {
        WarningFiltersBuilder warningFiltersBuilder = new WarningFiltersBuilder();

        if (filters.containsKey("severity")) {
            List<String> severities = Arrays.stream(filters.get("severity").split(";")).collect(Collectors.toList());
            warningFiltersBuilder.anyOfSeverity(severities);
        }

        if (filters.containsKey("period")) {
            long timestamp = mapPeriodToTimestamp(filters.get("period"));
            warningFiltersBuilder.createdAfter(timestamp);
        }

        if (filters.containsKey("reason")) warningFiltersBuilder.reason(filters.get("reason"));
        if (filters.containsKey("expired")) warningFiltersBuilder.expired(Boolean.parseBoolean(filters.get("expired")));
        if (filters.containsKey("issuerName")) warningFiltersBuilder.warnerName(filters.get("issuerName"));
        if (filters.containsKey("server")) warningFiltersBuilder.server(filters.get("server"));
        if (filters.containsKey("targetName")) warningFiltersBuilder.culpritName(filters.get("targetName"));
        return warningFiltersBuilder.build();
    }

}
