package com.chaveze.bloodpressure;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.HealthDataTypes;
import com.google.android.gms.fitness.request.DataReadRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    final String TAG = "Google Fit";

    final int REQUEST_CODE_OATH20 = 111;
    final int REQUEST_CODE_ACTIVITY_PERMISSION = 222;

    DataReadRequest readRequest = null;
    GoogleSignInOptionsExtension fitnessOptions = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult() + codes: "+ requestCode + " " + grantResults[0]);

        switch (requestCode) {
            case REQUEST_CODE_ACTIVITY_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GetHistory();
                }
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult() + codes: "+ requestCode + " " + resultCode);

        switch (requestCode) {
            case REQUEST_CODE_OATH20:
                if (resultCode == PackageManager.PERMISSION_GRANTED) {
                    GetHistory();
                } else {
                    InitPermissions();
                }
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
            // TODO Delete account
            // TODO Delete data

            btn.setText(R.string.txt_enable_gfit);
        }
    }

    private void InitRequest() {
        // Read the data that's been collected throughout the past week.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ZonedDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault());
//            ZonedDateTime endTime = LocalDateTime.now().atZone(ZoneId.systemDefault());
            ZonedDateTime startTime = endTime.minusWeeks(1);
            Log.i(TAG, "Range Start: "+startTime);
            Log.i(TAG, "Range End: "+endTime);

            readRequest = new DataReadRequest.Builder()
                    // The data request can specify multiple data types to return,
                    // effectively combining multiple data queries into one call.
                    // This example demonstrates aggregating only one data type.
                    // TODO Replace step count with blood pressure
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
                    // Analogous to a "Group By" in SQL, defines how data should be
                    // aggregated.
                    // bucketByTime allows for a time span, while bucketBySession allows
                    // bucketing by sessions.
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
                    .build();
        }
    }

    private void InitAccount() {
        if (readRequest != null) {
            fitnessOptions =
                    FitnessOptions.builder()
                            // TODO Replace step count with blood pressure
                            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                            .build();

            GoogleSignInAccount googleSignInAccount =
                    GoogleSignIn.getAccountForExtension(this, fitnessOptions);

            if (!GoogleSignIn.hasPermissions(googleSignInAccount, fitnessOptions)) {
                Log.i(TAG, "Asking for permission");

                GoogleSignIn.requestPermissions(
                        this,
                        REQUEST_CODE_OATH20,
                        googleSignInAccount,
                        fitnessOptions
                );
            } else {
                InitPermissions();
            }
        }
    }

    private void InitPermissions() {
        // TODO Verify if permission ACTIVITY_RECOGNITION is necessary for blood pressure
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.ACTIVITY_RECOGNITION },
                    REQUEST_CODE_ACTIVITY_PERMISSION);
        } else {
            GetHistory();
        }
    }

    private void GetHistory() {
        Log.d(TAG, "GetHistory()");

        Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
                .readData(readRequest)
                .addOnSuccessListener (response -> {
                    // The aggregate query puts datasets into buckets, so convert to a
                    // single list of datasets
                    for (Bucket bucket : response.getBuckets()) {
                        for (DataSet dataSet : bucket.getDataSets()) {
                            dumpDataSet(dataSet);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Log.w(TAG, "There was an error reading data from Google Fit", e));
    }

    private void dumpDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: "+dataSet.getDataType().getName());
        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(TAG,"Data point:");
            Log.i(TAG,"\tType: "+dp.getDataType().getName());
            Log.i(TAG,"\tStart: "+dp.getStartTime(TimeUnit.SECONDS));
            Log.i(TAG,"\tEnd: "+dp.getEndTime(TimeUnit.SECONDS));
            for (Field field : dp.getDataType().getFields()) {
                Log.i(TAG,"\tField: "+field.getName()+" Value: "+dp.getValue(field));
            }
        }
    }
}