/*
 * MIT License
 *
 * Copyright (c) 2020, 2025, Mark Schmieder. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is part of the fxcadgui Library
 *
 * You should have received a copy of the MIT License along with the fxcadgui
 * Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxcadgui
 */
package com.mhschmieder.fxcadgui.util;

import com.mhschmieder.fxcadcontrols.util.CadMessageFactory;
import com.mhschmieder.fxgui.stage.NoticeBox;
import com.mhschmieder.jcommons.util.SystemType;

import java.net.URL;

/**
 * This is a utility class for grabbing Help resources and pop-ups for CAD
 * features.
 */
public final class CadHelpUtilities {

    public static NoticeBox getGraphicsImportHelp(
            final SystemType systemType ) {
        // Get the URL associated with the JAR-loaded HTML-based Help file.
        final URL graphicsImportHelpUrl = getGraphicsImportHelpAsUrl();

        // Make a Notice Box to display the local Help until dismissed.
        final String graphicsImportHelpBanner = CadMessageFactory
                .getGraphicsImportHelpBanner();

        return new NoticeBox(
                systemType,
                graphicsImportHelpBanner,
                graphicsImportHelpUrl );
    }

    @SuppressWarnings("nls")
    public static URL getGraphicsImportHelpAsUrl() {
        // Get the URL associated with the JAR-loaded HTML-based Help file.
        return CadHelpUtilities.class.getResource(
                "/html/GraphicsImportHelp.html" );
    }
}
