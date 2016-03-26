package com.generic.mapapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.generic.mapapp.domain.Store;
import com.generic.mapapp.domain.StoreType;
import com.generic.mapapp.service.StoreService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapSettingsActivity extends AppCompatActivity {

    private static final int OK = 1;

    protected LinearLayout linearLayout;
    protected List<CheckBox> checkboxes;
    protected List<StoreType> storeTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map_settings);
        final ArrayList<StoreType> criteria = (ArrayList<StoreType>) getIntent().getSerializableExtra("criteria");

        linearLayout = (LinearLayout) findViewById(R.id.checkbox_container);
        checkboxes = new ArrayList<>();

        AsyncTask task = new AsyncTask<Object, Void, List<StoreType>>() {

            @Override
            protected List<StoreType> doInBackground(Object... params) {
                storeTypes = StoreService.get().getStoreTypes();
                return storeTypes;
            }

            @Override
            protected void onPostExecute(List<StoreType> storeTypes) {
                
                for (StoreType type : storeTypes) {
                    CheckBox checkbox = new CheckBox(MapSettingsActivity.this);
                    checkbox.setId(type.getId());
                    checkbox.setText(type.getName());
                    checkbox.setTextColor(Color.WHITE);
                    checkbox.setChecked(criteria.contains(type));
                    checkboxes.add(checkbox);
                    linearLayout.addView(checkbox);
                }

            }
        };

        task.execute();
    }

    public void sendOk(View view) {

        Intent data = new Intent();

        ArrayList<StoreType> criteria = new ArrayList<StoreType>();
        Map<Integer,StoreType> map = new HashMap<Integer,StoreType>();
        for (StoreType t : storeTypes) map.put(t.getId(),t);

        for (CheckBox checkBox : checkboxes) {
            if (checkBox.isChecked()) {
                StoreType type = map.get(checkBox.getId());
                criteria.add(type);
            }
        }

        data.putExtra("criteria", criteria);
        setResult(OK, data);
        finish();
    }

    public void sendCancel(View view) {
        finish();
    }

}
