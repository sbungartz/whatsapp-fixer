package de.sbungartz.whatsappfixer.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by simon on 10.09.15.
 */
public class WhatsappRestarting {
    public static void registerNextAlarm(Context context) {
        //SharedPreferences prefs = context.getSharedPreferences(Heartbeats.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long interval;
        try {
            interval = Long.parseLong(prefs.getString("whatsapp.restarting.interval", Heartbeats.DEFAULT_GCM_TRIGGER_INTERVAL));
        } catch(NumberFormatException e) {
            return;
        }
        boolean enabled = prefs.getBoolean("whatsapp.restarting.enabled", true);


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
    }
}
