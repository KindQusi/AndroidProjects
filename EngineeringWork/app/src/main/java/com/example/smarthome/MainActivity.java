package com.example.smarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    final int PERMISSION_REQUEST_CODE = 1;
    BluetoothAdapter btAdapter;

    Button bt_Connect;

    boolean didWeGetPermission = false;

    String[] permission = new String[]{ Manifest.permission.BLUETOOTH , Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize

        bt_Connect = findViewById(R.id.bt_connect);

        // Na początku sprawdzamy czy jest bluetooth
        if (btAdapter == null)
            CheckIfPhoneHasBluetooth();

        if ( btAdapter != null)
        {
            Log.d("DEBUG_LOG", "onCreate: We get a bluetooth hardware");

        }
        else
        {
            // Wymagamy bluetootha
            Log.d("DEBUG_LOG", "onCreate: We didn't get a bluetooth hardware");
        }

    }

    public void Click_bt_Connect(View v)
    {
        CheckIfWeHavePermissions();
        if (didWeGetPermission)
        {
            Log.d("DEBUG_LOG", "Click_bt_Connect: We get a permission");
            // Otwieramy okno z wyborem urzadzenia do polaczenia
            DialogOfDevices();
        }
        else
            {
            Log.d("DEBUG_LOG", "Click_bt_Connect: We didn't get a permission");
        }
    }

    private void CheckIfWeHavePermissions()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
            // You can use the API that requires the permission.
                didWeGetPermission = true;
            }
        else
            {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
                didWeGetPermission = false;
                Log.d("DEBUG_LOG", "CheckIfWeHavePermissions: Ask for permission");
                requestPermissions(permission,PERMISSION_REQUEST_CODE);

            }

    }

    private void DialogOfDevices()
    {
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        ArrayList<Devices> devices = new ArrayList<>();

        if (pairedDevices.size() > 0) {
            Log.d("DEBUG_LOG", "DialogOfDevices: Get list of devices");
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                devices.add(new Devices(deviceName,deviceHardwareAddress));
            }
        }
        Dialog dialog = new SelectDevice_Dialog(this,devices);
        dialog.show();
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
        // Sprawdzamy czy powinien byc w systemie
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH  ) || !getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE ))
        {
            this.btAdapter = null;
            return false;
        }
        // Sprawdzamy czy mamy do niego dostep
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null)
            return false;
        else
            return true;
    }



}