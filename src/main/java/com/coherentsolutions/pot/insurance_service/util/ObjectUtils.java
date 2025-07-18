package com.coherentsolutions.pot.insurance_service.util;

import java.util.function.Consumer;

public class ObjectUtils {

    private ObjectUtils() {
        
    }

    public static <T> void setIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}