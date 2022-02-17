package com.chaveze.bloodpressure;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
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

public class MainActivity extends AppCompatActivity {

    final String TAG = "Google Fit";

    final int REQUEST_CODE_OATH20 = 111;

    DataReadRequest readRequest = null;
    GoogleSignInAccount googleSignInAccount = null;
    GoogleSignInOptionsExtension fitnessOptions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult() + codes: "+ requestCode + " " + resultCode);

        switch (requestCode) {
            case REQUEST_CODE_OATH20:
                GetHistory();
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void ToggleGoogleFit(View v) {
        Button btn = (Button) v;

        if (btn.getText().equals(getText(R.string.txt_enable_gfit))) {
            InitRequest();
            InitAccount();

            btn.setText(R.string.txt_disable_gfit);
        } else if (btn.getText().equals(getText(R.string.txt_disable_gfit))) {
            DeleteAccount();
            // TODO Delete data

            btn.setText(R.string.txt_enable_gfit);
        }
    }

    private void InitRequest() {
        // Read the data that's been collected throughout the past week.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault());
            ZonedDateTime endTime = LocalDateTime.now().atZone(ZoneId.systemDefault());
            ZonedDateTime startTime = endTime.minusDays(30);
            Log.i(TAG, "Range Start: " + startTime);
            Log.i(TAG, "Range End: " + endTime);

            readRequest = new DataReadRequest.Builder()
                    // The data request can specify multiple data types to return,
                    // effectively combining multiple data queries into one call.
                    // This example demonstrates aggregating only one data type.
                    .read(HealthDataTypes.TYPE_BLOOD_PRESSURE)
//                    .aggregate(HealthDataTypes.TYPE_BLOOD_PRESSURE)
                    // Analogous to a "Group By" in SQL, defines how data should be
                    // aggregated.
                    // bucketByTime allows for a time span, while bucketBySession allows
                    // bucketing by sessions.
//                    .bucketByTime(1, TimeUnit.HOURS)
                    .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
                    .build();
        }
    }

    private void InitAccount() {
        if (readRequest != null) {
            fitnessOptions =
                    FitnessOptions.builder()
                            .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
                            .build();

            googleSignInAccount =
                    GoogleSignIn.getAccountForExtension(this, fitnessOptions);

            if (GoogleSignIn.hasPermissions(googleSignInAccount, fitnessOptions)) {
                GetHistory();
            } else {
                Log.i(TAG, "Asking for permission");

                GoogleSignIn.requestPermissions(
                        this,
                        REQUEST_CODE_OATH20,
                        googleSignInAccount,
                        fitnessOptions
                );
            }
        }
    }

    private void DeleteAccount() {
        if (fitnessOptions != null && googleSignInAccount != null) {
            Fitness.getConfigClient(this, googleSignInAccount)
                .disableFit()
                .addOnSuccessListener(unused ->
                    Log.i(TAG, "Disabled Google Fit"))
                .addOnFailureListener(e ->
                    Log.w(TAG, "Error when disabling Fitness", e));
        }
    }

    private void GetHistory() {
        Log.d(TAG, "GetHistory()");

        Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
                .readData(readRequest)
                .addOnSuccessListener (response -> {
                    // The aggregate query puts datasets into buckets, so convert to a
                    // single list of datasets
//                    for (Bucket bucket : response.getBuckets()) {
                        for (DataSet dataSet : response.getDataSets()) {
                            dumpDataSet(dataSet);
                        }
//                    }
                })
                .addOnFailureListener(e ->
                        Log.w(TAG, "There was an error reading data from Google Fit", e));
    }

    private void dumpDataSet(DataSet dataSet) {
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
}