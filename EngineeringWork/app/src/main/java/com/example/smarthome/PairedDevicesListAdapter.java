package com.example.smarthome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Custom adapter
public class PairedDevicesListAdapter extends RecyclerView.Adapter<PairedDevicesListAdapter.DeviceRowView>
{
    // Our list of devices
    ArrayList<Devices> devices;
    Context context;
    public PairedDevicesListAdapter(ArrayList<Devices> devices , Context contex)
    {
        this.devices = devices;
        this.context = contex;
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
                    Toast.makeText(context,
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
