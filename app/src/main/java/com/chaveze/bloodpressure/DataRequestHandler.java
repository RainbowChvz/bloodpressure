package com.chaveze.bloodpressure;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.HealthDataTypes;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataUpdateListenerRegistrationRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

public class DataRequestHandler {
    final String TAG = "DataRequestHandler";

    DataReadRequest readingsRequest = null;
    DataUpdateListenerRegistrationRequest updateRequest = null;
    DataRequestUI dataUI = null;

    public boolean waitingResponse;
    public boolean isResponseReady;

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
        if (readingsRequest == null) {
            Log.e(TAG, "DataReadRequest is NULL!!");
            return;
        }

        if (account == null || account.googleSignInAccount == null) {
            Log.e(TAG, "FitAccountHandler has not been successfully initialized!!");
            return;
        }

        waitingResponse = true;
        Fitness.getHistoryClient(ctx, account.googleSignInAccount)
            .readData(readingsRequest)
            .addOnSuccessListener (response -> {
                Log.i(TAG, "History has been successfully read");
                dataUI = new DataRequestUI(response.getDataSets());

                waitingResponse = false;
                if (response.getDataSets().size() > 0 && response.getDataSets().get(0).getDataPoints().size() > 0)
                    isResponseReady = true;
            })
            .addOnFailureListener(e -> {
                Log.w(TAG, "There was an error reading data from Google Fit", e);
                waitingResponse = false;
            });
    }

    public void InitUpdateListener(Context ctx) {
        Intent i = new Intent(ctx, DataRequestService.class);
        PendingIntent pending = PendingIntent.getService(ctx, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        updateRequest = new DataUpdateListenerRegistrationRequest.Builder()
            .setDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE)
            .setPendingIntent(pending)
            .build();
    }

    public void RegisterUpdateListener(Context ctx, FitAccountHandler account) {
        if (account == null || account.googleSignInAccount == null) {
            Log.e(TAG, "FitAccountHandler has not been successfully initialized!!");
            return;
        }

        waitingResponse = true;
        Fitness.getHistoryClient(ctx, account.googleSignInAccount)
            .registerDataUpdateListener(updateRequest)
            .addOnSuccessListener(unused -> {
                Log.i(TAG, "DataUpdateListener registered");
                waitingResponse = false;
            })
            .addOnFailureListener(e -> {
                Log.w(TAG, "Error while registering update listener");
                e.printStackTrace();
                waitingResponse = false;
            });
    }

    private long GetStartTime() {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).minusMonths(1).toEpochSecond();
    }

    private long GetCurrentTime() {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
    }
}
