package com.hxl.hermes.core.utils;

import java.lang.reflect.Method;

/**
 * --
 * <p>
 * Created by hxl on 2018/8/28 0028.
 */
public class TypeUtils {

    public static String getMethodId(Method method){
        StringBuffer result = new StringBuffer(method.getName());
        result.append("(").append(getMethodParameters(method.getParameterTypes())).append(")");
        return result.toString();
    }

    private static String getMethodParameters(Class<?>[] classes){
        StringBuffer result = new StringBuffer();
        int length = classes.length;
        if (length == 0){
            return result.toString();
        }
        result.append(getClassName(classes[0]));
        for (int i = 1; i < length; ++i) {
            result.append(",").append(getClassName(classes[i]));
        }
        return result.toString();
    }

    //boolean, byte, char, short, int, long, float, and double void
    private static String getClassName(Class<?> clazz) {
        if (clazz == Boolean.class) {
            return "boolean";
        } else if (clazz == Byte.class) {
            return "byte";
        } else if (clazz == Character.class) {
            return "char";
        } else if (clazz == Short.class) {
            return "short";
        } else if (clazz == Integer.class) {
            return "int";
        } else if (clazz == Long.class) {
            return "long";
        } else if (clazz == Float.class) {
            return "float";
        } else if (clazz == Double.class) {
            return "double";
        } else if (clazz == Void.class) {
            return "void";
        } else {
            return clazz.getName();
        }
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes ){
        Method result = null;
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) && classAssignable(method.getParameterTypes(), parameterTypes)){
                result = method;
            }
        }
        return result;
    }

    public static boolean classAssignable(Class<?>[] classes1, Class<?>[] classes2) {
        if (classes1.length != classes2.length) {
            return false;
        }
        int length = classes2.length;
        for (int i = 0; i < length; ++i) {
            if (classes2[i] == null) {
                continue;
            }
            if (primitiveMatch(classes1[i], classes2[i])) {
                continue;
            }
            if (!classes1[i].isAssignableFrom(classes2[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean primitiveMatch(Class<?> class1, Class<?> class2) {
        if (!class1.isPrimitive() && !class2.isPrimitive()) {
            return false;
        } else if (class1 == class2) {
            return true;
        } else if (class1.isPrimitive()) {
            return primitiveMatch(class2, class1);
            //class2 is primitive
            //boolean, byte, char, short, int, long, float, and double void
        } else if (class1 == Boolean.class && class2 == boolean.class) {
            return true;
        } else if (class1 == Byte.class && class2 == byte.class) {
            return true;
        } else if (class1 == Character.class && class2 == char.class) {
            return true;
        } else if (class1 == Short.class && class2 == short.class) {
            return true;
        } else if (class1 == Integer.class && class2 == int.class) {
            return true;
        } else if (class1 == Long.class && class2 == long.class) {
            return true;
        } else if (class1 == Float.class && class2 == float.class) {
            return true;
        } else if (class1 == Double.class && class2 == double.class) {
            return true;
        } else if (class1 == Void.class && class2 == void.class) {
            return true;
        } else {
            return false;
        }
    }
}
