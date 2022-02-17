package com.chaveze.bloodpressure;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";

    public final static int REQUEST_CODE_OATH20 = 111;

    DataRequestHandler readingsHandler = null;
    FitAccountHandler accountHandler = null;
    FitAccountAuth accountAuth = null;

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
                accountAuth.SetAuthStatus(true);
                readingsHandler.RequestHistory(this, accountHandler);
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void ToggleGoogleFit(View v) {
        Button btn = (Button) v;

        if (btn.getText().equals(getText(R.string.txt_enable_gfit))) {
            readingsHandler = new DataRequestHandler();
            accountHandler = new FitAccountHandler(this);
            accountAuth = new FitAccountAuth(this, accountHandler);

            if (accountAuth.GetAuthStatus())
                readingsHandler.RequestHistory(this, accountHandler);

            btn.setText(R.string.txt_disable_gfit);
        } else if (btn.getText().equals(getText(R.string.txt_disable_gfit))) {
            accountHandler.Close(this);
            accountAuth.SetAuthStatus(false);
            // TODO Delete data

            btn.setText(R.string.txt_enable_gfit);
        }
    }
}