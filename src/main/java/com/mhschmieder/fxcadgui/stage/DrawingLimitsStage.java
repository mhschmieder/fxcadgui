/**
 * MIT License
 *
 * Copyright (c) 2020, 2025 Mark Schmieder
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

import com.mhschmieder.fxcadgui.layout.DrawingLimitsPane;
import com.mhschmieder.fxcadgui.model.DrawingLimitsProperties;
import com.mhschmieder.fxcontrols.action.SimulationActions;
import com.mhschmieder.fxcontrols.control.PredictToolBar;
import com.mhschmieder.fxgui.stage.XStage;
import com.mhschmieder.fxphysicsgui.model.Extents2DProperties;
import com.mhschmieder.jcommons.branding.ProductBranding;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jphysics.DistanceUnit;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;

public final class DrawingLimitsStage extends XStage {

    public static final String DRAWING_LIMITS_FRAME_TITLE_DEFAULT  = "Drawing Limits"; //$NON-NLS-1$

    // Default frame dimensions.
    private static final double DRAWING_LIMITS_FRAME_WIDTH_DEFAULT  = 680.0d;
    private static final double DRAWING_LIMITS_FRAME_HEIGHT_DEFAULT = 280.0d;

    // Declare the actions.
    public SimulationActions simulationActions;

    // Declare the main tool bar.
    public PredictToolBar toolBar;

    // Declare the main content pane.
    public DrawingLimitsPane drawingLimitsPane;

    // Cache the Auto-Sync label, as it is needed by lazy initialization.
    protected String autoSyncLabel;

    // Cache a reference to the global Drawing Limits.
    protected DrawingLimitsProperties drawingLimitsProperties;

    public DrawingLimitsStage( final String pAutoSyncLabel,
                               final ProductBranding pProductBranding,
                               final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( DRAWING_LIMITS_FRAME_TITLE_DEFAULT,
               "drawingLimits",
               true,
               true,
               pProductBranding,
               pClientProperties );

        autoSyncLabel = pAutoSyncLabel;

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
        simulationActions = new SimulationActions( clientProperties );
    }

    @Override
    protected Node loadContent() {
        // Instantiate and return the custom Content Node.
        drawingLimitsPane = new DrawingLimitsPane( clientProperties,
                                                    autoSyncLabel,
                                                    true,
                                                    -Double.MAX_VALUE,
                                                    Double.MAX_VALUE,
                                                    "Drawing Limits" );
        return drawingLimitsPane;
    }

    // Add the Tool Bar for this Stage.
    @Override
    public ToolBar loadToolBar() {
        // Build the Tool Bar for this Stage.
        toolBar = new PredictToolBar( clientProperties, 
                                      simulationActions );

        // Return the Tool Bar so the superclass can use it.
        return toolBar;
    }

    public void setAutoSyncBoundary( final Extents2DProperties pAutoSyncBoundary ) {
        // Forward this method to the Drawing Limits Pane.
        drawingLimitsPane.setAutoSyncBoundary( pAutoSyncBoundary );
    }

    // Set and propagate the Drawing Limits reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setDrawingLimits( final DrawingLimitsProperties pDrawingLimitsProperties) {
        // Cache the Drawing Limits reference.
        drawingLimitsProperties = pDrawingLimitsProperties;

        // Forward this reference to the Drawing Limits Pane.
        drawingLimitsPane.setDrawingLimits(pDrawingLimitsProperties);
    }

    /**
     * Propagate the new Distance Unit to the subcomponents.
     */
    public void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        // Forward this reference to the Drawing Limits Pane.
        drawingLimitsPane.updateDistanceUnit( distanceUnit );
    }
}
