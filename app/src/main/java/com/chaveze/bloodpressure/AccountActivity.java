package com.chaveze.bloodpressure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class AccountActivity extends Activity
        implements IConstants {

    final String TAG = "AccountActivity";

    static DataRequestHandler readingsHandler = null;
    static FitAccountHandler accountHandler = null;
    static FitAccountAuth accountAuth = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_account);

        Update();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUESTCODE_OAUTH20) {
            if (resultCode == RESULTCODE_GOOGLE_PERMISSION_GRANTED) {
                accountAuth.SetAuthStatus(true);
                setResult(RESULTCODE_SUCCESS_GRANTED);
            }
            finish();
        }
    }

    void Update() {
        int authStep = getIntent().getIntExtra("STEP", 0);
        setResult(RESULTCODE_ERROR_DEFAULT);

        switch (authStep) {
            case AUTHSTEP_INIT:
                InitAccount();
                break;

            case AUTHSTEP_PERMISSIONS:
                InitPermissions();
                break;

            case AUTHSTEP_DATA_REQUEST:
                InitDataRequest();
                break;
        }
    }

    void InitAccount() {
        readingsHandler = new DataRequestHandler();
        accountHandler = new FitAccountHandler(this);
        accountAuth = new FitAccountAuth(accountHandler);

        setResult(RESULTCODE_SUCCESS);
        if (accountAuth.GetAuthStatus())
            setResult(RESULTCODE_SUCCESS_GRANTED);
        finish();
    }

    void InitPermissions() {
        if (accountAuth == null)
            return;

        if (!accountAuth.GetAuthStatus())
            accountAuth.Request(this, accountHandler);

        setResult(RESULTCODE_SUCCESS);
    }

    void InitDataRequest() {
        if (accountAuth == null)
            return;

        if (accountAuth.GetAuthStatus())
            readingsHandler.RequestHistory(this, accountHandler);

        AsyncWaitOnResponse();
    }

    void AsyncWaitOnResponse() {
        Thread wait = new Thread() {
            public void run() {
                long start = System.currentTimeMillis();
                while ((readingsHandler.waitingResponse
                    || !readingsHandler.isResponseReady)
                    && System.currentTimeMillis() - start < TIMEOUT_DATA_REQUEST
                ) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {}
                }

                if (readingsHandler.isResponseReady)
                    setResult(RESULTCODE_SUCCESS);
                else
                    setResult(RESULTCODE_ERROR_DEFAULT);

                finish();
            }
        };
        wait.start();
    }
}