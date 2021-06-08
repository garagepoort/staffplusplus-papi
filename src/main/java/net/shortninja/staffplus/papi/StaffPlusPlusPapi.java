package net.shortninja.staffplus.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.shortninja.staffplusplus.IStaffPlus;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.shortninja.staffplus.papi.Placeholders.placeholders;

public class StaffPlusPlusPapi extends PlaceholderExpansion {

    private final String version = getClass().getPackage().getImplementationVersion();

    private static final int UPDATE_INTERVAL = 30000;
    private Map<String, String> placeholderCache = new HashMap<>();
    private Long nextUpdateTimestamp = System.currentTimeMillis();


    // We get an instance of the plugin later.
    private IStaffPlus plugin;

    /**
     * Since this expansion requires api access to the plugin "SomePlugin"
     * we must check if said plugin is on the server or not.
     *
     * @return true or false depending on if the required plugin is installed.
     */
    @Override
    public boolean canRegister() {
        if (!Bukkit.getPluginManager().isPluginEnabled(getRequiredPlugin())) {
            return false;
        }
        plugin = (IStaffPlus) Bukkit.getPluginManager().getPlugin(getRequiredPlugin());
        return plugin != null;
    }


    /**
     * Returns the name of the required plugin.
     *
     * @return {@code DeluxeTags} as String
     */
    @Override
    public String getRequiredPlugin() {
        return "StaffPlus";
    }

    public @NotNull String getIdentifier() {
        return "staffplusplus";
    }

    public @NotNull String getAuthor() {
        return "Garagepoort";
    }

    public @NotNull String getVersion() {
        return version;
    }

    /**
     * This method is called when a placeholder is used and maches the set
     * {@link #getIdentifier() identifier}
     *
     * @param offlinePlayer The player to parse placeholders for
     * @param params        The part after the identifier ({@code %identifier_params%})
     * @return Possible-null String
     */
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
            String finalParams = setPlaceholders(offlinePlayer, params);
            String result = placeholderCache.computeIfAbsent(params, s -> placeholders.get(key.get()).apply(finalParams, plugin));
            if(getDuration(nextUpdateTimestamp) == 0) {
                placeholderCache.clear();
                nextUpdateTimestamp = System.currentTimeMillis() + UPDATE_INTERVAL;
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
