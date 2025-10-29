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

import com.mhschmieder.fxcadcontrols.control.LinearObjectPropertiesControls;
import com.mhschmieder.fxcadcontrols.model.LinearObjectProperties;
import com.mhschmieder.fxcadgraphics.GraphicalObject;
import com.mhschmieder.fxcadgraphics.GraphicalObjectCollection;
import com.mhschmieder.fxcadgraphics.LinearObject;
import com.mhschmieder.fxgui.util.GuiUtilities;
import com.mhschmieder.fxlayercontrols.util.LayerPropertiesManager;
import com.mhschmieder.fxlayergraphics.Layer;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.util.List;

public class LinearObjectPropertiesPane extends BorderPane {

    // Declare the table column header names.
    private static final String        COLUMN_HEADER_LINEAR_OBJECT_LABEL = "Unique Label";         //$NON-NLS-1$
    private static final String        COLUMN_HEADER_LAYER            = "Layer";                    //$NON-NLS-1$
    private static final String        COLUMN_HEADER_PROJECTOR_DEFAULT = "Projector";               //$NON-NLS-1$
    private static final String        COLUMN_HEADER_PROJECTION_ZONES_DEFAULT = "Projection Zones"; //$NON-NLS-1$

    // Declare static constant to use for symbolically referencing grid column
    // indices, to ensure no errors, and ease of extensibility.
    public static final int            COLUMN_FIRST                   = 0;
    public static final int            COLUMN_LINEAR_OBJECT_LABEL     = COLUMN_FIRST;
    public static final int            COLUMN_LAYER                   = COLUMN_LINEAR_OBJECT_LABEL + 1;
    public static final int            COLUMN_PROJECTOR               = COLUMN_LAYER + 1;
    public static final int            COLUMN_PROJECTION_ZONES        = COLUMN_PROJECTOR + 1;
    public static final int            COLUMN_LAST                    = COLUMN_PROJECTION_ZONES;
    public static final int            NUMBER_OF_COLUMNS              =
                                                         ( COLUMN_LAST - COLUMN_FIRST ) + 1;

    // Declare static constant to use for symbolically referencing grid row
    // indices, to ensure no errors, and ease of extensibility.
    public static final int            ROW_HEADER                     = 0;
    public static final int            ROW_PROPERTIES_FIRST           = ROW_HEADER + 1;
    public static final int            ROW_PROPERTIES_LAST            = ROW_PROPERTIES_FIRST;
    public static final int            ROW_LAST                       = ROW_PROPERTIES_LAST;

    // Keep track of how many unique Column Headers there are (due to spanning).
    public static final int            NUMBER_OF_COLUMN_HEADERS       = NUMBER_OF_COLUMNS;

    // Declare the main GUI nodes that are needed beyond initialization time.
    protected GridPane                 _linearObjectPropertiesGrid;

    // Declare the Linear Object Properties Controls.
    public LinearObjectPropertiesControls _linearObjectPropertiesControls;

    // Cache the Linear Object Properties, for data binding.
    protected LinearObjectProperties _linearObjectProperties;

    public LinearObjectPropertiesPane( final ClientProperties pClientProperties,
                                       final String linearObjectLabelDefault,
                                       final GraphicalObjectCollection< ? extends LinearObject > linearObjectCollection ) {
        this( pClientProperties, 
              linearObjectLabelDefault, 
              linearObjectCollection, 
              COLUMN_HEADER_PROJECTOR_DEFAULT,
              COLUMN_HEADER_PROJECTION_ZONES_DEFAULT,
              null );
    }

