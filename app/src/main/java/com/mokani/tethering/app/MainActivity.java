package com.mokani.tethering.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// Some comment
public class MainActivity extends Activity implements View.OnClickListener {
    public static final String PREF_SSID_NAME = "ssid_name";
    public static final String DEFAULT_SSID_NAME = "AndroidAP";

    TextView ssidText;
    Button saveButton;
    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ssidText = (TextView) findViewById(R.id.ssidText);
        saveButton = (Button) findViewById(R.id.saveButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ssidText.setText(getSSIDName());
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        ssidText.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.saveButton) {
            ssidText = (TextView) findViewById(R.id.ssidText);
            setSSIDName(ssidText.getText().toString());
            saveButton.setEnabled(false);
            cancelButton.setEnabled(false);
        } else if (view.getId() == R.id.cancelButton) {
            ssidText = (TextView) findViewById(R.id.ssidText);
            ssidText.setText(getSSIDName());
            saveButton.setEnabled(false);
            cancelButton.setEnabled(false);
        } else if (view.getId() == R.id.ssidText) {
            saveButton.setEnabled(true);
            cancelButton.setEnabled(true);
        }
    }

    public String getSSIDName() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        return sharedPreferences.getString(PREF_SSID_NAME, DEFAULT_SSID_NAME);
    }

    private void setSSIDName(String ssidName) {
        if (ssidName.isEmpty()) {
            Toast.makeText(this.getApplicationContext(), R.string.provide_ssid, Toast.LENGTH_LONG);
            return;
        }
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(PREF_SSID_NAME, ssidName).apply();
    }
}
