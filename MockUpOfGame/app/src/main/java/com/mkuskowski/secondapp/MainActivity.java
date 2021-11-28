package com.mkuskowski.secondapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TextView usernickText;
    TextView highscoreText;

    public static String dialogID = "FocusDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernickText = (TextView) findViewById(R.id.nickView);
        highscoreText = (TextView) findViewById(R.id.highscoreView);

        usernickText.setText("Witaj , " + getIntent().getStringExtra("userNick"));

        FocusDialog focusDialog = new FocusDialog();
        focusDialog.show(getSupportFragmentManager(),dialogID);
    }

    public void Play_Button(View view)
    {
        //Creating new intent who is our connection with new Activity
        Intent intent = new Intent(this, PlayActivity.class);
        //Start new child activity
        startActivity(intent);
    }

    public void Leadboard_Button(View view)
    {
        notifyUser("Not implemented");
    }

    public void Settings_Button(View view)
    {
        //Creating new intent who is our connection with new Activity
        Intent intent = new Intent(this, SettingsActivity.class);
        //Start new child activity
        startActivity(intent);
    }

    public void Achievement_Button(View view)
    {
        //Creating new intent who is our connection with new Activity
        Intent intent = new Intent(this, AchievementActivity.class);
        //Start new child activity
        startActivity(intent);
    }

    //Do pokazywania powiadomien
    private void notifyUser(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}