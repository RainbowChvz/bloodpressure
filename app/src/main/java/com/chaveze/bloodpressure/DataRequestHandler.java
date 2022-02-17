package com.chaveze.bloodpressure;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.HealthDataTypes;
import com.google.android.gms.fitness.request.DataReadRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

public class DataRequestHandler {
    final String TAG = "DataRequestHandler";

    DataReadRequest readingsRequest = null;

    DataRequestHandler() {
        BuildDataRequest();
    }

    private void BuildDataRequest () {
        readingsRequest = new DataReadRequest.Builder()
            .read(HealthDataTypes.TYPE_BLOOD_PRESSURE)
            .setTimeRange(GetStartTime(), GetCurrentTime(), TimeUnit.SECONDS)
            .build();
    }

    public void RequestHistory(Context ctx, FitAccountHandler account) {
        Log.d(TAG, "GetHistory()");

        if (readingsRequest == null) {
            Log.e(TAG, "DataReadRequest is NULL!!");
            return;
        }

        if (account == null || account.googleSignInAccount == null) {
            Log.e(TAG, "FitAccountHandler has not been successfully initialized!!");
            return;
        }

        Fitness.getHistoryClient(ctx, account.googleSignInAccount)
            .readData(readingsRequest)
            .addOnSuccessListener (response -> {
                for (DataSet dataSet : response.getDataSets()) {
                    LogData(dataSet);
                }
            })
            .addOnFailureListener(e ->
                Log.w(TAG, "There was an error reading data from Google Fit", e));
    }

    // TODO Delete this method when Adapter is implemented
    private void LogData(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: "+dataSet.getDataType().getName());
        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(TAG,"Data point:");
            Log.i(TAG,"\tType: "+dp.getDataType().getName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i(TAG, "\tStart: " + ZonedDateTime.ofInstant(Instant.ofEpochSecond(dp.getStartTime(TimeUnit.SECONDS)), ZoneId.systemDefault()));
                Log.i(TAG, "\tEnd: " + ZonedDateTime.ofInstant(Instant.ofEpochSecond(dp.getEndTime(TimeUnit.SECONDS)), ZoneId.systemDefault()));
            }
            for (Field field : dp.getDataType().getFields()) {
                Log.i(TAG,"\tField: "+field.getName()+" Value: "+dp.getValue(field));
            }
        }
    }

    private long GetStartTime() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return LocalDateTime.now().atZone(ZoneId.systemDefault()).minusMonths(1).toEpochSecond();
        }

        return 0;
    }

    private long GetCurrentTime() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        }

        return 0;
    }
}
