package com.jetbrains;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Specifies fallback implementation for the {@link Service @Service}.
 * Fallback implementation class must be inherited from the service and have a no-arg constructor.
 */
@Target(TYPE)
@Retention(CLASS)
@interface Fallback {

    /**
     * Fallback implementation class for the service.
     * @return fallback implementation class for the service.
     */
    Class<?> value();
}
