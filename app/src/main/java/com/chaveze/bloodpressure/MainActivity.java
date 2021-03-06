package com.chaveze.bloodpressure;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity
        implements IConstants {
    final String TAG = "MainActivity";

    final boolean hasDisableGFitButton = false;

    Button googleFitButton = null;
    TextView lastSyncDate = null;
    Intent authIntent = null;
    RecyclerView entriesView = null;

    DataRequestAdapter entriesAdapter = null;
    InternetManager internetMgr = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        internetMgr = new InternetManager(this);

        entriesView = findViewById(R.id.entriesView);
        entriesView.setLayoutManager(new LinearLayoutManager(this));
        entriesView.setAdapter(entriesAdapter);

        CreateNotificationChannel();
        StartAuthActivity(AUTHSTEP_INIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case AUTHSTEP_INIT:
            case AUTHSTEP_PERMISSIONS:
                if (resultCode == RESULTCODE_SUCCESS_GRANTED)
                    StartAuthActivity(AUTHSTEP_DATA_REQUEST);
                break;

            case AUTHSTEP_DATA_REQUEST:
                if (resultCode == RESULTCODE_SUCCESS_NO_HISTORY) {
                    Toast t = Toast.makeText(this, getString(R.string.txt_no_history), Toast.LENGTH_SHORT);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                    return;
                } else {
                    UpdateAdapter();
                    UpdateDateView();
                    ToggleGoogleFitButtonStatus(false);
                }

                if (resultCode != RESULTCODE_ERROR_DEFAULT) {

                    StartAuthActivity(AUTHSTEP_INIT_LISTENER);
                }
                break;

            case AUTHSTEP_INIT_LISTENER:
                StartAuthActivity(AUTHSTEP_REGISTER_LISTENER);
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
        if (!internetMgr.IsConnected()) {
            Toast t = Toast.makeText(this, getString(R.string.txt_connection_error), Toast.LENGTH_SHORT);
            t.setGravity(Gravity.CENTER, 0, 0);
            t.show();
            return;
        }


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

    TextView GetDateView() {
        if (lastSyncDate == null)
            lastSyncDate = (TextView) findViewById(R.id.txtLastSyncDate);

        return lastSyncDate;
    }

    Button GetGoogleFitButton() {
        if (googleFitButton == null)
            googleFitButton = (Button) findViewById(R.id.buttonEnable);

        return googleFitButton;
    }

    void UpdateDateView() {
        if (DataRequestUI.GetLatestItem() == null)
            return;

        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(DataRequestUI.GetLatestItem().getStartTime(TimeUnit.SECONDS)), ZoneId.systemDefault());
        String date = dateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
        GetDateView().setText(getText(R.string.txt_last_sync_date) + " " + date);
    }

    void ToggleGoogleFitButtonStatus(boolean status) {
        if (!hasDisableGFitButton) {
            GetGoogleFitButton().setVisibility(status ? View.VISIBLE : View.GONE);
        } else {
            GetGoogleFitButton().setText(status ? R.string.txt_disable_gfit : R.string.txt_disable_gfit);
        }
    }

    void CreateNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NOTIFICATION_CHANNEL_" + getString(R.string.app_name);
            String description = "Default notification channel for "+getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}