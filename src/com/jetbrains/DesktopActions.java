/*
 * Copyright 2000-2022 JetBrains s.r.o.
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

import java.io.File;
import java.io.File;
import java.io.File;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URI;
import java.net.URI;

/**
 * Allows desktop actions, like opening a file, or webpage to be overridden.
 * @see java.awt.Desktop
 */
@Service
public interface DesktopActions {

    /**
     * Set global desktop action handler. All handler methods not
     * overridden explicitly are ignored and default behavior is triggered.
     * @param handler new action handler
     */
    void setHandler(Handler handler);

    /**
     * Desktop action handler.
     */
    @Client
    interface Handler {

        /**
         * Launches the associated application to open the file.
         * @param file the file to be opened with the associated application
         * @throws IOException if the specified file has no associated
         * application or the associated application fails to be launched
         * @see java.awt.Desktop#open(java.io.File)
         */
        default void open(File file) throws IOException { throw new UnsupportedOperationException(); }

        /**
         * Launches the associated editor application and opens a file for editing.
         * @param file the file to be opened for editing
         * @throws IOException if the specified file has no associated
         * editor, or the associated application fails to be launched
         * @see java.awt.Desktop#edit(java.io.File)
         */
        default void edit(File file) throws IOException { throw new UnsupportedOperationException(); }

        /**
         * Prints a file with the native desktop printing facility, using
         * the associated application's print command.
         * @param file the file to be printed
         * @throws IOException if the specified file has no associated
         * application that can be used to print it
         * @see java.awt.Desktop#print(java.io.File)
         */
        default void print(File file) throws IOException { throw new UnsupportedOperationException(); }

        /**
         * Launches the mail composing window of the user default mail
         * client, filling the message fields specified by a {@code mailto:} URI.
         * @param mailtoURL the specified {@code mailto:} URI
         * @throws IOException if the user default mail client is not
         * found or fails to be launched
         * @see java.awt.Desktop#mail(java.net.URI)
         */
        default void mail(URI mailtoURL) throws IOException { throw new UnsupportedOperationException(); }

        /**
         * Launches the default browser to display a {@code URI}.
         * @param uri the URI to be displayed in the user default browser
         * @throws IOException if the user default browser is not found,
         * or it fails to be launched, or the default handler application
         * failed to be launched
         * @see java.awt.Desktop#browse(java.net.URI)
         */
        default void browse(URI uri) throws IOException { throw new UnsupportedOperationException(); }
    }

}
