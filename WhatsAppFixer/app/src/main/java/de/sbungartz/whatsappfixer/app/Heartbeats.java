package de.sbungartz.whatsappfixer.app;

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
public class Heartbeats {
    public static final String PREFS_NAME = "prefs";
    public static final String PREF_GCM_TRIGGER_INTERVAL = "gcm.interval";
    public static final String DEFAULT_GCM_TRIGGER_INTERVAL = "900000";

    public static void registerNextAlarm(Context context) {
        //SharedPreferences prefs = context.getSharedPreferences(Heartbeats.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long interval;
        try {
            interval = Long.parseLong(prefs.getString("gcm.heartbeat.interval", Heartbeats.DEFAULT_GCM_TRIGGER_INTERVAL));
        } catch(NumberFormatException e) {
            return;
        }
        boolean enabled = prefs.getBoolean("gcm.heartbeat.enabled", true);


        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, HeartbeatTriggerAlarmReceiver.class), 0);
        if(enabled) {
            alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + interval, alarmIntent);
            Log.i("heartbeater", "added: " + interval);
        } else {
            alarmMgr.cancel(alarmIntent);
            Log.i("heartbeater", "cancelled");
        }
    }

    public static void triggerHeartbeat(Context context) {
        context.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        context.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
        Log.i("heartbeater", "beating");
    }
}
