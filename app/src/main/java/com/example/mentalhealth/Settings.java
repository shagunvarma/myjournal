package com.example.mentalhealth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.biometrics.BiometricManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Settings extends AppCompatActivity {

    private String fileStorageName = "userJournalStorage";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        BottomAppBar menu = findViewById(R.id.bottomAppBar);
        setSupportActionBar(menu);
        Button clear = findViewById(R.id.clearJournalsButton);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearJournalDialogue();
            }
        });
        Switch finger = findViewById(R.id.fingerprintSwitch);
        SharedPreferences settings = getSharedPreferences("UserSettings", 0);
        boolean fingSetting = settings.getBoolean("Fingerprint", false);
        finger.setChecked(fingSetting);
        finger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fingerprintChanged(isChecked);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.JournalNavButton) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("NeedFinger", false);
            startActivity(intent);
            finish();
        }
        if (id == R.id.PanicNavButton) {
            Intent intent = new Intent(this, PanicAttack.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.CalendarNavButton) {
            Intent intent = new Intent(this, CalendarActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearJournalDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete all your entries? This cannot be undone.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            FileOutputStream file = openFileOutput(fileStorageName, Context.MODE_PRIVATE);
                            file.write("".getBytes());
                            file.close();
                        } catch(Exception e) {

                        }
                    }
                })
                .setNegativeButton("Cancel", null);
        builder.create();
        builder.show();
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private void fingerprintChanged(boolean given) {
        if (given) {
            Switch finger = findViewById(R.id.fingerprintSwitch);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(this);
                boolean hasHardware = fingerprintManager.isHardwareDetected();
                boolean hasFinger = fingerprintManager.hasEnrolledFingerprints();
                if (!hasHardware) {
                    finger.setChecked(false);
                    Toast.makeText(getApplicationContext(),
                            "Your device does not have biometric hardware.", Toast.LENGTH_SHORT)
                            .show();
                } else if (!hasFinger) {
                    finger.setChecked(false);
                    Toast.makeText(getApplicationContext(),
                            "You do not have any fingerprints set up.", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    SharedPreferences settings = getSharedPreferences("UserSettings", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("Fingerprint", true);
                    editor.commit();
                }
            } else {
                androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(this);
                switch (biometricManager.canAuthenticate()) {
                    case BiometricManager.BIOMETRIC_SUCCESS:
                        SharedPreferences settings = getSharedPreferences("UserSettings", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("Fingerprint", true);
                        editor.commit();
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                        finger.setChecked(false);
                        Toast.makeText(getApplicationContext(),
                                "Your device does not have biometric hardware.", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                        finger.setChecked(false);
                        Toast.makeText(getApplicationContext(),
                                "Your device biometric hardware is unavailable.", Toast.LENGTH_SHORT)
                                .show();
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                        finger.setChecked(false);
                        Toast.makeText(getApplicationContext(),
                                "You do not have any fingerprints set up.", Toast.LENGTH_SHORT)
                                .show();
                        break;
                }
            }
        } else {
            SharedPreferences settings = getSharedPreferences("UserSettings", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("Fingerprint", false);
            editor.commit();
        }
    }
}
