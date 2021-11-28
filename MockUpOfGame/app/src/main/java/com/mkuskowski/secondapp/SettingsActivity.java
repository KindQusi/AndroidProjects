package com.mkuskowski.secondapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void back(View view)
    {
        finish();
    }

    public void turnSound(View v)
    {
        notifyUser("Not implemented");
    }
    public void temp_Button(View v)
    {
        notifyUser("Not implemented");
    }

    private void notifyUser(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}