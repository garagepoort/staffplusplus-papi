package net.shortninja.staffplus.papi.providers;

import net.shortninja.staffplus.papi.common.FilterUtil;
import net.shortninja.staffplusplus.IStaffPlus;
import net.shortninja.staffplusplus.mute.IMute;
import net.shortninja.staffplusplus.mute.MuteFilters;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static net.shortninja.staffplus.papi.common.FilterUtil.mapPeriodToTimestamp;
import static net.shortninja.staffplus.papi.common.ReflectionUtil.getMethodValue;

public class MutePlaceholderProviders {

    public static final BiFunction<String, IStaffPlus, String> MUTE_COUNT = (placeholder, iStaffPlus) -> {
        Map<String, String> filters = FilterUtil.getFilters(placeholder);

        return String.valueOf(iStaffPlus.getMuteService().getMuteCount(getMuteFiltersBuilderFromParams(filters)));
    };

    public static final BiFunction<String, IStaffPlus, String> NEWEST_MUTED_PLAYER = (s, iStaffPlus) -> {
        try {
            String withoutPrefix = s.replace("mutes_newest_", "");
            int index = Integer.parseInt(withoutPrefix.split("_")[0]);
            String  placeholderMethod = withoutPrefix.split("_")[1];

            List<? extends IMute> mutes = iStaffPlus.getMuteService().getAllPaged(0, index);
            if(index > mutes.size()) {
                return "";
            }

            return getMethodValue(placeholderMethod, mutes.get(index - 1));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    };

    private static MuteFilters getMuteFiltersBuilderFromParams(Map<String, String> filters) {
        MuteFilters.MuteFiltersBuilder muteFiltersBuilder = new MuteFilters.MuteFiltersBuilder();

        if (filters.containsKey("period")) {
            long timestamp = mapPeriodToTimestamp(filters.get("period"));
            muteFiltersBuilder.createdAfter(timestamp);
        }

        if (filters.containsKey("active")) {
            if(filters.get("active").equalsIgnoreCase("true")) {
                muteFiltersBuilder.active();
            }else{
                muteFiltersBuilder.notActive();
            }
        }
        if (filters.containsKey("issuer")) muteFiltersBuilder.issuerName(filters.get("issuer"));
        if (filters.containsKey("target")) muteFiltersBuilder.targetName(filters.get("target"));
        if (filters.containsKey("server")) muteFiltersBuilder.server(filters.get("server"));
        return muteFiltersBuilder.build();
    }
}
