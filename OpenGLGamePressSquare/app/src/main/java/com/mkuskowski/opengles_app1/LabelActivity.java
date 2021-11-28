package com.mkuskowski.opengles_app1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class LabelActivity extends AppCompatActivity {

    private ArrayAdapter historyAdapter;
    private ListView historyView;
    private ArrayList<String> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);

        Bundle bundle = getIntent().getExtras();

        if(bundle.getStringArrayList("scores") != null)
            arrayList = bundle.getStringArrayList("scores");
        else {
            arrayList = new ArrayList<>();
            Log.println(Log.DEBUG,"@DEMOAPPLOG","Empty list");
        }

        historyView = (ListView) findViewById(R.id.ListViewLabel);

        historyAdapter = new ArrayAdapter<String>(this, R.layout.activity_label_listview,arrayList);

        historyView.setAdapter(historyAdapter);

        historyAdapter.notifyDataSetChanged();
    }
}