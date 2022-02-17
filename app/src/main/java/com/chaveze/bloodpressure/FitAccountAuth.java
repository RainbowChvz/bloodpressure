package com.chaveze.bloodpressure;

import android.app.Activity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;

public class FitAccountAuth {
    final String TAG = "FitAccountAuth";

    boolean authGranted = false;

    FitAccountAuth(Activity mainAct, FitAccountHandler account) {
        if (!GoogleSignIn.hasPermissions(
                account.googleSignInAccount,
                account.fitnessOptions)
        ) {
            authGranted = false;
            GoogleSignIn.requestPermissions(
                mainAct,
                MainActivity.REQUEST_CODE_OATH20,
                account.googleSignInAccount,
                account.fitnessOptions
            );
        } else {
            authGranted = true;
        }
    }

    public void SetAuthStatus (boolean status) {
        authGranted = status;
    }

    public boolean GetAuthStatus () {
        return authGranted;
    }
}