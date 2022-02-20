package com.chaveze.bloodpressure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.fitness.data.DataSet;

public class MainActivity extends Activity
        implements IConstants {
    final String TAG = "MainActivity";

    final boolean hasDisableGFitButton = false;

    Button googleFitButton = null;
    Intent authIntent = null;

    DataRequestAdapter entriesAdapter = null;
    RecyclerView entriesView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        entriesView = findViewById(R.id.entriesView);
        entriesView.setLayoutManager(new LinearLayoutManager(this));
        entriesView.setAdapter(entriesAdapter);

        StartAuthActivity(AUTHSTEP_INIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case AUTHSTEP_INIT:
                if (resultCode == RESULTCODE_SUCCESS_GRANTED)
                    StartAuthActivity(AUTHSTEP_PERMISSIONS);
                break;

            case AUTHSTEP_PERMISSIONS:
                if (resultCode == RESULTCODE_SUCCESS_GRANTED)
                    StartAuthActivity(AUTHSTEP_DATA_REQUEST);
                break;

            case AUTHSTEP_DATA_REQUEST:
                if (resultCode == RESULTCODE_SUCCESS) {
                    ToggleGoogleFitButtonStatus(false);
                    UpdateAdapter();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    void StartAuthActivity(int step) {
        startActivityForResult(GetAuthIntent().putExtra("STEP", step), step);
    }

    void UpdateAdapter() {
        entriesAdapter = new DataRequestAdapter();
        entriesAdapter.notifyDataSetChanged();
        entriesView.setAdapter(entriesAdapter);
    }

    public void ToggleGoogleFit(View v) {
        Button btn = (Button) v;

        if (btn.getText().equals(getText(R.string.txt_enable_gfit))) {

            StartAuthActivity(AUTHSTEP_PERMISSIONS);
            return;
        }
    }

    Intent GetAuthIntent() {
        if (authIntent == null)
            authIntent = new Intent(this, AccountActivity.class);

        return authIntent;
    }

    Button GetGoogleFitButton() {
        if (googleFitButton == null)
            googleFitButton = (Button) findViewById(R.id.buttonEnable);

        return googleFitButton;
    }

    void ToggleGoogleFitButtonStatus(boolean status) {
        if (!hasDisableGFitButton) {
            GetGoogleFitButton().setVisibility(status ? View.VISIBLE : View.GONE);
        } else {
            GetGoogleFitButton().setText(status ? R.string.txt_disable_gfit : R.string.txt_disable_gfit);
        }
    }
}