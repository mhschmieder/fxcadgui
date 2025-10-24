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

import com.mhschmieder.fxcadgui.layout.GraphicsImportPreviewPane;
import com.mhschmieder.fxcadgui.model.DrawingLimitsProperties;
import com.mhschmieder.fxcadgui.util.CadHelpUtilities;
import com.mhschmieder.fxdxfimport.DxfShapeGroup;
import com.mhschmieder.fxdxfimport.GraphicsImportOptions;
import com.mhschmieder.fxgui.stage.NoticeBox;
import com.mhschmieder.fxgui.stage.XStage;
import com.mhschmieder.jcommons.branding.ProductBranding;
import com.mhschmieder.jcommons.util.ClientProperties;
import com.mhschmieder.jphysics.DistanceUnit;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;

/**
 * This Preview window is for use with Graphics Import actions, and is designed
 * to give the users a chance to clip the import and specify the Distance Unit.
 * Therefore, it is specifically oriented towards the peculiarities of unitless
 * DXF files for now, but could be enhanced later to switch its presentation
 * choices based on the file type of the imported graphics.
 */
public final class GraphicsImportPreview extends XStage {

    public static final String       GRAPHICS_IMPORT_PREVIEW_TITLE_DEFAULT =
                                                                           "Graphics Import Preview"; //$NON-NLS-1$

    // Declare the main content pane.
    public GraphicsImportPreviewPane _graphicsImportPreviewPane;

    // Declare the main action button bar.
    private ButtonBar              _actionButtonBar;

    // Declare the main action buttons.
    private Button                 _graphicsImportButton;
    private Button                 _cancelImportButton;
    private Button                 _helpButton;

    // Declare uninitialized pop-ups.
    private NoticeBox _graphicsImportHelp;

    // For the sake of post-processing, keep track of user cancellation.
    private boolean                  _canceled;

