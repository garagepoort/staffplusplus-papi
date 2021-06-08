package net.shortninja.staffplus.papi.providers;

import net.shortninja.staffplus.papi.common.FilterUtil;
import net.shortninja.staffplusplus.IStaffPlus;
import net.shortninja.staffplusplus.reports.IReport;
import net.shortninja.staffplusplus.reports.ReportFilters;
import net.shortninja.staffplusplus.reports.ReportStatus;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static net.shortninja.staffplus.papi.common.FilterUtil.mapPeriodToTimestamp;
import static net.shortninja.staffplus.papi.common.ReflectionUtil.getMethodValue;

public class ReportPlaceholderProviders {

    public static final BiFunction<String, IStaffPlus, String> REPORT_COUNT = (placeholder, iStaffPlus) -> {
        Map<String, String> filters = FilterUtil.getFilters(placeholder);

        return String.valueOf(iStaffPlus.getReportService().getReportCount(getReportFiltersBuilderFromParams(filters)));
    };

    public static final BiFunction<String, IStaffPlus, String> NEWEST_REPORT_PLAYER = (placeholder, iStaffPlus) -> {
        try {
            Map<String, String> filters = FilterUtil.getFilters(placeholder);

            String withoutPrefix = placeholder.replace("reports_newest_", "");
            int index = Integer.parseInt(withoutPrefix.split("_")[0]);
            String  placeholderMethod = withoutPrefix.split("_")[1];

            List<? extends IReport> reports = iStaffPlus.getReportService().findReports(getReportFiltersBuilderFromParams(filters), 0, index);
            if(index > reports.size()) {
                return "";
            }

            return getMethodValue(placeholderMethod, reports.get(index - 1));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    };

    private static ReportFilters getReportFiltersBuilderFromParams(Map<String, String> filters) {
        ReportFilters.ReportFiltersBuilder reportFiltersBuilder = new ReportFilters.ReportFiltersBuilder();

        List<ReportStatus> status = Arrays.asList(ReportStatus.values());
        if (filters.containsKey("status")) {
            status = Arrays.stream(filters.get("status").split(";"))
                    .map(String::toUpperCase)
                    .map(ReportStatus::valueOf)
                    .collect(Collectors.toList());
        }
        reportFiltersBuilder.anyOfReportStatus(status);

        if (filters.containsKey("period")) {
            long timestamp = mapPeriodToTimestamp(filters.get("period"));
            reportFiltersBuilder.createdAfter(timestamp);
        }

        if (filters.containsKey("type")) reportFiltersBuilder.type(filters.get("type"));
        if (filters.containsKey("reporter")) reportFiltersBuilder.reportName(filters.get("reporter"));
        if (filters.containsKey("assignee")) reportFiltersBuilder.assigneeName(filters.get("assignee"));
        if (filters.containsKey("server")) reportFiltersBuilder.server(filters.get("server"));
        if (filters.containsKey("culprit")) reportFiltersBuilder.culpritName(filters.get("culprit"));
        return reportFiltersBuilder.build();
    }
}
