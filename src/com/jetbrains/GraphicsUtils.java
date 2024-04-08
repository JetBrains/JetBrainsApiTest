/*
 * Copyright 2023 JetBrains s.r.o.
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
import java.awt.geom.Rectangle2D;

/**
 * Graphics2D utilities.
 */
@Service
@Provided
public interface GraphicsUtils {

    /**
     * Constructs {@link Graphics2D} instance, delegating all calls
     * to given {@code graphics2D} and combining it with given
     * {@code constrainable} handler.
     * @param graphics2D Graphics2D delegate
     * @param constrainable ConstrainableGraphics2D handler
     * @return combined Graphics2D instance
     */
    Graphics2D createConstrainableGraphics(Graphics2D graphics2D,
                                           ConstrainableGraphics2D constrainable);

    /**
     * Allows to permanently install a rectangular maximum clip that
     * cannot be extended with setClip.
     * This is similar to {@code sun.awt.ConstrainableGraphics},
     * but allows floating-point coordinates.
     */
    @Provides
    public interface ConstrainableGraphics2D {
        /**
         * Destination that this Graphics renders to.
         * Similar to {@code sun.java2d.SunGraphics2D#getDestination()}.
         * @return rendering destination
         */
        Object getDestination();

        /**
         * Constrain this graphics object to have a permanent device space
         * origin of (x, y) and a permanent maximum clip of (x,y,w,h).
         * This overload allows floating-point coordinates.
         * @param region constraint rectangle
         * @see #constrain(int, int, int, int)
         */
        void constrain(Rectangle2D region);

        /**
         * Constrain this graphics object to have a permanent device space
         * origin of (x, y) and a permanent maximum clip of (x,y,w,h).
         * Similar to {@code sun.awt.ConstrainableGraphics#constrain(int, int, int, int)}.
         * @param x x coordinate of the constraint rectangle
         * @param y y coordinate of the constraint rectangle
         * @param w width of the constraint rectangle
         * @param h height of the constraint rectangle
         */
        void constrain(int x, int y, int w, int h);
    }
}
