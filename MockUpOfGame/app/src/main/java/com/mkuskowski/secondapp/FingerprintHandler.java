package com.mkuskowski.secondapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class FingerprintHandler extends FingerprintManager.AuthenticationCallback
{

    // IMPORTANT
    //
    // oldCode , For old depreciated fingerprint Manager for api lower then 28 ( android 9.0 )
    // Not used in application only to show what i wrote before when trying to figure out how it works

    private Context context;
    private String nick;

    public FingerprintHandler(Context context , String nick)
    {
        this.context = context;
        this.nick = nick;
    }

    private void Update(String s , boolean b)
    {
        TextView fingerHelper = (TextView) ((Activity)context).findViewById(R.id.fingerprintHelper);
        if(b == false)
        {
            fingerHelper.setTextColor(ContextCompat.getColor(context,R.color.design_default_color_error));
            fingerHelper.setText(s);
        }
        else
        {
            fingerHelper.setTextColor(ContextCompat.getColor(context,R.color.black));
            fingerHelper.setText("Success");

            //Tworzymy intenta ktory jest lacznikiem miedzy naszymi aktywnosciami
            Intent intent = new Intent(context, MainActivity.class);
            //Dodajemy do intenta nick naszego usera
            intent.putExtra("userNick", nick);
            //Startujemy nowa potomna aktywnosc
            context.startActivity(intent);

        }
    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject)
    {
        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal,0,this,null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString)
    {
        this.Update("There was an Auth error" + errString , false);
    }

    @Override
    public void onAuthenticationFailed()
    {
        this.Update("Auth failed." , false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString)
    {
        //For example badly scanned finger
        this.Update("Error " + helpString , false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result)
    {
        this.Update("You can now access the app" , true);
    }
}
