package net.shortninja.staffplus.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.shortninja.staffplusplus.IStaffPlus;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static net.shortninja.staffplus.papi.Placeholders.placeholders;

public class StaffPlusPlusPapi extends PlaceholderExpansion implements Configurable {

    private final Map<String, String> placeholderCache = new ConcurrentHashMap<>();
    private Long nextUpdateTimestamp = System.currentTimeMillis();
    private IStaffPlus plugin;

    @Override
    public boolean canRegister() {
        if (!Bukkit.getPluginManager().isPluginEnabled(getRequiredPlugin())) {
            return false;
        }
        plugin = (IStaffPlus) Bukkit.getPluginManager().getPlugin(getRequiredPlugin());
        return plugin != null;
    }
    @Override
    public Map<String, Object> getDefaults() {
        final Map<String, Object> defaults = new HashMap<>();
        defaults.put("cache-clear-interval", 30000);
        defaults.put("cache-disabled-placeholders", Arrays.asList("session", "player_count"));
        return defaults;
    }
    @Override
    public String getRequiredPlugin() {
        return "StaffPlusPlus";
    }

    public String getIdentifier() {
        return "staffplusplus";
    }

    public String getAuthor() {
        return "Garagepoort";
    }

    public String getVersion() {
        return "1.5.0";
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String params) {
        if (params.equals("test")) {
            return "success";
        }
        if (offlinePlayer == null || !offlinePlayer.isOnline()) {
            return "player is not online";
        }

        Optional<String> key = placeholders.keySet().stream().filter(params::startsWith).findFirst();
        if (key.isPresent()) {
            List<String> disabledPlaceholders = this.getStringList("cache-disabled-placeholders");
            String finalParams = setPlaceholders(offlinePlayer, params);
            String result;
            if(disabledPlaceholders.contains(key.get())) {
                result = placeholders.get(key.get()).apply(finalParams, plugin);
            }else{
                result = placeholderCache.computeIfAbsent(params, s -> placeholders.get(key.get()).apply(finalParams, plugin));
                if(getDuration(nextUpdateTimestamp) == 0) {
                    placeholderCache.clear();
                    nextUpdateTimestamp = System.currentTimeMillis() + this.getLong("cache-clear-interval", 30000);
                }
            }
            return result;
        }
        return null;
    }

    private String setPlaceholders(OfflinePlayer sender, String message) {
        message = message
                .replaceAll("\\$\\{", "%")
                .replaceAll("\\}\\$", "%");
        return PlaceholderAPI.setPlaceholders(sender, message);
    }

    private long getDuration(long timestamp) {
        if (timestamp <= System.currentTimeMillis()) {
            return 0;
        }
        return Math.abs(System.currentTimeMillis() - timestamp);
    }
}
