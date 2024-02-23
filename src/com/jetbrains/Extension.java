package com.jetbrains;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks JBR API extension method. Extension methods are optional,
 * meaning that service would still be considered supported even if
 * a method reachable from it has no implementation.
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface Extension {

    /**
     * Returns the extension marked method corresponds to.
     * @return the extension marked method corresponds to
     */
    Extensions value();
}
