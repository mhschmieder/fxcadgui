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

import com.mhschmieder.fxcadcontrols.action.Region2DActions;
import com.mhschmieder.fxcadcontrols.control.CadMenuFactory;
import com.mhschmieder.fxcadcontrols.control.Region2DToolBar;
import com.mhschmieder.fxcadcontrols.model.Region2DProperties;
import com.mhschmieder.fxcadgraphics.Region2D;
import com.mhschmieder.fxcadgui.layout.Region2DPane;
import com.mhschmieder.fxgui.stage.XStage;
import com.mhschmieder.jcommons.branding.ProductBranding;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jphysics.DistanceUnit;
import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;

public final class Region2DStage extends XStage {

    public static final String REGION2D_FRAME_TITLE_DEFAULT  = "Region2D";

    // Default window locations and dimensions.
    public static final int    REGION2D_STAGE_X_DEFAULT      = 20;
    public static final int    REGION2D_STAGE_Y_DEFAULT      = 20;
    private static final int   REGION2D_STAGE_WIDTH_DEFAULT  = 700;
    private static final int   REGION2D_STAGE_HEIGHT_DEFAULT = 440;

    // Declare the actions.
    public Region2DActions     _actions;

    // Declare the main tool bar.
    public Region2DToolBar     _toolBar;

    // Declare the main content pane.
    protected Region2DPane _region2DPane;

    // Cache a reference to the global Region2D.
    protected Region2DProperties region2DProperties;

    // Flag for whether vector graphics are supported.
    protected final boolean _vectorGraphicsSupported;
    
    public Region2DStage( final String frameTitle,
                          final String windowKeyPrefix,
                          final String jarRelativeIconFilename,
                          final String pGraphicsCategory,
                          final ProductBranding productBranding,
                          final ClientProperties pClientProperties,
                          final boolean vectorGraphicsSupported ) {
        // Always call the superclass constructor first!
        super( frameTitle, windowKeyPrefix, true, true, productBranding, pClientProperties );

        graphicsCategory = pGraphicsCategory;
        
        _vectorGraphicsSupported = vectorGraphicsSupported;

        try {
            initStage( jarRelativeIconFilename, true );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // Add all of the relevant action handlers.
    @Override
    protected void addActionHandlers() {
        // Load the action handlers for the "Export" actions.
        _actions.fileActions._exportActions._exportRasterGraphicsAction
                .setEventHandler( evt -> doExportImageGraphics() );
        _actions.fileActions._exportActions._exportVectorGraphicsAction
                .setEventHandler( evt -> doExportVectorGraphics() );

        // Load the action handlers for the "Background Color" choices.
        addBackgroundColorChoiceHandlers( _actions.settingsActions._backgroundColorChoices );

        // Load the action handlers for the "Window Size" actions.
        addWindowSizeActionHandlers( _actions.settingsActions._windowSizeActions );

        // Load the action handlers for the "Tools" actions.
        // NOTE: These are registered at the top-most level of the application.

        // Load the action handler for the "Reset" action.
        _actions.resetAction.setEventHandler( evt -> doReset() );
    }

    // Add the Tool Bar's event listeners.
    // TODO: Use appropriate methodology to add an action linked to both
    // the toolbar buttons and their associated menu items, so that when one
    // is disabled the other is as well. Is this already true of what we do?
    @Override
    protected void addToolBarListeners() {
        // Detect the ENTER key while the Reset Button has focus, and use it to
        // trigger its action (standard expected behavior).
        _toolBar._resetButton.setOnKeyReleased( keyEvent -> {
            final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
            if ( keyCombo.match( keyEvent ) ) {
                // Trigger the Reset action.
                doReset();

                // Consume the ENTER key so it doesn't get processed
                // twice.
                keyEvent.consume();
            }
        } );
    }

    protected void doReset() {
        reset();
    }

    protected void initStage( final String jarRelativeIconFilename, final boolean resizable ) {
        // First have the superclass initialize its content.
        initStage( jarRelativeIconFilename,
                   REGION2D_STAGE_WIDTH_DEFAULT,
                   REGION2D_STAGE_HEIGHT_DEFAULT,
                   resizable );
    }

    // Load the relevant actions for this Stage.
    @Override
    protected void loadActions() {
        // Make all the actions.
        _actions = new Region2DActions( clientProperties, graphicsCategory );
    }

    @Override
    protected Node loadContent() {
        // Instantiate and return the custom Content Node.
        _region2DPane = new Region2DPane( clientProperties,
                                          Region2D.SIZE_METERS_MINIMUM,
                                          Region2D.SIZE_METERS_MAXIMUM,
                                          graphicsCategory );
        return _region2DPane;
    }

    // Add the Menu Bar for this Stage.
    @Override
    protected MenuBar loadMenuBar() {
        // Build the Menu Bar for this Stage.
        final MenuBar menuBar = CadMenuFactory.getRegion2DMenuBar( clientProperties, _actions, _vectorGraphicsSupported );

        // Return the Menu Bar so the superclass can use it.
        return menuBar;
    }

    // Add the Tool Bar for this Stage.
    @Override
    public ToolBar loadToolBar() {
        // Build the Tool Bar for this Stage.
        _toolBar = new Region2DToolBar( clientProperties, _actions );

        // Return the Tool Bar so the superclass can use it.
        return _toolBar;
    }

    // Reset all fields to the default values, regardless of state.
    // NOTE: This is done from the view vs. the model, as there may be more
    // than one component per property.
    @Override
    protected void reset() {
        // Forward this method to the Region2D Pane.
        _region2DPane.reset();
    }
    
    @Override
    public String getBackgroundColor() {
        return _actions.getSelectedBackgroundColorName();
    }

    @Override
    public void selectBackgroundColor( final String backgroundColorName ) {
        _actions.selectBackgroundColor( backgroundColorName );
    }

    @Override
    public void setForegroundFromBackground( final Color backColor ) {
        // Take care of general styling first, as that also loads shared
        // variables.
        super.setForegroundFromBackground( backColor );

        // Forward this method to the Region2D Pane.
        _region2DPane.setForegroundFromBackground( backColor );
    }

    // Set and propagate the Region2D reference.
    // NOTE: This should be done only once, to avoid breaking bindings.
    public void setRegion2D( final Region2DProperties pRegion2DProperties) {
        // Cache the Region2D reference.
        region2DProperties = pRegion2DProperties;

        // Forward this reference to the Region2D Pane.
        _region2DPane.setRegion2D(pRegion2DProperties);
    }

    @Override
    public void updateView() {
        // Forward this reference to the Region2D Pane.
        _region2DPane.updateView();
    }

    /**
     * Propagate the new Distance Unit to the subcomponents.
     */
    public void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        // Forward this reference to the Region2D Pane.
        _region2DPane.updateDistanceUnit( distanceUnit );
    }
}
