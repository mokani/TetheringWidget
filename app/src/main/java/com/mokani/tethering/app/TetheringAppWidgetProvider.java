package com.mokani.tethering.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class TetheringAppWidgetProvider extends AppWidgetProvider {
    private static final String TAG = TetheringAppWidgetProvider.class.getSimpleName();

    private boolean startUp;

    public TetheringAppWidgetProvider() {
        startUp = true;
        Log.d(TAG, "Constructor");
    }

    @Override
    public void onAppWidgetOptionsChanged(android.content.Context context,
            android.appwidget.AppWidgetManager appWidgetManager, int appWidgetId,
            android.os.Bundle newOptions) {
        Log.i(TAG, "onAppWidgetOptionsChanged");
    }

    @Override
    public void onDeleted(android.content.Context context, int[] appWidgetIds) {
        Log.i(TAG, "onDeleted");
    }

    @Override
    public void onDisabled(android.content.Context context) {
        Log.i(TAG, "onDisabled");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.tetheringwidget);
        String action = "tethering.app.intent.action.TOGGLE";
        remoteViews.setOnClickPendingIntent(R.id.imageButton,
                buildButtonPendingIntent(context, action));
        pushWidgetUpdate(context, remoteViews);
    }

    public static PendingIntent buildButtonPendingIntent(Context context, String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
        ComponentName myWidget = new ComponentName(context, TetheringAppWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }
}