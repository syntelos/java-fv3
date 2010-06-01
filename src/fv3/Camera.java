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

import fv3.cop.*;

import fv3.math.Vector;
import fv3.math.Matrix;
import fv3.tk.Fv3Screen;

import lxl.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * The {@link World} manages a set of cameras, and can dynamically
 * change cameras by name.  Cameras perform both projection and
 * modelview operations.
 * 
 * Projection operations are performed in the "init" method, and
 * modelview operations are performed in the "display" method.  These
 * methods are named for the events of the same names that invoke them
 * from {@link World}.
 * 
 * This class endeavors to provide an essential feature set while
 * remaining amenable to modification in subclasses.
 * 
 * <h3>Operation</h3>
 * 
 * As called from {@link World}, the root of the scene- event tree,
 * the camera loads both the projection and modelview matrices in its
 * init and display methods.
 * 
 * Because the Camera modelview matrix is loaded first on the
 * modelview matrix stack, it is effectively pre-multiplied (M * C)
 * into the modelview matrix and operates as a view matrix -- rather
 * than post-multiplied (C * M) and operating as a model matrix.
 * 
 * <h3>Lifecycle</h3>
 * 
 * Cameras are defined in world and component constructors, before the
 * init event for a primary camera, and before a camera's first use
 * for any secondary camera's.
 * 
 */
