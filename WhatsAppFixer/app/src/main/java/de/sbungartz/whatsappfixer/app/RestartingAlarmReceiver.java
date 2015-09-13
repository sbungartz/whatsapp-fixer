package de.sbungartz.whatsappfixer.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Created by simon on 10.09.15.
 */
public class RestartingAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "HEARTBEATSIMON");
        Log.i("restarting", "acquire wakelock");
        wakeLock.acquire();


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(prefs.getBoolean("whatsapp.restarting.nonotification", true)) {
            context.startService(new Intent(NotificationListener.RESTARTING_ACTION, null, context, NotificationListener.class));
        } else {
            WhatsappRestarting.restartNow(context);
        }

        WhatsappRestarting.registerNextAlarm(context);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("restarting", "release wakelock");
                wakeLock.release();
            }
        }, WhatsappRestarting.getWakeupDuration(context));
    }
}
