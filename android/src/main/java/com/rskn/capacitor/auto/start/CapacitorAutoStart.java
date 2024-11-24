package com.rskn.capacitor.auto.start;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import com.getcapacitor.PluginCall;

public class CapacitorAutoStart {

    public static final String PREFS = "autostart";
    public static final String ACTIVITY_CLASS_NAME = "class";
    public static final String SERVICE_CLASS_NAME = "service";

    public void enableAutoStart(final Context context, final String className, boolean enabled, boolean isService, PluginCall call) {
        int componentState;
        SharedPreferences sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (enabled) {
            componentState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
            // Store the class name of your service or main activity for AppStarter
            final String preferenceKey = isService ? SERVICE_CLASS_NAME : ACTIVITY_CLASS_NAME;
            if (className != null) {
                editor.putString(preferenceKey, className);
            } else {
                Log.e("CapacitorAutoStart", "No classname provided for enabling the plugin, seems getActivity() not working...");
                call.reject("No classname provided for enabling the plugin, seems getActivity() not working...");
            }
        } else {
            componentState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
            editor.remove(ACTIVITY_CLASS_NAME);
            editor.remove(SERVICE_CLASS_NAME);
        }
        editor.commit();
        // Enable or Disable BootCompletedReceiver
        ComponentName bootCompletedReceiver = new ComponentName(context, BootCompletedReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(bootCompletedReceiver, componentState, PackageManager.DONT_KILL_APP);
        call.resolve();
    }
}