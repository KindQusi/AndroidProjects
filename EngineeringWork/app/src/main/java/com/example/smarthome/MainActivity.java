package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    final int PERMISSION_REQUEST_CODE = 1;
    public static final String SELECTED_DEVICE_NAME = "deviceName";
    public static final String SELECTED_DEVICE_ADDRESS = "deviceAddress";


    BluetoothAdapter btAdapter;
    SelectDevice_Dialog selectDevice_dialog;
    Devices selectedDevice;


    Button bt_Connect;

    boolean didWeGetPermission = false;

    String[] permission = new String[]{ Manifest.permission.BLUETOOTH , Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize

        bt_Connect = findViewById(R.id.bt_connect);

        // If we have bluetooth
        if (btAdapter == null)
            CheckIfPhoneHasBluetooth();

        if ( btAdapter != null)
        {
            Log.d("DEBUG_LOG", "onCreate: We get a bluetooth hardware");

        }
        else
        {
            // We need bluetooth
            Log.d("DEBUG_LOG", "onCreate: We didn't get a bluetooth hardware");
        }

    }

    public void Click_bt_Connect(View v)
    {
        CheckIfWeHavePermissions();
        if (didWeGetPermission)
        {
            Log.d("DEBUG_LOG", "Click_bt_Connect: We get a permission");
            // Dialog with choose device
            DialogOfDevices();
        }
        else
            {
            Log.d("DEBUG_LOG", "Click_bt_Connect: We didn't get a permission");
        }
    }

    private void CheckIfWeHavePermissions()
    {
        if (btAdapter != null)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // You can use the API that requires the permission.
                didWeGetPermission = true;
            } else {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                didWeGetPermission = false;
                Log.d("DEBUG_LOG", "CheckIfWeHavePermissions: Ask for permission");
                requestPermissions(permission, PERMISSION_REQUEST_CODE);

            }
        }

    }

    public void StartConnecting(Devices device)
    {
        Toast.makeText(this,
                         "You clicked: " + device.name + " with address: " + device.address, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(MainActivity.this,ConnectedActivity.class);

        intent.putExtra(SELECTED_DEVICE_NAME,device.name);
        intent.putExtra(SELECTED_DEVICE_ADDRESS, device.address);

        startActivity(intent);
    }

    private void DialogOfDevices()
    {
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        ArrayList<Devices> devices = new ArrayList<>();

        // If 0 user can have bluetooth turned off
        if (pairedDevices.size() > 0) {
            Log.d("DEBUG_LOG", "DialogOfDevices: Get list of devices");
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                devices.add(new Devices(deviceName,deviceHardwareAddress));
            }
        }

        selectDevice_dialog = new SelectDevice_Dialog(this,devices);
        selectDevice_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                StartConnecting(selectDevice_dialog.device);
            }
        });
        selectDevice_dialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("DEBUG_LOG", "OnRequestPermResult String[] =  " + permissions[0]);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("DEBUG_LOG", "OnRequestPermResult: We got an permission");

                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Log.d("DEBUG_LOG", "OnRequestPermResult: We didn't get an permission");
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }


    private boolean CheckIfPhoneHasBluetooth()
    {
        // Check if phone has bluetooth
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH  ) || !getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE ))
        {
            this.btAdapter = null;
            return false;
        }
        // Check if we get access ( permission )
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null)
            return false;
        else
            return true;
    }



}