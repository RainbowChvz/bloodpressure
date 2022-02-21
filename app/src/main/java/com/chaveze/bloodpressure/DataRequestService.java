package com.chaveze.bloodpressure;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.fitness.data.DataUpdateNotification;

import java.util.concurrent.TimeUnit;

public class DataRequestService extends IntentService {
    final String TAG = "DataRequestService";

    public DataRequestService() {
        super("DataRequestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            DataUpdateNotification update = DataUpdateNotification.getDataUpdateNotification(intent);

            // Template implementation from Android studio,
            // automatically added when creating IntentService
            // TODO Read datapoint and add it to existing DataSet
            long start = 0;
            long end = 0;
            if (update != null) {
                start = update.getUpdateStartTime(TimeUnit.MILLISECONDS);
                end = update.getUpdateEndTime(TimeUnit.MILLISECONDS);
            }

            Log.i(TAG, "Data Update start: " + start + " end: " + end + " DataType: " + update.getDataType().getName());
        }
    }
}