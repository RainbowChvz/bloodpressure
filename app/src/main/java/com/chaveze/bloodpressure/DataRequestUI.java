package com.chaveze.bloodpressure;

import android.util.Log;

import com.google.android.gms.fitness.data.DataSet;

import java.util.List;

public class DataRequestUI {
    final String TAG = "DataRequestUI";

    List<DataSet> requestSets;

    DataRequestUI(List<DataSet> list) {
        requestSets = list;

        UpdateRecyclerView();
    }

    private void UpdateRecyclerView() {
        Log.i(TAG, "Number of data sets: "+requestSets.size());
        MainActivity.SetAdapter(requestSets.get(0));
    }
}
