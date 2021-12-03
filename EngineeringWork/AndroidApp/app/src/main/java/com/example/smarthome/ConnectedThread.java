package com.example.smarthome;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
        mmBuffer = new byte[1024];
        int numBytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        while (true)
        {
            try
            {
                Log.d("DEBUG_LOG_ConnectedThread", "run: Read inputStream");
                // Read from the InputStream.
                numBytes = mmInStream.read(mmBuffer);
                // Send the obtained bytes to the UI activity.

                //String mess = new String (numBytes);
                Log.d("DEBUG_LOG_ConnectedThread", "run: Try send to handler");
                Message readMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_READ, numBytes, MessageConstants.HANDLED,
                        mmBuffer);
                readMsg.sendToTarget();
                Log.d("DEBUG_LOG_ConnectedThread", "run: Sent to handler");
            }
            catch (Exception e)
            {
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
            Message writtenMsg = handler.obtainMessage(
                    //MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
                    MessageConstants.MESSAGE_WRITE, MessageConstants.HANDLED, MessageConstants.FAILED, mmBuffer);
            writtenMsg.sendToTarget();
            Log.d("DEBUG_LOG_ConnectedThread", "write: Sent to handler");
        }
        catch (IOException e)
        {
            Log.e("DEBUG_LOG_CONNECTED", "Error occurred when sending data", e);

            // Send a failure message back to the activity.
            Message writeErrorMsg =
                    handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString("toast",
                    "Couldn't send data to the other device");
            writeErrorMsg.setData(bundle);
            handler.sendMessage(writeErrorMsg);
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
