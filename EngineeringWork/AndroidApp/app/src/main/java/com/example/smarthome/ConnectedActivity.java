package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
    //private final UUID uuid = UUID.fromString("0c767513-fcb6-48b9-b572-75484db2ab45");
    private final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

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
        Log.d("DEBUG_LOG", "Step 3 ConnectedActivity onCreate");
        Log.d("DEBUG_LOG_ConnectedActivity", "Step A: onCreate: Init");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        bt_Send = findViewById(R.id.bt_sendMessage);
        tv_input = findViewById(R.id.tv_Input);
        tv_output = findViewById(R.id.tv_Output);
        tv_DeviceLabel = findViewById(R.id.tv_DeviceNameLabel);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null)
        {
            Log.d("DEBUG_LOG_ConnectedActivity", "onCreate: Getting bundle");
            device = new Devices( bundle.getString(MainActivity.SELECTED_DEVICE_NAME),bundle.getString(MainActivity.SELECTED_DEVICE_ADDRESS) );
            tv_DeviceLabel.setText(device.name);

            Log.d("DEBUG_LOG_ConnectedActivity", "onCreate: Creating Handler");
            CreateHandler();

            Log.d("DEBUG_LOG_ConnectedActivity", "onCreate: Creating btDevice");
            this.btAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice btDevice = btAdapter.getRemoteDevice(device.address);

            Log.d("DEBUG_LOG", "Step 4 ConnectedActivity: init connectThread");
            Log.d("DEBUG_LOG_ConnectedActivity", "Step B onCreate: connectThread init");
            connectThread = new ConnectThread(uuid,btDevice,handler);
            connectThread.run();

            if (connectThread.GetMySocket().isConnected())
            {
                Log.d("DEBUG_LOG_ConnectedActivity", "onCreate: Getting socket from connectTread");
                BluetoothSocket socket = connectThread.GetMySocket();

                if (socket.isConnected())
                {
                    Log.d("DEBUG_LOG", "Step 5 ConnectedActivity: init connectedThread");
                    Log.d("DEBUG_LOG_ConnectedActivity", "Step C onCreate: connectedThread init");
                    connectedThread = new ConnectedThread(socket, handler);
                    //connectedThread.run();
                    Log.d("DEBUG_LOG_ConnectedActivity", "onCreate: connectedThread run state: " + connectThread.getState());
                }
                else
                {
                    Log.e("DEBUG_LOG_ConnectedActivity", "onCreate: Socket from connectThread isn't connected");
                    connectThread.cancel();
                    finish();
                }
            }
            else
            {
                Log.e("DEBUG_LOG_ConnectedActivity", "onCreate: Socket from connectThread isn't connected");
                connectThread.cancel();
                finish();
            }
        }
        else
        {
            Log.e("DEBUG_LOG_ConnectedActivity", "onCreate: We didnt get a bundle");
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

    @Override
    public void onBackPressed()
    {
        connectThread.cancel();
        connectedThread.cancel();
        finish();
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