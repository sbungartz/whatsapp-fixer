package de.sbungartz.whatsappfixer.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by simon on 10.09.15.
 */
public class WhatsappRestarting {
    public static void registerNextAlarm(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enabled = prefs.getBoolean("whatsapp.restarting.enabled", true);
        long interval = 0;

        if(enabled) {
            String connType = getConnectionType(context);
            if(connType.equals("disconnected")) {
                enabled = false;
            } else {
                interval = getStringPrefAsLong(prefs, "whatsapp.restarting.interval." + connType);
            }
        }

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, RestartingAlarmReceiver.class), 0);
        if(enabled) {
            alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + interval, alarmIntent);
            Log.i("restarting", "added: " + interval);
        } else {
            alarmMgr.cancel(alarmIntent);
            Log.i("restarting", "cancelled");
        }
    }
    
    private static String getConnectionType(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        String connType;
        if (netInfo == null || netInfo.isConnected() == false) {
            connType = "disconnected";
        } else if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            connType = "wifi";
        } else {
            connType = "mobile";
        }
        return connType;
    }

    private static long getStringPrefAsLong(SharedPreferences prefs, String key) {
        long value = 0;
        try {
            value = Long.parseLong(prefs.getString(key, "0"));
        } catch(NumberFormatException e) {
            // ignore, 0 will be used.
        }
        return value;
    }

    public static long getWakeupDuration(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long duration;
        try {
            duration = Long.parseLong(prefs.getString("whatsapp.restarting.wakeduration", "5000"));
        } catch(NumberFormatException e) {
            duration = 5000;
        }
        return duration;
    }

    public static void restartNow(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        am.killBackgroundProcesses("com.whatsapp");
        Log.i("restarting", "restarted");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("whatsapp.restarting.lasttime", System.currentTimeMillis());
        editor.commit();
    }
}
