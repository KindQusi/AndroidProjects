package com.example.smarthome;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;

public class ConnectedThread extends Thread
{
    Handler handler;
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private byte[] mmBuffer; // mmBuffer store for the stream

    public ConnectedThread(BluetoothSocket socket , Handler handler)
    {
        Log.d("DEBUG_LOG_ConnectedThread", "Step A Constructor init");
        this.handler = handler;
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try
        {
            tmpIn = mmSocket.getInputStream();
            Log.d("DEBUG_LOG_ConnectedThread", "Constructor: We get inputStream which is: " + tmpIn.available());
        }
        catch (IOException e)
        {
            Log.e("DEBUG_LOG_ConnectedThread", "Constructor: Error occurred when creating input stream", e);
        }

        try
        {
            tmpOut = mmSocket.getOutputStream();
            Log.d("DEBUG_LOG_ConnectedThread", "Constructor: We get outputStream");
        }
        catch (IOException e)
        {
            Log.e("DEBUG_LOG_ConnectedThread", "Constructor: Error occurred when creating output stream", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run()
    {
        Log.d("DEBUG_LOG_ConnectedThread", "Step B run");

        int sizeBuffer = 1024;
        mmBuffer = new byte[sizeBuffer]; // bytes returned from read()
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        while (true)
        {
            try {
                if (mmInStream.available() > 0) {
                    Log.d("DEBUG_LOG_ConnectedThread", "run: mmInStream.available: " + mmInStream.available());
                    Log.d("DEBUG_LOG_ConnectedThread", "run: Reading message");

                    int length = mmInStream.available();
                    for (int howManyArrays = mmInStream.available() / sizeBuffer; howManyArrays > -1; howManyArrays--)
                    {
                        mmInStream.read(mmBuffer);
                        result.write(mmBuffer, 0, length);
                        Log.d("DEBUG_LOG_ConnectedThread", "run: Reading message for length: " + length + " How many arrays: " + howManyArrays);
                    }

                    Log.d("DEBUG_LOG_ConnectedThread", "run: Message in ByteArray");
                    String message = result.toString("UTF-8");
                    // Resetting to delete previous message
                    result.reset();

                    Log.d("DEBUG_LOG_ConnectedThread", "run: Try send to handler");
                    handler.obtainMessage(MessageConstants.MESSAGE_READ, message).sendToTarget();
                    Log.d("DEBUG_LOG_ConnectedThread", "run: Sent to handler: " + message);

                }
                else
                {
                    Log.d("DEBUG_LOG_ConnectedThread", "run: mmInStream.available: " + mmInStream.available());
                    SystemClock.sleep(1000);
                }

            } catch (Exception e) {
                // TODO Add handler , info for user we need to reconnect
                Log.e("DEBUG_LOG_ConnectedThread", "run: Input stream was disconnected", e);
                break;
            }
        }
    }

    // Call this from the main activity to send data to the remote device.
    public void write(byte[] bytes)
    {
        Log.d("DEBUG_LOG_ConnectedThread", "write");
        try
        {
            Log.d("DEBUG_LOG_ConnectedThread", "write: send message as bytes");
            mmOutStream.write(bytes);

            // Share the sent message with the UI activity.
            Log.d("DEBUG_LOG_ConnectedThread", "write: Try send to handler");
            handler.obtainMessage(MessageConstants.MESSAGE_WRITE, MessageConstants.HANDLED).sendToTarget();
            Log.d("DEBUG_LOG_ConnectedThread", "write: Sent to handler");
        }
        catch (IOException e)
        {
            Log.e("DEBUG_LOG_CONNECTED", "Error occurred when sending data", e);
            handler.obtainMessage(MessageConstants.MESSAGE_READ, MessageConstants.FAILED).sendToTarget();
        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel()
    {
        try
        {
            mmSocket.close();
        }
        catch (IOException e)
        {
            Log.e("DEBUG_LOG_CONNECTED", "Could not close the connect socket", e);
        }
    }


}
