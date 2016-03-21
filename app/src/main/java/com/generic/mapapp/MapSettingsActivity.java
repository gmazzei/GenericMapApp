package com.generic.mapapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.generic.mapapp.domain.Store;
import com.generic.mapapp.domain.StoreType;
import com.generic.mapapp.service.StoreService;

import java.util.ArrayList;
import java.util.List;

public class MapSettingsActivity extends AppCompatActivity {

    private CheckBox retailerCheckbox;
    private CheckBox massStorageCheckbox;
    private CheckBox otherCheckbox;
    private StoreService storeService = new StoreService();
    private List<CheckBox> checkboxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_settings);
        ArrayList<StoreType> criteria = (ArrayList<StoreType>) getIntent().getSerializableExtra("criteria");
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.checkbox_container);

        checkboxes = new ArrayList<>();
        for (StoreType type : storeService.getStoreTypes()) {
            CheckBox checkbox = new CheckBox(this);
            checkbox.setId(type.getId());
            checkbox.setText(type.getName());
            checkbox.setTextColor(Color.WHITE);
            checkbox.setChecked(criteria.contains(type));
            checkboxes.add(checkbox);
            linearLayout.addView(checkbox);
        }
    }

    public void sendOk(View view) {

        Intent data = new Intent();
        ArrayList<StoreType> criteria = new ArrayList<StoreType>();

        for (CheckBox checkBox : checkboxes) {
            if (checkBox.isChecked()) {
                StoreType type = storeService.findType(checkBox.getId());
                criteria.add(type);
            }
        }

        data.putExtra("criteria", criteria);
        setResult(1, data);
        finish();
    }

    public void sendCancel(View view) {
        finish();
    }

}
