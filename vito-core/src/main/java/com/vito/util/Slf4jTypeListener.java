package com.vito.util;

import java.lang.reflect.Field;

import com.vito.framework.annotations.logging.InjectLogger;
import org.slf4j.Logger;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class Slf4jTypeListener implements TypeListener {

    public <I> void hear(TypeLiteral<I> aTypeLiteral, TypeEncounter<I> aTypeEncounter) {

        for (Field field : aTypeLiteral.getRawType().getDeclaredFields()) {
            if (field.getType() == Logger.class
                    && field.isAnnotationPresent(InjectLogger.class)) {
                aTypeEncounter.register(new Slf4jMembersInjector<I>(field));
            }
        }
    }
}