    public LinearObjectPropertiesPane( final ClientProperties pClientProperties,
                                       final String linearObjectLabelDefault,
                                       final GraphicalObjectCollection< ? extends LinearObject > linearObjectCollection,
                                       final String projectorType,
                                       final String projectionZonesType,
                                       final String projectionZonesUsageContext ) {
        // Always call the superclass constructor first!
        super();

        // Make default Linear Object Properties to give a valid reference for
        // later updates.
        _linearObjectProperties = new LinearObjectProperties(
                linearObjectLabelDefault,
                LayerPropertiesManager.DEFAULT_LAYER_NAME,
                LinearObject.USE_AS_PROJECTOR_DEFAULT,
                LinearObject.NUMBER_OF_PROJECTION_ZONES_DEFAULT );

        try {
            initPane( pClientProperties, 
                      linearObjectLabelDefault, 
                      linearObjectCollection, 
                      projectorType,
                      projectionZonesType,
                      projectionZonesUsageContext );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private final void bindProperties() {
        // Bind the Linear Object Properties to their respective controls.
        // NOTE: Bind the label property to our custom value property vs. the
        // textField's built-in text property, as this is designed to more reliably
        // reflect committed edits vs. incomplete or uncorrected typing.
        _linearObjectPropertiesControls._linearObjectLabelEditor.textProperty()
                .bindBidirectional( _linearObjectProperties.labelProperty() );
        _linearObjectPropertiesControls._layerSelector.valueProperty()
                .bindBidirectional( _linearObjectProperties.layerNameProperty() );
        _linearObjectPropertiesControls._useAsProjectorCheckBox.selectedProperty()
                .bindBidirectional( _linearObjectProperties.useAsProjectorProperty() );
        _linearObjectPropertiesControls._projectionZonesSelector.valueProperty()
                .bindBidirectional( _linearObjectProperties.numberOfProjectionZonesProperty() );
    }

    public final String getNewLinearObjectLabelDefault() {
        // Forward this method to the Linear Object Properties Group.
        return _linearObjectPropertiesControls.getNewLinearObjectLabelDefault();
    }

    public final String getUniqueLinearObjectLabel( final String linearObjectLabelCandidate ) {
        // Forward this method to the Linear Object Properties Group.
        return _linearObjectPropertiesControls.getUniqueLinearObjectLabel( linearObjectLabelCandidate );
    }

    public final LinearObjectProperties getLinearObjectProperties() {
        return _linearObjectProperties;
    }

    private final void initPane( final ClientProperties pClientProperties,
                                 final String linearObjectLabelDefault,
                                 final GraphicalObjectCollection< ? extends LinearObject > linearObjectCollection,
                                 final String projectorType,
                                 final String projectionZonesType,
                                 final String projectionZonesUsageContext ) {
        // Make the grid of individual Linear Object Properties controls.
        _linearObjectPropertiesGrid = new GridPane();

        // We center the column header labels to follow common conventions.
        final Label linearObjectLabelLabel = GuiUtilities
                .getColumnHeader( COLUMN_HEADER_LINEAR_OBJECT_LABEL );
        final Label layerLabel = GuiUtilities.getColumnHeader( COLUMN_HEADER_LAYER );
        final Label projectorLabel = GuiUtilities.getColumnHeader( projectorType );
        final Label projectionZonesLabel = GuiUtilities.getColumnHeader( projectionZonesType );

        // Force all the labels to center within the grid.
        GridPane.setHalignment( linearObjectLabelLabel, HPos.CENTER );
        GridPane.setHalignment( layerLabel, HPos.CENTER );
        GridPane.setHalignment( projectorLabel, HPos.CENTER );
        GridPane.setHalignment( projectionZonesLabel, HPos.CENTER );

        _linearObjectPropertiesGrid.setPadding( new Insets( 6.0d ) );
        _linearObjectPropertiesGrid.setHgap( 16d );
        _linearObjectPropertiesGrid.setVgap( 2.0d );

        _linearObjectPropertiesGrid.add( linearObjectLabelLabel, COLUMN_LINEAR_OBJECT_LABEL, ROW_HEADER );
        _linearObjectPropertiesGrid.add( layerLabel, COLUMN_LAYER, ROW_HEADER );
        _linearObjectPropertiesGrid.add( projectorLabel, COLUMN_PROJECTOR, ROW_HEADER );
        _linearObjectPropertiesGrid.add( projectionZonesLabel, COLUMN_PROJECTION_ZONES, ROW_HEADER );

        // Make the individual Linear Object Properties Controls and place in a
        // Grid.
        _linearObjectPropertiesControls = new LinearObjectPropertiesControls( pClientProperties,
                                                                              true,
                                                                              linearObjectLabelDefault,
                                                                              linearObjectCollection,
                                                                              projectorType,
                                                                              projectionZonesType,
                                                                              projectionZonesUsageContext );

        _linearObjectPropertiesGrid.add( _linearObjectPropertiesControls._linearObjectLabelEditor,
                                         COLUMN_LINEAR_OBJECT_LABEL,
                                         ROW_PROPERTIES_FIRST );
        _linearObjectPropertiesGrid.add( _linearObjectPropertiesControls._layerSelector,
                                         COLUMN_LAYER,
                                         ROW_PROPERTIES_FIRST );
        _linearObjectPropertiesGrid.add( _linearObjectPropertiesControls._useAsProjectorCheckBox,
                                         COLUMN_PROJECTOR,
                                         ROW_PROPERTIES_FIRST );
        _linearObjectPropertiesGrid.add( _linearObjectPropertiesControls._projectionZonesSelector,
                                         COLUMN_PROJECTION_ZONES,
                                         ROW_PROPERTIES_FIRST );

        // Center the grid, as that will always be the easiest on the eyes.
        _linearObjectPropertiesGrid.setAlignment( Pos.CENTER );

        // Center the grid, as there are no other layout elements.
        setCenter( _linearObjectPropertiesGrid );

        setPadding( new Insets( 6.0d ) );

        // Prevent small drop-lists from minimizing their width below wide
        // labels.
        _linearObjectPropertiesControls._projectionZonesSelector.minWidthProperty()
                .bind( projectionZonesLabel.widthProperty() );

        // Bind the Linear Object Properties to their respective controls.
        bindProperties();
    }

    // Find out if the candidate label is unique.
    public final boolean isLinearObjectLabelUnique( final String linearObjectLabelCandidate ) {
        // Forward this method to the Linear Object Properties Group.
        return _linearObjectPropertiesControls.isLinearObjectLabelUnique( linearObjectLabelCandidate );
    }

    public final void setLayerCollection( final List<Layer> layerCollection ) {
        // Forward this method to the Linear Object Properties Group.
        _linearObjectPropertiesControls.setLayerCollection( layerCollection );
    }

    public final void updateLayerNameSelection( final GraphicalObject linearObject ) {
        // Forward this method to the Linear Object Properties Group.
        final String layerName = linearObject.getLayerName();
        final SingleSelectionModel< String > selectionModel =
                                                            _linearObjectPropertiesControls._layerSelector
                                                                    .getSelectionModel();
        selectionModel.select( layerName );
    }

    public final void updateLinearObjectView( final LinearObject linearObject ) {
        // Update the table to match the new Linear Object Properties.
        final LinearObjectProperties linearObjectProperties = getLinearObjectProperties();
        linearObjectProperties.setLabel( linearObject.getLabel() );
        linearObjectProperties.setLayerName( linearObject.getLayerName() );

        // Update the Projector values.
        linearObjectProperties.setUseAsProjector( linearObject.isUseAsProjector() );
        linearObjectProperties.setNumberOfProjectionZones( linearObject.getNumberOfProjectionZones() );

        // Make sure the cached textField value matches the latest saved label.
        _linearObjectPropertiesControls._linearObjectLabelEditor.setValue( linearObject.getLabel() );
    }

    public final void updateLayerNames( final boolean preserveSelectedLayerByIndex,
                                        final boolean preserveSelectedLayerByName ) {
        // Forward this method to the Linear Object Properties Group.
        _linearObjectPropertiesControls._layerSelector.updateLayerNames( preserveSelectedLayerByIndex,
                                                                         preserveSelectedLayerByName );
    }

    public final void updateLayerNames( final int currentSelectedIndex ) {
        updateLayerNames( currentSelectedIndex, true, false );
    }

    public final void updateLayerNames( final int currentSelectedIndex,
                                        final boolean preserveSelectedLayerByIndex,
                                        final boolean preserveSelectedLayerByName ) {
        // Forward this method to the Linear Object Properties Group.
        _linearObjectPropertiesControls._layerSelector.updateLayerNames( currentSelectedIndex,
                                                                         preserveSelectedLayerByIndex,
                                                                         preserveSelectedLayerByName );
    }

}
