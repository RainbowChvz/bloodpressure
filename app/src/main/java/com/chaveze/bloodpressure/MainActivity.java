package com.chaveze.bloodpressure;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.fitness.data.DataSet;

public class MainActivity extends AppCompatActivity {

    final public static int REQUEST_CODE_OATH20 = 111;

    final String TAG = "MainActivity";
    final boolean hasDisableGFitButton = false;

    DataRequestHandler readingsHandler = null;
    FitAccountHandler accountHandler = null;
    FitAccountAuth accountAuth = null;

    static DataRequestAdapter entriesAdapter = null;
    static RecyclerView entriesView = null;

    Button googleFitButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        entriesView = findViewById(R.id.entriesView);
        entriesView.setLayoutManager(new LinearLayoutManager(this));
        entriesView.setAdapter(entriesAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        InitHandlers();
        UseHandlers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult() + codes: "+ requestCode + " " + resultCode);

        switch (requestCode) {
            case REQUEST_CODE_OATH20:
                // Dismiss permission prompt:   resultCode = 0
                // Grant permission:            resultCode = -1
                if (resultCode == -1) {
                    accountAuth.SetAuthStatus(true);
                    UseHandlers();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void InitHandlers() {
        readingsHandler = new DataRequestHandler();
        accountHandler = new FitAccountHandler(this);
        accountAuth = new FitAccountAuth(accountHandler);
    }

    private void UseHandlers() {
        if (accountAuth.GetAuthStatus()) {
            readingsHandler.RequestHistory(this, accountHandler);
            ToggleGoogleFitButtonStatus(false);
        }
    }

    public static void SetAdapter(DataSet set) {
        entriesAdapter = new DataRequestAdapter(set);
        entriesAdapter.notifyDataSetChanged();
        entriesView.setAdapter(entriesAdapter);
    }

    public void ToggleGoogleFit(View v) {
        Button btn = (Button) v;

        if (btn.getText().equals(getText(R.string.txt_enable_gfit))) {
            if (!accountAuth.GetAuthStatus())
                accountAuth.Request(this, accountHandler);
            return;
        }

        if (!hasDisableGFitButton)
            return;

        if (btn.getText().equals(getText(R.string.txt_disable_gfit))) {
            accountHandler.Close(this);
            accountAuth.SetAuthStatus(false);
            // TODO Delete data

            ToggleGoogleFitButtonStatus(true);
        }
    }

    public Button GetGoogleFitButton() {
        if (googleFitButton == null)
            googleFitButton = (Button) findViewById(R.id.buttonEnable);

        return googleFitButton;
    }

    private void ToggleGoogleFitButtonStatus(boolean status) {
        if (!hasDisableGFitButton) {
            GetGoogleFitButton().setVisibility(status ? View.VISIBLE : View.GONE);
        } else {
            GetGoogleFitButton().setText(status ? R.string.txt_disable_gfit : R.string.txt_disable_gfit);
        }
    }
}