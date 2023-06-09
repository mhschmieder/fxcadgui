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

import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxcadgraphics.Region2D;
import com.mhschmieder.fxguitoolkit.layout.LayoutFactory;
import com.mhschmieder.fxphysicsgui.layout.ExtentsPane;
import com.mhschmieder.physicstoolkit.DistanceUnit;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public final class Region2DPane extends VBox {

    protected ExtentsPane _extentsPane;

    // Declare a Surface Legend that shows the labeling correspondence.
    private SurfaceLegend _surfaceLegend;

    public SurfacesPane   _surfacesPane;

    // Cache a reference to the global Region2D.
    protected Region2D    region2D;

    public Region2DPane( final ClientProperties pClientProperties,
                         final double extentsSizeMinimumMeters,
                         final double extentsSizeMaximumMeters,
                         final String propertiesCategory ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( pClientProperties,
                      extentsSizeMinimumMeters,
                      extentsSizeMaximumMeters,
                      propertiesCategory );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void initPane( final ClientProperties pClientProperties,
                           final double extentsDimensionMinimum,
                           final double extentsSizeMaximumMeters,
                           final String propertiesCategory ) {
        _extentsPane = new ExtentsPane( pClientProperties,
                                        extentsDimensionMinimum,
                                        extentsSizeMaximumMeters,
                                        propertiesCategory );

        _surfaceLegend = new SurfaceLegend( pClientProperties );

        _surfacesPane = new SurfacesPane( pClientProperties );

        final HBox hbox = new HBox();
        hbox.getChildren().setAll( _extentsPane, _surfaceLegend );

        hbox.setSpacing( 16d );
        hbox.setAlignment( Pos.CENTER );

        getChildren().addAll( hbox, _surfacesPane );

        setAlignment( Pos.CENTER );
        setSpacing( 3.0d );
        setPadding( new Insets( 3.0d, 3.0d, 3.0d, 0.0d ) );
    }

    // Reset all fields to the default values, regardless of state.
    // NOTE: This is done from the view vs. the model, as there may be more
    // than one component per property.
    public void reset() {
        // Forward this method to the subcomponents.
        _extentsPane.reset();
        _surfacesPane.reset();
    }

    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        setBackground( background );

        // Forward this method to the lower-level layout containers.
        _extentsPane.setForegroundFromBackground( backColor );
        _surfaceLegend.setForegroundFromBackground( backColor );
        _surfacesPane.setForegroundFromBackground( backColor );
    }

    // Set and bind the Region2D reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setRegion2D( final Region2D pRegion2D ) {
        // Cache the Region2D reference.
        region2D = pRegion2D;

        // Forward this reference to the subsidiary panes.
        _extentsPane.setExtents( pRegion2D );
        _surfacesPane.setSurfaceProperties( pRegion2D.getSurfaceProperties() );
    }

    /**
     * Propagate the new Distance Unit to the subcomponents.
     */
    public void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        // Forward this method to the Extents Pane.
        _extentsPane.updateDistanceUnit( distanceUnit );
    }

    public void updateView() {
        // Forward this method to the Surfaces Pane.
        _surfacesPane.updateView();
    }

}
