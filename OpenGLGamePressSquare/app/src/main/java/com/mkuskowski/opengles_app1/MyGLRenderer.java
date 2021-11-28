package com.mkuskowski.opengles_app1;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

public class MyGLRenderer implements GLSurfaceView.Renderer
{
    // MyGLRenderer
    // Controls what is drawn within MyGLSurfaceView

    private Triangle mTriangle;
    private Square mSquare;

    public Boolean GameRunning = false;

    private final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    float[] ToScreenMatrix = new float[16];

    private float viewPortWitdh;
    private float viewPortHeight;
    float ratio;

    public float[] GetWidthHeight()
    {
        return new float[]{viewPortWitdh, viewPortHeight,ratio};
    }

    // Called once to set up the view's OpenGL ES environment
    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        // Set the background frame color
        //GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClearColor(0f,100f,0f,80);
        // initialize a triangle
        // mTriangle = new Triangle();
        // initialize a square
        mSquare = new Square();
    }

    // Called for each redraw of the view.
    public void onDrawFrame(GL10 gl)
    {
        if( GameRunning ) {
            // Redraw background color
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            //GLES20.glClearColor(0f,100f,0f,80);
            // Set the camera position (View matrix)
            Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            // Calculate the projection and view transformation
            Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
            // Move our square in random direction
            // Matrix.translateM(vPMatrix,0,RandomFloat(-0.5f,0.5f),RandomFloat(-0.8f,0.8f),0f);
            // Save our matrix
            ToScreenMatrix = vPMatrix;
            // Draw square
            mSquare.draw(vPMatrix, viewPortWitdh, viewPortHeight);
        }
        else
        {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        }
    }
    // Called if the geometry of the view changes, for example
    // when the device's screen orientation changes.
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        this.viewPortHeight = height;
        this.viewPortWitdh = width;
        this.ratio = ratio;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        // Matrix.frustumM(projectionMatrix, 0, 0, width, height, 0, 3, 7);
    }

    public static int loadShader(int type, String shaderCode)
    {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public float[] GetBoxCords()
    {
        return mSquare.GetWorldCoordinates();
    }

}