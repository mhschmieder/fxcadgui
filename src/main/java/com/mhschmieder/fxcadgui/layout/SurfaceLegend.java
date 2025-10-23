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

import com.mhschmieder.fxcontrols.GuiUtilities;
import com.mhschmieder.fxcontrols.layout.LayoutFactory;
import com.mhschmieder.fxgraphics.image.ImageUtilities;
import com.mhschmieder.fxgraphics.paint.ColorUtilities;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class SurfaceLegend extends GridPane {

    private Label     _surfaceLegendHeader;

    // Use Image Views to load the Surface Legends.
    private ImageView _surfaceLegendWhite;
    private ImageView _surfaceLegendBlack;

    // Use a Label to host the active Logo Image View.
    private Label     _surfaceLegendLabel;

    public SurfaceLegend( final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( pClientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private final void initPane( final ClientProperties pClientProperties ) {
        // Get the column header for the Surface Legend.
        _surfaceLegendHeader = GuiUtilities.getColumnHeader( "Legend" ); //$NON-NLS-1$

        // Place the Surface Legend image in an ImageView container.
        // NOTE: Specifying width is enough to scale to a desired overall size.
        final String jarRelativeSurfaceLegendWhiteFilename =
                                                           "/icons/mhschmieder/SurfaceLegendWhite.png"; //$NON-NLS-1$
        final String jarRelativeSurfaceLegendBlackFilename =
                                                           "/icons/mhschmieder/SurfaceLegendBlack.png"; //$NON-NLS-1$
        _surfaceLegendWhite = ImageUtilities
                .createLegend( jarRelativeSurfaceLegendWhiteFilename, true, -1d, 90d, -1d );
        _surfaceLegendBlack = ImageUtilities
                .createLegend( jarRelativeSurfaceLegendBlackFilename, true, -1d, 90d, -1d );

        // Make a Label to host the Surface Legend Image Icon, to control sizing
        // etc.
        _surfaceLegendLabel = new Label();

        // Make sure the Surface Legend Image doesn't get clipped, by aligning
        // to the top of the Label host.
        _surfaceLegendLabel.setAlignment( Pos.TOP_LEFT );

        add( _surfaceLegendHeader, 0, 0 );
        add( _surfaceLegendLabel, 0, 1 );

        GridPane.setHalignment( _surfaceLegendHeader, HPos.CENTER );
        GridPane.setHalignment( _surfaceLegendLabel, HPos.CENTER );

        setAlignment( Pos.CENTER );

        setPadding( new Insets( 6.0d ) );
        setVgap( 8.0d );

        // Make sure the Surface Legend Icon is always on the left, with minimal
        // gaps.
        // TODO: Use ScenicView to compare setPadding() vs. setMargins().
        setMargin( _surfaceLegendLabel, new Insets( 6.0d ) );

        // Try to prevent the Surface Legend from getting clipped or hidden.
        // NOTE: We give the image a chance to load before binding to it.
        Platform.runLater( () -> {
            minWidthProperty().bind( _surfaceLegendWhite.fitWidthProperty() );
        } );
    }

    public final void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        setBackground( background );

        // Forward this method to the lower-level layout containers.
        final Color foregroundColor = ColorUtilities.getForegroundFromBackground( backColor );

        _surfaceLegendHeader.setTextFill( foregroundColor );

        // Replace with white Surface Legend if switching to a dark background.
        // NOTE: We also set the label's background, for consistent insets.
        final ImageView logo = ColorUtilities.isColorDark( backColor )
            ? _surfaceLegendWhite
            : _surfaceLegendBlack;
        _surfaceLegendLabel.setBackground( background );
        _surfaceLegendLabel.setGraphic( logo );
    }

}
