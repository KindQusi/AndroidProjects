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
        //this.btAdapter = btAdapter;
        this.handler = handler;
        this.bluetoothDevice = btDevice;

        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        //bluetoothDevice = btAdapter.getRemoteDevice(device.address);

        Log.e("DEBUG_LOG", "ConnectThread: ConnectThread: btDevice: " + btDevice.getName() + " " + btDevice.getAddress());
        //Log.e("DEBUG_LOG", "ConnectThread: ConnectThread: Address: " + device.address + " State of bt: " + btAdapter.getState());

        /*
        if ( ! btAdapter.isEnabled() )
        {
            Log.e("DEBUG_LOG", "ConnectThread: ConnectThread: bt Not enabled");
        }
        else
        {
            Log.e("DEBUG_LOG", "ConnectThread: ConnectThread: bt Is enabled");
            // ListPairedDevices();
        }
        */



        try
        {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            // TODO Zamienić insecure
            //tmp = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            Log.e("DEBUG_LOG", "ConnectThread: ConnectThread: We created connect. TMP: " + tmp.isConnected());
        }
        //catch (IOException e)
        catch (Exception e)
        {
            Log.e("DEBUG_LOG_CLIENT", "Socket's create() method failed, ", e);
            this.handler.sendMessage(this.handler.obtainMessage(MessageConstants.CONNECTION_STATUS,MessageConstants.FAILED));
        }



        mmSocket = tmp;

        /*try
        {
            mmSocket.connect();
            Log.d("DEBUG_LOG", "Step 2b: Connected" );
        }
        catch (Exception e)
        {
            Log.e("DEBUG_LOG", "Step 2b: " + e);
        }*/
    }

    public void run()
    {
        // Cancel discovery because it otherwise slows down the connection.
        // btAdapter.cancelDiscovery(); NO SCAN PREMMISION API 31 +
        Log.e("DEBUG_LOG", "Step 2: Procceding after creating socket ");
        try
        {

            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
            Log.e("DEBUG_LOG", "Step 2b: Connected");
            //handler.sendMessage(handler.obtainMessage(MessageConstants.CONNECTION_STATUS,MessageConstants.HANDLED));
        }
        catch (Exception connectException)
        {
            // Unable to connect; close the socket and return.
            Log.e("DEBUG_LOG", "Step 2b: Not connected");
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
        // manageMyConnectedSocket(mmSocket);
        // handler.sendMessage(handler.obtainMessage(MessageConstants.CONNECTION_STATUS,MessageConstants.HANDLED));
    }

    public BluetoothSocket GetMySocket()
    {
        return mmSocket;
    }

    /*private void ListPairedDevices()
    {
        Set<BluetoothDevice> mPairedDevices = btAdapter.getBondedDevices();
        if (mPairedDevices.size() > 0)
        {
            for (BluetoothDevice mDevice : mPairedDevices)
            {
                Log.v("DEBUG_LOG_CLIENT", "PairedDevices: " + mDevice.getName() + " " + mDevice.getAddress());
            }
        }
    }*/

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