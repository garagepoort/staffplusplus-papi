package net.shortninja.staffplus.papi;

import net.shortninja.staffplusplus.IStaffPlus;
import net.shortninja.staffplusplus.session.IPlayerSession;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Placeholders {

    public static final Map<String, BiFunction<String, IStaffPlus, String>> placeholders = new HashMap<>();

    static {
        placeholders.put("bans_all_count", (p, s) -> String.valueOf(s.getBanService().getTotalBanCount()));
        placeholders.put("bans_active_count", (p, s) -> String.valueOf(s.getBanService().getActiveBanCount()));
        placeholders.put("bans_last", BanPlaceholderProviders.LAST_BANNED_PLAYERS);

        placeholders.put("mutes_all_count", (p, s) -> String.valueOf(s.getMuteService().getTotalMuteCount()));
        placeholders.put("mutes_active_count", (p, s) -> String.valueOf(s.getMuteService().getActiveMuteCount()));
        placeholders.put("mutes_last", MutePlaceholderProviders.LAST_MUTED_PLAYER);

        placeholders.put("warnings_count", WarningPlaceholderProviders.WARN_COUNT);
        placeholders.put("warnings_last", WarningPlaceholderProviders.LAST_WARNINGS);
        placeholders.put("warnings_score", WarningPlaceholderProviders.SCORE);

        placeholders.put("staff_members_online", (p, s) -> String.valueOf(s.getSessionManager().getOnlineStaffMembers().size()));
        placeholders.put("staff_members_in_mode", (p, s) -> String.valueOf(s.getSessionManager().getAll().stream().filter(IPlayerSession::isInStaffMode).count()));

        placeholders.put("reports_count", ReportPlaceholderProviders.REPORT_COUNT);
        placeholders.put("reports_last", ReportPlaceholderProviders.LAST_REPORT_PLAYER);
    }
}
