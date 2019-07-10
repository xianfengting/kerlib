package com.src_resources.kerlib.asmtest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JavaExecutor {
    static void callExampleMethod(Method m) throws InvocationTargetException, IllegalAccessException {
        m.invoke(null, new Object[] {null});
    }
    static void callInvokeMethod(Method m) throws InvocationTargetException, IllegalAccessException {
        m.invoke(null, null);
    }
}
