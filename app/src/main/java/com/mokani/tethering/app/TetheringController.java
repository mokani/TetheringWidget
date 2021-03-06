package com.mokani.tethering.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TetheringController extends BroadcastReceiver {
    private static final String TAG = TetheringController.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals("tethering.app.intent.action.TOGGLE")) {
            Log.d(TAG, "Action Toggle");
            handleBroadcast(context, action);
        }
    }

    private void handleBroadcast(Context context, String action) {
        Log.d(TAG, "handleBroadcast");
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.tetheringwidget);

        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);

        int imageToSet = getImageToSet(wifiManager);
        remoteViews.setImageViewResource(R.id.imageButton, imageToSet);


        boolean enable = false;
        if (imageToSet == R.drawable.tethering_on) {
            enable = true;
        }

        // setWifiApEnabled is hidden method, so we have to use reflection.
        Method[] methods = wifiManager.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals("setWifiApEnabled")) {
                WifiConfiguration netConfig = new WifiConfiguration();
                netConfig.SSID = getSSIDName(context);
                if (!getSecurityType(context).equals(MainActivity.SECURITY_NONE)) {
                    netConfig.preSharedKey = getPassword(context);
                    // TODO (mokani,csunil): Give UI option to create hidden SSID.
                    netConfig.hiddenSSID = false;
                    netConfig.status = WifiConfiguration.Status.ENABLED;
                    netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                }
                Log.i(TAG, "SSID Name : " + netConfig.SSID);
                try {
                    if (enable) {
                        wifiManager.setWifiEnabled(false);
                    }
                    method.invoke(wifiManager, netConfig, enable);
                    if (!enable) {
                        wifiManager.setWifiEnabled(true);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }

        remoteViews.setOnClickPendingIntent(R.id.imageButton,
                TetheringAppWidgetProvider.buildButtonPendingIntent(context, action));

        TetheringAppWidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
    }

    private String getSecurityType(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getString(
                MainActivity.PREF_SECURITY_TYPE, MainActivity.SECURITY_NONE);
    }

    private String getSSIDName(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getString(
                MainActivity.PREF_SSID_NAME, MainActivity.DEFAULT_SSID_NAME);
    }

    private String getPassword(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.getString(MainActivity.PREF_PASSWORD, "");
    }

    /**
     * Returns the image based on current tethering status.
     *
     * If tethering is on, returns tethering off image and vice versa.
     */
    public int getImageToSet(WifiManager wifiManager) {
        Method[] methods = wifiManager.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals("isWifiApEnabled")) {
                try {
                    boolean enabled = (Boolean) method.invoke(wifiManager);
                    if (enabled) {
                        return R.drawable.tethering_off;

                    } else {
                        return R.drawable.tethering_on;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.wtf(TAG, "No wifi tethering status");
        return -1;
    }
}
