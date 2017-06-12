package com.example.mateuszskolimowski.inzynierka.activities.routes_list;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.utils.SharedPreferencesUtils;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;

public class SettingsActivity extends AppCompatActivity {

    private CheckBox addEventToCalendarCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.initToolbarTitle(getSupportActionBar(), "Ustawienia");
        setContentView(R.layout.activity_settings);
        findLayoutComponents();
        setUpGUI();
    }

    private void findLayoutComponents() {
        addEventToCalendarCheckBox = (CheckBox) findViewById(R.id.add_event_to_calendar_checkbox);
    }

    private void setUpGUI() {
        initCheckBox();
    }

    private void initCheckBox() {
        addEventToCalendarCheckBox.setChecked(SharedPreferencesUtils.shouldCalendarDialogBeShown(getApplicationContext()));
        addEventToCalendarCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferencesUtils.setShouldCalendarDialogBeShown(SettingsActivity.this,b);
            }
        });
    }
}
