package com.mkuskowski.opengles_app1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.transition.ScaleProvider;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Usefull link ( based on ) ;
    // https://developer.android.com/training/graphics/opengl

    //private OpenGLFragment openGLFragment;
    private MyGLSurfaceView gLView;

    private ArrayList<String> arrayList;

    private CountDownTimer countDownTimer;
    private TextView intTimer;
    private TextView intScore;
    private TextView intHealth;

    final long maxCounter = 1000; // 1 s
    final long diff = 1000;
    private int Health = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gLView = (MyGLSurfaceView) findViewById(R.id.MyGLSurfaceView);
        gLView.SetController(this);
        intTimer = (TextView) findViewById(R.id.intTimer);
        intScore = (TextView) findViewById(R.id.intScore);
        intHealth = (TextView) findViewById(R.id.intHealth);

        arrayList = new ArrayList<>();

        InitializeTimer();
    }
    public void onClick_Startbtn(View v)
    {
        StartGame();
    }
    public void onClick_Stopbtn(View v)
    {
        StopGame();
    }
    public void onClick_OpenLabel(View v)
    {
        Intent intent = new Intent(this,LabelActivity.class);
        Bundle bundle = new Bundle();

        bundle.putStringArrayList("scores",arrayList);
        intent.putExtras(bundle);

        startActivity(intent);
    }
    private void StartGame()
    {
        UpdateHealth();
        NewCycleGame();
    }

    private void NewCycleGame()
    {
        StartTimer();
        gLView.GenerateNewBox();
    }

    public void Scored()
    {
        String temp = (String) intScore.getText();
        int Score = Integer.parseInt(temp) + 1;
        //Log.println(Log.DEBUG,"DEMOAPPLOG", "Temp is: " + temp);
        intScore.setText(String.valueOf(Score));
        NewCycleGame();
    }

    private void StopGame()
    {
        String temp = (String) intScore.getText();
        int Score = Integer.parseInt(temp);
        if(Score > 0)
            AddToLabel(Score);
        CancelTimer();
        intScore.setText("0");
        intHealth.setText("0");
        Health = 3;
        gLView.StopGame();
    }

    private void AddToLabel(int Score)
    {
        int size = arrayList.size();
        if (size > 9)
        {
            arrayList.remove(0);
            size--;
        }
        arrayList.add("Gracz " + size + " zdobył: "+ Score);
    }

    private void UpdateHealth()
    {
        intHealth.setText(String.valueOf(Health));
    }

    private void InitializeTimer()
    {
        countDownTimer = new CountDownTimer(maxCounter , diff)
        {
            public void onTick(long millisUntilFinished)
            {
                long diff = maxCounter - millisUntilFinished;
                intTimer.setText(String.valueOf(diff  / 1000 ));
                //here you can have your logic to set text to edittext
            }
            public void onFinish()
            {
                // New position of box
                if(Health > 1)
                {
                    Health -= 1;
                    UpdateHealth();
                    NewCycleGame();
                }
                else
                {
                    StopGame();
                }
            }

        };
    }

    private void CancelTimer()
    {
        if(countDownTimer != null)
        {
            intTimer.setText("0");
            countDownTimer.cancel();
        }
    }
    private void StartTimer()
    {
        if ( countDownTimer != null)
        {
            intTimer.setText("0");
            countDownTimer.start();
        }
    }
}
