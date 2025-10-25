/**
 * MIT License
 *
 * Copyright (c) 2020, 2023 Mark Schmieder
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
 * This file is part of the FxCadGui Library
 *
 * You should have received a copy of the MIT License along with the FxCadGui
 * Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxcadgui
 */
package com.mhschmieder.fxcadgui.layout;

import com.mhschmieder.fxcontrols.control.ControlUtilities;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

/**
 * This is a container for a set of choices for the source of Drawing Limits to
 * be used for a specific purpose, such as Graphics Import.
 */
public class GraphicsImportDrawingLimitsSourcePane extends BorderPane {

    public ToggleGroup _drawingLimitsSourceToggleGroup;
    public RadioButton _computedBoundsRadioButton;
    public RadioButton _applicationDrawingLimitsRadioButton;
    public RadioButton _graphicsFileRadioButton;

    public GraphicsImportDrawingLimitsSourcePane( final String productName ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( productName );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private final void initPane( final String productName ) {
        _drawingLimitsSourceToggleGroup = new ToggleGroup();
        _computedBoundsRadioButton = ControlUtilities
                .getRadioButton( "Use " + productName + " to Compute Bounding Box", //$NON-NLS-1$ //$NON-NLS-2$
                                 _drawingLimitsSourceToggleGroup,
                                 true );
        _applicationDrawingLimitsRadioButton = ControlUtilities
                .getRadioButton( "Use Current Drawing Limits from " + productName, //$NON-NLS-1$
                                 _drawingLimitsSourceToggleGroup,
                                 false );
        _graphicsFileRadioButton = ControlUtilities
                .getRadioButton( "Use Drawing Limits from Graphics File", //$NON-NLS-1$
                                 _drawingLimitsSourceToggleGroup,
                                 false );

        final GridPane gridPane = new GridPane();
        gridPane.setHgap( 6.0d );
        gridPane.setVgap( 6.0d );

        gridPane.add( _computedBoundsRadioButton, 0, 0 );
        gridPane.add( _applicationDrawingLimitsRadioButton, 0, 1 );
        gridPane.add( _graphicsFileRadioButton, 0, 2 );

        gridPane.setAlignment( Pos.CENTER );
        gridPane.setPadding( new Insets( 6.0d ) );

        setLeft( gridPane );
    }

}
