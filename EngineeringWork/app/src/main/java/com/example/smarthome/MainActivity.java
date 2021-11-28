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
        Log.d("DEBUG_LOG", "DialogOfDevices: Attempt to display list of devices");
        Dialog dialog = new Dialog( this );

        // Settings of dialog
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_selectdevice_dialog);
        dialog.setCanceledOnTouchOutside(true);

        // Getting devices list

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.table_devices);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(dialog.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        ArrayList<Devices> devices = new ArrayList<Devices>();

        if (recyclerView == null)
            Log.d("DEBUG_LOG", "DialogOfDevices: Null recuclerView");
        if (pairedDevices.size() > 0) {
            Log.d("DEBUG_LOG", "DialogOfDevices: Get list of devices");
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                devices.add(new Devices(deviceName,deviceHardwareAddress));
            }
        }
        // Preparing and pluging adapter
        PairedDevicesListAdapter pairedDevicesListAdapter = new PairedDevicesListAdapter(devices);
        recyclerView.setAdapter(pairedDevicesListAdapter);

        dialog.show();

    }

    // Custom adapter
    public class PairedDevicesListAdapter extends RecyclerView.Adapter<PairedDevicesListAdapter.DeviceRowView>
    {
        // Our list of devices
        ArrayList<Devices> devices;
        public PairedDevicesListAdapter( ArrayList<Devices> devices )
        {
            this.devices = devices;
        }

        // Preparing how our row looks like
        public class DeviceRowView extends RecyclerView.ViewHolder
        {
            TextView tv_DeviceName , tv_DeviceAddress;
            LinearLayout linearLayout;

            public DeviceRowView(@NonNull View itemView) {
                super(itemView);
                tv_DeviceAddress = itemView.findViewById(R.id.tv_DeviceAddress);
                tv_DeviceName = itemView.findViewById(R.id.tv_DeviceName);
                linearLayout = itemView.findViewById(R.id.linearLayout);

                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this,
                                "You clicked: " + tv_DeviceName.getText() + " with address: " + tv_DeviceAddress.getText(), Toast.LENGTH_LONG).show();
                    }
                });
                // Add id to relative Layout

            }
        }

        // Binding with our row with layout
        @NonNull
        @Override
        public DeviceRowView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectdevice_row,parent,false);
            DeviceRowView deviceRowView = new DeviceRowView(view);
            return deviceRowView;
        }

        // Binding our row with data
        @Override
        public void onBindViewHolder(@NonNull DeviceRowView holder, int position) {
            holder.tv_DeviceName.setText(devices.get(position).name);
            holder.tv_DeviceAddress.setText(devices.get(position).address);
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }


    }
    private class Devices{
        public String name;
        public String address;
        public Devices(String name, String address)
        {
            this.address = address;
            this.name = name;
        }
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
                    Log.d("DEBUG_LOG", "OnRequestPermResult: We didn't an permission");
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