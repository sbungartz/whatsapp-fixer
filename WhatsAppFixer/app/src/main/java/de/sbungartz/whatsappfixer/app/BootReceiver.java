package de.sbungartz.whatsappfixer.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by simon on 09.09.15.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Heartbeats.registerNextAlarm(context);
            WhatsappRestarting.registerNextAlarm(context);
        }
    }
}
