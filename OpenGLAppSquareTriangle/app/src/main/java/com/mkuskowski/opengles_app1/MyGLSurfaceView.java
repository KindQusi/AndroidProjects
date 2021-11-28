package com.mkuskowski.opengles_app1;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.core.view.MotionEventCompat;

public class MyGLSurfaceView extends GLSurfaceView {

    // GLSurfaceView
    // A view container for graphics drawn with OpenGL via MyGLRenderer

    private final MyGLRenderer renderer;
    private ScaleGestureDetector scaleGestureDetector;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;

    public MyGLSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        renderer = new MyGLRenderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }


    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        // Refer our class to handle scale gesture
        scaleGestureDetector.onTouchEvent(e);

        switch (e.getAction())
        {

            case MotionEvent.ACTION_MOVE:

                float dx = x - previousX;
                float dy = y - previousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }

                renderer.setAngle(
                        renderer.getAngle() +
                                ((dx + dy) * -TOUCH_SCALE_FACTOR));
                requestRender();
        }

        previousX = x;
        previousY = y;
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            renderer.scaleFactor *= detector.getScaleFactor();
            //Log.println(Log.INFO,"@@@DEMOAPPLOG","Settings scale on: " + renderer.scaleFactor);

            // To prevent getting too large or too small
            renderer.scaleFactor = Math.max(0.1f, Math.min(renderer.scaleFactor, 2.0f));

            return true;
        }
    }

}