    public GraphicsImportPreview( final ProductBranding productBranding,
                                  final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super( Modality.WINDOW_MODAL,
               GRAPHICS_IMPORT_PREVIEW_TITLE_DEFAULT,
               "graphicsImportPreview",
               true,
               true,
               productBranding,
               pClientProperties );

        // Always default to not canceled, until user edits begin.
        _canceled = false;

        try {
            initStage();
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    /**
     * Cancel Button callback.
     */
    public void cancel() {
        // Set the "canceled" status to query in any context.
        setCanceled( true );

        // Now exit the window, whether modal or modeless.
        setVisible( false, false );
    }

    /**
     * Done Button callback.
     */
    public void done() {
        // Set the "canceled" status to query in any context.
        setCanceled( false );

        // Now exit the window, whether modal or modeless.
        setVisible( false, false );
    }

    protected void help() {
        // Display the Graphics Import Help until the user dismisses it.
        if ( _graphicsImportHelp != null ) {
            if ( !_graphicsImportHelp.isShowing() ) {
                _graphicsImportHelp.show( this );
            }
        }
    }

    private void initStage() {
        // First have the superclass initialize its content.
        // TODO: Review this default size, as we didn't provide one before.
        initStage( "/icons/fatCow/FileExtensionDwg16.png", 940d, 780d, false ); //$NON-NLS-1$

        // Build the main action button bar, and register its callbacks.
        _actionButtonBar = new ButtonBar();
        _actionButtonBar.setPadding( new Insets( 12d ) );

        _graphicsImportButton = com.mhschmieder.fxcontrols.control.LabeledControlFactory
                .getGraphicsImportButton( "Import Graphics Using Specified Distance Unit and Corner Points" ); //$NON-NLS-1$
        ButtonBar.setButtonData( _graphicsImportButton, ButtonData.OK_DONE );

        _cancelImportButton = com.mhschmieder.fxcontrols.control.LabeledControlFactory
                .getCancelImportButton();
        ButtonBar.setButtonData( _cancelImportButton, ButtonData.CANCEL_CLOSE );

        _helpButton = com.mhschmieder.fxcontrols.control.LabeledControlFactory.getHelpButton( false );
        ButtonBar.setButtonData( _helpButton, ButtonData.HELP );

        // Make the Graphics Import Button consume the ENTER key when possible.
        _graphicsImportButton.setDefaultButton( true );

        final ObservableList< Node > actionButtons = _actionButtonBar.getButtons();
        actionButtons.add( _graphicsImportButton );
        actionButtons.add( _cancelImportButton );
        actionButtons.add( _helpButton );

        _root.setBottom( _actionButtonBar );

        // Load the event handler for the Help Button.
        _helpButton.setOnAction( evt -> help() );

        // Load the event handler for the Import Graphics Button.
        _graphicsImportButton.setOnAction( evt -> done() );

        // Load the event handler for the Cancel Button.
        _cancelImportButton.setOnAction( evt -> cancel() );

        // Filter for the ENTER key so we can use it to trigger the OK Button.
        // TODO: Find a better way to filter for this so that we wait for focus
        //  lost on the editing controls, but this is probably not possible until
        //  the next revision of the JavaFX 8u40, at which point we probably have
        //  direct control of ENTER anyway by assigning the Default Button.
        // NOTE: Actually, this is a good way to deal with special keys as they
        //  can't be typed, so it is appropriate to test for down or pressed.
        // NOTE: We now look for ALT + ENTER vs. ENTER for OK, as we don't
        //  yet have a finer-grained way of allowing multi-row tables to process
        //  ENTER key events (to commit edits and go to the cell below) without
        //  also exiting the host window, thus disrupting workflow in the table.
        addEventFilter( KeyEvent.KEY_RELEASED, keyEvent -> {
            final KeyCombination enterKeyCombo = new KeyCodeCombination( KeyCode.ENTER,
                                                                         KeyCombination.ALT_DOWN );
            final KeyCombination enterModifiedKeyCombo =
                                                       new KeyCodeCombination( KeyCode.ENTER,
                                                                               KeyCombination.SHORTCUT_DOWN );
            if ( enterKeyCombo.match( keyEvent ) || enterModifiedKeyCombo.match( keyEvent ) ) {
                done();

                // Consume the ENTER key so it doesn't get processed twice.
                keyEvent.consume();
            }
        } );

        // Filter for the platform-specific window-closing icon, and treat it
        // like a "Cancel" request.
        setOnCloseRequest( evt -> cancel() );

        // Do not allow an import action until units have been chosen.
        _graphicsImportButton.disableProperty()
                .bind( _graphicsImportPreviewPane._distanceUnitSelector.valueProperty()
                        .isEqualTo( DistanceUnit.UNITLESS ) );
    }

    public boolean isCanceled() {
        return _canceled;
    }

    @Override
    protected Node loadContent() {
        // Instantiate and return the custom Content Node.
        _graphicsImportPreviewPane = new GraphicsImportPreviewPane( _productBranding.productName,
                                                                    clientProperties );
        return _graphicsImportPreviewPane;
    }

    @Override
    protected void loadPopups() {
        super.loadPopups();

        // Instantiate the Graphics Import Help.
        _graphicsImportHelp = CadHelpUtilities.getGraphicsImportHelp( clientProperties.systemType );
        _windowManager.addPopup( _graphicsImportHelp );
    }

    /**
     * Throws away the imported graphics so we can recover memory.
     */
    @Override
    public void reset() {
        // Reset the Graphics Import Preview.
        _graphicsImportPreviewPane.resetGraphicsImportPreview();
    }

    public void setApplicationDrawingLimits(
            final DrawingLimitsProperties applicationDrawingLimitsProperties) {
        // Forward this method to the Graphics Import Preview Pane.
        _graphicsImportPreviewPane.setApplicationDrawingLimits(
                applicationDrawingLimitsProperties );
    }

    public void setCanceled( final boolean canceled ) {
        _canceled = canceled;
    }

    @Override
    public void setForegroundFromBackground( final Color backColor ) {
        // Take care of general styling first, as that also loads shared
        // variables.
        super.setForegroundFromBackground( backColor );

        // Forward this method to the Graphics Import Preview Pane.
        _graphicsImportPreviewPane.setForegroundFromBackground( backColor );
    }

    public void setGeometryContainer( final DxfShapeGroup geometryContainer ) {
        // Forward this method to the Graphics Import Preview Pane.
        _graphicsImportPreviewPane.setGeometryContainer( geometryContainer );
    }

    public void setGraphicsImportOptions( final GraphicsImportOptions graphicsImportOptions ) {
        // Forward this method to the Graphics Import Preview Pane.
        _graphicsImportPreviewPane.setGraphicsImportOptions( graphicsImportOptions );
    }

    // Common open method for opening a modal textField as a fresh session.
    @Override
    public void showAndWait() {
        // Always default to not canceled, until user edits begin.
        setCanceled( false );

        // Wait for the user to dismiss via OK or Cancel Button.
        // NOTE: The visibility test is defensive programming, as the base
        // class throws an exception vs. recovering nicely.
        if ( isShowing() ) {
            toFront();
        }
        else {
            super.showAndWait();
        }
    }

    /**
     * Updates this preview window's Geometry Container reference.
     * <p>
     * NOTE: <i>gui</i> does not have a dependency on <i>dxfparser</i>,
     *  otherwise a {@code DxfNode} could be passed in instead of a
     *  {@link DxfShapeGroup} with separate {@code minimum} and {@code maximum}.
     *
     * @param geometryContainer
     *            The container for the imported geometry
     */
    public void updateGraphicsImportPreview(
            final DxfShapeGroup geometryContainer ) {
        // Update the Graphics Import Context, to trigger a preview update.
        setGeometryContainer( geometryContainer );
    }
}
