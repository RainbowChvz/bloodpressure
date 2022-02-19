package com.chaveze.bloodpressure;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.TimeUnit;

public class DataRequestAdapter extends RecyclerView.Adapter<DataRequestAdapter.ViewHolder> {
    final String TAG = "DataRequestAdapter";

    // Values according to
    // https://www.nhs.uk/common-health-questions/lifestyle/what-is-blood-pressure/
    final int MAX_IDEAL_SYS_PRESSURE = 120;
    final int MAX_IDEAL_DIA_PRESSURE = 80;
    final int MIN_IDEAL_SYS_PRESSURE = 90;
    final int MIN_IDEAL_DIA_PRESSURE = 60;

    final int UNSAFE_HIGH_SYS_PRESSURE = 140;
    final int UNSAFE_HIGH_DIA_PRESSURE = 90;
    final int UNSAFE_LOW_SYS_PRESSURE = MIN_IDEAL_SYS_PRESSURE;
    final int UNSAFE_LOW_DIA_PRESSURE = MIN_IDEAL_DIA_PRESSURE;

    DataSet entriesSet = null;

    public DataRequestAdapter(DataSet set) {
        entriesSet = set;
    }

    protected enum IMG {
        HEART, UNSAFE
    }

    protected enum TXT {
        SYS, DIA, DATE, TIME
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView heartView;
        private final TextView sysView;
        private final TextView diaView;
        private final TextView dateView;
        private final TextView timeView;
        private final ImageView exclamationView;

        public ViewHolder(View v) {
            super(v);

            heartView = (ImageView) v.findViewById(R.id.heartIcon);
            sysView = (TextView) v.findViewById(R.id.txtSys);
            diaView = (TextView) v.findViewById(R.id.txtDia);
            dateView = (TextView) v.findViewById(R.id.txtDate);
            timeView = (TextView) v.findViewById(R.id.txtTime);
            exclamationView = (ImageView) v.findViewById(R.id.unsafeIcon);
        }

        public ImageView getImageView(IMG v) {
            switch (v) {
                case HEART:  return heartView;
                case UNSAFE: return exclamationView;
            }

            return null;
        }

        public TextView getTextView(TXT v) {
            switch (v) {
                case SYS:  return sysView;
                case DIA:  return diaView;
                case DATE: return dateView;
                case TIME: return timeView;
            }

            return null;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_row_bloodpressure, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        DataPoint dp = entriesSet.getDataPoints().get(position);

        // TODO To be moved into a more appropriate class or method
        boolean inSafeZone = true;
        for (Field field : dp.getDataType().getFields()) {
            if (field.getName().contains("sys")) {
                float sysPressure = dp.getValue(field).asFloat();

                viewHolder.getTextView(TXT.SYS).setText("Sys: "+dp.getValue(field).toString());
                if (sysPressure < MIN_IDEAL_SYS_PRESSURE || sysPressure > MAX_IDEAL_SYS_PRESSURE)
                    inSafeZone = false;
            } else if (field.getName().contains("dia")) {
                float diaPressure = dp.getValue(field).asFloat();

                viewHolder.getTextView(TXT.DIA).setText("Dia: "+dp.getValue(field).toString());
                if (diaPressure < MIN_IDEAL_DIA_PRESSURE || diaPressure > MAX_IDEAL_DIA_PRESSURE)
                    inSafeZone = false;
            }
        }

        if (inSafeZone) {
            viewHolder.getTextView(TXT.SYS).setTextColor(Color.WHITE);
            viewHolder.getTextView(TXT.DIA).setTextColor(Color.WHITE);
        } else {
            viewHolder.getTextView(TXT.SYS).setTextColor(Color.RED);
            viewHolder.getTextView(TXT.DIA).setTextColor(Color.RED);
        }

        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dp.getStartTime(TimeUnit.SECONDS)), ZoneId.systemDefault());
        String date = dateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
        String time = dateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));

        if (inSafeZone) {
            viewHolder.getTextView(TXT.DATE).setText(date);
            viewHolder.getTextView(TXT.TIME).setText(time);
            viewHolder.getTextView(TXT.TIME).setVisibility(View.VISIBLE);
            viewHolder.getImageView(IMG.UNSAFE).setVisibility(View.GONE);
        } else {
            viewHolder.getTextView(TXT.DATE).setText(date + "\n" + time);
            viewHolder.getTextView(TXT.TIME).setVisibility(View.GONE);
            viewHolder.getImageView(IMG.UNSAFE).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return entriesSet.getDataPoints().size();
    }
}
