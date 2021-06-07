package net.shortninja.staffplus.papi;

import net.shortninja.staffplusplus.IStaffPlus;
import net.shortninja.staffplusplus.mute.IMute;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiFunction;

public class MutePlaceholderProviders {

    static final BiFunction<String, IStaffPlus, String> NEWEST_MUTED_PLAYER = (s, iStaffPlus) -> {
        try {
            String withoutPrefix = s.replace("mutes_newest_", "");
            int index = Integer.parseInt(withoutPrefix.split("_")[0]);
            String  placeholderMethod = withoutPrefix.split("_")[1];

            List<? extends IMute> mutes = iStaffPlus.getMuteService().getAllPaged(0, index);
            if(index > mutes.size()) {
                return "";
            }

            IMute mute = mutes.get(index - 1);
            String methodName = "get" + placeholderMethod.substring(0, 1).toUpperCase() + placeholderMethod.substring(1);
            Method fieldGetter = mute.getClass().getMethod(methodName);

            return String.valueOf(fieldGetter.invoke(mute));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    };
}
