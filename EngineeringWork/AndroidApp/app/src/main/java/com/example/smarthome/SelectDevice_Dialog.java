package com.example.smarthome;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SelectDevice_Dialog extends Dialog
{

    Devices device;
    Context context;
    public SelectDevice_Dialog(@NonNull Context context , ArrayList<Devices> devices )
    {
        super(context);
        this.context = context;
        basicSettings();

        // Initialize recycle view of devices in dialog
        RecyclerView recyclerView = (RecyclerView) this.findViewById(R.id.table_devices);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        PairedDevicesListAdapter pairedDevicesListAdapter = new PairedDevicesListAdapter(devices,this.getContext(),this);
        recyclerView.setAdapter(pairedDevicesListAdapter);

    }

    private void basicSettings()
    {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setCancelable(true);
        this.setContentView(R.layout.activity_selectdevice_dialog);
        this.setCanceledOnTouchOutside(true);

    }

    public void SetSelectedDevice(String Name,String Address)
    {
        this.device = new Devices(Name,Address);
        this.dismiss();
    }


}
