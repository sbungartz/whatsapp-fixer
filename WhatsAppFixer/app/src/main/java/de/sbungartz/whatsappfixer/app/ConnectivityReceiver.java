package de.sbungartz.whatsappfixer.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * Created by simon on 11.09.15.
 */
public class ConnectivityReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Just update scheduling of alarms after connectivity change.
        // The scheduling algorithm respects the current connection type and state.
        WhatsappRestarting.registerNextAlarm(context);
    }
}
