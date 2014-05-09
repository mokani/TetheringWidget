package com.mokani.tethering.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main activity for Tethering Widget application.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    public static final String PREF_SSID_NAME = "ssid_name";
    public static final String PREF_PASSWORD = "password";
    public static final String DEFAULT_SSID_NAME = "AndroidAP";
    private static final int MINIMUM_PASSWORD_LENGTH = 8;

    TextView ssidText;
    TextView passwordText;
    Button saveButton;
    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ssidText = (TextView) findViewById(R.id.ssidText);
        passwordText = (TextView) findViewById(R.id.passwordText);
        saveButton = (Button) findViewById(R.id.saveButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Put initial value in text boxes.
        ssidText.setText(getSSIDName());
        passwordText.setText(getPassword());

        // Set required listeners on widgets.
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        ssidText.setOnClickListener(this);
        passwordText.setOnClickListener(this);

        passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // N/A
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // N/A
            }

            /**
             * If password length is not sufficient, disable save button.
             * Enable save button, if password length is sufficient.
             */
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() < MINIMUM_PASSWORD_LENGTH) {
                    saveButton.setEnabled(false);
                } else {
                    saveButton.setEnabled(true);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.saveButton) {
            setSSIDName(ssidText.getText().toString());
            setPassword(passwordText.getText().toString());
            Toast.makeText(
                    this.getApplicationContext(), "Information Saved", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.cancelButton) {
            ssidText.setText(getSSIDName());
            passwordText.setText(getPassword());
            if (passwordText.getText().toString().length() < MINIMUM_PASSWORD_LENGTH) {
                saveButton.setEnabled(false);
            } else {
                saveButton.setEnabled(true);
            }
        }
    }

    /** Returns SSID name stored in shared preference. */
    public String getSSIDName() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        return sharedPreferences.getString(PREF_SSID_NAME, DEFAULT_SSID_NAME);
    }

    /**
     * Stores SSID name into SharedPreference.
     *
     * @param ssidName Name of network SSID.
     */
    private void setSSIDName(String ssidName) {
        // If user enters empty SSID name, ask to provide SSID name using Toast message.
        if (ssidName.isEmpty()) {
            Toast.makeText(this.getApplicationContext(), R.string.provide_ssid,
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Stored the SSID preference to SharedPreference, so that we can load it next time.
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(PREF_SSID_NAME, ssidName).apply();
    }

    /** Returns password stored in shared preference. */
    private String getPassword() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        return sharedPreferences.getString(PREF_PASSWORD, "");
    }

    /** Set password in SharedPreference. */
    private void setPassword(String password) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(PREF_PASSWORD, password).apply();
    }
}
