package net.shortninja.staffplus.papi.providers;

import net.shortninja.staffplusplus.IStaffPlus;
import net.shortninja.staffplusplus.mute.IMute;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.BiFunction;

import static net.shortninja.staffplus.papi.common.ReflectionUtil.getMethodValue;

public class MutePlaceholderProviders {

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
}
