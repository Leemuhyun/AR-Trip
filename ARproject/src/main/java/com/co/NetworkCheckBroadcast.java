package com.co;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.co.util.KeyValue;


public class NetworkCheckBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        KeyValue.NETWORKCHECK = getWhatKindOfNetwork(context);
    }

    public static boolean getWhatKindOfNetwork(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        Toast.makeText(context,"네트워크연결 상태를 체크하세요.",Toast.LENGTH_SHORT).show();
        return true;
    }
}
