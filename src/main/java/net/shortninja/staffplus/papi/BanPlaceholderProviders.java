package net.shortninja.staffplus.papi;

import net.shortninja.staffplusplus.IStaffPlus;
import net.shortninja.staffplusplus.ban.IBan;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiFunction;

public class BanPlaceholderProviders {

    static final BiFunction<String, IStaffPlus, String> NEWEST_BANNED_PLAYERS = (s, iStaffPlus) -> {
        try {
            String withoutPrefix = s.replace("bans_newest_", "");
            int index = Integer.parseInt(withoutPrefix.split("_")[0]);
            String  placeholderMethod = withoutPrefix.split("_")[1];

            List<? extends IBan> bans = iStaffPlus.getBanService().getAllPaged(0, index);
            if(index > bans.size()) {
                return "";
            }

            IBan ban = bans.get(index - 1);
            String methodName = "get" + placeholderMethod.substring(0, 1).toUpperCase() + placeholderMethod.substring(1);
            Method fieldGetter = ban.getClass().getMethod(methodName);

            return String.valueOf(fieldGetter.invoke(ban));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    };
}
