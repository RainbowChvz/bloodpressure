package com.chaveze.bloodpressure;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.fitness.data.DataUpdateNotification;

public class DataRequestService extends IntentService {
    final String TAG = "DataRequestService";

    public DataRequestService() {
        super("DataRequestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            DataUpdateNotification update = DataUpdateNotification.getDataUpdateNotification(intent);

            if (update != null
                    && update.getOperationType() == DataUpdateNotification.OPERATION_INSERT) {
                BuildNotification();
            }
        }
    }

    void BuildNotification() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pending = PendingIntent.getActivity(this, 0, i, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, IConstants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.bloodpressure_icon_dashboard_whte)
            .setContentTitle(getString(R.string.txt_notification_title))
            .setContentText(getString(R.string.txt_notification_content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pending)
            .setAutoCancel(true);

        NotificationManagerCompat nManager = NotificationManagerCompat.from(this);
        nManager.notify(0, builder.build());
    }
}