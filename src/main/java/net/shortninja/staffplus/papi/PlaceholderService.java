package net.shortninja.staffplus.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

public class PlaceholderService {

    public String setPlaceholders(OfflinePlayer sender, String message) {
        message = message
                .replaceAll("\\$\\{", "%")
                .replaceAll("\\}\\$", "%");
        return PlaceholderAPI.setPlaceholders(sender, message);
    }
}
