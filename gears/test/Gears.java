
package test;

import fv3.Camera;

import fv3.nui.Light;

import fv3.tk.Animator;
import fv3.tk.Fv3Screen;

import javax.media.opengl.GL2;

import com.sun.javafx.newt.MouseEvent;

/**
 * <a
 * href="http://www.opengl.org/resources/code/samples/glut_examples/mesademos/gears.c">Brian
 * Paul's popular GL Gears demo</a> in Fv3.
 * 
 * @author Brian Paul
 * @author Ron Cemer
 * @author Sven Goethel
 * @author John Pritchard
 */
public class Gears
    extends fv3.World
{
    public static void main(String[] argv){
        try {
            Gears gears = new Gears();
            Animator animator = new Animator(gears);
            animator.start();
        }
        catch (Exception exc){
            exc.printStackTrace();
            System.exit(1);
        }
    }



    private final static float LightPos[] = { 5.0f, 5.0f, 10.0f, 0.0f };

    static float Angle = 0.0f;



    public Gears(){
        super();
        //this.translate(0,0,-40);

        this.add(new Light(0,LightPos));
        this.add(new Gear1());
        this.add(new Gear2());
        this.add(new Gear3());

        //this.defineCamera('A').moveto(3,0,6).view(this).frustrum(5.0,60.0);
        this.defineCamera('A').orthoFront(this);
        this.defineCamera('B').orthoTop(this);
        this.defineCamera('C').orthoLeft(this);
        this.defineCamera('D').orthoRight(this);
    }


    public void display(GL2 gl){
        Angle += 1.0f;
        super.display(gl);
    }
}