public class Camera
    extends java.lang.Object
{
    /**
     * An operator is called from the camera init event, once the
     * camera has the viewport aspect ratio.  
     * 
     * The operator works on the camera matrix to produce the final
     * camera matrix.  A null matrix is an identity matrix, and the
     * initial state of a matrix is the identity matrix.  Typically
     * the operator should accomodate any preexisting matrices if it
     * is to be correct.
     * 
     * If the operator has no work to do on a matrix, one is not
     * created and the identity matrix is loaded rather than the
     * camera matrix.
     * 
     * The operator methods are called in the order of projection,
     * then view.  Shared computation can be performed once in the
     * projection method.
     * 
     * @see fv3.cop.Frustrum
     * @see fv3.cop.Ortho
     * @see fv3.cop.Perspective
     */
    public interface Operator {

        public boolean hasCircumSphere();

        public Bounds.CircumSphere getCircumSphere();

        /**
         * @return The end state for the camera projection matrix.
         * Null for no preexisting matrix and no work to do -- for the
         * identity matrix.
         */
        public Matrix projection(Camera c);
        /**
         * @return The end state for the camera view matrix.  Null for
         * no preexisting matrix and no work to do -- for the identity
         * matrix.
         */
        public Matrix view(Camera c);
    }


    public final char name;

    public final int index;

    protected volatile Camera.Operator operator;

    protected volatile double vpAspect;

    protected volatile boolean vp = false;

    protected volatile int vpX, vpY, vpWidth, vpHeight;

    protected volatile Matrix projection, view;


    public Camera(char name){
        super();
        if ('A' <= name && 'Z' >= name){
            this.name = name;
            this.index = (name - 'A');
        }
        else
            throw new IllegalArgumentException(String.format("0x%x",(int)name));
    }
    public Camera(char name, Camera copy){
        this(name);
        if (null != copy){
            this.operator = copy.operator;

            this.vpAspect = copy.vpAspect; 
            this.vp = copy.vp;
            this.vpX = copy.vpX;
            this.vpY = copy.vpY;
            this.vpWidth = copy.vpWidth;
            this.vpHeight = copy.vpHeight;

            if (null != copy.projection)
                this.projection = new Matrix(copy.projection);

            if (null != copy.view)
                this.view = new Matrix(copy.view);
        }
    }


    public boolean isViewport(){
        return this.vp;
    }
    public Camera setViewport(int x, int y, int w, int h){
        this.vp = true;
        this.vpX = x;
        this.vpY = y;
        this.vpWidth = w;
        this.vpHeight = h;
        double wd = w;
        double hd = h;
        this.vpAspect = (wd/hd);
        return this;
    }
    public Camera unsetViewport(){
        this.vp = false;
        return this;
    }
    public int getViewportX(){
        return this.vpX;
    }
    public int getViewportY(){
        return this.vpY;
    }
    public int getViewportWidth(){
        return this.vpWidth;
    }
    public int getViewportHeight(){
        return this.vpHeight;
    }
    public boolean hasAspect(){
        return (0.0 != this.vpAspect);
    }
    public double getAspect(){
        double aspect = this.vpAspect;
        if (0.0 != aspect)
            return aspect;
        else
            throw new IllegalStateException();
    }
    public boolean hasOperator(){
        return (null != this.operator);
    }
    public boolean hasNotOperator(){
        return (null == this.operator);
    }
    public Camera.Operator getOperator(){
        return this.operator;
    }
    public Camera setOperator(Camera.Operator cop){
        this.operator = cop;
        return this;
    }
    public boolean hasView(){
        return (null != this.view);
    }
    public boolean hasNotView(){
        return (null == this.view);
    }
    public Matrix getView(){
        Matrix view = this.view;
        if (null == view){
            view = new Matrix();
            this.view = view;
        }
        return view;
    }
    public boolean hasProjection(){
        return (null != this.projection);
    }
    public boolean hasNotProjection(){
        return (null == this.projection);
    }
    public Matrix getProjection(){
        Matrix projection = this.projection;
        if (null == projection){
            projection = new Matrix();
            this.projection = projection;
        }
        return projection;
    }
    public Camera clear(){
        this.view = null;
        return this;
    }
    public Camera translate(double x, double y, double z){
        this.getView().translate(x,y,z);
        return this;
    }
    public Camera scale(double x, double y, double z){
        this.getView().scale(x,y,z);
        return this;
    }
    public Camera rotate(double x, double y, double z){
        this.getView().rotate(x,y,z);
        return this;
    }
    public Camera frustrum(double left, double right, double bottom, double top, double near, double far){
        this.operator = new Frustrum(left,right,bottom,top,near,far);
        return this;
    }
    public Camera frustrum(double near, double far){
        this.operator = new Frustrum(near,far);
        return this;
    }
    public Camera ortho(double left, double right, double bottom, double top, double near, double far){
        this.operator = new Ortho(left,right,bottom,top,near,far);
        return this;
    }
    public Camera ortho(double near, double far){
        this.operator = new Ortho(near,far);
        return this;
    }
    public Camera orthoFront(Component c){
        this.operator = new OrthoFront(new Bounds.CircumSphere(c));
        return this;
    }
    public Camera orthoBack(Component c){
        this.operator = new OrthoBack(new Bounds.CircumSphere(c));
        return this;
    }
    public Camera orthoTop(Component c){
        this.operator = new OrthoTop(new Bounds.CircumSphere(c));
        return this;
    }
    public Camera orthoLeft(Component c){
        this.operator = new OrthoLeft(new Bounds.CircumSphere(c));
        return this;
    }
    public Camera orthoRight(Component c){
        this.operator = new OrthoRight(new Bounds.CircumSphere(c));
        return this;
    }
    /**
     * @param fovy Field of view (degrees) in Y
     */
    public Camera perspective(double near, double far, double fovy){
        this.operator = new Perspective(near,far,fovy);
        return this;
    }
    /**
     * @param fovy Field of view (degrees) in Y
     */
    public Camera perspective(Component c, double fovy){
        this.operator = new Perspective(new Bounds.CircumSphere(c),fovy);
        return this;
    }
    public String getName(){
        return String.valueOf(this.name);
    }
    public void init(GL2 gl, GLU glu){
        {

            if (this.vp)

                gl.glViewport(this.vpX,this.vpY,this.vpWidth,this.vpHeight);

            else {

                Fv3Screen fv3s = Fv3Screen.Current();

                this.vpAspect = (fv3s.width / fv3s.height);

                this.vpWidth = (int)fv3s.width;
                this.vpHeight = (int)fv3s.height;
            }


            Camera.Operator operator = this.operator;
            if (null != operator){

                this.projection = operator.projection(this);

                this.view = operator.view(this);
            }
        }

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        Matrix projection = this.projection;

        if (null == projection)
            gl.glLoadIdentity();
        else
            gl.glLoadMatrixd(projection.buffer());

    }
    public void display(GL2 gl, GLU glu){

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        
        Matrix view = this.view;

        if (null == view)
            gl.glLoadIdentity();
        else
            gl.glLoadMatrixd(view.buffer());

    }

    public String toString(){

        String projection;
        if (null == this.projection)
            projection = "";
        else
            projection = this.projection.toString("\t","\n");

        String view;
        if (null == this.view)
            view = "";
        else
            view = this.view.toString("\t","\n");

        return String.format("%c\tP\n%s\n\tV\n%s",this.name,projection,view);
    }
}
