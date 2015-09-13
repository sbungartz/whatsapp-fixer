package de.sbungartz.whatsappfixer.app;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by simon on 13.09.15.
 */
public class NotificationListener extends NotificationListenerService {
    public static final String RESTARTING_ACTION = "de.sbungartz.whatsappfixer.notifications.restart.if.no.whatsapp";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(RESTARTING_ACTION.equals(intent.getAction())) {
            StatusBarNotification[] notifications = getActiveNotifications();

            boolean shouldRestart = true;
            if(notifications != null) {
                for(StatusBarNotification notification : notifications) {
                    if("com.whatsapp".equals(notification.getPackageName())) {
                        shouldRestart = false;
                        break;
                    }
                }
            }

            if(shouldRestart) {
                WhatsappRestarting.restartNow(getApplicationContext());
            }
            stopSelf();
            return START_NOT_STICKY;
        } else {
            return super.onStartCommand(intent, flags, startId);
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }
}
