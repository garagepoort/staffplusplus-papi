package net.shortninja.staffplus.papi;

import net.shortninja.staffplusplus.IStaffPlus;
import net.shortninja.staffplusplus.reports.ReportStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class ReportPlaceholderProvider implements BiFunction<String, IStaffPlus, String> {

    @Override
    public String apply(String placeholder, IStaffPlus iStaffPlus) {
        Map<String, String> filters = PlaceholderUtil.getFilters(placeholder);
        List<ReportStatus> status = Arrays.asList(ReportStatus.values());
        if(filters.containsKey("status")) {
            status = Arrays.stream(filters.get("status").split(";"))
                    .map(String::toUpperCase)
                    .map(ReportStatus::valueOf)
                    .collect(Collectors.toList());
        }
        Long timestamp = null;
        if(filters.containsKey("period")) {
            String period = filters.get("period");
            if(period.equalsIgnoreCase("month")) {
                timestamp = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
            }
        }

        return String.valueOf(iStaffPlus.getReportService().getReportCount(status, timestamp));
    }
}
