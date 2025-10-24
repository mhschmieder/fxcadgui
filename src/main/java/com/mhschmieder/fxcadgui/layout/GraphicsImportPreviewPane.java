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
 * This file is part of the fxcadgui Library.
 *
 * You should have received a copy of the MIT License along with the fxcadgui
 * Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxcadgui
 */
package com.mhschmieder.fxcadgui.layout;

import com.mhschmieder.fxcadgraphics.DrawingLimits;
import com.mhschmieder.fxcadgui.model.DrawingLimitsProperties;
import com.mhschmieder.fxchart.control.ChartLabeledControlFactory;
import com.mhschmieder.fxcontrols.control.LabeledControlFactory;
import com.mhschmieder.fxcontrols.control.XComboBox;
import com.mhschmieder.fxcontrols.util.RegionUtilities;
import com.mhschmieder.fxdxfimport.DxfShapeGroup;
import com.mhschmieder.fxdxfimport.GraphicsImportOptions;
import com.mhschmieder.fxgui.layout.UnitlessPositionPane;
import com.mhschmieder.fxgui.util.GuiUtilities;
import com.mhschmieder.fxphysicscontrols.control.PhysicsControlFactory;
import com.mhschmieder.fxphysicsgui.model.Extents2DProperties;
import com.mhschmieder.fxphysicsgui.util.FxPhysicsUtilities;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jcommons.util.SystemType;
import com.mhschmieder.jphysics.DistanceUnit;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.commons.math3.util.FastMath;

/**
 * This is the main content pane for Graphics Import Preview windows.
 */
public final class GraphicsImportPreviewPane extends GridPane {

    /**
     * This class exposes a protected method to get the layout to redraw, but
     * may not actually fix the problem it was intended to address. Still, may
     * be useful.
     */
    public final class GeometryImportContainer extends VBox {
        public void forceSetNeedsLayout( final boolean needsLayout ) {
            setNeedsLayout( needsLayout );
        }
    }

    protected static final double                 DEFAULT_SCROLL_DELTA                 = 1.3d;

    protected static final double                 IMPORTED_GRAPHICS_STROKE_WIDTH_RATIO = 0.75d;

    public XComboBox< DistanceUnit >              _distanceUnitSelector;
    private GraphicsImportDrawingLimitsSourcePane _drawingLimitsSourcePane;
    private UnitlessPositionPane _minimumPane;
    private UnitlessPositionPane _maximumPane;

    /**
     * The Reset Button brings the Drawing Limits back to the selected source.
     */
    private Button                                _drawingLimitsResetButton;

    /**
     * Cache the application's Drawing Limits for real-time bounds queries.
     */
    private DrawingLimitsProperties _applicationDrawingLimitsProperties;

    /**
     * Cache the Graphics Import Options as a global singleton reference.
     */
    private GraphicsImportOptions                 _graphicsImportOptions;

    /**
     * Cache the imported geometry for later use, as this window is modeless.
     */
    private DxfShapeGroup                         _geometryContainer;

    /**
     * Wraps {@link #_geometryContainer} to receive a scale transform so that:
     * <ol>
     * <li>the result of the transform is considered in the calculation of the
     * bounds of the geometry preview (in concert with
     * _importedGeometryPreviewGroup) and</li>
     * <li>the transform doesn't have to be removed from the given geometry
     * later (why the transform is not on {@link #_geometryContainer} )</li>
     * </ol>
     */
    private Group                                 _geometryGroup;

    /** The x axis displays ticks along the bottom of the Sound Field. */
    protected NumberAxis                          _xAxis;

    /** The y axis displays ticks along the left of the Sound Field. */
    protected NumberAxis                          _yAxis;

    /**
     * Cache the node representation of the Prospective Drawing Limits so we can
     * remove the old one before adding the new one, when something changes.
     */
    private Rectangle                             _drawingLimitsNode;

    /**
     * Wraps {@link #_geometryGroup} for bounds calculation including its
     * transform.
     */
    private Group                                 _importedGeometryPreviewGroup;

    /**
     * Anchor panes make it easier to align the axes with the graphics.
     */
    private AnchorPane                            _importedGeometryPreviewAnchorPane;

    /**
     * Container for the whole graphics preview. This is removed and regenerated
     * in {@link #updateGeometryPreview}.
     */
    private StackPane                             _importedGeometryPreviewStackPane;

    /**
     * Current zoom which corresponds to the current Sound Field size.
     */
    public Bounds                                 _zoomBox;

    protected double                              _scrollDeltaY                        = 0.0d;
    protected double                              _scrollScale                         = 1.0d;

    /** Cache the model space to screen scale factor, for zooming etc. */
    private double                                _modelSpaceToScreenScaleFactor;

    /** Cache the current Background Color as it is needed for new visuals. */
    // private Color _backColor;

    /**
     * Cache the Client Properties (System Type, Locale, etc.).
     */
    public ClientProperties                     _clientProperties;

    /**
     * Cache the listeners so that we can remove them and re-add them during
     * programmatic changes, to avoid order-dependency and side effects.
     */
    private EventHandler< ActionEvent >           distanceUnitSelectionHandler;
    private EventHandler< ActionEvent >           drawingLimitsResetHandler;
    private ChangeListener< Toggle >              drawingLimitsSourceChangeListener;

