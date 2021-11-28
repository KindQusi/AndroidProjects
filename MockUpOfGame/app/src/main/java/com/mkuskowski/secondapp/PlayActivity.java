package com.mkuskowski.secondapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
    }

    public void back(View view)
    {
        finish();
    }

    public void CustomGame(View view)
    {
        //Creating new intent who is our connection with new Activity
        Intent intent = new Intent(this, CustomGameActivity.class);
        //Start new child activity
        startActivity(intent);
    }
    public void PlayGame(View v)
    {
        //Creating new intent who is our connection with new Activity
        Intent intent = new Intent(this, GameActivity.class);
        //Start new child activity
        startActivity(intent);
    }
}