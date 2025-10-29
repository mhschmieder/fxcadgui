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
 * This file is part of the FxCadGui Library
 *
 * You should have received a copy of the MIT License along with the FxCadGui
 * Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxcadgui
 */
package com.mhschmieder.fxcadgui.stage;

import com.mhschmieder.fxcadgraphics.CartesianLine;
import com.mhschmieder.fxcadgraphics.GraphicalObjectCollection;
import com.mhschmieder.fxcadgui.layout.CartesianLinePane;
import com.mhschmieder.fxgraphics.input.ScrollingSensitivity;
import com.mhschmieder.fxgui.stage.ObjectPropertiesEditor;
import com.mhschmieder.fxlayergraphics.Layer;
import com.mhschmieder.jcommons.branding.ProductBranding;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jphysics.AngleUnit;
import com.mhschmieder.jphysics.DistanceUnit;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public final class CartesianLineEditor extends ObjectPropertiesEditor {

    // Declare the main content pane.
    protected CartesianLinePane _cartesianLinePane;

    // Maintain a reference to the current Cartesian Line object.
    protected CartesianLine                              _cartesianLineReference;

    // Maintain a reference to the Cartesian Line collection.
    protected GraphicalObjectCollection< CartesianLine > _cartesianLineCollection;
    
    // Allow for customization of Cartesian Line Type (name identifier, not behavior).
    protected String _cartesianLineType;

    // Allow for customization of Projector Type (name identifier, not behavior).
    protected String _projectorType;
    
    // Allow for customization of Projection Zones Type (name identifier, not behavior).
    protected String _projectionZonesType;
    
    // Projection Zones usage context, for constructing tooltips.
    protected String _projectionZonesUsageContext;

    @SuppressWarnings("nls")
    public CartesianLineEditor( final boolean insertMode,
                                final GraphicalObjectCollection< CartesianLine > cartesianLineCollection,
                                final ProductBranding productBranding,
                                final ClientProperties pClientProperties,
                                final boolean pResetApplicable,
                                final String cartesianLineType,
                                final String projectorType,
                                final String projectionZonesType,
                                final String projectionZonesUsageContext ) {
        // Always call the superclass constructor first!
        super( insertMode, 
               cartesianLineType, 
               "cartesianLine", 
               productBranding, 
               pClientProperties,
               pResetApplicable );

        _cartesianLineCollection = cartesianLineCollection;
        
        _cartesianLineType = cartesianLineType;
        _projectorType = projectorType;
        _projectionZonesType = projectionZonesType;
        _projectionZonesUsageContext = projectionZonesUsageContext;

        // Start with a default Cartesian Line until editing.
        _cartesianLineReference = CartesianLine.getDefaultCartesianLine();

        try {
            initStage();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // Open the textField initialized to a mouse-selected Cartesian Line.
    public void editCartesianLine( final CartesianLine cartesianLine ) {
        // Make sure an active editing session is always enabled when visible.
        setDisable( false );

        // Make sure the Layer Names are up-to-date, and that we avoid any side
        // effects against the selected Layer for the new Cartesian Line Reference.
        final Layer currentLayer = cartesianLine.getLayer();
        updateLayerNames( currentLayer );

        // Ensure that an object already being edited doesn't reset and re-sync
        // the reference lest it flush its view for the last cached values.
        if ( _cartesianLineReference.equals( cartesianLine ) ) {
            return;
        }

        // Replace the current Cartesian Line reference with the one selected
        // for the Edit action when opening this window.
        setCartesianLineReference( cartesianLine );

        // Update the TextField from the selected Cartesian Line.
        updateView();
    }

    public CartesianLine getCartesianLineReference() {
        return _cartesianLineReference;
    }

    public String getNewCartesianLineLabelDefault() {
        // Forward this method to the Cartesian Line Pane.
        return _cartesianLinePane.getNewCartesianLineLabelDefault();
    }

    @SuppressWarnings("nls")
    private void initStage() {
        // First have the superclass initialize its content.
        initStage( "/icons/wooThemes/Ruler16.png",
                   _cartesianLineType,
                   1080d,
                   460d,
                   false,
                   false,
                   false );
    }

    @Override
    protected Node loadContent() {
        // Instantiate and return the custom Content Node.
        _cartesianLinePane = new CartesianLinePane( clientProperties,
                                                    _cartesianLineCollection,
                                                    _cartesianLineType,
                                                    _projectorType,
                                                    _projectionZonesType,
                                                    _projectionZonesUsageContext );
        return _cartesianLinePane;
    }

    @Override
    protected void reset() {
        // Cache the current values that we want to preserve.
        // TODO: Determine whether location is the best positional field to
        // save/restore.
        // final Point2D location =
        // _cartesianLineReference.getLocation();
        final String cartesianLineLabel = _cartesianLineReference.getLabel();

        // Make a default Cartesian Line to effectively reset all the fields.
        final CartesianLine cartesianLineDefault = CartesianLine
                .getDefaultCartesianLine();
        _cartesianLineReference.setCartesianLine( cartesianLineDefault );

        // Restore the fields we want to preserve.
        // _cartesianLineReference.setLocation( location );
        _cartesianLineReference.setLabel( cartesianLineLabel );

        // Update the view to match the new model, but don't apply it yet.
        updateView();
    }

    public void setCartesianLineReference( final CartesianLine cartesianLine ) {
        _cartesianLineReference = cartesianLine;
    }

    @Override
    public void setDisable( final boolean disable ) {
        // First, disable anything that is shared as part of the parent class,
        // such as the action buttons.
        super.setDisable( disable );

        // Forward this method to the Cartesian Line Pane.
        _cartesianLinePane.setDisable( disable );
    }

    public void setGesturesEnabled( final boolean gesturesEnabled ) {
        // Forward this method to the Cartesian Line Pane.
        _cartesianLinePane.setGesturesEnabled( gesturesEnabled );
    }

    public void setLayerCollection( final ObservableList< Layer > layerCollection ) {
        // Forward this method to the Cartesian Line Pane.
        _cartesianLinePane.setLayerCollection( layerCollection );
    }

    /**
     * Set the new Scrolling Sensitivity for all the sliders.
     *
     * @param scrollingSensitivity
     *            The sensitivity of the mouse scroll wheel
     */
    public void setScrollingSensitivity( final ScrollingSensitivity scrollingSensitivity ) {
        // Forward this method to the Cartesian Line Pane.
        _cartesianLinePane.setScrollingSensitivity( scrollingSensitivity );
    }

    @Override
    protected void updateObjectPropertiesView() {
        // Forward this method to the Cartesian Line Pane.
        _cartesianLinePane.updateCartesianLineView( _cartesianLineReference );
    }

    @Override
    protected void updateObjectPropertiesModel() {
        // Forward this method to the Cartesian Line Pane.
        _cartesianLinePane.updateCartesianLineModel( _cartesianLineReference );
    }

    public void updateLayerNameSelection() {
        // Forward this method to the Cartesian Line Pane.
        _cartesianLinePane.updateLayerNameSelection( _cartesianLineReference );
    }

    public void toggleGestures() {
        // Forward this method to the Cartesian Line Pane.
        _cartesianLinePane.toggleGestures();
    }

    public void updateAngleUnit( final AngleUnit angleUnit ) {
        // Forward this method to the Cartesian Line Pane.
        _cartesianLinePane.updateAngleUnit( angleUnit );

        // Make sure all displayed fields update to the new Angle Unit.
        // NOTE: We skip this if running as a modal dialog, as this change can
        // only come from the dialog not showing anyway, and can hit
        // performance.
        if ( isEditMode() ) {
            updateObjectPropertiesView();
        }
    }

    public void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        // Forward this method to the Cartesian Line Pane.
        _cartesianLinePane.updateDistanceUnit( distanceUnit );

        // Make sure all displayed fields update to the new Distance Unit.
        // NOTE: We skip this if running as a modal dialog, as this change can
        // only come from the dialog not showing anyway, and can hit
        // performance.
        if ( isEditMode() ) {
            updateObjectPropertiesView();
        }
    }

    public void updateLayerNames( final boolean preserveSelectedLayerByIndex,
                                  final boolean preserveSelectedLayerByName ) {
        // Forward this method to the Cartesian Line Pane.
        _cartesianLinePane.updateLayerNames( preserveSelectedLayerByIndex,
                                             preserveSelectedLayerByName );
    }

    public void updateLayerNames( final Layer currentLayer ) {
        // Forward this method to the Cartesian Line Pane.
        _cartesianLinePane.updateLayerNames( currentLayer );
    }

    // TODO: Verify whether we need to synchronize both positions.
    @Override
    public void updatePositioning() {
        // Forward this method to the Cartesian Line Pane.
        _cartesianLinePane.updatePositioning( _cartesianLineReference );
    }

    @Override
    public void updatePreview() {
        // Forward this method to the Cartesian Line Pane.
        _cartesianLinePane.updatePreview( _cartesianLineReference );
    }

}
