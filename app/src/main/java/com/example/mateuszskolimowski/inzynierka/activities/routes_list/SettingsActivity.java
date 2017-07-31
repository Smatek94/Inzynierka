package com.example.mateuszskolimowski.inzynierka.activities.routes_list;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.mateuszskolimowski.inzynierka.R;
import com.example.mateuszskolimowski.inzynierka.dialog_fragments.LoadingDialog;
import com.example.mateuszskolimowski.inzynierka.utils.PermissionsUtils;
import com.example.mateuszskolimowski.inzynierka.utils.SharedPreferencesUtils;
import com.example.mateuszskolimowski.inzynierka.utils.Utils;

import java.io.File;

public class SettingsActivity extends AppCompatActivity implements LoadingDialog.fragmentInteractionInterface{

    private static final int REQUEST_PATH = 1;
    private static final int FILES_PERMISSION_CODE = 2;
    private CheckBox addEventToCalendarCheckBox;
    private String curFileName;
    private Button testButton;
    private String[] permissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private boolean fillChoosed;
    private String filePath;
    private AsyncTask<Void, Void, Void> optimizingRouteFromFileAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.initToolbarTitle(getSupportActionBar(), "Ustawienia");
        setContentView(R.layout.activity_settings);
        findLayoutComponents();
        setUpGUI();
//        getfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(fillChoosed) {
            fillChoosed = false;
            Utils.showLoadingDialog("optymalizowanie...", this);
            optimizingRouteFromFileAsyncTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    Tests.test(new File(filePath));
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    hideLoadingDialog();
                }
            };
            optimizingRouteFromFileAsyncTask.execute();
        }
    }

    private void findLayoutComponents() {
        addEventToCalendarCheckBox = (CheckBox) findViewById(R.id.add_event_to_calendar_checkbox);
        testButton = (Button) findViewById(R.id.testing_button);
    }

    private void setUpGUI() {
        initCheckBox();
        initTestButton();
    }

    private void initTestButton() {
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionsUtils.requestPermission(SettingsActivity.this, SettingsActivity.this, permissions, FILES_PERMISSION_CODE)) {
                    getfile();
                }
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FILES_PERMISSION_CODE) {
            PermissionsUtils.handleRequestPermissionResult(
                    grantResults,
                    SettingsActivity.this,
                    permissions,
                    "Te pozwolenia są wymagane ponieważ bez nich aplikacja nie ma dostępu do plików z danymi testowymi.", "Bez tych pozwoleń nie jesteś w stanie przeprowadzić testów.",
                    new PermissionsUtils.OnPermissionResultListener() {
                        @Override
                        public void onDone() {
                            getfile();
                        }
                    });
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initCheckBox() {
        addEventToCalendarCheckBox.setChecked(SharedPreferencesUtils.shouldCalendarDialogBeShown(getApplicationContext()));
        addEventToCalendarCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferencesUtils.setShouldCalendarDialogBeShown(SettingsActivity.this, b);
            }
        });
    }

    public void getfile() {
        Intent intent1 = new Intent(this, FileChooserActivity.class);
        startActivityForResult(intent1, REQUEST_PATH);
    }

    // Listen for results.
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        // See which child activity is calling us back.
        if (requestCode == REQUEST_PATH) {
            if (resultCode == RESULT_OK) {
                fillChoosed = true;
                filePath = data.getStringExtra("GetPath") + "/" + data.getStringExtra("GetFileName");
            }
        }
    }

    public void hideLoadingDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.executePendingTransactions();
        LoadingDialog customDialog = (LoadingDialog) fragmentManager.findFragmentByTag(LoadingDialog.TAG);
        if (customDialog != null) {
            customDialog.dismissAllowingStateLoss();
        }
    }

    @Override
    public void backPressedWhenDialogWasVisible() {
        optimizingRouteFromFileAsyncTask.cancel(true);
        finish();
    }
}
