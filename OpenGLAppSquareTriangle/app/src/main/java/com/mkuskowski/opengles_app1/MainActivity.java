package com.mkuskowski.opengles_app1;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    // Usefull link ( based on ) ;
    // https://developer.android.com/training/graphics/opengl

    private GLSurfaceView gLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gLView = new MyGLSurfaceView(this);
        //gLView = new GLSurfaceView(this);
        setContentView(gLView);
    }
}