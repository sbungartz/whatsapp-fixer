package de.sbungartz.whatsappfixer.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/**
 * Created by simon on 09.09.15.
 */
public class HeartbeatTriggerAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "HEARTBEATSIMON");
        Log.i("heartbeater", "acquire wakelock");
        wakeLock.acquire();

        try {
            Heartbeats.triggerHeartbeat(context);
            Heartbeats.registerNextAlarm(context);
        } finally {
            Log.i("heartbeater", "release wakelock");
            wakeLock.release();
        }
    }
}
