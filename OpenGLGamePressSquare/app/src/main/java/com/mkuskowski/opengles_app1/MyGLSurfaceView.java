package com.mkuskowski.opengles_app1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import java.util.Arrays;

public class MyGLSurfaceView extends GLSurfaceView {

    // GLSurfaceView
    // A view container for graphics drawn with OpenGL via MyGLRenderer

    private final MyGLRenderer renderer;

    private int widthPixels;
    private int heightPixels;

    float widthOpenGL;
    float heightOpenGL;
    MainActivity controler;

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        renderer = new MyGLRenderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        //Log.println(Log.DEBUG,"DEMOAPPLOG", "onTouch, CornerOfOpenGL X: " + cornerOfOpenGL.x + " Y: " + cornerOfOpenGL.y );
        //Log.println(Log.DEBUG,"DEMOAPPLOG", "onTouch, CenterOfOpenGL X: " + centerOfOpenGL.x + " Y: " + centerOfOpenGL.y );

    }

    public void GenerateNewBox() {
        renderer.GameRunning = true;
        requestRender();
    }

    public void StopGame() {
        renderer.GameRunning = false;
        requestRender();
    }
    public void SetController( MainActivity mainActivity)
    {
        this.controler = mainActivity;
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        // Size of our screen
        widthPixels = Resources.getSystem().getDisplayMetrics().widthPixels; //1000
        heightPixels = Resources.getSystem().getDisplayMetrics().heightPixels; //1200

        //Log.println(Log.DEBUG, "DEMOAPPLOG", "onTouch, CornerOfOpenGL X: " + widthPixels + " Y: " + heightPixels);

        // Size of our OpenGL widget
        float[] temp = renderer.GetWidthHeight();

        widthOpenGL = temp[0];
        heightOpenGL = temp[1];


        float x = e.getX();
        float y = e.getY();

        //Log.println(Log.DEBUG,"DEMOAPPLOG", "SurfaceView => onTouch event, Click X: " + x + " Y: " + y );
        float [] boxCoordinates = renderer.GetBoxCords();
        if(boxCoordinates != null)
        {
            //Log.println(Log.DEBUG,"DEMOAPPLOG", boxCoordinates[0] + " > (CLICK) X: "+ x + " > " + boxCoordinates[9]);
            //Log.println(Log.DEBUG,"DEMOAPPLOG", boxCoordinates[1] + " < (CLICK) Y: "+ y + " < " + boxCoordinates[4]);
            if ( boxCoordinates[0] > x && x > boxCoordinates[9] ) // If it's between x coordinates
            {
                //Log.println(Log.DEBUG,"DEMOAPPLOG", "SurfaceView => onTouch event, X Passed");
                if ( boxCoordinates[1] < y &&  y < boxCoordinates[4]) // If it's between y coordinates
                {
                    //Log.println(Log.DEBUG,"DEMOAPPLOG", "SurfaceView => onTouch event, Y Passed");
                    //Log.println(Log.DEBUG,"DEMOAPPLOG", "SurfaceViev => onTouch event, Scored");
                    controler.Scored();
                }
            }
        }
        else
        {
            Log.println(Log.DEBUG,"DEMOAPPLOG", "SurfaceViev => onTouch event, No boxCoordinates");
        }

        return true;
    }
}
