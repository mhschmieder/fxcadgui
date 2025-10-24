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

import com.mhschmieder.fxcadgraphics.CartesianLine;
import com.mhschmieder.fxcontrols.GuiUtilities;
import com.mhschmieder.fxgraphics.input.ScrollingSensitivity;
import com.mhschmieder.fxphysicscontrols.layout.CartesianPositionPane;
import com.mhschmieder.fxphysicscontrols.layout.PositioningPane;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jphysics.AngleUnit;
import com.mhschmieder.jphysics.DistanceUnit;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public final class CartesianLinePlacementPane extends HBox {

    protected GraphicalObjectPreviewPane _previewPane;
    public CartesianPositionPane         _startCartesianPositionPane;
    public PositioningPane               _endPositionPane;

    public CartesianLinePlacementPane( final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( pClientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void initPane( final ClientProperties pClientProperties ) {
        _previewPane = new GraphicalObjectPreviewPane( 80, 100 );
        final Node previewBorderNode = GuiUtilities.getTitledBorderWrappedNode( _previewPane,
                                                                                "Preview" ); //$NON-NLS-1$

        _startCartesianPositionPane = new CartesianPositionPane( pClientProperties );
        final Node startCartesianPositionBorderNode = GuiUtilities
                .getTitledBorderWrappedNode( _startCartesianPositionPane, "First Point" ); //$NON-NLS-1$

        _endPositionPane = new PositioningPane( pClientProperties );
        final Node endPositionBorderNode = GuiUtilities
                .getTitledBorderWrappedNode( _endPositionPane, "Second Point" ); //$NON-NLS-1$

        getChildren().addAll( previewBorderNode,
                              startCartesianPositionBorderNode,
                              endPositionBorderNode );

        setSpacing( 6.0d );
        setPadding( new Insets( 3.0d ) );
    }

    public void saveEdits() {
        _startCartesianPositionPane.saveEdits();
        _endPositionPane.saveEdits();
    }

    protected void setEndPointPosition( final CartesianLine cartesianLine ) {
        _endPositionPane.setCartesianPosition2D( cartesianLine.getX2(),
                                                 cartesianLine.getY2() );
    }

    protected void setEndPolarPosition( final CartesianLine cartesianLine ) {
        _endPositionPane.setPolarPosition( cartesianLine.getAngleDegrees(),
                                           cartesianLine.getDistance() );
    }

    public void setGesturesEnabled( final boolean gesturesEnabled ) {
        // Forward this method to the End Position Pane.
        _endPositionPane.setGesturesEnabled( gesturesEnabled );
    }

    /**
     * Set the new Scrolling Sensitivity for all of the sliders.
     *
     * @param scrollingSensitivity
     *            The sensitivity of the mouse scroll wheel
     */
    public void setScrollingSensitivity( final ScrollingSensitivity scrollingSensitivity ) {
        // Forward this method to the End Position Pane.
        _endPositionPane.setScrollingSensitivity( scrollingSensitivity );
    }

    protected void setStartPointPosition( final CartesianLine cartesianLine ) {
        _startCartesianPositionPane.setCartesianPosition2D( cartesianLine.getX1(),
                                                            cartesianLine.getY1() );
    }

    public void updateCartesianLineModel( final CartesianLine cartesianLine ) {
        final Point2D startPosition2D = _startCartesianPositionPane.getCartesianPosition2D();
        if ( _endPositionPane.isCartesianPositionActive() ) {
            final Point2D endPosition2D = _endPositionPane.getCartesianPosition2D();
            cartesianLine.setLine( startPosition2D.getX(),
                                   startPosition2D.getY(),
                                   endPosition2D.getX(),
                                   endPosition2D.getY() );
        }
        else {
            final double angleDegrees = _endPositionPane.getRotationAngle();
            final double distance = _endPositionPane.getDistance();
            cartesianLine.setLine( startPosition2D, angleDegrees, distance );
        }

        // Update the preview of the current Cartesian Line.
        updatePreview( cartesianLine );
    }

    public void updateCartesianLineView( final CartesianLine cartesianLine ) {
        // Make sure the positioning parameters are in sync with the data model
        // as they could change outside this textField, such as via mouse
        // move/rotate in the Sound Field.
        updatePositioning( cartesianLine );
    }

    public void toggleGestures() {
        // Forward this method to the End Position Pane.
        _endPositionPane.toggleGestures();
    }

    public void updateAngleUnit( final AngleUnit angleUnit ) {
        // Forward this method to the relevant subsidiary components.
        _endPositionPane.updateAngleUnit( angleUnit );
    }

    public void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        // Forward this method to the relevant subsidiary components.
        _startCartesianPositionPane.updateDistanceUnit( distanceUnit );
        _endPositionPane.updateDistanceUnit( distanceUnit );
    }

    public void updatePositioning( final CartesianLine cartesianLine ) {
        setStartPointPosition( cartesianLine );
        setEndPointPosition( cartesianLine );

        // NOTE: We have to avoid recursion between the Cartesian and Polar
        // Coordinate editors, as the sliders may have set "Snap to Ticks" and
        // this auto-rounding can cause subsequent syncing calls to the
        // Cartesian coordinates to then re-sync the Polar coordinates,
        // eventually causing stack overflow from too much recursion.
        // NOTE: This is unconditionally re-enabled, as otherwise selecting a
        // different Linear Object while the Editor is open, can result in the
        // Polar Coordinates not updating, if they are the active selection.
        // There does not appear to be any resultant recursion, so probably we
        // added some data binding approaches after this code was first written.
        // if ( _endPositionPane.isCartesianPositionActive() ) {
        setEndPolarPosition( cartesianLine );
        // }
    }

    public void updatePreview( final CartesianLine cartesianLineCurrent ) {
        // Forward this to the preview pane, at the origin.
        final CartesianLine cartesianLine = new CartesianLine( cartesianLineCurrent );
        final double x1 = 0;
        final double y1 = 0;
        final double x2 = cartesianLine.getX2() - cartesianLine.getX1();
        final double y2 = cartesianLine.getY2() - cartesianLine.getY1();
        cartesianLine.setLine( x1, y1, x2, y2 );

        _previewPane.updatePreview( cartesianLine, 2.0d );
    }
}
