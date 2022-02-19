package com.chaveze.bloodpressure;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.HealthDataTypes;
import com.google.android.gms.fitness.request.DataReadRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

public class DataRequestHandler {
    final String TAG = "DataRequestHandler";

    DataReadRequest readingsRequest = null;
    DataRequestUI dataUI = null;

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
                dataUI = new DataRequestUI(response.getDataSets());
            })
            .addOnFailureListener(e ->
                Log.w(TAG, "There was an error reading data from Google Fit", e));
    }

    private long GetStartTime() {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).minusMonths(1).toEpochSecond();
    }

    private long GetCurrentTime() {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
    }
}
