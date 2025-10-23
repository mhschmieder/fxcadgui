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
 * This file is part of the fxcadgui Library
 *
 * You should have received a copy of the MIT License along with the fxcadgui
 * Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxcadgui
 */
package com.mhschmieder.fxcadgui.layout;

import com.mhschmieder.fxcadcontrols.control.SurfaceSelectorControls;
import com.mhschmieder.fxcadgui.model.Region2DProperties;
import com.mhschmieder.fxcadgui.model.SurfaceProperties;
import com.mhschmieder.fxcadgui.util.SurfaceNameManager;
import com.mhschmieder.fxcontrols.GuiUtilities;
import com.mhschmieder.fxcontrols.layout.LayoutFactory;
import com.mhschmieder.fxgraphics.paint.ColorUtilities;
import com.mhschmieder.jcommons.text.NumberFormatUtilities;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public final class SurfacesPane extends BorderPane {

    // Declare the column header names.
    public static final String                    COLUMN_HEADER_SURFACE_ID    = "ID";             //$NON-NLS-1$
    public static final String                    COLUMN_HEADER_SURFACE_NAME  = "Surface Name";   //$NON-NLS-1$
    public static final String                    COLUMN_HEADER_STATUS        = "Status";         //$NON-NLS-1$
    public static final String                    COLUMN_HEADER_MATERIAL_NAME = "Material Name";  //$NON-NLS-1$

    // Declare static constant to use for symbolically referencing column
    // indices, to ensure no errors, and ease of extensibility.
    private static final int                      COLUMN_FIRST                = 0;
    private static final int                      COLUMN_SURFACE_ID           = COLUMN_FIRST;
    private static final int                      COLUMN_SURFACE_NAME         =
                                                                      COLUMN_SURFACE_ID + 1;
    private static final int                      COLUMN_STATUS               =
                                                                COLUMN_SURFACE_NAME + 1;
    private static final int                      COLUMN_MATERIAL_NAME        = COLUMN_STATUS + 1;
    private static final int                      COLUMN_LAST                 =
                                                              COLUMN_MATERIAL_NAME;
    public static final int                       NUMBER_OF_COLUMNS           =
                                                                    ( COLUMN_LAST - COLUMN_FIRST )
                                                                            + 1;

    // Declare static constant to use for symbolically referencing grid row
    // indices, to ensure no errors, and ease of extensibility.
    // TODO: Find and use an existing symbolic constant for Surface Count.
    public static final int                       ROW_HEADER                  = 0;
    public static final int                       ROW_SURFACE_FIRST           = ROW_HEADER + 1;
    public static final int                       ROW_SURFACE_LAST            =
                                                                   ( ROW_SURFACE_FIRST + 4 ) - 1;
    public static final int                       ROW_LAST                    = ROW_SURFACE_LAST;

    // Keep track of how many unique Column Headers there are (due to spanning).
    public static final int                       NUMBER_OF_COLUMN_HEADERS    = NUMBER_OF_COLUMNS;

    // Declare the main GUI nodes that are needed beyond initialization time.
    protected GridPane                            _surfaceSelectorGrid;

    private Label                                 _surfaceSelectorTitle;

    private Label                                 _surfaceIdLabel;
    private Label                                 _surfaceNameLabel;
    private Label                                 _statusLabel;
    private Label                                 _materialNameLabel;

    // Give global scope to the Surface Selector Groups so we can access the
    // controls directly without casting from Node via getChildren().
    protected List< SurfaceSelectorControls >     _surfaceSelectorGroups;

    // Cache a reference to the Surface Properties.
    protected ObservableList< SurfaceProperties > _surfaceProperties;

    // Number format cache used for locale-specific number formatting of
    // uniquefier appendices.
    protected NumberFormat                        _uniquefierNumberFormat;

    // Cache the full Client Properties (System Type, Locale, etc.).
    protected ClientProperties                  _clientProperties;

    public SurfacesPane( final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super();

        _clientProperties = pClientProperties;

        // TODO: Find and use an existing symbolic constant for Surface Count.
        _surfaceSelectorGroups = new ArrayList<>( 4 );

        try {
            initPane();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void bindProperties() {
        if ( _surfaceProperties != null ) {
            int surfaceIndex = 0;
            for ( final SurfaceProperties numberedSurfaceProperties : _surfaceProperties ) {
                final SurfaceSelectorControls surfaceSelectorGroup = _surfaceSelectorGroups
                        .get( surfaceIndex++ );

                surfaceSelectorGroup._surfaceStatusButton.selectedProperty()
                        .bindBidirectional( numberedSurfaceProperties.surfaceBypassedProperty() );
                surfaceSelectorGroup._surfaceMaterialSelector.valueProperty()
                        .bindBidirectional( numberedSurfaceProperties.surfaceMaterialProperty() );
            }
        }
    }

    public ObservableList< SurfaceProperties > getSurfaceProperties() {
        return _surfaceProperties;
    }

    private void initPane() {
        _uniquefierNumberFormat = NumberFormatUtilities
                .getUniquefierNumberFormat( _clientProperties.locale );

        // Use banner style headlining for the overall title.
        _surfaceSelectorTitle = GuiUtilities.getTitleLabel( "Surfaces" ); //$NON-NLS-1$
        final HBox titlePane = GuiUtilities.getTitlePane( _surfaceSelectorTitle );

        // Make the array of individual Surface Selector controls.
        _surfaceSelectorGrid = new GridPane();

        // We center the column header labels to follow modern conventions.
        _surfaceIdLabel = GuiUtilities.getColumnHeader( COLUMN_HEADER_SURFACE_ID );
        _surfaceNameLabel = GuiUtilities.getColumnHeader( COLUMN_HEADER_SURFACE_NAME );
        _statusLabel = GuiUtilities.getColumnHeader( COLUMN_HEADER_STATUS );
        _materialNameLabel = GuiUtilities.getColumnHeader( COLUMN_HEADER_MATERIAL_NAME );

        // Force all the column header labels to center within the grid.
        GridPane.setHalignment( _surfaceIdLabel, HPos.CENTER );
        GridPane.setHalignment( _surfaceNameLabel, HPos.CENTER );
        GridPane.setHalignment( _statusLabel, HPos.CENTER );
        GridPane.setHalignment( _materialNameLabel, HPos.CENTER );

        _surfaceSelectorGrid.setPadding( new Insets( 6.0d ) );
        _surfaceSelectorGrid.setHgap( 16d );
        _surfaceSelectorGrid.setVgap( 8.0d );

        _surfaceSelectorGrid.add( _surfaceIdLabel, COLUMN_SURFACE_ID, ROW_HEADER );
        _surfaceSelectorGrid.add( _surfaceNameLabel, COLUMN_SURFACE_NAME, ROW_HEADER );
        _surfaceSelectorGrid.add( _statusLabel, COLUMN_STATUS, ROW_HEADER );
        _surfaceSelectorGrid.add( _materialNameLabel, COLUMN_MATERIAL_NAME, ROW_HEADER );

        // TODO: Find and use an existing symbolic constant for Surface Count.
        // NOTE: We do not register callbacks here because we are instead using
        // data binding as we did earlier in the table-based implementation.
        for (int surfaceIndex = 0; surfaceIndex < Region2DProperties.NUMBER_OF_SURFACES; surfaceIndex++ ) {
            final int surfaceRowIndex = ROW_SURFACE_FIRST + surfaceIndex;
            final SurfaceSelectorControls surfaceSelectorControls =
                                                                  new SurfaceSelectorControls( _clientProperties,
                                                                                               true,
                                                                                               surfaceRowIndex );

            // NOTE: Need a final variable to pass to registered listeners.
            final int currentSurfaceIndex = surfaceIndex;

            _surfaceSelectorGrid.add( surfaceSelectorControls._surfaceIdLabel,
                                      COLUMN_SURFACE_ID,
                                      surfaceRowIndex );

            // Make sure the Surface ID Label is centered, so that it lines up
            // with the column label and it's clear what column it goes with.
            GridPane.setHalignment( surfaceSelectorControls._surfaceIdLabel, HPos.CENTER );

            _surfaceSelectorGrid.add( surfaceSelectorControls._surfaceNameEditor,
                                      COLUMN_SURFACE_NAME,
                                      surfaceRowIndex );

            surfaceSelectorControls._surfaceNameEditor.focusedProperty()
                    .addListener( ( observableValue, wasFocused, isNowFocused ) -> {
                        // Once the Surface Name Editor loses editing focus,
                        // uniquefy the Name (if necessary) and commit.
                        if ( !isNowFocused ) {
                            updateSurfaceNameView( currentSurfaceIndex );
                        }
                    } );
            surfaceSelectorControls._surfaceNameEditor.setOnKeyReleased( keyEvent -> {
                // Detect the ENTER key while the Surface Name Editor has focus,
                // and use it to save edits in place.
                final KeyCombination keyCombo = new KeyCodeCombination( KeyCode.ENTER );
                if ( keyCombo.match( keyEvent ) ) {
                    // Save the current Surface Name edits to its view controller.
                    updateSurfaceNameView( currentSurfaceIndex );

                    // Consume the ENTER key so it doesn't get processed twice.
                    keyEvent.consume();
                }
            } );

            _surfaceSelectorGrid.add( surfaceSelectorControls._surfaceStatusButton,
                                      COLUMN_STATUS,
                                      surfaceRowIndex );

            _surfaceSelectorGrid.add( surfaceSelectorControls._surfaceMaterialSelector,
                                      COLUMN_MATERIAL_NAME,
                                      surfaceRowIndex );

            _surfaceSelectorGroups.add( surfaceSelectorControls );
        }

        _surfaceSelectorGrid.setAlignment( Pos.CENTER );

        // Stack the two panes with sufficient gaps to distinguish purpose.
        setTop( titlePane );
        setCenter( _surfaceSelectorGrid );

        setPadding( new Insets( 6.0d ) );
        setAlignment( _surfaceSelectorGrid, Pos.CENTER );
    }

    // Reset all fields to the default values.
    public void reset() {
        for ( final SurfaceProperties numberedSurfaceProperties : _surfaceProperties ) {
            numberedSurfaceProperties.setSurfaceBypassed( SurfaceProperties.BYPASSED_DEFAULT );
            numberedSurfaceProperties.setSurfaceMaterial( SurfaceProperties.SURFACE_MATERIAL_DEFAULT);
        }
    }

    public void setForegroundFromBackground( final Color backColor ) {
        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = LayoutFactory.makeRegionBackground( backColor );
        setBackground( background );

        // Forward this method to the lower-level layout containers.
        final Color foregroundColor = ColorUtilities.getForegroundFromBackground( backColor );

        _surfaceSelectorTitle.setTextFill( foregroundColor );

        // Set the column header label foreground.
        GuiUtilities.setColumnHeaderLabelForeground( _surfaceSelectorGrid,
                                                     COLUMN_FIRST,
                                                     COLUMN_LAST,
                                                     foregroundColor );

        // Set the row header label foreground.
        GuiUtilities.setRowHeaderLabelForeground( _surfaceSelectorGrid,
                                                  ROW_SURFACE_FIRST,
                                                  ROW_SURFACE_LAST,
                                                  NUMBER_OF_COLUMN_HEADERS,
                                                  COLUMN_SURFACE_ID,
                                                  NUMBER_OF_COLUMNS,
                                                  foregroundColor );
    }

    public void setSurfaceProperties( final ObservableList< SurfaceProperties > surfaceProperties ) {
        // Temporarily unbind the properties so we can set new Surface
        // Properties.
        unbindProperties();

        // Cache the new Surface Properties.
        _surfaceProperties = surfaceProperties;

        // Update the GUI with the new values, since we have too many
        // complexities to be able to use data binding here.
        int surfaceIndex = 0;
        for ( final SurfaceProperties numberedSurfaceProperties : surfaceProperties ) {
            final SurfaceSelectorControls surfaceSelectorGroup = _surfaceSelectorGroups
                    .get( surfaceIndex++ );

            surfaceSelectorGroup._surfaceNameEditor
                    .setText( numberedSurfaceProperties.getSurfaceName() );
            surfaceSelectorGroup._surfaceStatusButton
                    .setSelected( numberedSurfaceProperties.isSurfaceBypassed() );
            surfaceSelectorGroup._surfaceMaterialSelector
                    .setValue( numberedSurfaceProperties.getSurfaceMaterial() );
        }

        // Bind the data model to the respective GUI components.
        bindProperties();
    }

    private void updateSurfaceNameView( final int surfaceIndex ) {
        if ( ( _surfaceProperties == null )
                || ( _surfaceProperties.size() < Region2DProperties.NUMBER_OF_SURFACES ) ) {
            return;
        }

        final SurfaceSelectorControls surfaceSelectorGroup = _surfaceSelectorGroups
                .get( surfaceIndex );
        final String newSurfaceName = surfaceSelectorGroup._surfaceNameEditor.getText();

        final SurfaceProperties surfaceProperties = _surfaceProperties.get( surfaceIndex );

        // Get a unique Surface Name from the candidate name.
        final String correctedSurfaceName = SurfaceNameManager
                .getUniqueSurfaceName( _surfaceProperties,
                                       surfaceProperties,
                                       newSurfaceName,
                                       _uniquefierNumberFormat );

        final String oldSurfaceName = surfaceProperties.getSurfaceName();
        final boolean surfaceNameChanged = !correctedSurfaceName.equals( oldSurfaceName );

        if ( surfaceNameChanged ) {
            // Update the Surface Name if it changed.
            surfaceProperties.setSurfaceName( correctedSurfaceName );

            // Re-sync the GUI as well, as we don't use bindings for this due to
            // the complexities and re-entrancy of name uniqueness algorithms.
            surfaceSelectorGroup._surfaceNameEditor.setValue( correctedSurfaceName );
        }
    }

    private void unbindProperties() {
        if ( _surfaceProperties != null ) {
            int surfaceIndex = 0;
            for ( final SurfaceProperties numberedSurfaceProperties : _surfaceProperties ) {
                final SurfaceSelectorControls surfaceSelectorGroup = _surfaceSelectorGroups
                        .get( surfaceIndex++ );

                surfaceSelectorGroup._surfaceStatusButton.selectedProperty()
                        .unbindBidirectional(
                                numberedSurfaceProperties.surfaceBypassedProperty() );
                surfaceSelectorGroup._surfaceMaterialSelector.valueProperty()
                        .unbindBidirectional(
                                numberedSurfaceProperties.surfaceMaterialProperty() );
            }
        }
    }

    public void updateSurfaceNames() {
        if ( _surfaceProperties != null ) {
            int surfaceIndex = 0;
            for ( final SurfaceProperties numberedSurfaceProperties : _surfaceProperties ) {
                final SurfaceSelectorControls surfaceSelectorGroup = _surfaceSelectorGroups
                        .get( surfaceIndex++ );

                final String surfaceName = numberedSurfaceProperties.getSurfaceName();
                surfaceSelectorGroup._surfaceNameEditor.setValue( surfaceName );
            }
        }
    }

    public void updateView() {
        // NOTE: All other properties are kept up-to-date via data binding.
        updateSurfaceNames();
    }

}
