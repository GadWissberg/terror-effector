package com.gadarts.te.common.utils;

import com.badlogic.gdx.utils.Disposable;

import java.lang.reflect.Field;
import java.util.Arrays;

public final class GeneralUtils {
    public static <T> void disposeObject(T instance, Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            if (Disposable.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    Object fieldValue = field.get(instance);
                    if (fieldValue instanceof Disposable) {
                        ((Disposable) fieldValue).dispose();
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
