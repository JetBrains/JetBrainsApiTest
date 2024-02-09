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

import java.awt.*;

/**
 * Extended version of {@link java.awt.FileDialog}.
 */
@Proxy
public interface JBRFileDialog {

    /**
     * Whether to select files, directories or both (used when common file dialogs are enabled on Windows, or on macOS)
     */
    public static final int SELECT_FILES_HINT = 1, SELECT_DIRECTORIES_HINT = 2;
    /**
     * Whether to allow creating directories or not (used on macOS)
     */
    public static final int CREATE_DIRECTORIES_HINT = 4;

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
     * Set JBR-specific file dialog hints:
     * <ul>
     *     <li>SELECT_FILES_HINT, SELECT_DIRECTORIES_HINT - Whether to select files, directories or both
     *     (used when common file dialogs are enabled on Windows, or on macOS),
     *     both unset bits are treated as a default platform-specific behavior</li>
     *     <li>CREATE_DIRECTORIES_HINT - Whether to allow creating directories or not (used on macOS)</li>
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
     * Overrides file dialog button names (on Windows only).
     * @param openButtonText "Open" button
     * @param selectFolderButtonText "Select Folder" button
     */
    void setLocalizationStrings(String openButtonText, String selectFolderButtonText);
}

@Service
interface JBRFileDialogService {
    JBRFileDialogService INSTANCE = JBR.getService(JBRFileDialogService.class);
    JBRFileDialog getFileDialog(FileDialog dialog);
}
