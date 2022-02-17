package com.chaveze.bloodpressure;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.HealthDataTypes;

public class FitAccountHandler {
    final String TAG = "FitAccountHandler";

    GoogleSignInAccount googleSignInAccount = null;
    GoogleSignInOptionsExtension fitnessOptions = null;

    FitAccountHandler(Context ctx) {
        Init(ctx);
    }

    private void Init(Context ctx) {
        fitnessOptions =
            FitnessOptions.builder()
                .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
                .build();

        googleSignInAccount =
            GoogleSignIn.getAccountForExtension(ctx, fitnessOptions);
    }

    public void Close(Context ctx) {
        if (fitnessOptions != null && googleSignInAccount != null) {
            Fitness.getConfigClient(ctx, googleSignInAccount)
                .disableFit()
                .addOnSuccessListener(unused ->
                    Log.i(TAG, "Disabled Google Fit"))
                .addOnFailureListener(e ->
                    Log.w(TAG, "Error when disabling Fitness", e)
            );
        }
    }
}
