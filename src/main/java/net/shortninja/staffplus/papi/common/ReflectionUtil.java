package net.shortninja.staffplus.papi.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtil {

    public static String getMethodValue(String placeholderMethod, Object object) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String methodName = "get" + placeholderMethod.substring(0, 1).toUpperCase() + placeholderMethod.substring(1);
        Method fieldGetter = object.getClass().getMethod(methodName);
        return String.valueOf(fieldGetter.invoke(object));
    }

}
