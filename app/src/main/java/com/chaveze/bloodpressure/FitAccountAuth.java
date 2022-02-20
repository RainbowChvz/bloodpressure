package com.chaveze.bloodpressure;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;

public class FitAccountAuth {
    final String TAG = "FitAccountAuth";

    boolean authGranted = false;

    FitAccountAuth(FitAccountHandler account) {
        CheckAuth(account);
    }

    public void CheckAuth(FitAccountHandler account) {
        if (account == null) {
            Log.e(TAG, "FitAccountHandler has not been successfully initialized!!");
            return;
        }

        if (GoogleSignIn.hasPermissions( account.googleSignInAccount, account.fitnessOptions)) {
            authGranted = true;
        }
    }

    public void Request(Activity mainAct, FitAccountHandler account) {
        if (account == null) {
            Log.e(TAG, "FitAccountHandler has not been successfully initialized!!");
            return;
        }

        if (!GoogleSignIn.hasPermissions( account.googleSignInAccount, account.fitnessOptions)) {
            authGranted = false;
            GoogleSignIn.requestPermissions(
                mainAct,
                IConstants.REQUESTCODE_OAUTH20,
                account.googleSignInAccount,
                account.fitnessOptions
            );
        }
    }

    public void SetAuthStatus (boolean status) {
        authGranted = status;
    }

    public boolean GetAuthStatus () {
        return authGranted;
    }
}