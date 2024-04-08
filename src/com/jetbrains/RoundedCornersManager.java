/*
 * Copyright 2000-2023 JetBrains s.r.o.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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

import java.awt.Window;

/**
 * This manager allows decorate awt Window with rounded corners.
 * Appearance depends on operating system.
 */
@Service
@Provided
public interface RoundedCornersManager {
    /**
     * Setup rounded corners on window.
     *
     * Possible values for macOS:
     * <ul>
     *   <li>{@link Float} object with radius</li>
     *   <li>{@link Object} array with:<ul>
     *     <li>{@link Float} for radius</li>
     *     <li>{@link Integer} for border width</li>
     *     <li>{@link java.awt.Color} for border color</li>
     *   </ul></li>
     * </ul>
     *
     * Possible values for Windows 11 ({@link java.lang.String}):
     * <ul>
     *   <li>"default" - let the system decide whether or not to round window corners</li>
     *   <li>"none" - never round window corners</li>
     *   <li>"full" - round the corners if appropriate</li>
     *   <li>"small" - round the corners if appropriate, with a small radius</li>
     * </ul>
     *
     * @param window window to setup rounded corners on
     * @param params rounded corners hint
     */
    void setRoundedCorners(Window window, Object params);
}
