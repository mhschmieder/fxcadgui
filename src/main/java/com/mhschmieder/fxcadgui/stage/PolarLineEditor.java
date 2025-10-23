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

import com.mhschmieder.fxcadcontrols.layout.PolarLinePane;
import com.mhschmieder.fxcadgraphics.GraphicalObjectCollection;
import com.mhschmieder.fxcadgraphics.PolarLine;
import com.mhschmieder.fxcontrols.stage.ObjectPropertiesEditor;
import com.mhschmieder.fxgraphics.input.ScrollingSensitivity;
import com.mhschmieder.fxlayergraphics.model.LayerProperties;
import com.mhschmieder.jcommons.branding.ProductBranding;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jphysics.AngleUnit;
import com.mhschmieder.jphysics.DistanceUnit;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public final class PolarLineEditor extends ObjectPropertiesEditor {

    // Declare the main content pane.
    protected PolarLinePane                          _polarLinePane;

    // Maintain a reference to the current Polar Line object.
    protected PolarLine                              _polarLineReference;

    // Maintain a reference to the Polar Line collection.
    protected GraphicalObjectCollection< PolarLine > _polarLineCollection;
    
    // Allow for customization of Polar Line Type (name identifier, not behavior).
    protected String _polarLineType;

    // Allow for customization of Projector Type (name identifier, not behavior).
    protected String _projectorType;

    // Allow for customization of Projection Zones Type (name identifier, not behavior).
    protected String _projectionZonesType;
    
    // Projection Zones usage context, for constructing tooltips.
    protected String _projectionZonesUsageContext;

    @SuppressWarnings("nls")
    public PolarLineEditor( final boolean insertMode,
                            final GraphicalObjectCollection< PolarLine > polarLineCollection,
                            final ProductBranding productBranding,
                            final ClientProperties pClientProperties,
                            final boolean pResetApplicable,
                            final String polarLineType,
                            final String projectorType,
                            final String projectionZonesType,
                            final String projectionZonesUsageContext ) {
        // Always call the superclass constructor first!
        super( insertMode, 
               polarLineType, 
               "polarLine", 
               productBranding, 
               pClientProperties,
               pResetApplicable );
        
        _polarLineCollection = polarLineCollection;
        
        _polarLineType = polarLineType;
        _projectorType = projectorType;
        _projectionZonesType = projectionZonesType;
        _projectionZonesUsageContext = projectionZonesUsageContext;

        // Start with a default Polar Line until editing.
        _polarLineReference = PolarLine.getDefaultPolarLine();

        try {
            initStage();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    // Open the textField initialized to a mouse-selected Polar Line.
    public void editPolarLine( final PolarLine polarLine ) {
        // Make sure an active editing session is always enabled when visible.
        setDisable( false );

        // Make sure the Layer Names are up-to-date, and that we avoid any side
        // effects against the selected Layer for the new Polar Line Reference.
        final LayerProperties currentLayer = polarLine.getLayer();
        updateLayerNames( currentLayer );

        // Ensure that an object already being edited doesn't reset and re-sync
        // the reference lest it flush its view for the last cached values.
        if ( _polarLineReference.equals( polarLine ) ) {
            return;
        }

        // Replace the current Polar Line reference with the one selected
        // for the Edit action when opening this window.
        setPolarLineReference( polarLine );

        // Update the TextField to match the selected Polar Line.
        updateView();
    }

    public PolarLine getPolarLineReference() {
        return _polarLineReference;
    }

    public String getNewPolarLineLabelDefault() {
        // Forward this method to the Polar Line Pane.
        return _polarLinePane.getNewPolarLineLabelDefault();
    }

    private void initStage() {
        // First have the superclass initialize its content.
        initStage( "/icons/fatCow/Measure16.png", //$NON-NLS-1$
                   _polarLineType,
                   1200,
                   420,
                   false,
                   false,
                   false );
    }

    @Override
    protected Node loadContent() {
        // Instantiate and return the custom Content Node.
        _polarLinePane = new PolarLinePane( clientProperties,
                                            _polarLineCollection,
                                            _polarLineType,
                                            _projectorType,
                                            _projectionZonesType,
                                            _projectionZonesUsageContext );
        return _polarLinePane;
    }

    @Override
    protected void reset() {
        // Cache the current values that we want to preserve.
        // TODO: Determine whether location is the best positional field to
        // save/restore.
        // final Point2D location = _polarLineReference.getLocation();
        final String polarLineLabel = _polarLineReference.getLabel();

        // Make a default Polar Line to effectively reset all the fields.
        final PolarLine polarLineDefault = PolarLine
                .getDefaultPolarLine();
        _polarLineReference.setPolarLine( polarLineDefault );

        // Restore the fields we want to preserve.
        // _polarLineReference.setLocation( location );
        _polarLineReference.setLabel( polarLineLabel );

        // Update the view to match the new model, but don't apply it yet.
        updateView();
    }

    @Override
    public void setDisable( final boolean disable ) {
        // First, disable anything that is shared as part of the parent class,
        // such as the action buttons.
        super.setDisable( disable );

        // Forward this method to the Polar Line Pane.
        _polarLinePane.setDisable( disable );
    }

    public void setGesturesEnabled( final boolean gesturesEnabled ) {
        // Forward this method to the Polar Line Pane.
        _polarLinePane.setGesturesEnabled( gesturesEnabled );
    }

    public void setLayerCollection( final ObservableList< LayerProperties > layerCollection ) {
        // Forward this method to the Polar Line Pane.
        _polarLinePane.setLayerCollection( layerCollection );
    }

    public void setPolarLineReference( final PolarLine polarLine ) {
        _polarLineReference = polarLine;
    }

    /**
     * Set the new Scrolling Sensitivity for all of the sliders.
     *
     * @param scrollingSensitivity
     *            The sensitivity of the mouse scroll wheel
     */
    public void setScrollingSensitivity( final ScrollingSensitivity scrollingSensitivity ) {
        // Forward this method to the Polar Line Pane.
        _polarLinePane.setScrollingSensitivity( scrollingSensitivity );
    }

    @Override
    protected void updateObjectPropertiesView() {
        // Forward this method to the Polar Line Pane.
        _polarLinePane.updatePolarLineView( _polarLineReference );
    }

    @Override
    protected void updateObjectPropertiesModel() {
        // Forward this method to the Polar Line Pane.
        _polarLinePane.updatePolarLineModel( _polarLineReference );
    }

    public void updateLayerNameSelection() {
        // Forward this method to the Polar Line Pane.
        _polarLinePane.updateLayerNameSelection( _polarLineReference );
    }

    public void toggleGestures() {
        // Forward this method to the Polar Line Pane.
        _polarLinePane.toggleGestures();
    }

    public void updateAngleUnit( final AngleUnit angleUnit ) {
        // Forward this method to the Polar Line Pane.
        _polarLinePane.updateAngleUnit( angleUnit );

        // Make sure all displayed fields update to the new Angle Unit.
        // NOTE: We skip this if running as a modal dialog, as this change can
        // only come from the dialog not showing anyway, and can hit
        // performance.
        if ( isEditMode() ) {
            updateObjectPropertiesView();
        }
    }

    public void updateDistanceUnit( final DistanceUnit distanceUnit ) {
        // Forward this method to the Polar Line Pane.
        _polarLinePane.updateDistanceUnit( distanceUnit );

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
        // Forward this method to the Polar Line Pane.
        _polarLinePane.updateLayerNames( preserveSelectedLayerByIndex,
                                                   preserveSelectedLayerByName );
    }

    public void updateLayerNames( final LayerProperties currentLayer ) {
        // Forward this method to the Polar Line Pane.
        _polarLinePane.updateLayerNames( currentLayer );
    }

    // TODO: Verify whether we need to synchronize both positions.
    @Override
    public void updatePositioning() {
        // Forward this method to the Polar Line Pane.
        _polarLinePane.updatePositioning( _polarLineReference );
    }

    @Override
    public void updatePreview() {
        // Forward this method to the Polar Line Pane.
        _polarLinePane.updatePreview( _polarLineReference );
    }

}
