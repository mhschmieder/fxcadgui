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
 * This file is part of the FxCadControls Library
 *
 * You should have received a copy of the MIT License along with the
 * FxCadControls Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxcadcontrols
 */
package com.mhschmieder.fxcadgui.model;

import com.mhschmieder.fxcadgraphics.SurfaceMaterial;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// TODO: Switch to wrapping the SurfaceMaterial enum vs. its string label. That
//  requires changes in multiple libraries but is likely doable.
public final class SurfaceProperties {

    // Surfaces are bypassed by default as they are only approximate.
    public static final boolean   BYPASSED_DEFAULT      = true;

    // Declare the Surface Material default name.
    public static final SurfaceMaterial SURFACE_MATERIAL_DEFAULT
            = SurfaceMaterial.RIGID;

    private final IntegerProperty surfaceNumber;
    private final StringProperty  surfaceName;
    private final BooleanProperty surfaceBypassed;
    private final ObjectProperty< SurfaceMaterial > surfaceMaterial;

    public SurfaceProperties( final int pSurfaceNumber,
                              final String pSurfaceName,
                              final boolean pSurfaceBypassed,
                              final SurfaceMaterial pMaterialName ) {
        surfaceNumber = new SimpleIntegerProperty( pSurfaceNumber );
        surfaceName = new SimpleStringProperty( pSurfaceName );
        surfaceBypassed = new SimpleBooleanProperty( pSurfaceBypassed );
        surfaceMaterial = new SimpleObjectProperty<>( pMaterialName );
    }

    public IntegerProperty surfaceNumberProperty() {
        return surfaceNumber;
    }

    public int getSurfaceNumber() {
        return surfaceNumber.get();
    }

    public void setSurfaceNumber( final int pSurfaceNumber ) {
        surfaceNumber.set( pSurfaceNumber );
    }

    public BooleanProperty surfaceBypassedProperty() {
        return surfaceBypassed;
    }

    public boolean isSurfaceBypassed() {
        return surfaceBypassed.get();
    }

    public void setSurfaceBypassed( final boolean pSurfaceBypassed ) {
        surfaceBypassed.set( pSurfaceBypassed );
    }

    public StringProperty surfaceNameProperty() {
        return surfaceName;
    }

    public String getSurfaceName() {
        return surfaceName.get();
    }

    public void setSurfaceName( final String pSurfaceName ) {
        surfaceName.set( pSurfaceName );
    }

    public ObjectProperty< SurfaceMaterial > surfaceMaterialProperty() {
        return surfaceMaterial;
    }

    public SurfaceMaterial getSurfaceMaterial() {
        return surfaceMaterial.get();
    }

    public void setSurfaceMaterial( final SurfaceMaterial pMaterialName ) {
        surfaceMaterial.set( pMaterialName );
    }
}
