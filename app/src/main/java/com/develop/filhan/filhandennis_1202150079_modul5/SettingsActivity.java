package com.develop.filhan.filhandennis_1202150079_modul5;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private ListView listSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        listSettings=(ListView)findViewById(R.id.listSettings);

        final ArrayList<HashMap<String, String>> settings = new ArrayList<>();

        HashMap<String, String> item = new HashMap<>();
        item.put("key","Shape Color");
        item.put("","Red");

        ArrayAdapter dataAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, settings){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView text1=(TextView)v.findViewById(android.R.id.text1);
                TextView text2=(TextView)v.findViewById(android.R.id.text2);
                text1.setText(settings.get(position).get("key"));
                text2.setText(settings.get(position).get("val"));
                return v;
            }
        };
    }
}
