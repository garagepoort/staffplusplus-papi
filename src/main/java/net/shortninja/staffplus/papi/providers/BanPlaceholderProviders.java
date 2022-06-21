package net.shortninja.staffplus.papi.providers;

import net.shortninja.staffplus.papi.common.FilterUtil;
import net.shortninja.staffplusplus.IStaffPlus;
import net.shortninja.staffplusplus.ban.BanFilters;
import net.shortninja.staffplusplus.ban.IBan;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static net.shortninja.staffplus.papi.common.FilterUtil.mapPeriodToTimestamp;
import static net.shortninja.staffplus.papi.common.ReflectionUtil.getMethodValue;

public class BanPlaceholderProviders {

    public static final BiFunction<String, IStaffPlus, String> BAN_COUNT = (placeholder, iStaffPlus) -> {
        Map<String, String> filters = FilterUtil.getFilters(placeholder);

        return String.valueOf(iStaffPlus.getBanService().getBanCount(getBanFiltersBuilderFromParams(filters)));
    };

    public static final BiFunction<String, IStaffPlus, String> NEWEST_BANNED_PLAYERS = (s, iStaffPlus) -> {
        try {
            String withoutPrefix = s.replace("bans_newest_", "");
            int index = Integer.parseInt(withoutPrefix.split("_")[0]);
            String  placeholderMethod = withoutPrefix.split("_")[1];

            List<? extends IBan> bans = iStaffPlus.getBanService().getAllPaged(0, index);
            if(index > bans.size()) {
                return "";
            }

            return getMethodValue(placeholderMethod, bans.get(index - 1));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    };

    private static BanFilters getBanFiltersBuilderFromParams(Map<String, String> filters) {
        BanFilters.BanFiltersBuilder banFiltersBuilder = new BanFilters.BanFiltersBuilder();

        if (filters.containsKey("period")) {
            long timestamp = mapPeriodToTimestamp(filters.get("period"));
            banFiltersBuilder.createdAfter(timestamp);
        }

        if (filters.containsKey("active")) {
            if(filters.get("active").equalsIgnoreCase("true")) {
                banFiltersBuilder.active();
            }else{
                banFiltersBuilder.notActive();
            }
        }
        if (filters.containsKey("issuer")) banFiltersBuilder.issuerName(filters.get("issuer"));
        if (filters.containsKey("target")) banFiltersBuilder.targetName(filters.get("target"));
        if (filters.containsKey("server")) banFiltersBuilder.server(filters.get("server"));
        return banFiltersBuilder.build();
    }
}
