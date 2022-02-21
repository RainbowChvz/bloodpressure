package com.chaveze.bloodpressure;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class InternetManager {
    final String TAG = "InternetManager";

    NetworkRequest networkRequest;
    ConnectivityManager connectivityManager;
    ConnectivityManager.NetworkCallback networkCallback;

    InternetManager(Context ctx) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            TODO Context.getSystemService(Class<T>) is only available from API 23
//            Therefore, connectivity won't be checked on lower API levels due to time constraints
            isConnected = true;
            return;
        }

        ConnectionRequest();
        ConnectionCallback();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ConnectionRegister(ctx);
        }
    }

    boolean isConnected;
    protected boolean IsConnected() {
        return isConnected;
    }

    void ConnectionRequest() {
        networkRequest = new NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//            TODO Commented out due to java.lang.IllegalArgumentException:
//            Cannot request network with VALIDATED
//            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build();
    }

    void ConnectionCallback() {
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);

                isConnected = true;
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);

                isConnected = false;
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities);
                boolean hasCellular = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                boolean hasWifi = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);

                isConnected = hasCellular || hasWifi;
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void ConnectionRegister(Context ctx) {
        connectivityManager = ctx.getSystemService(ConnectivityManager.class);
        connectivityManager.requestNetwork(networkRequest, networkCallback);
    }
}
