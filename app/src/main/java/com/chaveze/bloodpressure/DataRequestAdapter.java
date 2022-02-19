package com.chaveze.bloodpressure;

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
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(dp.getStartTime(TimeUnit.SECONDS)), ZoneId.systemDefault());
        String date = dateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
        viewHolder.getTextView(TXT.DATE).setText(date);
        String time = dateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
        viewHolder.getTextView(TXT.TIME).setText(time);

        for (Field field : dp.getDataType().getFields()) {
            if (field.getName().contains("sys")) {
                viewHolder.getTextView(TXT.SYS).setText(dp.getValue(field).toString());
            } else if (field.getName().contains("dia")) {
                viewHolder.getTextView(TXT.DIA).setText(dp.getValue(field).toString());
            }
        }
    }

    @Override
    public int getItemCount() {
        return entriesSet.getDataPoints().size();
    }
}
