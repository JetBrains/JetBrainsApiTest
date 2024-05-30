/*
 * Copyright 2000-2024 JetBrains s.r.o.
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

import java.awt.*;

/**
 * Extensions to the AWT {@code FileDialog} that allow clients fully use a native file chooser
 * on supported platforms (currently macOS and Windows; the latter requires setting
 * {@code sun.awt.windows.useCommonItemDialog} property to {@code true}).
 */
@Provided
public interface JBRFileDialog {

    /**
     * Whether to select files, directories or both (used when common file dialogs are enabled on Windows, or on macOS)
     */
    int SELECT_FILES_HINT = 1, SELECT_DIRECTORIES_HINT = 2;
    /**
     * Whether to allow creating directories or not (used on macOS)
     */
    int CREATE_DIRECTORIES_HINT = 4;

    /**
     * "open" button when a file is selected in the list
     */
    String OPEN_FILE_BUTTON_KEY = "jbrFileDialogOpenFile";
    /**
     * "open" button when a directory is selected in the list
     */
    String OPEN_DIRECTORY_BUTTON_KEY = "jbrFileDialogSelectDir";
    /**
     * "all files" item in the file filter combo box
     */
    String ALL_FILES_COMBO_KEY = "jbrFileDialogAllFiles";

    /**
     * Get {@link JBRFileDialog} from {@link FileDialog}, if supported.
     * @param dialog file dialog
     * @return file dialog extension, or null
     */
    static JBRFileDialog get(FileDialog dialog) {
        if (JBRFileDialogService.INSTANCE == null) return null;
        else return JBRFileDialogService.INSTANCE.getFileDialog(dialog);
    }

    /**
     * Set file dialog hints:
     * <ul>
     *     <li>SELECT_FILES_HINT, SELECT_DIRECTORIES_HINT - whether to select files, directories, or both;
     *     if neither of the two is set, the behavior is platform-specific</li>
     *     <li>CREATE_DIRECTORIES_HINT - whether to allow creating directories or not (macOS)</li>
     * </ul>
     * @param hints bitmask of selected hints
     */
    void setHints(int hints);

    /**
     * Retrieve extended hints set on file dialog.
     * @return bitmask of selected hints
     * @see #setHints(int) 
     */
    int getHints();

    /**
     * Change text of UI elements (Windows).
     * Supported keys:
     * <ul>
     *     <li>OPEN_FILE_BUTTON_KEY - "open" button when a file is selected in the list</li>
     *     <li>OPEN_DIRECTORY_BUTTON_KEY - "open" button when a directory is selected in the list</li>
     *     <li>ALL_FILES_COMBO_KEY - "all files" item in the file filter combo box</li>
     * </ul>
     * @param key key
     * @param text localized text
     */
    void setLocalizationString(String key, String text);

    /**
     * Set file filter - a set of file extensions for files to be visible (Windows)
     * or not greyed out (macOS), and a name for the file filter combo box (Windows).
     * @param fileFilterDescription file filter description
     * @param fileFilterExtensions file filter extensions
     */
    void setFileFilterExtensions(String fileFilterDescription, String[] fileFilterExtensions);
}

@Service
@Provided
interface JBRFileDialogService {
    JBRFileDialogService INSTANCE = JBR.getService(JBRFileDialogService.class);
    JBRFileDialog getFileDialog(FileDialog dialog);
}
