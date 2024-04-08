package com.jetbrains;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks classes and interfaces which provide their functionality to JBR API.
 * These types are usually intended to be inherited by client code (e.g. callbacks).
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Provides {
}
