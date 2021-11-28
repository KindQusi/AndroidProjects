package com.mkuskowski.opengles_app1;

import android.content.res.Resources;
import android.graphics.Point;
import android.opengl.GLES20;
import android.os.Debug;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.text.DecimalFormat;
import java.util.Random;

public class Square {

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private final int mProgram;

    public final float distanceFromCenter = 0.20f;

    // Zakres w jakim ma się kwadrat pojawić
    //public static final float ScreenWidth = 1.6f;  // ( -0.8 , 0.8 ) X
    //public static final float ScreenHeight = 1.6f; // ( - 0.8 , 0.8 ) Y
    public static final float ScreenWidth = 1.2f;  // ( -0.6 , 0.6 ) X
    public static final float ScreenHeight = 1.8f; // ( -0.9 , 0.9 ) X

    float viewPortWidth;
    float viewPortHeight;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float squareCoords[] = {
            -0.99f,  0.99f, 0.0f,   // top left
            -0.99f, -0.99f, 0.0f,   // bottom left
            0.99f, -0.99f, 0.0f,   // bottom right
            0.99f,  0.99f, 0.0f }; // top right
    static float squareCoordsDP[] = {
            0.0f,  0.0f, 0.0f,   // top left
            0.0f,  0.0f, 0.0f,   // bottom left
            0.0f,  0.0f, 0.0f,   // bottom right
            0.0f,  0.0f, 0.0f }; // top right

    void GenerateCords()
    {

        //Log.println(Log.DEBUG,"DEMOAPPLOG", "randsize is: "+ randSize);
        //Log.println(Log.DEBUG,"DEMOAPPLOG", "random is: "+ random);

        //float randomX = RandomFloat(-0.5f,0.5f);
        //float randomY = RandomFloat(-0.8f,0.8f);
        float randomX = RandomFloat( ScreenWidth /-2f ,ScreenWidth/2f );
        float randomY = RandomFloat( ScreenHeight/-2f, ScreenHeight/2f);
        //Log.println(Log.DEBUG,"DEMOAPPLOG", "Random przed Abs X : "+ randomX + " , Y: " + randomY);

        DecimalFormat df = new DecimalFormat("#.#");
        df.format(randomX);
        df.format(randomY);

        squareCoords[0] = randomX -  distanceFromCenter; // x top left
        squareCoords[1] = randomY +  distanceFromCenter; // y top left
        squareCoords[2] = 0.0f;                  // z top left
        squareCoords[3] = randomX -  distanceFromCenter; // bottom left
        squareCoords[4] = randomY -  distanceFromCenter;
        squareCoords[5] = 0.0f;
        squareCoords[6] = randomX +  distanceFromCenter; // bottom right
        squareCoords[7] = randomY -  distanceFromCenter;
        squareCoords[8] = 0.0f;
        squareCoords[9] = randomX +  distanceFromCenter; // top right
        squareCoords[10] = randomY + distanceFromCenter;
        squareCoords[11] = 0.0f;

        OpenGLToScreen();
    }

    float RandomFloat (float minRange , float maxRange)
    {
        float rand = minRange + (maxRange - minRange) * new Random().nextFloat();
        return rand;
    }


    void OpenGLToScreen()
    {
        squareCoordsDP[0]  = CalculateX(squareCoords[0]);
        squareCoordsDP[1]  = CalculateY(squareCoords[1]);

        squareCoordsDP[3]  = CalculateX(squareCoords[3]);
        squareCoordsDP[4]  = CalculateY(squareCoords[4]);

        squareCoordsDP[6]  = CalculateX(squareCoords[6]);
        squareCoordsDP[7]  = CalculateY(squareCoords[7]);

        squareCoordsDP[9]  = CalculateX(squareCoords[9]);
        squareCoordsDP[10] = CalculateY(squareCoords[10]);

        //Log.println(Log.DEBUG,"DEMOAPPLOG", "squareCord X: "+ squareCoords[0] + " , Y: " + squareCoords[1]);
        //Log.println(Log.DEBUG,"DEMOAPPLOG", "squareCordDP X: "+ squareCoordsDP[0] + " , Y: " + squareCoordsDP[1]);
    }

    float CalculateX(float point)
    {
        return Math.abs(point - ScreenWidth/2 )* viewPortWidth/ScreenWidth;
    }
    float CalculateY(float point)
    {
        return Math.abs(point - ScreenHeight/2 )*viewPortHeight/ScreenHeight;
    }

    public float[] GetWorldCoordinates()
    {
        return squareCoordsDP;
    }


    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    public Square() {
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);
        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }


    //  Shaders
    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    // Use to access and set the view transformation
    private int vPMatrixHandle;

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";


    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    private int positionHandle;
    private int colorHandle;

    private final int vertexCount = squareCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public void draw(float[] mvpMatrix ,float viewPortWidth ,float viewPortHeight) {
        this.viewPortWidth = viewPortWidth;
        this.viewPortHeight = viewPortHeight;

        GenerateCords();
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // get handle to shape's transformation matrix
        vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);
        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        // Draw the triangle
        // GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        // Draw the Square
        // We have to use it as we have glDrawOrder
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
