package net.shortninja.staffplus.papi.providers;

import net.shortninja.staffplus.papi.common.FilterUtil;
import net.shortninja.staffplusplus.IStaffPlus;
import net.shortninja.staffplusplus.session.IPlayerSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.BiFunction;

import static net.shortninja.staffplus.papi.common.ReflectionUtil.findMethodValue;

public class SessionProviders {

    public static final BiFunction<String, IStaffPlus, String> SESSION_VALUE = (placeholder, iStaffPlus) -> {
        try {
            String withoutPrefix = placeholder.replace("session_", "");
            String placeholderMethod = withoutPrefix.split("_")[0];

            Map<String, String> filters = FilterUtil.getFilters(placeholder);
            if(!filters.containsKey("player")) {
                return null;
            }
            String player = filters.get("player");
            Player playerExact = Bukkit.getPlayerExact(player);
            if(playerExact == null) {
                return null;
            }

            IPlayerSession iPlayerSession = iStaffPlus.getSessionManager().get(playerExact.getUniqueId());

            return findMethodValue(placeholderMethod, iPlayerSession);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    };
}
