package com.example.smarthome;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

// Connecting to arduino
public class ConnectThread extends Thread
{
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice bluetoothDevice;

    //private BluetoothAdapter btAdapter;
    private Handler handler;

    public ConnectThread( UUID uuid , BluetoothDevice btDevice  , Handler handler )//BluetoothAdapter btAdapter
    {
        Log.d("DEBUG_LOG_ConnectThread", "Step A: Constructor init");
        //this.btAdapter = btAdapter;
        this.handler = handler;
        this.bluetoothDevice = btDevice;

        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;

        Log.d("DEBUG_LOG_ConnectThread", "Constructor init btDevice name: " + btDevice.getName() + " address: " + btDevice.getAddress());
        try
        {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            // TODO Zamienić insecure
            //tmp = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            //Log.d("DEBUG_LOG_ConnectThread", "Constructor init We created connect. TMP: " + tmp.isConnected());
            Log.d("DEBUG_LOG_ConnectThread", "Constructor init We created tmp socket.");
        }
        //catch (IOException e)
        catch (Exception e)
        {
            Log.e("DEBUG_LOG_ConnectThread", "Constructor init failed createInsecureTF.. , ", e);
            this.handler.sendMessage(this.handler.obtainMessage(MessageConstants.CONNECTION_STATUS,MessageConstants.FAILED));
        }

        mmSocket = tmp;
    }

    public void run()
    {
        // Cancel discovery because it otherwise slows down the connection.
        // btAdapter.cancelDiscovery(); NO SCAN PREMMISION API 31 +
        Log.d("DEBUG_LOG_ConnectThread", "Step B run ");
        try
        {

            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
            Log.d("DEBUG_LOG_ConnectThread", "run: We connected ");
            //handler.sendMessage(handler.obtainMessage(MessageConstants.CONNECTION_STATUS,MessageConstants.HANDLED));
        }
        catch (Exception connectException)
        {
            // Unable to connect; close the socket and return.
            Log.e("DEBUG_LOG_ConnectThread", "run: We didn't connect");
            handler.sendMessage(handler.obtainMessage(MessageConstants.CONNECTION_STATUS,MessageConstants.FAILED));
            try
            {
                mmSocket.close();
                Log.e("DEBUG_LOG_ConnectThread", "run: Socket Closed");
            }
            catch (IOException closeException)
            {
                Log.e("DEBUG_LOG_ConnectThread", "run: Could not close the client socket: ", closeException);
            }

            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        // manageMyConnectedSocket(mmSocket);
        // handler.sendMessage(handler.obtainMessage(MessageConstants.CONNECTION_STATUS,MessageConstants.HANDLED));
    }

    public BluetoothSocket GetMySocket()
    {
        Log.d("DEBUG_LOG_ConnectThread", "GetMySocket: which is: " + mmSocket.isConnected());
        return mmSocket;
    }


    // Closes the client socket and causes the thread to finish.
    public void cancel()
    {
        try
        {
            mmSocket.close();
        } catch (IOException e)
        {
            Log.e("DEBUG_LOG_CLIENT", "Could not close the client socket", e);
        }
    }
}