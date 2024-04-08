/*
 * Copyright 2022 JetBrains s.r.o.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.jetbrains;

import java.awt.*;
import java.util.function.Supplier;

/**
 * {@link java.awt.GraphicsEnvironment}-related utilities.
 */
@Service
@Provided
public interface ProjectorUtils {

    /**
     * Override {@link GraphicsEnvironment#getLocalGraphicsEnvironment()}
     * with custom provider. Provider is called only once, returned value is cached.
     * Calling this method after {@link GraphicsEnvironment} initialization (e.g.
     * after {@link GraphicsEnvironment#getLocalGraphicsEnvironment()} was called)
     * will have no effect.
     * @param geProvider GraphicsEnvironment provider
     */
    void setLocalGraphicsEnvironmentProvider(Supplier<GraphicsEnvironment> geProvider);

    /**
     * Override {@link GraphicsEnvironment#getLocalGraphicsEnvironment()}
     * with custom instance.
     * Calling this method after {@link GraphicsEnvironment} initialization (e.g.
     * after {@link GraphicsEnvironment#getLocalGraphicsEnvironment()} was called)
     * will have no effect.
     * @param overriddenGE new GraphicsEnvironment
     */
    void overrideGraphicsEnvironment(GraphicsEnvironment overriddenGE);
}
