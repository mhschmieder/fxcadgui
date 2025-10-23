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
import com.mhschmieder.fxcadgraphics.GraphicalObjectCollection;
import com.mhschmieder.fxcadgraphics.PolarLine;
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

public final class PolarLinePane extends VBox {

    public LinearObjectPropertiesPane _linearObjectPropertiesPane;
    public PolarLinePlacementPane     _polarLinePlacementPane;

    /** Layer Collection reference. */
    private ObservableList< LayerProperties > _layerCollection;

    /** Client Properties (System Type, Locale, etc.). */
    public ClientProperties                 _clientProperties;

    public PolarLinePane( final ClientProperties pClientProperties,
                          final GraphicalObjectCollection< PolarLine > polarLineCollection,
                          final String polarLineType,
                          final String projectorType,
                          final String projectionZonesType,
                          final String projectionZonesUsageContext ) {
        // Always call the superclass constructor first!
        super();

        _clientProperties = pClientProperties;

        // Avoid chicken-or-egg null pointer problems during startup.
        _layerCollection = LayerUtilities.makeLayerCollection();

        try {
            initPane( polarLineCollection, 
                      polarLineType,
                      projectorType, 
                      projectionZonesType, 
                      projectionZonesUsageContext );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public String getNewPolarLineLabelDefault() {
        // Forward this method to the Linear Object Properties Pane.
        return _linearObjectPropertiesPane.getNewLinearObjectLabelDefault();
    }

    public String getUniquePolarLineLabel( final String polarLineLabelCandidate ) {
        // Forward this method to the Linear Object Properties Pane.
        return _linearObjectPropertiesPane
                .getUniqueLinearObjectLabel( polarLineLabelCandidate );
    }

    public LinearObjectProperties getLinearObjectProperties() {
        // Forward this method to the Linear Object Properties Pane.
        return _linearObjectPropertiesPane.getLinearObjectProperties();
    }

    private void initPane( final GraphicalObjectCollection< PolarLine > polarLineCollection,
                           final String polarLineType,
                           final String projectorType,
                           final String projectionZonesType,
                           final String projectionZonesUsageContext ) {
        final String polarLineLabelDefault = polarLineType;
        _linearObjectPropertiesPane = new LinearObjectPropertiesPane( _clientProperties,
                                                                      polarLineLabelDefault,
                                                                      polarLineCollection,
                                                                      projectorType,
                                                                      projectionZonesType,
                                                                      projectionZonesUsageContext );

        _polarLinePlacementPane = new PolarLinePlacementPane( _clientProperties );

        final ObservableList< Node > layout = getChildren();
        layout.addAll( _linearObjectPropertiesPane, _polarLinePlacementPane );

        setSpacing( 12 );
        setPadding( new Insets( 6 ) );

        // Make sure the Placement Pane always gets grow priority.
        VBox.setVgrow( _polarLinePlacementPane, Priority.ALWAYS );

        // If the Projector status changes in any way, update the Preview.
        _linearObjectPropertiesPane._linearObjectPropertiesControls._useAsProjectorCheckBox
                .selectedProperty().addListener( ( observable, oldValue, newValue ) -> {
                    final PolarLine polarLine = new PolarLine();
                    updatePolarLineModel( polarLine );
                } );
        _linearObjectPropertiesPane._linearObjectPropertiesControls._projectionZonesSelector
                .setOnAction( evt -> {
                    final PolarLine polarLine = new PolarLine();
                    updatePolarLineModel( polarLine );
                } );

        // Make sure that any edits to any of the coordinates or angles, update
        // the model so that the preview stays in sync.
        // NOTE: We make a dummy object to serve as an intermediary for now,
        // until we move the coordinate system transform code to a utility
        // class, so that we don't prematurely apply changes and prevent
        // reversion to a previous state.
        _polarLinePlacementPane._inclinometerPositionPane._xPositionEditor
                .focusedProperty().addListener( ( observable, oldValue, newValue ) -> {
                    final PolarLine polarLine = new PolarLine();
                    updatePolarLineModel( polarLine );
                } );
        _polarLinePlacementPane._inclinometerPositionPane._yPositionEditor
                .focusedProperty().addListener( ( observable, oldValue, newValue ) -> {
                    final PolarLine polarLine = new PolarLine();
                    updatePolarLineModel( polarLine );
                } );
        _polarLinePlacementPane._startPolarPositionPane._anglePane._angleEditor
                .focusedProperty().addListener( ( observable, oldValue, newValue ) -> {
                    final PolarLine polarLine = new PolarLine();
                    updatePolarLineModel( polarLine );
                } );
        _polarLinePlacementPane._startPolarPositionPane._anglePane._angleSlider
                .valueProperty().addListener( ( observable, oldValue, newValue ) -> {
                    final PolarLine polarLine = new PolarLine();
                    updatePolarLineModel( polarLine );
                } );
        _polarLinePlacementPane._startPolarPositionPane._distanceEditor.focusedProperty()
                .addListener( ( observable, oldValue, newValue ) -> {
                    final PolarLine polarLine = new PolarLine();
                    updatePolarLineModel( polarLine );
                } );
        _polarLinePlacementPane._endPolarPositionPane._anglePane._angleEditor
                .focusedProperty().addListener( ( observable, oldValue, newValue ) -> {
                    final PolarLine polarLine = new PolarLine();
                    updatePolarLineModel( polarLine );
                } );
        _polarLinePlacementPane._endPolarPositionPane._anglePane._angleSlider
                .valueProperty().addListener( ( observable, oldValue, newValue ) -> {
                    final PolarLine polarLine = new PolarLine();
                    updatePolarLineModel( polarLine );
                } );
        _polarLinePlacementPane._endPolarPositionPane._distanceEditor.focusedProperty()
                .addListener( ( observable, oldValue, newValue ) -> {
                    final PolarLine polarLine = new PolarLine();
                    updatePolarLineModel( polarLine );
                } );
    }

    public boolean isPolarLineLabelUnique( final String polarLineLabelCandidate ) {
        // Forward this method to the Linear Object Properties Pane.
        return _linearObjectPropertiesPane.isLinearObjectLabelUnique( polarLineLabelCandidate );
    }

    public void saveEdits() {
        // NOTE: We only need to save edits in non-bean-based components.
        _polarLinePlacementPane.saveEdits();
    }

    public void setGesturesEnabled( final boolean gesturesEnabled ) {
        // Forward this method to the Polar Line Placement Pane.
        _polarLinePlacementPane.setGesturesEnabled( gesturesEnabled );
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
        // Forward this method to the Polar Line Placement Pane.
        _polarLinePlacementPane.setScrollingSensitivity( scrollingSensitivity );
    }

    public void updatePolarLineModel( final PolarLine polarLine ) {
        // Get all of the Linear Object Properties.
        final LinearObjectProperties linearObjectProperties = getLinearObjectProperties();
        polarLine.setLabel( linearObjectProperties.getLabel() );

        // Cache the current Layer selection via Layer Name lookup.
        final String layerName = linearObjectProperties.getLayerName();
        final LayerProperties layer = LayerUtilities.getLayerByName( _layerCollection, layerName );
        polarLine.setLayer( layer );

        // Update the Projector values.
        polarLine.setUseAsProjector( linearObjectProperties.isUseAsProjector() );
        polarLine.setNumberOfProjectionZones( linearObjectProperties.getNumberOfProjectionZones() );

        // Forward this method to the Polar Line Placement Pane.
        _polarLinePlacementPane.updatePolarLineModel( polarLine );
    }

    public void updateLayerNameSelection( final PolarLine polarLine ) {
        // Forward this method to the Linear Object Properties Pane.
        _linearObjectPropertiesPane.updateLayerNameSelection( polarLine );
    }

    public void updatePolarLineView( final PolarLine polarLine ) {
        // Forward this method to the Linear Object Properties Pane.
        _linearObjectPropertiesPane.updateLinearObjectView( polarLine );

        // Forward this method to the Polar Line Placement Pane.
        _polarLinePlacementPane.updatePolarLineView( polarLine );
    }

    public void toggleGestures() {
        // Forward this method to the Polar Line Placement Pane.
        _polarLinePlacementPane.toggleGestures();
    }

    public void updateAngleUnit( final AngleUnit angleUnit ) {
        // Forward this method to the Polar Line Placement Pane.
        _polarLinePlacementPane.updateAngleUnit( angleUnit );
    }

    public void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        // Forward this method to the Polar Line Placement Pane.
        _polarLinePlacementPane.updateDistanceUnit( distanceUnit );
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

    public void updatePositioning( final PolarLine polarLine ) {
        // Forward this method to the Polar Line Placement Pane.
        _polarLinePlacementPane.updatePositioning( polarLine );
    }

    public void updatePreview( final PolarLine polarLineCurrent ) {
        // Forward this method to the Polar Line Placement Pane.
        _polarLinePlacementPane.updatePreview( polarLineCurrent );
    }

}