    public GraphicsImportPreviewPane( final String productName,
                                      final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super();

        _clientProperties = pClientProperties;

        // Avoid potential null pointers prior to global reference settings.
        _applicationDrawingLimitsProperties = new DrawingLimitsProperties( true );
        _graphicsImportOptions = new GraphicsImportOptions();

        // Avoid potential null pointers on empty or unfinished import actions.
        _geometryContainer = null;
        _geometryGroup = null;

        // Avoid null pointers at startup by making a minimal rectangle to
        // represent uninitialized Prospective Drawing Limits.
        _drawingLimitsNode = new Rectangle( 0.0d, 0.0d, 1.0d, 1.0d );

        _importedGeometryPreviewGroup = null;
        _importedGeometryPreviewStackPane = null;

        _zoomBox = new BoundingBox( Extents2DProperties.X_METERS_DEFAULT,
                                    Extents2DProperties.Y_METERS_DEFAULT,
                                    Extents2DProperties.WIDTH_METERS_DEFAULT,
                                    Extents2DProperties.HEIGHT_METERS_DEFAULT );

        _modelSpaceToScreenScaleFactor = 1.0d;

        // _backColor = Color.BLACK;

        try {
            initPane( productName );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    private void addCallbackListeners() {
        // Add the event handler for the Distance Unit Selector.
        if ( distanceUnitSelectionHandler == null ) {
            makeDistanceUnitSelectionHandler();
        }
        _distanceUnitSelector.setOnAction( distanceUnitSelectionHandler );

        // Add the event handler for the Drawing Limits Reset Button.
        if ( drawingLimitsResetHandler == null ) {
            makeDrawingLimitsResetHandler();
        }
        _drawingLimitsResetButton.setOnAction( drawingLimitsResetHandler );

        // Add the event handler for the Drawing Limits Source Buttons.
        if ( drawingLimitsSourceChangeListener == null ) {
            makeDrawingLimitsSourceChangeListener();
        }
        _drawingLimitsSourcePane._drawingLimitsSourceToggleGroup.selectedToggleProperty()
                .addListener( drawingLimitsSourceChangeListener );
    }

    private void addDrawingLimitsNode() {
        if ( _drawingLimitsNode != null ) {
            if ( _geometryGroup != null ) {
                // Try to prevent the Drawing Limits Node from obscuring other
                // parts of the GUI outside the chart preview.
                // NOTE: We have to give some fudge factor for stroke width.
                final Bounds geometryBounds = _geometryContainer.getBoundsInLocal();
                final double fudgeFactor = 2.0d * _geometryContainer.getStrokeWidth();
                final Rectangle geometryBoundsAdjusted =
                                                       new Rectangle( geometryBounds.getMinX()
                                                               - fudgeFactor,
                                                                      geometryBounds.getMinY()
                                                                              - fudgeFactor,
                                                                      geometryBounds.getWidth()
                                                                              + ( 2.0d * fudgeFactor ),
                                                                      geometryBounds.getHeight()
                                                                              + ( 2.0d * fudgeFactor ) );
                _drawingLimitsNode.setClip( geometryBoundsAdjusted );

                // Add the Drawing Limits Node to the encapsulating Node Group.
                _geometryGroup.getChildren().add( _drawingLimitsNode );

                // Make sure the Drawing Limits node is visible by being on top,
                // as imported geometry may have a lot of strong colors and
                // thick strokes going on.
                _drawingLimitsNode.toFront();
            }
        }
    }

    /**
     * This method encapsulates the Grid Pane layout position of the Imported
     * Graphics Preview Node.
     *
     * @param importedGeometryPreviewNode
     *            Imported geometry preview node
     */
    private void addImportedGeometryPreviewNode( final Node importedGeometryPreviewNode ) {
        add( importedGeometryPreviewNode, 0, 1, 3, 1 );
    }

    /**
     * Adds mouse event handlers which will make the graphical nodes follow the
     * user's mouse as they drag, as well as detecting context menu triggers.
     **/
    protected void addMouseEventHandlers() {
        // Add scroll-zoom handlers.
        final Node clickableNode = getGraphicsImportClickableNode();
        clickableNode.setOnScroll( this::zoom );
    }

    public void adjustToZoomBox() {
        // Reset the chart range based on current Zoom Box and Distance Unit.
        resetChartRange();

        // Scale the graphics by the new zoom amount.
        _importedGeometryPreviewGroup
                .setScaleX( _importedGeometryPreviewGroup.getScaleX() * _scrollScale );
        _importedGeometryPreviewGroup
                .setScaleY( _importedGeometryPreviewGroup.getScaleY() * _scrollScale );
    }

    /**
     * Resets the Drawing Limits and throw out user edits.
     */
    private void doResetDrawingLimits() {
        // Reset the Drawing Limits to whichever was chosen as the source.
        final Toggle drawingLimitsSource = _drawingLimitsSourcePane._drawingLimitsSourceToggleGroup
                .getSelectedToggle();
        resetDrawingLimits( drawingLimitsSource );
    }

    /**
     * Specifies what node should be used to populate a Click Location.
     *
     * @return the node to listen on for mouse events used for location
     */
    private Node getGraphicsImportClickableNode() {
        return _importedGeometryPreviewGroup != null
            ? _importedGeometryPreviewStackPane // _importedGeometryPreviewGroup
            : _importedGeometryPreviewAnchorPane; // _importedGraphicsPreviewNode;
    }

    // TODO: Break this out into custom layout panes, as this method is
    //  getting a bit long and hard to read and verify or modify.
    private void initPane( final String productName ) {
        // Specifically query the Graphics Import Distance Unit, as most DXF
        // files are unitless and it is unlikely this unit will match the
        // current preference in the application itself.
        _distanceUnitSelector = PhysicsControlFactory.makeDistanceUnitSelector( 
                _clientProperties,
                true,
                true,
                DistanceUnit.defaultValue() );
        _distanceUnitSelector
                .setTooltip( new Tooltip( "Distance Unit for Graphics Import Source" ) ); //$NON-NLS-1$

        final HBox distanceUnitPane = GuiUtilities.getLabeledComboBoxPane( "Distance Unit", //$NON-NLS-1$
                                                                           _distanceUnitSelector );

        final Label pleaseSelectUnitLabel =
                                          new Label( "Please Select the Distance Unit Used in the Graphics File:" ); //$NON-NLS-1$
        final VBox labelAndComboBox = new VBox( pleaseSelectUnitLabel, distanceUnitPane );

        final Node measurementUnitsNode = GuiUtilities
                .getTitledBorderWrappedNode( labelAndComboBox,
                                             "Measurement Units for Graphics Import" ); //$NON-NLS-1$

        _drawingLimitsSourcePane = new GraphicsImportDrawingLimitsSourcePane( productName );

        final Node drawingLimitsSourceNode = GuiUtilities
                .getTitledBorderWrappedNode( _drawingLimitsSourcePane,
                                             "Drawing Limits Source for Graphics Import" ); //$NON-NLS-1$

        // Stack the Measurement Units and Drawing Limits Source as they roughly
        // match the combined height of the Drawing Limits editing controls and
        // Reset Button.
        final VBox parameterPane = new VBox();
        parameterPane.getChildren().addAll( measurementUnitsNode, drawingLimitsSourceNode );

        // Present the user with lower left and upper right corner choices, as
        // this is easier to correlate to the Graphic Preview than Origin and
        // Width/Height as they will likely adjust this to match axis marks.
        final Label minimumPaneLabel = GuiUtilities.getColumnHeader( "Lower Left Corner" ); //$NON-NLS-1$
        _minimumPane = new UnitlessPositionPane( _clientProperties );
        final Label maximumPaneLabel = GuiUtilities.getColumnHeader( "Upper Right Corner" ); //$NON-NLS-1$
        _maximumPane = new UnitlessPositionPane( _clientProperties );

        final GridPane minMaxGrid = new GridPane();
        minMaxGrid.setHgap( 10.0d );
        minMaxGrid.add( minimumPaneLabel, 0, 0 );
        minMaxGrid.add( _minimumPane, 0, 1 );
        minMaxGrid.add( maximumPaneLabel, 1, 0 );
        minMaxGrid.add( _maximumPane, 1, 1 );

        GridPane.setHalignment( minimumPaneLabel, HPos.CENTER );
        GridPane.setHalignment( maximumPaneLabel, HPos.CENTER );

        minMaxGrid.setAlignment( Pos.TOP_CENTER );

        // The Reset Button needs to be separate from the editing controls.
        _drawingLimitsResetButton = LabeledControlFactory.getResetButton( "Drawing Limits" ); //$NON-NLS-1$
        _drawingLimitsResetButton.setAlignment( Pos.CENTER_RIGHT );

        final Label resetButtonLabel =
                                     new Label( "Press Reset Button to Reset Corners to Drawing Limits Source:" ); //$NON-NLS-1$
        resetButtonLabel.setAlignment( Pos.CENTER_LEFT );

        // Make a Grid Pane to give more control over the Reset Button.
        final GridPane resetPane = new GridPane();
        resetPane.setHgap( 10.0d );
        resetPane.setAlignment( Pos.CENTER );
        resetPane.add( resetButtonLabel, 0, 0 );
        resetPane.add( _drawingLimitsResetButton, 1, 0 );

        // Make a general help label that describes how Drawing Limits are used.
        final Label helpLabel =
                              new Label( "These Drawing Limits Will Be Used as the New Prediction Plane After Graphics are Imported" ); //$NON-NLS-1$
        helpLabel.setAlignment( Pos.CENTER );
        final BorderPane helpPanel = new BorderPane();
        helpPanel.setCenter( helpLabel );

        // Stack the editing controls and the Reset Button so they stay grouped.
        final BorderPane minMaxPane = new BorderPane();
        minMaxPane.setTop( minMaxGrid );
        minMaxPane.setCenter( resetPane );
        minMaxPane.setBottom( helpPanel );

        final Node minMaxWrapper = GuiUtilities
                .getTitledBorderWrappedNode( minMaxPane,
                                             "Drawing Limits Extents for Graphics Import" ); //$NON-NLS-1$

        // Now lay out the main content pane.
        add( parameterPane, 0, 0 );
        add( minMaxWrapper, 1, 0 );

        setHgap( 8.0d );
        setVgap( 8.0d );

        setAlignment( Pos.CENTER );
        setPadding( new Insets( 12d ) );

        // Try to avoid the minimum and maximum layout panes from getting too
        // wide if the preview node is elongated, by giving horizontal grow
        // priority to the Distance Unit outermost layout container.
        GridPane.setHgrow( parameterPane, Priority.SOMETIMES );

        // Do not allow Application Drawing Limits until units have been chosen.
        _drawingLimitsSourcePane._applicationDrawingLimitsRadioButton.disableProperty()
                .bind( _distanceUnitSelector.valueProperty()
                        .isEqualTo( DistanceUnit.UNITLESS ) );
    }

    // Make the event handler for the Distance Unit Selector.
    private void makeDistanceUnitSelectionHandler() {
        distanceUnitSelectionHandler = evt -> {
            // Update the Distance Unit as it applies to Drawing Limits.
            final DistanceUnit distanceUnit = _distanceUnitSelector.getValue();
            setDistanceUnit( distanceUnit );
        };
    }

    private void makeDrawingLimitsNode() {
        final DrawingLimits prospectiveDrawingLimits
                = _graphicsImportOptions.getProspectiveDrawingLimits();
        _drawingLimitsNode = new Rectangle( prospectiveDrawingLimits.getX(),
                                            prospectiveDrawingLimits.getY(),
                                            prospectiveDrawingLimits.getWidth(),
                                            prospectiveDrawingLimits.getHeight() );
        _drawingLimitsNode.setStroke( Color.rgb( 255, 0, 255, 0.5d ) );
        _drawingLimitsNode.getStrokeDashArray().setAll( 1.0d, 1.5d );

        // TODO: Review whether this should be a set number, or some scale
        // factor of the stroke width from the Graphics Import, as right now
        // it can be too small to see or too blockish to avoid obscuring
        // details of nearby graphics.
        _drawingLimitsNode.strokeWidthProperty().bind( _geometryContainer.strokeWidthProperty() );

        // Rectangles are by default filled, so we have to turn that off.
        _drawingLimitsNode.setFill( null );
    }

    // Make the event handler for the Drawing Limits Reset Button.
    private void makeDrawingLimitsResetHandler() {
        drawingLimitsResetHandler = evt -> doResetDrawingLimits();
    }

    // Make the event handler for the Drawing Limits Source Buttons.
    private void makeDrawingLimitsSourceChangeListener() {
        drawingLimitsSourceChangeListener = ( observable, oldToggle, newToggle ) -> {
            // If no toggle button selected, re-select the previous button, but
            // wrap this in a JavaFX runLater thread to ensure all FX event code
            // precedes the custom selection.
            if ( ( newToggle == null ) ) {
                Platform.runLater( () -> _drawingLimitsSourcePane._drawingLimitsSourceToggleGroup
                        .selectToggle( oldToggle ) );
                return;
            }

            // Reset the Drawing Limits to whichever was chosen as the source.
            resetDrawingLimits( newToggle );
        };
    }

    private void removeCallbackListeners() {
        // Remove the event handler from the Distance Unit Selector.
        if ( distanceUnitSelectionHandler != null ) {
            _distanceUnitSelector.removeEventHandler( ActionEvent.ACTION,
                                                      distanceUnitSelectionHandler );
        }

        // Remove the event handler from the Drawing Limits Reset Button.
        if ( drawingLimitsResetHandler != null ) {
            _drawingLimitsResetButton.removeEventHandler( ActionEvent.ACTION,
                                                          drawingLimitsResetHandler );
        }

        // Remove the event handler for the Drawing Limits Source Buttons.
        if ( drawingLimitsSourceChangeListener != null ) {
            _drawingLimitsSourcePane._drawingLimitsSourceToggleGroup.selectedToggleProperty()
                    .removeListener( drawingLimitsSourceChangeListener );
        }
    }

    private void removeDrawingLimitsNode() {
        if ( _drawingLimitsNode != null ) {
            if ( _geometryGroup != null ) {
                _geometryGroup.getChildren().remove( _drawingLimitsNode );
            }
        }
    }

    /**
     * Resets the chart range based on current zoom, in current display units.
     */
    private void resetChartRange() {
        // NOTE: Working in metric units and only setting the axis labels to
        // display units works ONLY because we do not plot data in this context.
        final Bounds zoomCurrent = _zoomBox;
        final double x1 = zoomCurrent.getMinX();
        final double x2 = zoomCurrent.getMinX() + zoomCurrent.getWidth();
        _xAxis.setLowerBound( x1 );
        _xAxis.setUpperBound( x2 );

        final double y1 = zoomCurrent.getMinY();
        final double y2 = zoomCurrent.getMinY() + zoomCurrent.getHeight();
        _yAxis.setLowerBound( y1 );
        _yAxis.setUpperBound( y2 );
    }

    /**
     * Resets the Drawing Limits to whichever was chosen as the source.
     *
     * @param drawingLimitsSource
     *            The currently selected Drawing Limits Source {@link Toggle}
     */
    private void resetDrawingLimits( final Toggle drawingLimitsSource ) {
        // Inquire as to which Drawing Limits are the current source.
        if ( _drawingLimitsSourcePane._graphicsFileRadioButton.equals( drawingLimitsSource ) ) {
            // Use the bounds of the actual geometry from the graphics file
            // (when present -- otherwise null), not the Application Drawing
            // Limits or the Computed Bounds.
            final Rectangle2D geometryBounds = _geometryContainer.getExplicitBounds();
            final DrawingLimits prospectiveDrawingLimits
                    = new DrawingLimits( geometryBounds );
            setProspectiveDrawingLimits( prospectiveDrawingLimits );
        }
        else if ( _drawingLimitsSourcePane._computedBoundsRadioButton
                .equals( drawingLimitsSource ) ) {
            final Bounds computedBounds = _geometryContainer.getBoundsInLocal();
            final DrawingLimits prospectiveDrawingLimits
                    = new DrawingLimits( computedBounds );
            setProspectiveDrawingLimits( prospectiveDrawingLimits );
        }
        else if ( _drawingLimitsSourcePane._applicationDrawingLimitsRadioButton
                .equals( drawingLimitsSource ) ) {
            final Extents2DProperties applicationBounds = FxPhysicsUtilities
                    .getExtentsInDistanceUnit(
                            _applicationDrawingLimitsProperties,
                            _distanceUnitSelector.getValue() );
            final DrawingLimits prospectiveDrawingLimits
                    = new DrawingLimits( applicationBounds );
            setProspectiveDrawingLimits( prospectiveDrawingLimits );
        }
    }

    /**
     * Discards the geometry wrapper associated with previous Graphics Import.
     * <p>
     * NOTE: This is functionally required, as well as being necessary for
     *  hinting the garbage collector to release memory vs. holding onto obsolete
     *  references. This has been proven necessary using the heap profiler.
     * <p>
     * TODO: Separate out the graphics vs. GUI layout stuff, so we can also
     *  properly reset the GUI when user choices change -- graphics should only
     *  clear when freeing memory (from a cancel operation or failure e.g.) or
     *  when loading new geometry as part of updateGeometryContainer().
     */
    public void resetGraphicsImportPreview() {
        try {
            // Clear the Geometry Group wrapper, to recover resources.
            if ( _geometryGroup != null ) {
                _geometryGroup.getChildren().clear();
                _geometryGroup = null;
            }

            // Allow the garbage collector to remove the nodes if no longer
            // referenced elsewhere, by nulling this local reference.
            _geometryContainer = null;

            // Now we must remove all GUI references to the Import Graphics.
            if ( _importedGeometryPreviewGroup != null ) {
                if ( _importedGeometryPreviewAnchorPane != null ) {
                    _importedGeometryPreviewAnchorPane.getChildren()
                            .remove( _importedGeometryPreviewGroup );
                }
                _importedGeometryPreviewGroup.getChildren().clear();
                _importedGeometryPreviewGroup = null;
            }

            if ( _importedGeometryPreviewAnchorPane != null ) {
                if ( _importedGeometryPreviewStackPane != null ) {
                    _importedGeometryPreviewStackPane.getChildren()
                            .remove( _importedGeometryPreviewAnchorPane );
                }
                _importedGeometryPreviewAnchorPane.getChildren().clear();
                _importedGeometryPreviewAnchorPane = null;
            }

            if ( _importedGeometryPreviewStackPane != null ) {
                getChildren().remove( _importedGeometryPreviewStackPane );
                _importedGeometryPreviewStackPane.getChildren().clear();
                _importedGeometryPreviewStackPane = null;
            }
        }
        catch ( final NullPointerException | UnsupportedOperationException e ) {
            e.printStackTrace();
        }
    }

    public void setApplicationDrawingLimits( final DrawingLimitsProperties applicationDrawingLimitsProperties) {
        _applicationDrawingLimitsProperties = applicationDrawingLimitsProperties;
    }

    /**
     * Updates anything that depends on the current Distance Unit choice. Most
     * likely this will only apply to parameters with pre-known values that
     * can't be changed but which need to be displayed in a rational basis to
     * compare with initially unitless parameters from a Graphics File.
     *
     * @param distanceUnit
     *            The new Distance Unit choice
     */
    public void setDistanceUnit( final DistanceUnit distanceUnit ) {
        // Update the cached Distance Unit for the Graphics Import.
        // TODO: make this an Observable Value wrapper and bind to it?
        _graphicsImportOptions.setDistanceUnit( distanceUnit );

        // The only time that the minimum and maximum must be programmatically
        // reset, and the associated Drawing Limits node regenerated, is when
        // the Distance Unit changes while Application Drawing Limits are chosen
        // as the source.
        if ( _drawingLimitsSourcePane._applicationDrawingLimitsRadioButton.isSelected() ) {
            final Extents2DProperties applicationBounds = FxPhysicsUtilities
                    .getExtentsInDistanceUnit(
                            _applicationDrawingLimitsProperties,
                            distanceUnit );
            final DrawingLimits prospectiveDrawingLimits
                    = new DrawingLimits( applicationBounds );
            setProspectiveDrawingLimits( prospectiveDrawingLimits );
        }
    }

    public void setForegroundFromBackground( final Color backColor ) {
        // Cache the new Background Color as we need it for dynamic contrast
        // changes to the Chart Overlay Group as content is added.
        // _backColor = backColor;

        // Set the new Background first, so it sets context for CSS derivations.
        final Background background = RegionUtilities.makeRegionBackground( backColor );
        setBackground( background );
    }

    /**
     * Sets a new reference to a container for imported geometry, then wraps it
     * in a group and conditionally proposes Drawing Limits from the imported
     * geometry (if the parser supported explicit bounds).
     * <p>
     * It is advised to pre-clear the related variables and their children (when
     * relevant) to free up resources from previous graphics import actions.
     *
     * @param geometryContainer
     *            The container for group of imported geometry entities
     */
    public void setGeometryContainer( final DxfShapeGroup geometryContainer ) {
        // Clear any previous geometry to free memory by releasing references.
        resetGraphicsImportPreview();

        // Cache the Imported Geometry container so we can fetch it after this
        // window is dismissed, from anywhere in an application host.
        _geometryContainer = geometryContainer;

        // TODO: Avoid masking of Imported Geometry against window background?
        // NOTE: Users may prefer to see the true colors initially, regardless.
        // final Color foreColor = ColorUtilities.getForegroundFromBackground(
        // _backColor );
        // _geometryContainer.setForeground( foreColor, false );

        // Wrap the Imported Geometry in a new Group for easier bounds testing.
        _geometryGroup = new Group( _geometryContainer );

        // The y-axis is flipped for Screen Coordinates vs. Model Space.
        // NOTE: We do it at this level so it includes the Drawing Limits Node,
        // and also so that it doesn't have to be removed later during Import.
        _geometryGroup.setScaleY( -1d );

        // Set the initial graphics import options based on whatever defaults
        // make sense given the new geometry container context.
        _graphicsImportOptions.updateGraphicsImportOptions( _geometryContainer );

        // Use the new initial graphics import choices to set the GUI controls.
        setInitialGraphicsImportChoices();

        // Update the Cartesian Positions in the editors.
        updateCartesianPositions();

        // Update the associated graphics node for the Drawing Limits.
        updateDrawingLimitsNode();

        // Update the geometry preview to re-scale the geometry to fit.
        updateGeometryPreview();
    }

    private void setGraphicsImportDistanceUnit( final DistanceUnit graphicsImportDistanceUnit ) {
        // Initialize the GUI to reflect the initial Distance Unit.
        _distanceUnitSelector.setValue( graphicsImportDistanceUnit );
    }

    public void setGraphicsImportOptions( final GraphicsImportOptions graphicsImportOptions ) {
        _graphicsImportOptions = graphicsImportOptions;
    }

    /**
     * Encapsulates all of the initial import choices that are conditional in
     * nature and which can't be done through bindings due to API restrictions.
     * It is generally only called when importing new geometry.
     */
    private void setInitialGraphicsImportChoices() {
        // Remove the callback listeners from the main buttons and controls.
        removeCallbackListeners();

        // Do not allow Graphics File Drawing Limits if not present.
        // TODO: Make this an Observable Boolean and bind to it at startup?
        final boolean hasExplicitBounds = _geometryContainer.hasExplicitBounds();
        _drawingLimitsSourcePane._graphicsFileRadioButton.setDisable( !hasExplicitBounds );

        // Default to the computed bounds, as the likely best fit.
        _drawingLimitsSourcePane._computedBoundsRadioButton.setSelected( true );

        // Start with the initial Distance Unit choice (from the Graphics File,
        // if present), as it affects enablement of other controls.
        if ( _graphicsImportOptions.isInitialDistanceUnitFromGraphicsFile() ) {
            setGraphicsImportDistanceUnit( _graphicsImportOptions.getDistanceUnit() );
        }
        else {
            // TODO: Add a hint for "Choose One" as in the old string-based version
            //  of the Distance Unit Selector before we made it enum object based.
            //  We now say "unitless" in the drop-list and the displayed text field.
            setGraphicsImportDistanceUnit( DistanceUnit.UNITLESS );
        }

        // Re-add the callback listeners for the main buttons and controls.
        addCallbackListeners();
    }

    /*
     * Set the bounds for the Drawing Limits of the Graphics Import, which could
     * become the actual Drawing Limits if the user chooses. Top-to-bottom sense
     * is flipped, as we are in the model space context vs. screen pixel space.
     * <p>
     * NOTE: We make a copy, so that reference-switching via user choice
     * doesn't cause confusion -- especially if we convert units more than once.
     */
    private void setProspectiveDrawingLimits(
            final DrawingLimits pProspectiveDrawingLimits ) {
        // Cache a copy of the new prospective Drawing Limits in the Graphics
        // Import Options, so that we only have one reference to concern
        // ourselves with. This helps avoid too many interim conversions.
        _graphicsImportOptions.setProspectiveDrawingLimits(
                pProspectiveDrawingLimits );

        // Update the Cartesian Positions in the editors.
        updateCartesianPositions();

        // Update the associated graphics node for Prospective Drawing Limits.
        updateDrawingLimitsNode();
    }

    public void setZoomBox( final Bounds zoomBox ) {
        // Update the current Zoom Box.
        _zoomBox = zoomBox;

        // Adjust anything that is scaled or translated by the Zoom Box.
        adjustToZoomBox();
    }

    private void updateCartesianPositions() {
        // TODO: Show all three bounds, as well as the current clipping.
        final DrawingLimits prospectiveDrawingLimits
                = _graphicsImportOptions.getProspectiveDrawingLimits();
        _minimumPane.setCartesianPosition2D( prospectiveDrawingLimits
                .getMinimumPoint() );
        _maximumPane.setCartesianPosition2D( prospectiveDrawingLimits
                .getMaximumPoint() );
    }

    private void updateDrawingLimitsNode() {
        // Remove any existing node for old Drawing Limits.
        removeDrawingLimitsNode();

        // Make a new Drawing Limits node based on current metrics.
        makeDrawingLimitsNode();

        // Add this new graphical representation to the overall group container.
        addDrawingLimitsNode();
    }

    // TODO: Modularize this method for better decoupling and
    //  order-independence, noting that some redundant operations take place.
    @SuppressWarnings("nls")
    private void updateGeometryPreview() {
        // Add unitless Cartesian axes, set to scale to the Computed Bounds, as
        // those are the most useful for full context when the user chooses
        // a preferred Drawing Limits Source and Graphics Import Distance Unit.
        // TODO: Come up with an algorithm for the axes limits and ticks, for a
        //  reasonable tick increment, and then round the min and max to be
        //  integer multipliers of that increment, maintaining full containment.
        final Bounds computedBounds = _geometryContainer.getBoundsInLocal();
        final double minDimension =
                FastMath.min( computedBounds.getWidth(), computedBounds.getHeight() );
        final double tickUnit = FastMath.round( minDimension / 10.0d );

        _xAxis = ChartLabeledControlFactory.getUnitlessAxis( "X",
                                                             computedBounds.getMinX(),
                                                             computedBounds.getMaxX(),
                                                             tickUnit,
                                                             Side.BOTTOM );
        _yAxis = ChartLabeledControlFactory.getUnitlessAxis( "Y",
                                                             computedBounds.getMinY(),
                                                             computedBounds.getMaxY(),
                                                             tickUnit,
                                                             Side.LEFT );

        // NOTE: The extra Group layer is the only way to get the enclosing
        //  layout to honor size requests of a scaled Graphics Import Preview.
        _importedGeometryPreviewGroup = new Group( _geometryGroup );

        _importedGeometryPreviewAnchorPane = new AnchorPane();
        _importedGeometryPreviewAnchorPane.getChildren().addAll( _yAxis, _xAxis );
        AnchorPane.setLeftAnchor( _yAxis, 0.0d );
        AnchorPane.setTopAnchor( _yAxis, 0.0d );
        AnchorPane.setBottomAnchor( _xAxis, 0.0d );

        // Re-add the Imported Geometry Preview Node to the Grid Layout.
        _importedGeometryPreviewStackPane = new StackPane( _importedGeometryPreviewAnchorPane );
        addImportedGeometryPreviewNode( _importedGeometryPreviewStackPane );

        // Register all of the mouse event handlers (e.g. pop-up menu and data
        // tracker triggers and updaters, along with mouse drag handling etc.).
        // addMouseEventHandlers();

        // Defer the main layout until the axes have had a chance to expand to
        // their actual preferred widths and heights.
        // TODO: Review any order dependencies related to the deferred thread.
        Platform.runLater( () -> {
            // Get the user's screen size, for Full Screen Mode and user
            // statistics.
            // NOTE: This query is done on-the-fly as the user may switch
            //  screens during the application session.
            final Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            // final double screenWidth = visualBounds.getWidth();
            final double screenHeight = visualBounds.getHeight();

            // Scale the imported geometry group if its bounds are too big.
            final double maxVerticalScreenSpace = screenHeight - 40d;
            // final double maxHorizontalScreenSpace = screenWidth - 40d;

            final double anchorPaneOffsetLeft = _yAxis.getWidth();
            final Bounds geometryPreviewGroupLayoutBounds = _importedGeometryPreviewGroup != null
                ? _importedGeometryPreviewGroup.getLayoutBounds()
                : new BoundingBox( -500d, -500d, 500d, 500d );
            final Bounds anchorPaneBoundsInParent = _importedGeometryPreviewAnchorPane
                    .getBoundsInParent();
            AnchorPane.setLeftAnchor( _xAxis, anchorPaneOffsetLeft );

            // Anchoring to the bottom does appropriately size the yAxis, but
            // taking the strategy here of only anchoring to the top and left
            // AnchorPane.setBottomAnchor( yAxis, xAxis.getHeight() );
            // bigger or smaller?
            final double anchorPaneGapHorizontal = anchorPaneBoundsInParent.getWidth()
                    - anchorPaneOffsetLeft;
            double anchorPaneGapVertical =
                                         anchorPaneBoundsInParent.getHeight() - _xAxis.getHeight();

            // Add unused screen space.
            anchorPaneGapVertical += maxVerticalScreenSpace - getScene().getHeight();
            final double horizontalOverage = anchorPaneGapHorizontal
                    - geometryPreviewGroupLayoutBounds.getWidth();

            // In order to compare width and height in terms of re-scaling, we
            // need to adjust for the Aspect Ratio.
            final double widthToHeightAspectRatio = geometryPreviewGroupLayoutBounds.getWidth()
                    / geometryPreviewGroupLayoutBounds.getHeight();
            final double verticalOverage = ( anchorPaneGapVertical
                    - geometryPreviewGroupLayoutBounds.getHeight() ) * widthToHeightAspectRatio;

            if ( horizontalOverage < verticalOverage ) {
                // The graphic minX is closer to the left limit than the graphic
                // minY is to the bottom limit.
                _modelSpaceToScreenScaleFactor = anchorPaneGapHorizontal
                        / FastMath.abs( geometryPreviewGroupLayoutBounds.getWidth() );
            } // TODO: Switch to more of a "fuzzyEQ" strategy here.
            else if ( ( float ) geometryPreviewGroupLayoutBounds.getHeight() != 0f ) {
                // Scale the height to fit exactly.
                _modelSpaceToScreenScaleFactor = anchorPaneGapVertical
                        / FastMath.abs( geometryPreviewGroupLayoutBounds.getHeight() );
            }
            else {
                _modelSpaceToScreenScaleFactor = 1.0d;
            }

            // Finally, apply the model space to screen space scale factor.
            // TODO: Switch to more of a "fuzzyEQ" strategy here.
            if ( ( float ) _modelSpaceToScreenScaleFactor != 1f ) {
                final Scale scale = new Scale( _modelSpaceToScreenScaleFactor,
                                               _modelSpaceToScreenScaleFactor );
                _geometryGroup.getTransforms().add( scale );

                // Set the Stroke Width based on the display scaling ratio.
                final double displayToVenueScaleFactor = 1.0d / _modelSpaceToScreenScaleFactor;
                final double strokeWidth = IMPORTED_GRAPHICS_STROKE_WIDTH_RATIO
                        * displayToVenueScaleFactor;
                _geometryContainer.setStrokeWidth( strokeWidth );
            }
            _importedGeometryPreviewAnchorPane.getChildren().add( _importedGeometryPreviewGroup );
            AnchorPane.setTopAnchor( _importedGeometryPreviewGroup, 0.0d );
            AnchorPane.setTopAnchor( _xAxis,
                                     _importedGeometryPreviewGroup.getLayoutBounds().getHeight()
                                             + 1.0d );
            AnchorPane.setLeftAnchor( _importedGeometryPreviewGroup, anchorPaneOffsetLeft );
            AnchorPane.setLeftAnchor( _xAxis, anchorPaneOffsetLeft );
            _xAxis.setPrefWidth( _importedGeometryPreviewGroup.getLayoutBounds().getWidth() );
            _yAxis.setPrefHeight( _importedGeometryPreviewGroup.getLayoutBounds().getHeight() );
            _importedGeometryPreviewAnchorPane.setMaxWidth( _xAxis.getBoundsInParent().getMaxX() );
            StackPane.setAlignment( _importedGeometryPreviewAnchorPane, Pos.CENTER );

            // NOTE: Starting with macOS Catalina, this window goes back to
            //  default size and the lower left corner of the screen after
            //  initially sizing and centering correctly, from the second launch
            //  through to end of session, unless we make it resizable during the
            //  manual resizing and centering and then turn off the resizability
            //  flag right after sizing to fit the Imported Graphics content.
            if ( SystemType.MACOS.equals( _clientProperties.systemType ) ) {
                ( ( Stage ) ( getScene().getWindow() ) ).setResizable( true );
            }
            getScene().getWindow().sizeToScene();
            getScene().getWindow().centerOnScreen();
            if ( SystemType.MACOS.equals( _clientProperties.systemType ) ) {
                ( ( Stage ) ( getScene().getWindow() ) ).setResizable( false );
            }

            // Attempt to force a re-layout as layout sizes may have changed.
            setNeedsLayout( true );
        } );
    }

    private void zoom( final double zoomFactor, final Point2D zoomPositionPx ) {
        // First, determine whether any further zooming is possible. If not,
        // then zoom to the current zoom extents (if at the far end of the
        // scale) or do nothing (if at the near end of the scale in either
        // dimension, which is set to 0.5 meters x 0.5 meters), and then exit.
        // NOTE: Make sure to allow for zooming out once at maximum zoom.
        final Bounds zoomCurrent = _zoomBox;
        final double newWidth = zoomCurrent.getWidth() * zoomFactor;
        final double newHeight = zoomCurrent.getHeight() * zoomFactor;

        // Clip the zoom range to the Drawing Limits (if no imported geometry).
        // NOTE: We destroy the Aspect Ratio if we modify the width and/or
        //  height at this point in time. Besides, we already checked earlier
        //  against the zoom extents, so the worst that can happen here is
        //  that we use the correct zoom factor but shift the center of the
        //  zoom to keep the entire zoom window inside the zoom extents.
        final double newX = zoomPositionPx.getX() - ( 0.5d * newWidth );
        final double newY = zoomPositionPx.getY() - ( 0.5d * newHeight );

        // Check whether there is any resulting change to the Zoom Box.
        // NOTE: Commented out, because this prevents re-scaling if the window
        //  size changes, as we compare in meters instead of in pixels.
        // NOTE: Re-enabled, as it seems the calling contexts are in meters.
        final Bounds zoomAdjusted = new BoundingBox( newX, newY, newWidth, newHeight );
        if ( zoomAdjusted.equals( zoomCurrent ) ) {
            return;
        }

        // Zoom the view to the scaled and clipped new extents.
        setZoomBox( zoomAdjusted );
    }

    // NOTE: This is a more traditional scroll wheel handler, but can also
    //  cover gestures on a touch screen.
    public void zoom( final ScrollEvent event ) {
        if ( event.isDirect() ) {
            return;
        }

        // Try for slightly coarser resolution, to improve performance.
        _scrollDeltaY = event.getDeltaY();
        if ( FastMath.abs( _scrollDeltaY ) < 3.0d ) {
            return;
        }

        // TODO: Finish this new algorithm, and make use of the User Preference
        //  for Scrolling Sensitivity.
        final double scrollDeltaY = DEFAULT_SCROLL_DELTA;
        final double oldScrollScale = _scrollScale;
        double newScrollScale = oldScrollScale;
        _scrollDeltaY = event.getDeltaY();
        if ( _scrollDeltaY < 0.0d ) {
            newScrollScale /= scrollDeltaY;
        }
        else {
            newScrollScale *= scrollDeltaY;
        }
        double zoomFactor = newScrollScale - oldScrollScale;
        _scrollScale = zoomFactor;

        // TODO: Delete this modified old one-line algorithm after finishing
        //  the new algorithm above.
        final double zoomBasis = SystemType.MACOS.equals( _clientProperties.systemType )
            ? 1.0001d
            : 1.0003d;
        final double zoomExponent = event.getDeltaY();
        zoomFactor = FastMath.pow( zoomBasis, zoomExponent );

        final Point2D zoomPositionPx = new Point2D(
                                                    event.getSceneX()
                                                            / _modelSpaceToScreenScaleFactor,
                                                    event.getSceneY()
                                                            / _modelSpaceToScreenScaleFactor );
        zoom( zoomFactor, zoomPositionPx );
    }
}
