package com.jetbrains;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks classes and interfaces whose implementation is provided by JBR API.
 * These types must not be inherited by client code unless explicitly marked with {@link Provides}.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Provided {
}
