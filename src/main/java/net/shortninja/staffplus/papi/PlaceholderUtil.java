package net.shortninja.staffplus.papi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PlaceholderUtil {

    public static Map<String, String> getFilters(String placeholder) {
        Map<String, String> result = new HashMap<>();
        String[] split = placeholder.split("_@");
        if (split.length == 1) {
            return result;
        }

        String[] filters = Arrays.copyOfRange(split, 1, split.length);
        for (String filter : filters) {
            String[] filterSplit = filter.split("=");
            if (filterSplit.length != 2) {
                throw new StaffPlusPlusPapiException("Invalid filter provided in placeholder");
            }
            String key = filterSplit[0];
            String value = filterSplit[1];
            result.put(key, value);
        }
        return result;
    }

}
