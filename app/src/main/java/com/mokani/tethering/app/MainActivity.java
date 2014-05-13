package com.mokani.tethering.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Main activity for Tethering Widget application.
 */
public class MainActivity extends Activity
        implements View.OnClickListener, Spinner.OnItemSelectedListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String PREF_SSID_NAME = "ssid_name";
    public static final String PREF_PASSWORD = "password";
    public static final String PREF_SECURITY_TYPE = "security_type";
    public static final String DEFAULT_SSID_NAME = "AndroidAP";
    private static final int MINIMUM_PASSWORD_LENGTH = 8;
    public static final String SECURITY_NONE = "None";
    private static final String SECURITY_WPA_PSK = "WPA PSK";

    TextView ssidText;
    TextView passwordText;
    Button saveButton;
    Button cancelButton;
    CheckBox showPasswordCheckbox;
    Spinner securityTypeSpinner;
    TextView passwordLabel;
    TextView passwordWarning;
    ArrayAdapter<String> securityTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tethering_info);

        ssidText = (TextView) findViewById(R.id.ssidText);
        passwordText = (TextView) findViewById(R.id.passwordText);
        saveButton = (Button) findViewById(R.id.saveButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        showPasswordCheckbox = (CheckBox) findViewById(R.id.showPasswordCheck);
        securityTypeSpinner = (Spinner) findViewById(R.id.securityType);
        passwordLabel = (TextView) findViewById(R.id.passwordLabel);
        passwordWarning = (TextView) findViewById(R.id.passwordWarning);

        List<String> securityList = new ArrayList<String>();
        securityList.add(SECURITY_NONE);
        securityList.add(SECURITY_WPA_PSK);

        // Create an ArrayAdapter using the string array and a default spinner layout
        securityTypeAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, securityList);
        // Specify the layout to use when the list of choices appears
        securityTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        securityTypeSpinner.setAdapter(securityTypeAdapter);

        // Set required listeners on widgets.
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        ssidText.setOnClickListener(this);
        passwordText.setOnClickListener(this);
        showPasswordCheckbox.setOnClickListener(this);
        securityTypeSpinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Put initial value in text boxes.
        ssidText.setText(getSSIDName());
        final String password = getPassword();
        passwordText.setText(password);
        final String securityType = getSecurityType();

        enableOrDisableSaveButton(password.length(), securityType);
        setPasswordTextTransformationMethod();
        securityTypeSpinner.setSelection(securityTypeAdapter.getPosition(getSecurityType()));

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
                enableOrDisableSaveButton(
                        editable.length(), securityTypeSpinner.getSelectedItem().toString());
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.saveButton) {
            setSSIDName(ssidText.getText().toString());
            setPassword(passwordText.getText().toString());
            String securityType = securityTypeSpinner.getSelectedItem().toString();
            setSecurityType(securityType);
            if (securityType.equals(SECURITY_NONE)) {
                passwordText.setText("");
            }
            setPassword(passwordText.getText().toString());
            Toast.makeText(
                    this.getApplicationContext(), "Information Saved", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.cancelButton) {
            ssidText.setText(getSSIDName());
            passwordText.setText(getPassword());
            securityTypeSpinner.setSelection(securityTypeAdapter.getPosition(getSecurityType()));
            enableOrDisableSaveButton(passwordText.toString().length(), getSecurityType());
            Toast.makeText(this.getApplicationContext(), "Information Reverted",
                    Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.showPasswordCheck) {
            setPasswordTextTransformationMethod();
        }
    }

    private void setPasswordTextTransformationMethod() {
        if (showPasswordCheckbox.isChecked()) {
            passwordText.setTransformationMethod(null);
        } else {
            passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    public void setSecurityType(String securityType) {
        if (!securityType.equals(SECURITY_NONE) && !securityType.equals(SECURITY_WPA_PSK)) {
            Log.wtf(TAG, "Invalid Security Type " + securityType);
        }

        // Store the security type preference to SharedPreference, so that we can load it next time.
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(PREF_SECURITY_TYPE, securityType).apply();
    }

    public String getSecurityType() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        return sharedPreferences.getString(PREF_SECURITY_TYPE, SECURITY_NONE);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        String securityType = adapterView.getItemAtPosition(pos).toString();
        setVisibilityOfPasswordFields(securityType);
        enableOrDisableSaveButton(getPassword().length(), securityType);
    }

    private void setVisibilityOfPasswordFields(String securityType) {
        if (securityType == SECURITY_NONE) {
            passwordText.setVisibility(View.GONE);
            passwordLabel.setVisibility(View.GONE);
            passwordWarning.setVisibility(View.GONE);
            showPasswordCheckbox.setVisibility(View.GONE);
        } else if (securityType == SECURITY_WPA_PSK) {
            passwordText.setVisibility(View.VISIBLE);
            passwordLabel.setVisibility(View.VISIBLE);
            passwordWarning.setVisibility(View.VISIBLE);
            showPasswordCheckbox.setVisibility(View.VISIBLE);
        }
    }

    public void enableOrDisableSaveButton(int passwordLength, String securityType) {
        if (!securityType.equals(SECURITY_NONE) && passwordLength < MINIMUM_PASSWORD_LENGTH) {
            saveButton.setEnabled(false);
        } else {
            saveButton.setEnabled(true);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
