package com.jetbrains;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks types intended for inheritance by client code.
 * This annotation must always be used when interface is intended to be implemented, or class to be extended by client.
 * All JBR API client proxy types must be marked with this annotation, but those marked are not necessarily client proxies.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
@interface Client {
}
