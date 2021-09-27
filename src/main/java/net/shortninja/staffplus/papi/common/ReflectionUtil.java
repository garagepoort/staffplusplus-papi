package net.shortninja.staffplus.papi.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class ReflectionUtil {

    public static String getMethodValue(String placeholderMethod, Object object) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String methodName = "get" + placeholderMethod.substring(0, 1).toUpperCase() + placeholderMethod.substring(1);
        Method fieldGetter = object.getClass().getMethod(methodName);
        return String.valueOf(fieldGetter.invoke(object));
    }

    public static String findMethodValue(String placeholderMethod, Object object, String defaultValue) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        for (Method method : object.getClass().getMethods()) {
            if (method.getName().toLowerCase().contains(placeholderMethod.toLowerCase()) && (method.getName().startsWith("is") || method.getName().startsWith("get"))) {
                if (method.getReturnType() == Optional.class) {
                    Optional optional = (Optional) method.invoke(object);
                    return String.valueOf(optional.orElse(defaultValue));
                } else {
                    return String.valueOf(method.invoke(object));
                }
            }
        }
        return defaultValue;
    }

}
