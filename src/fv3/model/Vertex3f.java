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
package fv3.model;

import javax.media.opengl.GL2;

public final class Vertex3f
    extends fv3.model.Object
{
    private final float x, y, z;


    public Vertex3f(double x, double y, double z){
        this( (float)x, (float)y, (float)z);
    }
    public Vertex3f(float x, float y, float z){
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public void apply(GL2 gl){
        gl.glVertex3f(this.x,this.y,this.z);
    }
    public Object.Type getObjectType(){
        return Object.Type.Vertex3f;
    }
}
