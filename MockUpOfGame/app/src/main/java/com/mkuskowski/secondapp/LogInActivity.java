package com.mkuskowski.secondapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class LogInActivity extends AppCompatActivity {

    // Not used in application
    // Needed for old code using FingerPrint
    // private FingerprintManager fingerprintManager;
    // private KeyguardManager keyguardManager;

    //Needed for Biometric
    private Executor executor;
    private androidx.biometric.BiometricPrompt biometricPrompt;
    private androidx.biometric.BiometricPrompt.PromptInfo promptInfo;
    //Needed for notifications
    NotificationManagerCompat notificationManager;
    NotificationChannel channel;

    private String userNick;
    //Its needed as we should remove our notification from code as every notif. has id which we gave them
    public static int NOTIFICATION_ID = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize notification
        notificationManager = NotificationManagerCompat.from(this);
        //For android 8.0 and higher there is a require to setup Notification Channel
        createNotificationChannel();
        //Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.channel_name))
                .setSmallIcon(R.drawable.applogo)
                .setContentTitle("Notification")
                .setContentText("Succesfull login in")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        //Initialize biometric
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LogInActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override //When there was an error during auth
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                notifyUser("Authentication error: " + errString);
            }

            @Override // When everything went perfectly
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                notifyUser("Authentication succeed");
                notificationManager.notify(NOTIFICATION_ID,builder.build());
                LoginPass(userNick);
            }

            @Override // When there is finger which inst registered as owner fingerprint
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                notifyUser("Authentication failed");
            }
        });
        //Setup Biometric dialog
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Confirm that you are owner of that account by fingerprint")
                .setNegativeButtonText("Co to jest")
                .build();
    }

    public void LogIn(View view)
    {
        EditText userNick_EditText = (EditText) findViewById(R.id.textNick_);
        EditText passwordUser_EditText = (EditText) findViewById(R.id.textPassword_);

        if ( CheckCredits(userNick_EditText.getText().toString(),passwordUser_EditText.getText().toString()))
        {
            //LoginPass(nickUser.getText().toString());
            userNick = userNick_EditText.getText().toString();
            biometricPrompt.authenticate(promptInfo);
        }
    }

    //todo Check for credits ( Not only if user write something there ^^ )
    private Boolean CheckCredits(String userNick,String userPass)
    {
        if(userNick.equals("") || userPass.equals(""))
        {
            notifyUser("Wrong credits");
            return false;
        }
        else
            return true;
    }

    private void LoginPass(String nick)
    {
        //Creating new intent who is our connection with new Activity
        Intent intent = new Intent(this, MainActivity.class);
        //Adding our nick to intent
        intent.putExtra("userNick", nick);
        //Start new child activity
        startActivity(intent);
    }

    // oldCode , For old depreciated fingerprint Manager for api lower then 28 ( android 9.0 )
    // Not used in application only to show what i wrote before when trying to figure out how it works
    /*
    private void IfCanCheckFinger(String nick)
    {
        //Check 1: Android version is greater or equal to Marshmallow
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            //Check 2: Device has fingerprint scanner
            if(fingerprintManager.isHardwareDetected())
            {
                //Check 3: Have permission to use fingerscanner in app
                //if(ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED)
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED)
                {
                    //Granted
                    //Check 4: Lock screen is secured with at least 1 type of lock
                    if(keyguardManager.isKeyguardSecure())
                    {
                        //Check 5: At least 1 fingerprint is registered
                        if(fingerprintManager.hasEnrolledFingerprints())
                        {
                            //we can try to scan fingerprint
                            notifyUser("Please scan your finger");
                            CheckFingerPrint(nick);
                        }
                        else
                        {
                            //no fingerprints registered
                            notifyUser("You doesn't have any registered fingerprints");
                        }
                    }
                    else
                    {
                        //device not secured
                        notifyUser("Your phone doesn't have any lock secure");
                    }
                }
                else
                {
                    //Not granted prem
                    String[] perms = {"android.permission.USE_FINGERPRINT,android.permission.USE_BIOMETRIC"};

                    //notifyUser( String.valueOf(ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_BIOMETRIC))); ;

                    int permsRequestCode = 200;
                    requestPermissions(perms, permsRequestCode);

                    ActivityCompat.requestPermissions(LogInActivity.this,new String[]{ Manifest.permission.USE_FINGERPRINT } , 1);
                    notifyUser("App doesn't have access to fingerprint");
                }

            }
            else
            {
                //device doesn't have fingerprint scanner
                notifyUser("Your phone doesn't have fingerprint scanner");
            }
        }
        else
        {
            //Android version lover then marshmallow
            notifyUser("Your android version is too low , minimum is Marshallow");
        }
    }


    private void CheckFingerPrint(String nick)
    {
        FingerprintHandler fingerprintHandler = new FingerprintHandler(this, nick);
        fingerprintHandler.startAuth(fingerprintManager,null);
    }
    */

    public void Register(View view)
    {
        //todo Add new account for example based on credits in login&pass EditText
        notifyUser("Not implemented");
    }

    //Toast to make quick notification to user
    private void notifyUser(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            channel = new NotificationChannel(getString(R.string.channel_name), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
    }
}