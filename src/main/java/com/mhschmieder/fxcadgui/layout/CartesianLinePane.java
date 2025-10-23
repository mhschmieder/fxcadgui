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

import com.mhschmieder.fxcadcontrols.model.LinearObjectProperties;
import com.mhschmieder.fxcadgraphics.CartesianLine;
import com.mhschmieder.fxcadgraphics.GraphicalObjectCollection;
import com.mhschmieder.fxgraphics.input.ScrollingSensitivity;
import com.mhschmieder.fxlayergraphics.LayerUtilities;
import com.mhschmieder.fxlayergraphics.model.LayerProperties;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jphysics.AngleUnit;
import com.mhschmieder.jphysics.DistanceUnit;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public final class CartesianLinePane extends VBox {

    public LinearObjectPropertiesPane _linearObjectPropertiesPane;
    public CartesianLinePlacementPane _cartesianLinePlacementPane;

    /** Layer Collection reference. */
    private ObservableList< LayerProperties >  _layerCollection;

    /** Client Properties (System Type, Locale, etc.). */
    public ClientProperties                  _clientProperties;

    public CartesianLinePane( final ClientProperties pClientProperties,
                              final GraphicalObjectCollection< CartesianLine > cartesianLineCollection,
                              final String cartesianLineType,
                              final String projectorType,
                              final String projectionZonesType,
                              final String projectionZonesUsageContext ) {
        // Always call the superclass constructor first!
        super();

        _clientProperties = pClientProperties;

        // Avoid chicken-or-egg null pointer problems during startup.
        _layerCollection = LayerUtilities.makeLayerCollection();

        try {
            initPane( cartesianLineCollection, 
                      cartesianLineType,
                      projectorType, 
                      projectionZonesType,
                      projectionZonesUsageContext );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public String getNewCartesianLineLabelDefault() {
        // Forward this method to the Linear Object Properties Pane.
        return _linearObjectPropertiesPane.getNewLinearObjectLabelDefault();
    }

    public String getUniqueCartesianLineLabel( final String cartesianLineLabelCandidate ) {
        // Forward this method to the Linear Object Properties Pane.
        return _linearObjectPropertiesPane
                .getUniqueLinearObjectLabel( cartesianLineLabelCandidate );
    }

    public LinearObjectProperties getLinearObjectProperties() {
        // Forward this method to the Linear Object Properties Pane.
        return _linearObjectPropertiesPane.getLinearObjectProperties();
    }

    private void initPane( final GraphicalObjectCollection< CartesianLine > cartesianLineCollection,
                           final String cartesianLineType,
                           final String projectorType,
                           final String projectionZonesType,
                           final String projectionZonesUsageContext ) {
        final String cartesianLineLabelDefault = cartesianLineType;
        _linearObjectPropertiesPane = new LinearObjectPropertiesPane( _clientProperties,
                                                                      cartesianLineLabelDefault,
                                                                      cartesianLineCollection,
                                                                      projectorType,
                                                                      projectionZonesType,
                                                                      projectionZonesUsageContext );

        _cartesianLinePlacementPane = new CartesianLinePlacementPane( _clientProperties );

        setSpacing( 12 );
        setPadding( new Insets( 6 ) );

        final ObservableList< Node > layout = getChildren();
        layout.addAll( _linearObjectPropertiesPane, _cartesianLinePlacementPane );

        // Make sure the Placement Pane always gets grow priority.
        VBox.setVgrow( _cartesianLinePlacementPane, Priority.ALWAYS );

        // If the Projector status changes in any way, update the Preview.
        _linearObjectPropertiesPane._linearObjectPropertiesControls._useAsProjectorCheckBox
                .selectedProperty().addListener( ( observable, oldValue, newValue ) -> {
                    final CartesianLine cartesianLine = new CartesianLine();
                    updateCartesianLineModel( cartesianLine );
                } );
        _linearObjectPropertiesPane._linearObjectPropertiesControls._projectionZonesSelector
                .setOnAction( evt -> {
                    final CartesianLine cartesianLine = new CartesianLine();
                    updateCartesianLineModel( cartesianLine );
                } );

        // Make sure that any edits to one end position control affect the
        // others, so that the two coordinate systems are always in sync.
        // NOTE: We also update the model if the start position changes, in
        // order to get the preview to update.
        // NOTE: We make a dummy object to serve as an intermediary for now,
        // until we move the coordinate system transform code to a utility
        // class, so that we don't prematurely apply changes and prevent
        // reversion to a previous state.
        _cartesianLinePlacementPane._startCartesianPositionPane._xPositionEditor
                .focusedProperty().addListener( ( observable, oldValue, newValue ) -> {
                    final CartesianLine cartesianLine = new CartesianLine();
                    updateCartesianLineModel( cartesianLine );
                } );
        _cartesianLinePlacementPane._startCartesianPositionPane._yPositionEditor
                .focusedProperty().addListener( ( observable, oldValue, newValue ) -> {
                    final CartesianLine cartesianLine = new CartesianLine();
                    updateCartesianLineModel( cartesianLine );
                } );
        _cartesianLinePlacementPane._endPositionPane._cartesianPositionPane._xPositionEditor
                .focusedProperty().addListener( ( observable, oldValue, newValue ) -> {
                    final CartesianLine cartesianLine = new CartesianLine();
                    updateCartesianLineModel( cartesianLine );
                    updateCartesianLineView( cartesianLine );
                } );
        _cartesianLinePlacementPane._endPositionPane._cartesianPositionPane._yPositionEditor
                .focusedProperty().addListener( ( observable, oldValue, newValue ) -> {
                    final CartesianLine cartesianLine = new CartesianLine();
                    updateCartesianLineModel( cartesianLine );
                    updateCartesianLineView( cartesianLine );
                } );
        _cartesianLinePlacementPane._endPositionPane._polarPositionPane._anglePane._angleEditor
                .focusedProperty().addListener( ( observable, oldValue, newValue ) -> {
                    final CartesianLine cartesianLine = new CartesianLine();
                    updateCartesianLineModel( cartesianLine );
                    updateCartesianLineView( cartesianLine );
                } );
        _cartesianLinePlacementPane._endPositionPane._polarPositionPane._anglePane._angleSlider
                .valueProperty().addListener( ( observable, oldValue, newValue ) -> {
                    final CartesianLine cartesianLine = new CartesianLine();
                    updateCartesianLineModel( cartesianLine );
                    updateCartesianLineView( cartesianLine );
                } );
        _cartesianLinePlacementPane._endPositionPane._polarPositionPane._distanceEditor
                .focusedProperty().addListener( ( observable, oldValue, newValue ) -> {
                    final CartesianLine cartesianLine = new CartesianLine();
                    updateCartesianLineModel( cartesianLine );
                    updateCartesianLineView( cartesianLine );
                } );
    }

    public boolean isCartesianLineLabelUnique( final String cartesianLineLabelCandidate ) {
        // Forward this method to the Linear Object Properties Pane.
        return _linearObjectPropertiesPane
                .isLinearObjectLabelUnique( cartesianLineLabelCandidate );
    }

    public void saveEdits() {
        // NOTE: We only need to save edits in non-bean-based components.
        _cartesianLinePlacementPane.saveEdits();
    }

    public void setGesturesEnabled( final boolean gesturesEnabled ) {
        // Forward this method to the Cartesian Line Placement Pane.
        _cartesianLinePlacementPane.setGesturesEnabled( gesturesEnabled );
    }

    public void setLayerCollection( final ObservableList< LayerProperties > layerCollection ) {
        // Cache a local copy of the Layer Collection.
        _layerCollection = layerCollection;

        // Forward this method to the Linear Object Properties Pane.
        _linearObjectPropertiesPane.setLayerCollection( layerCollection );
    }

    /**
     * Set the new Scrolling Sensitivity for all of the sliders.
     *
     * @param scrollingSensitivity
     *            The sensitivity of the mouse scroll wheel
     */
    public void setScrollingSensitivity( final ScrollingSensitivity scrollingSensitivity ) {
        // Forward this method to the Cartesian Line Placement Pane.
        _cartesianLinePlacementPane.setScrollingSensitivity( scrollingSensitivity );
    }

    public void updateCartesianLineModel( final CartesianLine cartesianLine ) {
        // Get all of the Linear Object properties.
        final LinearObjectProperties linearObjectProperties = getLinearObjectProperties();
        cartesianLine.setLabel( linearObjectProperties.getLabel() );

        // Cache the current Layer selection via Layer Name lookup.
        final String layerName = linearObjectProperties.getLayerName();
        final LayerProperties layer = LayerUtilities.getLayerByName( _layerCollection, layerName );
        cartesianLine.setLayer( layer );

        // Update the Projector values.
        cartesianLine.setUseAsProjector( linearObjectProperties.isUseAsProjector() );
        cartesianLine
                .setNumberOfProjectionZones( linearObjectProperties.getNumberOfProjectionZones() );

        // Forward this method to the Cartesian Line Placement Pane.
        _cartesianLinePlacementPane.updateCartesianLineModel( cartesianLine );
    }

    public void updateLayerNameSelection( final CartesianLine cartesianLine ) {
        // Forward this method to the Linear Object Properties Pane.
        _linearObjectPropertiesPane.updateLayerNameSelection( cartesianLine );
    }

    public void updateCartesianLineView( final CartesianLine cartesianLine ) {
        // Forward this method to the Linear Object Properties Pane.
        _linearObjectPropertiesPane.updateLinearObjectView( cartesianLine );

        // Forward this method to the Cartesian Line Placement Pane.
        _cartesianLinePlacementPane.updateCartesianLineView( cartesianLine );
    }

    public void toggleGestures() {
        // Forward this method to the Cartesian Line Placement Pane.
        _cartesianLinePlacementPane.toggleGestures();
    }

    public void updateAngleUnit( final AngleUnit angleUnit ) {
        // Forward this method to the Linear Object Placement Pane.
        _cartesianLinePlacementPane.updateAngleUnit( angleUnit );
    }

    public void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        // Forward this method to the Linear Object Placement Pane.
        _cartesianLinePlacementPane.updateDistanceUnit( distanceUnit );
    }

    public void updateLayerNames( final boolean preserveSelectedLayerByIndex,
                                  final boolean preserveSelectedLayerByName ) {
        // Forward this method to the Linear Object Properties Pane.
        _linearObjectPropertiesPane.updateLayerNames( preserveSelectedLayerByIndex,
                                                   preserveSelectedLayerByName );
    }

    public void updateLayerNames( final LayerProperties currentLayer ) {
        final ObservableList< LayerProperties > layerCollection = _layerCollection;
        final int currentLayerIndex = LayerUtilities.getLayerIndex( layerCollection, currentLayer );

        // Forward this method to the Linear Object Properties Pane.
        _linearObjectPropertiesPane.updateLayerNames( currentLayerIndex );
    }

    public void updatePositioning( final CartesianLine cartesianLine ) {
        // Forward this method to the Cartesian Line Placement Pane.
        _cartesianLinePlacementPane.updatePositioning( cartesianLine );
    }

    public void updatePreview( final CartesianLine cartesianLineCurrent ) {
        // Forward this method to the Cartesian Line Placement Pane.
        _cartesianLinePlacementPane.updatePreview( cartesianLineCurrent );
    }

}
