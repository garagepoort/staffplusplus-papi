package net.shortninja.staffplus.papi;

import net.shortninja.staffplusplus.IStaffPlus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Placeholders {

    public static final Map<String, BiFunction<String, IStaffPlus, String>> placeholders = new HashMap<>();

    static {
        placeholders.put("all_bans_count", (p, s) -> String.valueOf(s.getBanService().getTotalBanCount()));
        placeholders.put("active_bans_count", (p, s) -> String.valueOf(s.getBanService().getActiveBanCount()));
        placeholders.put("all_mutes_count", (p, s) -> String.valueOf(s.getMuteService().getTotalMuteCount()));
        placeholders.put("active_mutes_count", (p, s) -> String.valueOf(s.getMuteService().getActiveMuteCount()));
        placeholders.put("reports_count", new ReportPlaceholderProvider());
    }
}
