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
package com.mhschmieder.fxcadgui.stage;

import com.mhschmieder.commonstoolkit.branding.ProductBranding;
import com.mhschmieder.commonstoolkit.util.ClientProperties;
import com.mhschmieder.fxcadgraphics.DrawingLimits;
import com.mhschmieder.fxcadgui.layout.DrawingLimitsPane;
import com.mhschmieder.fxgraphicstoolkit.geometry.Extents2D;
import com.mhschmieder.fxguitoolkit.action.ToolsActions;
import com.mhschmieder.fxguitoolkit.control.PredictToolBar;
import com.mhschmieder.fxguitoolkit.stage.XStage;
import com.mhschmieder.physicstoolkit.DistanceUnit;

import javafx.scene.Node;
import javafx.scene.control.ToolBar;

public final class DrawingLimitsStage extends XStage {

    public static final String DRAWING_LIMITS_FRAME_TITLE_DEFAULT  = "Drawing Limits"; //$NON-NLS-1$

    // Default frame dimensions.
    private static final int   DRAWING_LIMITS_FRAME_WIDTH_DEFAULT  = 680;
    private static final int   DRAWING_LIMITS_FRAME_HEIGHT_DEFAULT = 280;

    // Declare the actions.
    public ToolsActions        _toolsActions;

    // Declare the main tool bar.
    public PredictToolBar      _toolBar;

    // Declare the main content pane.
    public DrawingLimitsPane   _drawingLimitsPane;

    // Cache the Auto-Sync label, as it is needed by lazy initialization.
    protected String           _autoSyncLabel;

    // Cache a reference to the global Drawing Limits.
    protected DrawingLimits    drawingLimits;

    @SuppressWarnings("nls")
    public DrawingLimitsStage( final String autoSyncLabel,
                               final ProductBranding productBranding,
                               final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( DRAWING_LIMITS_FRAME_TITLE_DEFAULT,
               "drawingLimits",
               true,
               true,
               productBranding,
               pClientProperties );

        _autoSyncLabel = autoSyncLabel;

        try {
            initStage( "/icons/led24/RulerCrop16.png" );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    protected void initStage( final String jarRelativeIconFilename ) {
        // First have the superclass initialize its content.
        initStage( jarRelativeIconFilename,
                   DRAWING_LIMITS_FRAME_WIDTH_DEFAULT,
                   DRAWING_LIMITS_FRAME_HEIGHT_DEFAULT,
                   false );
    }

    // Load the relevant actions for this Stage.
    @Override
    protected void loadActions() {
        // Make all of the actions.
        _toolsActions = new ToolsActions( clientProperties );
    }

    @Override
    protected Node loadContent() {
        // Instantiate and return the custom Content Node.
        _drawingLimitsPane = new DrawingLimitsPane( clientProperties,
                                                    _autoSyncLabel,
                                                    true,
                                                    -Double.MAX_VALUE,
                                                    Double.MAX_VALUE,
                                                    "Drawing Limits" ); //$NON-NLS-1$
        return _drawingLimitsPane;
    }

    // Add the Tool Bar for this Stage.
    @Override
    public ToolBar loadToolBar() {
        // Build the Tool Bar for this Stage.
        _toolBar = new PredictToolBar( clientProperties, _toolsActions );

        // Return the Tool Bar so the superclass can use it.
        return _toolBar;
    }

    public void setAutoSyncBoundary( final Extents2D pAutoSyncBoundary ) {
        // Forward this method to the Drawing Limits Pane.
        _drawingLimitsPane.setAutoSyncBoundary( pAutoSyncBoundary );
    }

    // Set and propagate the Drawing Limits reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setDrawingLimits( final DrawingLimits pDrawingLimits ) {
        // Cache the Drawing Limits reference.
        drawingLimits = pDrawingLimits;

        // Forward this reference to the Drawing Limits Pane.
        _drawingLimitsPane.setDrawingLimits( pDrawingLimits );
    }

    /**
     * Propagate the new Distance Unit to the subcomponents.
     */
    public void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        // Forward this reference to the Drawing Limits Pane.
        _drawingLimitsPane.updateDistanceUnit( distanceUnit );
    }

}
