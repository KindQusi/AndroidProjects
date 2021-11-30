package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.UUID;

public class ConnectedActivity extends AppCompatActivity
{

    // Random UUID from generator
    // https://www.uuidgenerator.net
    private final UUID uuid = UUID.fromString("0c767513-fcb6-48b9-b572-75484db2ab45");

    private Devices device;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private BluetoothAdapter btAdapter;
    private static Handler handler;

    public Button bt_Send;
    public TextView tv_DeviceLabel,tv_input,tv_output;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        bt_Send = findViewById(R.id.bt_sendMessage);
        tv_input = findViewById(R.id.tv_Input);
        tv_output = findViewById(R.id.tv_Output);
        tv_DeviceLabel = findViewById(R.id.tv_DeviceNameLabel);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null)
        {
            device = new Devices( bundle.getString(MainActivity.SELECTED_DEVICE_NAME),bundle.getString(MainActivity.SELECTED_DEVICE_ADDRESS) );
            tv_DeviceLabel.setText(device.name);
            this.btAdapter = BluetoothAdapter.getDefaultAdapter();

            try
            {
                CreateHandler();

                connectThread = new ConnectThread(uuid,btAdapter,device,handler);
                connectThread.run();

                if (connectThread.GetMySocket().isConnected()) {
                    connectedThread = new ConnectedThread(connectThread.GetMySocket(), handler);
                    connectedThread.run();
                }
                else
                {
                    Log.e("DEBUG_LOG", "ConnectedActivity: onCreate: socket not connected");
                    connectThread.cancel();
                    finish();
                }
            }
            catch (Exception e)
            {

            }

        }
        else
        {
            Log.d("DEBUG_LOG", "ConnectedActivity: onCreate: We didnt get a bundle");
            finish();
        }


    }

    public void SendMessage(View v)
    {
        if( tv_input.getText().length() > 0)
        {
            connectedThread.write(tv_input.getText().toString().getBytes());
        }
        else
        {
            Log.d("DEBUG_LOG", "SendMessage: No message in tv_input");
        }
    }

    private void CreateHandler()
    {
        handler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage (Message msg)
            {
                switch ( msg.what )
                {
                    case MessageConstants.MESSAGE_READ:
                    {
                        String message = msg.obj.toString();
                        switch (msg.arg1)
                        {
                            case MessageConstants.FAILED:
                            {
                                Log.d("DEBUG_LOG_CONNECTED", msg.what +" " + msg.arg1 + " FAILED" + message);
                            }
                            case MessageConstants.HANDLED:
                            {
                                Log.d("DEBUG_LOG_CONNECTED", msg.what +" " + msg.arg1 + " HANDLED");

                            }
                        }
                    }
                    case MessageConstants.MESSAGE_WRITE:
                    {
                        switch (msg.arg1)
                        {
                            case MessageConstants.FAILED:
                            {
                                Log.d("DEBUG_LOG_CONNECTED", msg.what +" " + msg.arg1 + " FAILED");
                            }
                            case MessageConstants.HANDLED:
                            {
                                Log.d("DEBUG_LOG_CONNECTED", msg.what +" " + msg.arg1 + " HANDLED");
                            }
                        }
                    }
                    case MessageConstants.CONNECTION_STATUS:
                    {
                        switch (msg.arg1)
                        {
                            case MessageConstants.FAILED:
                            {
                                Log.d("DEBUG_LOG_CONNECTED", msg.what +" " + msg.arg1 + " FAILED");
                            }
                            case MessageConstants.HANDLED:
                            {
                                Log.d("DEBUG_LOG_CONNECTED", msg.what +" " + msg.arg1 + " HANDLED");
                            }
                        }
                    }

                }


            }
        };
    }
}