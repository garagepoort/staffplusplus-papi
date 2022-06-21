package net.shortninja.staffplus.papi;

import net.shortninja.staffplus.papi.providers.*;
import net.shortninja.staffplusplus.IStaffPlus;
import net.shortninja.staffplusplus.session.IPlayerSession;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Placeholders {

    public static final Map<String, BiFunction<String, IStaffPlus, String>> placeholders = new HashMap<>();

    static {
        placeholders.put("bans_all_count", (p, s) -> String.valueOf(s.getBanService().getTotalBanCount()));
        placeholders.put("bans_active_count", (p, s) -> String.valueOf(s.getBanService().getActiveBanCount()));
        placeholders.put("bans_newest", BanPlaceholderProviders.NEWEST_BANNED_PLAYERS);
        placeholders.put("bans_count", BanPlaceholderProviders.BAN_COUNT);

        placeholders.put("mutes_all_count", (p, s) -> String.valueOf(s.getMuteService().getTotalMuteCount()));
        placeholders.put("mutes_active_count", (p, s) -> String.valueOf(s.getMuteService().getActiveMuteCount()));
        placeholders.put("mutes_newest", MutePlaceholderProviders.NEWEST_MUTED_PLAYER);
        placeholders.put("mutes_count", MutePlaceholderProviders.MUTE_COUNT);

        placeholders.put("warnings_count", WarningPlaceholderProviders.WARN_COUNT);
        placeholders.put("warnings_newest", WarningPlaceholderProviders.NEWEST_WARNINGS);
        placeholders.put("warnings_score", WarningPlaceholderProviders.SCORE);

        placeholders.put("staff_members_online", (p, s) -> String.valueOf(s.getSessionManager().getOnlineStaffMembers().size()));
        placeholders.put("staff_members_in_mode", (p, s) -> String.valueOf(s.getSessionManager().getAll().stream().filter(IPlayerSession::isInStaffMode).count()));
        placeholders.put("staff_members_vanished", (p, s) -> String.valueOf(s.getSessionManager().getAll().stream().filter(IPlayerSession::isVanished).count()));

        placeholders.put("player_count", (p, s) -> {
            List<UUID> vanishedPlayers = s.getSessionManager().getAll().stream().filter(IPlayerSession::isVanished).map(IPlayerSession::getUuid).collect(Collectors.toList());
            return String.valueOf(Bukkit.getOnlinePlayers().stream().filter(onlinePlayer -> !vanishedPlayers.contains(onlinePlayer.getUniqueId())).count());
        });
        placeholders.put("session", SessionProviders.SESSION_VALUE);

        placeholders.put("reports_count", ReportPlaceholderProviders.REPORT_COUNT);
        placeholders.put("reports_newest", ReportPlaceholderProviders.NEWEST_REPORT_PLAYER);
    }
}
