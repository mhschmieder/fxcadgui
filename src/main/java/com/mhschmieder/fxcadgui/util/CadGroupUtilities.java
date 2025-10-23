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
package com.mhschmieder.fxcadgui.util;

import com.mhschmieder.fxcontrols.GuiUtilities;
import com.mhschmieder.fxcontrols.IconContext;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.apache.commons.math3.util.FastMath;

public final class CadGroupUtilities {

    public static Group getArchitectureToolIcon( final IconContext iconContext ) {
        final Group group = new Group();

        // First, draw a black diagonal line, using an even dash pattern.
        final int iconSize = GuiUtilities.getIconSize( iconContext );
        final int inset = GuiUtilities.getIconInset( iconContext );
        final int endPointBoxSideLength = 2;
        final int startX = inset + endPointBoxSideLength;
        final int startY = iconSize - inset - endPointBoxSideLength;
        final int endX = iconSize - inset - endPointBoxSideLength;
        final int endY = inset + endPointBoxSideLength;
        final Line diagonalLine = new Line( startX, startY, endX, endY );
        diagonalLine.setStrokeWidth( 0.5d );
        diagonalLine.getStrokeDashArray().addAll( 2.0d, 3.0d );

        // Now, draw two yellow end point grab markers, as small rectangles.
        final Rectangle lowerGrabMarker = new Rectangle( startX - endPointBoxSideLength,
                                                         startY,
                                                         endPointBoxSideLength,
                                                         endPointBoxSideLength );
        lowerGrabMarker.setFill( Color.YELLOW );

        final Rectangle upperGrabMarker = new Rectangle( endX,
                                                         endY - endPointBoxSideLength,
                                                         endPointBoxSideLength,
                                                         endPointBoxSideLength );
        upperGrabMarker.setFill( Color.YELLOW );

        // Add the individual shapes to the Node Group.
        group.getChildren().addAll( diagonalLine, lowerGrabMarker, upperGrabMarker );

        return group;
    }

    public static Group getRotateToolIcon( final IconContext iconContext ) {
        final Group group = new Group();

        // First, draw a black clockwise circular arc, starting at 3 o'clock
        // (zero degrees) and ending at 12 o'clock (minus 270 degrees).
        final int iconSize = GuiUtilities.getIconSize( iconContext );
        final int inset = GuiUtilities.getIconInset( iconContext );
        final int strokeWidthOffset = 1;
        final int diameter = iconSize - ( 2 * ( inset + strokeWidthOffset ) );
        final int radius = ( int ) FastMath.floor( 0.5d * diameter );
        final int centerX = ( int ) FastMath.floor( 0.5d * iconSize );
        final int centerY = centerX + strokeWidthOffset;
        final Arc arc = new Arc( centerX, centerY, radius, radius, 0.0d, -270d );
        arc.setType( ArcType.OPEN );
        arc.setFill( null );
        arc.setStroke( Color.BLACK );
        arc.setStrokeWidth( 0.5d );

        // Now, draw the black arrow tips.
        // NOTE: The y-values are offset by the arc thickness.
        // TODO: Improve the algorithm for computing arrow tip edge length.
        final int startX = inset + strokeWidthOffset;
        final int startY = inset + strokeWidthOffset;
        final int arrowTipEdgeLength = ( int ) FastMath.floor( 0.5d * diameter );

        final Line leftArrowTip = new Line( startX + arrowTipEdgeLength,
                                            startY + strokeWidthOffset,
                                            startX,
                                            startY + strokeWidthOffset );
        leftArrowTip.setStrokeWidth( 0.5d );

        final Line rightArrowTip = new Line( startX + arrowTipEdgeLength,
                                             startY + strokeWidthOffset,
                                             startX + arrowTipEdgeLength,
                                             startY + strokeWidthOffset + arrowTipEdgeLength );
        rightArrowTip.setStrokeWidth( 0.5d );

        // Add the individual shapes to the Node Group.
        group.getChildren().addAll( arc, leftArrowTip, rightArrowTip );

        return group;
    }

    public static Group getSelectToolIcon( final IconContext iconContext ) {
        final Group group = new Group();

        // First, draw a black diagonal line.
        final int iconSize = GuiUtilities.getIconSize( iconContext );
        final int inset = GuiUtilities.getIconInset( iconContext );
        final int strokeWidthOffset = 1;
        final int startX = iconSize - inset - strokeWidthOffset;
        final int startY = iconSize - inset - strokeWidthOffset;
        final int endX = inset + strokeWidthOffset;
        final int endY = inset + strokeWidthOffset;
        final Line diagonalLine = new Line( startX, startY, endX, endY );
        diagonalLine.setStrokeWidth( 0.5d );

        // Now, draw the black arrow tips at line end.
        final int arrowTipEdgeLength = ( int ) FastMath
                .floor( 0.5d * FastMath.min( startX - endX, startY - endY ) );

        final Line leftArrowTip = new Line( endX, endY, endX, endY + arrowTipEdgeLength );
        leftArrowTip.setStrokeWidth( 0.5d );

        final Line rightArrowTip = new Line( endX, endY, endX + arrowTipEdgeLength, endY );
        rightArrowTip.setStrokeWidth( 0.5d );

        // Add the individual shapes to the Node Group.
        group.getChildren().addAll( diagonalLine, leftArrowTip, rightArrowTip );

        return group;
    }

}
