package com.example.smarthome;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

// Connecting to arduino
public class ConnectThread extends Thread
{
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice bluetoothDevice;

    private BluetoothAdapter btAdapter;
    private Handler handler;

    public ConnectThread( UUID uuid , BluetoothAdapter btAdapter , Devices device , Handler handler)
    {
        this.btAdapter = btAdapter;
        this.handler = handler;
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        bluetoothDevice = btAdapter.getRemoteDevice(device.address);

        try
        {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            //tmp = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
        }
        catch (IOException e)
        {
            Log.e("DEBUG_LOG_CLIENT", "Socket's create() method failed", e);
            this.handler.sendMessage(this.handler.obtainMessage(MessageConstants.CONNECTION_STATUS,MessageConstants.FAILED));
        }
        mmSocket = tmp;
    }

    public void run()
    {
        // Cancel discovery because it otherwise slows down the connection.
        // btAdapter.cancelDiscovery(); NO SCAN PREMMISION API 31 +

        try
        {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
            //handler.sendMessage(handler.obtainMessage(MessageConstants.CONNECTION_STATUS,MessageConstants.HANDLED));
        }
        catch (IOException connectException)
        {
            // Unable to connect; close the socket and return.
            handler.sendMessage(handler.obtainMessage(MessageConstants.CONNECTION_STATUS,MessageConstants.FAILED));
            try
            {
                mmSocket.close();
            }
            catch (IOException closeException)
            {
                Log.e("DEBUG_LOG_CLIENT", "Could not close the client socket", closeException);
            }

            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        //manageMyConnectedSocket(mmSocket);
        handler.sendMessage(handler.obtainMessage(MessageConstants.CONNECTION_STATUS,MessageConstants.HANDLED));
    }

    public BluetoothSocket GetMySocket()
    {
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