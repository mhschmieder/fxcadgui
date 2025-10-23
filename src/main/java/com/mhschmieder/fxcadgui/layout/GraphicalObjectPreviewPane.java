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

import com.mhschmieder.fxcadgraphics.GraphicalObject;
import com.mhschmieder.fxgraphics.shape.ShapeGroup;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.apache.commons.math3.util.FastMath;

/**
 * The Graphical Object Preview Pane is a layout wrapper for displaying previews
 * of graphical objects in their Element Coordinate System, which means they
 * should be zeroed before passed into the preview updater, but will account for
 * rotation angle and inversion.
 */
public final class GraphicalObjectPreviewPane extends StackPane {

    /**
     * From inspecting the scene graph, it appears that this node only gets to
     * use up to the bounds of the parent minus 15 pixels at each edge.
     * <p>
     * NOTE: This doesn't appear to be the case anymore, after slightly
     * modifying the layout scheme and other factors, so the insets have been
     * reduced to ten pixels, hoping that is enough to cover diagonal
     * orientations of rotated elements, which have taller bounding boxes.
     */
    private static final double PARENT_INSETS = 3.0d;

    /**
     * The last graphic to be produced by the {@link GraphicalObject} given to
     * {@link #updatePreview}.
     */
    private ShapeGroup          _shapeFx;

    /**
     * We need a nested layout scheme, to take advantage of auto-clipping.
     */
    private Pane                _drawingPane;

    /**
     * The bounds of the parent at the time that they are first relevant for
     * sizing this node.
     */
    private Bounds              _parentBounds;

    public GraphicalObjectPreviewPane( final double width, final double height ) {
        // Always call the superclass constructor first!
        super();

        _shapeFx = null;
        _parentBounds = null;

        try {
            initPane( width, height );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void initPane( final double width, final double height ) {
        // Get the initial nested layout going, even without content.
        _drawingPane = new Pane();

        getChildren().setAll( _drawingPane );

        // _drawingPane.setPrefSize( width, height );
        setPrefSize( width, height );

        _drawingPane.maxWidthProperty().bind( widthProperty() );
        _drawingPane.minWidthProperty().bind( widthProperty() );
        _drawingPane.prefWidthProperty().bind( widthProperty() );

        // Clip the node to make sure it doesn't extend past the preview.
        // NOTE: This doesn't work as the layout bounds doesn't change, and
        // thus the registered callback is never invoked; thus no clipping.
        // SceneGraphNodeUtilities.clipChildren( _drawingPane, 3.0d );
    }

    @Override
    public boolean isResizable() {
        return false;
    }

    /**
     * This method updates the preview, based on a supplied graphical object.
     *
     * @param graphicalObject
     *            produces a {@link Node} to draw
     * @param scaleFactorAdjustment
     *            Adjustment to the scale factor; necessary in some contexts
     */
    public void updatePreview( final GraphicalObject graphicalObject,
                               final double scaleFactorAdjustment ) {
        // If the window/stage is actually showing, the parent bounds will not
        // be zero. Since the preview node is generated anew on each call to
        // getVectorGraphics it's good to skip the rest of the method otherwise.
        // TODO: Switch to more of a "fuzzyEQ" strategy here.
        final Bounds parentBounds = getParent().getBoundsInParent();
        if ( ( ( ( float ) parentBounds.getHeight() ) == 0f )
                || ( ( ( float ) parentBounds.getWidth() ) == 0f ) ) {
            return;
        }

        // Now that we've checked the parent bounds, generate graphical object
        // vector graphics, checking for missing implementations (null results).
        _shapeFx = graphicalObject.getVectorGraphics( true );
        if ( _shapeFx == null ) {
            System.err.println( "WARNING: " + graphicalObject //$NON-NLS-1$
                    + "\n\t returned null graphic from getVectorGraphics" ); //$NON-NLS-1$
            return;
        }

        // If the graphical object's geometry bounds are empty, nothing to do,
        // but not an error.
        // TODO: Switch to more of a "fuzzyEQ" strategy here.
        final Bounds graphicalObjectGeometryBounds = _shapeFx.getLayoutBounds();
        final double graphicalObjectGeometryHeight = graphicalObjectGeometryBounds.getHeight();
        final double graphicalObjectGeometryWidth = graphicalObjectGeometryBounds.getWidth();
        if ( ( ( ( float ) graphicalObjectGeometryHeight ) == 0f )
                || ( ( ( float ) graphicalObjectGeometryWidth ) == 0f ) ) {
            return;
        }

        // As parent bounds change when geometry bounds change, we need to work
        // based on just the first relevant view of the parent bounds.
        if ( _parentBounds == null ) {
            _parentBounds = parentBounds;
        }

        // Now that we know whether we are overwriting the parent bounds or
        // using the cached bounds, it is safe to compute the maximum node
        // dimensions based on the known parent insets.
        final double maxNodeWidth = _parentBounds.getWidth() - ( PARENT_INSETS * 2 );
        final double maxNodeHeight = _parentBounds.getHeight() - ( PARENT_INSETS * 2 );

        // Pick the smaller of the height ratio and width ratio to use as the
        // pixel mapping from object geometry to on-screen preview node.
        final double widthRatio = maxNodeWidth / FastMath.abs( graphicalObjectGeometryWidth );
        final double heightRatio = maxNodeHeight / FastMath.abs( graphicalObjectGeometryHeight );
        double scaleFactor = FastMath.min( widthRatio, heightRatio );

        // This is a hack to deal with issues with Loudspeaker and Microphone
        // bounding boxes being too large when using Inside Stroke.
        if ( scaleFactorAdjustment != 1.0d ) {
            scaleFactor *= scaleFactorAdjustment;
        }

        // Make sure we exit early if the resulting scale factor is zero.
        // TODO: Switch to more of a "fuzzyEQ" strategy here.
        if ( ( float ) scaleFactor == 0f ) {
            new Throwable( "WARNING: zero-scaled graphical object geometry preview" ) //$NON-NLS-1$
                    .printStackTrace();
            return;
        }

        // Apply the scale factors for model space to screen coordinates.
        // NOTE: It seems simpler and safer to modify the x-axis and y-axis
        // scale factors than to add a Scale Transform (as we did previously),
        // as these scale factors are applied after other transforms and apply
        // to the node rather than to its layout bounds.
        // NOTE: The y-axis is always flipped, to account for bottom-to-top for
        // Cartesian Space vs. top-to-bottom for screen coordinates.
        final double scaleX = _shapeFx.getScaleX();
        _shapeFx.setScaleX( scaleX * scaleFactor );
        final double scaleY = _shapeFx.getScaleY();
        _shapeFx.setScaleY( -scaleY * scaleFactor );

        // Set the stroke width based on current scale factors.
        _shapeFx.setStrokeWidth( 0.5 / scaleFactor );

        // Replace the layout's contents with just the new graphical node.
        final ObservableList< Node > nodes = _drawingPane.getChildren();
        nodes.setAll( _shapeFx );

        // Clip to the container's bounds to avoid painting over other controls.
        final double clipWidth = _parentBounds.getWidth();
        final double clipHeight = _parentBounds.getHeight();
        final Rectangle outputClip = new Rectangle( -0.5d * clipWidth,
                                                    -0.5d * clipHeight,
                                                    clipWidth,
                                                    clipHeight );
        _drawingPane.setClip( outputClip );
    }

}
