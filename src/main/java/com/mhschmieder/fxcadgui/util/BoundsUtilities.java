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
 * This file is part of the fxphysicsgui Library.
 *
 * You should have received a copy of the MIT License along with the
 * fxphysicsgui Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxphysicsgui
 */
package com.mhschmieder.fxcadgui.util;

import com.mhschmieder.fxcadcontrols.model.Extents2DProperties;
import com.mhschmieder.jphysics.DistanceUnit;
import com.mhschmieder.jphysics.UnitConversion;
import javafx.geometry.BoundingBox;
import javafx.geometry.Rectangle2D;

/**
 * Utilities for working with planar bounds and extents in various forms as well
 * as for converting measurement units of such bounds and extents.
 */
public class BoundsUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private BoundsUtilities() {}

    public static BoundingBox boundsFromExtents(
            final Extents2DProperties extents ) {
        return new BoundingBox(
                extents.getX(),
                extents.getY(),
                extents.getWidth(),
                extents.getHeight() );
    }

    public static Rectangle2D rectangle2DFromExtents(
            final Extents2DProperties extents ) {
        return new Rectangle2D(
                extents.getX(),
                extents.getY(),
                extents.getWidth(),
                extents.getHeight() );
    }

    // Get an AWT rectangle, converted from generic Extents.
    public static java.awt.geom.Rectangle2D rectangleAwtFromExtents(
            final Extents2DProperties extents ) {
        final double x = extents.getX();
        final double y = extents.getY();
        final double width = extents.getWidth();
        final double height = extents.getHeight();
        return new java.awt.geom.Rectangle2D.Double(
                x,
                y,
                width,
                height );
    }

    /*
     * Get a BoundingBox converted from Meters to current Distance Unit.
     */
    public static BoundingBox getBoundingBoxInDistanceUnit(
            final Extents2DProperties extents,
            final DistanceUnit distanceUnit ) {
        return getBoundingBoxInDistanceUnit(
                extents,
                DistanceUnit.METERS,
                distanceUnit );
    }

    /*
     * Get a BoundingBox converted from current to specified Distance Unit.
     */
    public static BoundingBox getBoundingBoxInDistanceUnit(
            final Extents2DProperties extents,
            final DistanceUnit oldDistanceUnit,
            final DistanceUnit newDistanceUnit ) {
        final double x = UnitConversion.convertDistance(
                extents.getX(), oldDistanceUnit, newDistanceUnit );
        final double y = UnitConversion.convertDistance(
                extents.getY(), oldDistanceUnit, newDistanceUnit );
        final double width = UnitConversion.convertDistance(
                extents.getWidth(), oldDistanceUnit, newDistanceUnit );
        final double height = UnitConversion.convertDistance(
                extents.getHeight(), oldDistanceUnit, newDistanceUnit );

        return new BoundingBox( x, y, width, height );
    }

    /*
     * Get a BoundingBox converted from current Distance Unit to Meters.
     */
    public static BoundingBox getBoundingBoxInMeters(
            final Extents2DProperties extents,
            final DistanceUnit distanceUnit ) {
        return getBoundingBoxInDistanceUnit(
                extents,
                distanceUnit,
                DistanceUnit.METERS );
    }

    /*
     * Get a Rectangle2D converted from current to specified Distance Unit.
     */
    public static Rectangle2D getRectangleInDistanceUnit(
            final Extents2DProperties extents,
            final DistanceUnit oldDistanceUnit,
            final DistanceUnit newDistanceUnit ) {
        final double x = UnitConversion.convertDistance(
                extents.getX(), oldDistanceUnit, newDistanceUnit );
        final double y = UnitConversion.convertDistance(
                extents.getY(), oldDistanceUnit, newDistanceUnit );
        final double width = UnitConversion.convertDistance(
                extents.getWidth(), oldDistanceUnit, newDistanceUnit );
        final double height = UnitConversion.convertDistance(
                extents.getHeight(), oldDistanceUnit, newDistanceUnit );

        return new Rectangle2D( x, y, width, height );
    }

    /*
     * Get a Rectangle2D converted from current Distance Unit to Meters.
     */
    public static Rectangle2D getRectangleInMeters(
            final Extents2DProperties extents,
            final DistanceUnit distanceUnit ) {
        return getRectangleInDistanceUnit(
                extents,
                distanceUnit,
                DistanceUnit.METERS );
    }

    /*
     * Get an Extents2D converted from current Distance Unit to Meters.
     */
    public static Extents2DProperties getExtentsInMeters(
            final Extents2DProperties extents,
            final DistanceUnit distanceUnit ) {
        return getExtentsInDistanceUnit(
                extents,
                distanceUnit,
                DistanceUnit.METERS );
    }

    /*
     * Get an Extents2D converted from Meters to specified Distance Unit.
     */
    public static Extents2DProperties getExtentsInDistanceUnit(
            final Extents2DProperties extents,
            final DistanceUnit distanceUnit ) {
        return getExtentsInDistanceUnit(
                extents,
                DistanceUnit.METERS,
                distanceUnit );
    }

    /*
     * Get an Extents2D converted from current to specified Distance Unit.
     */
    public static Extents2DProperties getExtentsInDistanceUnit(
            final Extents2DProperties extents,
            final DistanceUnit oldDistanceUnit,
            final DistanceUnit newDistanceUnit ) {
        final double x = UnitConversion.convertDistance(
                extents.getX(), oldDistanceUnit, newDistanceUnit );
        final double y = UnitConversion.convertDistance(
                extents.getY(), oldDistanceUnit, newDistanceUnit );
        final double width = UnitConversion.convertDistance(
                extents.getWidth(), oldDistanceUnit, newDistanceUnit );
        final double height = UnitConversion.convertDistance(
                extents.getHeight(), oldDistanceUnit, newDistanceUnit );

        return new Extents2DProperties( x, y, width, height );
    }

    public static java.awt.geom.Rectangle2D getRectangleMetersAwt(
            final Extents2DProperties extents,
            final DistanceUnit distanceUnit ) {
        final double x = UnitConversion.convertDistance(
                extents.getX(), distanceUnit, DistanceUnit.METERS );
        final double y = UnitConversion.convertDistance(
                extents.getY(), distanceUnit, DistanceUnit.METERS );
        final double width = UnitConversion.convertDistance(
                extents.getWidth(), distanceUnit, DistanceUnit.METERS );
        final double height = UnitConversion.convertDistance(
                extents.getHeight(), distanceUnit, DistanceUnit.METERS );
        return new java.awt.geom.Rectangle2D.Double(
                x,
                y,
                width,
                height );
    }
}
