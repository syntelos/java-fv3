/*
 * Fv3
 * Copyright (C) 2009  John Pritchard, jdp@syntelos.org
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fv3;

import java.nio.DoubleBuffer;

import fv3.math.AxisAngle;
import fv3.math.Matrix;
import fv3.math.Vector;

/**
 * A component that is not a {@link Region} is a leaf in a component
 * graph.  At least for the benefit of discussion, a component is a GL
 * vertex list.
 * @see Region
 * @see fv3.nui.Component
 */
public interface Component 
    extends fv3tk.Fv3Component,
            lxl.Component
{

    public boolean hasFv3Matrix();
    public boolean hasNotFv3Matrix();
    /**
     * A fast and effective way to manage coordinate spaces.  A {@link
     * World} or {@link Region} will push its own matrix.  This method
     * permits the Region display code to push matrices on behalf of
     * child components when present, but to not push matrices on
     * behalf of subordinate regions.
     * 
     * @return Whether a containing region should push this matrix
     * before calling display.  Should be true when: the component has
     * a matrix, the component has children or other operable display
     * code, and the component will not push the matrix itself.
     */
    public boolean pushFv3Matrix();
    /**
     * Defines the coordinate space within this component.  Because
     * Fv3 employs the model view matrix stack in the definition of
     * component coordinate spaces, GL's model view stack depth limit
     * of 32 is imposed.  At most thirty two coordinate spaces may be
     * defined in a single component tree branch from root to leaf.
     * @return Null to inherit the coordinate space.
     */
    public Matrix getFv3Matrix();

    public DoubleBuffer getFv3MatrixBuffer();

    public boolean hasFv3Bounds();
    public boolean hasNotFv3Bounds();
    /**
     * Bounds in the coordinate space within this component. 
     */
    public Bounds getFv3Bounds();
    /**
     * The current state of component visibility.  A component is
     * visible by default (typically).
     */
    public boolean isVisible();
    /**
     * A component is responsible for unmapping its vertex list should
     * it become invisible.
     */
    public Component setVisible(boolean b);

    public Component translate(double x, double y, double z);

    public Component translate(Vector v);

    public Component scale(double s);

    public Component rotate(AxisAngle a);

    public Component rotateX(double a);

    public Component rotateY(double a);

    public Component rotateZ(double a);

    public Component rotateXY(double ax, double ay);
}
