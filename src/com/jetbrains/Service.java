package com.jetbrains;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks JBR API service.
 * Services are available via {@code JBR#is<NAME>Available()} and {@code JBR#get<NAME>()}.
 * Service interfaces must not be implemented by client code.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Service {
}
