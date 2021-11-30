package com.example.smarthome;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

// Accept a connection to us , works as server
public class AcceptThread extends Thread
{

    private BluetoothAdapter btAdapter;
    private final BluetoothServerSocket mmServerSocket;


    // NOT USED
    public AcceptThread(BluetoothAdapter btAdapter, UUID uuid)
    {
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        BluetoothServerSocket tmp = null;

        this.btAdapter = btAdapter;
        //this.address = device.address;

        try
        {
            // Name is name of our server
            tmp = btAdapter.listenUsingRfcommWithServiceRecord(this.btAdapter.getName(), uuid);
        }
        catch (IOException e)
        {
            Log.e("DEBUG_LOG_SERVER", "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;
    }

    public void run()
    {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true)
        {
            try
            {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e("DEBUG_LOG_SERVER", "Socket's accept() method failed", e);
                break;
            }

            if (socket != null)
            {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                manageMyConnectedSocket(socket);
                try
                {
                    mmServerSocket.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void manageMyConnectedSocket(BluetoothSocket socket)
    {

    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel()
    {
        try
        {
            mmServerSocket.close();
        }
        catch (IOException e)
        {
            Log.e("DEBUG_LOG_SERVER", "Could not close the connect socket", e);
        }
    }
}