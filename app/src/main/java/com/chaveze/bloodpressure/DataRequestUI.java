package com.chaveze.bloodpressure;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;

import java.util.List;

public class DataRequestUI {
    final String TAG = "DataRequestUI";

    static List<DataSet> requestSets;

    DataRequestUI(List<DataSet> list) {
        requestSets = list;

//        UpdateRecyclerView();
    }

    public static DataSet GetDataSet() {
        return requestSets.get(0);
    }

    public static DataPoint GetLatestItem() {
        if (requestSets == null)
            return null;

        int numDataPoints = requestSets.get(0).getDataPoints().size();
        if (numDataPoints <= 0)
            return null;

        return requestSets.get(0).getDataPoints().get(numDataPoints - 1);
    }

//    private void UpdateRecyclerView() {
//        Log.i(TAG, "Number of data sets: "+requestSets.size());
//        MainActivity.SetAdapter(requestSets.get(0));
//    }
}
