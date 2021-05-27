package net.shortninja.staffplus.papi;

import net.shortninja.staffplusplus.IStaffPlus;
import net.shortninja.staffplusplus.reports.ReportFilters.ReportFiltersBuilder;
import net.shortninja.staffplusplus.reports.ReportStatus;

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

public class ReportPlaceholderProvider implements BiFunction<String, IStaffPlus, String> {

    @Override
    public String apply(String placeholder, IStaffPlus iStaffPlus) {
        Map<String, String> filters = PlaceholderUtil.getFilters(placeholder);
        ReportFiltersBuilder reportFiltersBuilder = new ReportFiltersBuilder();

        List<ReportStatus> status = Arrays.asList(ReportStatus.values());
        if (filters.containsKey("status")) {
            status = Arrays.stream(filters.get("status").split(";"))
                    .map(String::toUpperCase)
                    .map(ReportStatus::valueOf)
                    .collect(Collectors.toList());
        }
        reportFiltersBuilder.anyOfReportStatus(status);

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
            reportFiltersBuilder.createdAfter(timestamp);
        }

        if (filters.containsKey("type")) reportFiltersBuilder.type(filters.get("type"));
        if (filters.containsKey("reporter")) reportFiltersBuilder.reportName(filters.get("reporter"));
        if (filters.containsKey("assignee")) reportFiltersBuilder.assigneeName(filters.get("assignee"));
        if (filters.containsKey("server")) reportFiltersBuilder.server(filters.get("server"));
        if (filters.containsKey("culprit")) reportFiltersBuilder.culpritName(filters.get("culprit"));

        return String.valueOf(iStaffPlus.getReportService().getReportCount(reportFiltersBuilder.build()));
    }
}
