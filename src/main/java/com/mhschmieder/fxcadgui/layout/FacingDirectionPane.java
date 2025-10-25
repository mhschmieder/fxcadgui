/*
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
package com.mhschmieder.fxcadgui.layout;

import com.mhschmieder.fxcontrols.control.ControlUtilities;
import com.mhschmieder.fxgraphics.geometry.FacingDirection;
import com.mhschmieder.jcommons.util.ClientProperties;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class FacingDirectionPane extends BorderPane {

    public ToggleGroup _facingDirectionToggleGroup;
    public RadioButton _facingRightRadioButton;
    public RadioButton _facingLeftRadioButton;

    public FacingDirectionPane( final ClientProperties pClientProperties ) {
        // Always call the superclass constructor first!
        super();

        try {
            initPane( pClientProperties );
        }
        catch ( final Exception ex ) {
            ex.printStackTrace();
        }
    }

    public final FacingDirection getFacingDirection() {
        return _facingRightRadioButton.isSelected()
            ? FacingDirection.RIGHT
            : _facingLeftRadioButton.isSelected()
                ? FacingDirection.LEFT
                : FacingDirection.defaultValue();
    }

    private void initPane(final ClientProperties pClientProperties) {
        _facingDirectionToggleGroup = new ToggleGroup();
        _facingRightRadioButton = ControlUtilities.getRadioButton(
                FacingDirection.RIGHT.label(),
                _facingDirectionToggleGroup,
                true );
        _facingLeftRadioButton = ControlUtilities.getRadioButton(
                FacingDirection.LEFT.label(),
                _facingDirectionToggleGroup,
                false );

        final GridPane gridPane = new GridPane();
        gridPane.setHgap( 10.0d );
        gridPane.setVgap( 10.0d );

        gridPane.add( _facingRightRadioButton, 0, 0 );
        gridPane.add( _facingLeftRadioButton, 0, 1 );

        gridPane.setAlignment( Pos.CENTER );
        gridPane.setPadding( new Insets( 10.0d ) );

        setLeft( gridPane );
    }

    public final void saveEdits() {
        // NOTE: Currently there is nothing to do as all the data is saved in
        // the controls themselves.
    }

    public final void setFacingDirection( final FacingDirection facingDirection ) {
        // Forward this method to the subsidiary components.
        switch ( facingDirection ) {
        case RIGHT:
            _facingRightRadioButton.setSelected( true );
            break;
        case LEFT:
            _facingLeftRadioButton.setSelected( true );
            break;
        default:
            break;
        }
    }

}
