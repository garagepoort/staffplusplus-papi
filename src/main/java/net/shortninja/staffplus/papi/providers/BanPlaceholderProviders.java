package net.shortninja.staffplus.papi.providers;

import net.shortninja.staffplusplus.IStaffPlus;
import net.shortninja.staffplusplus.ban.IBan;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.BiFunction;

import static net.shortninja.staffplus.papi.common.ReflectionUtil.getMethodValue;

public class BanPlaceholderProviders {

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
}
