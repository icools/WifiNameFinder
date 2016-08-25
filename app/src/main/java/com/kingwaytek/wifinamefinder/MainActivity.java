package com.kingwaytek.wifinamefinder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import java.util.List;

public class MainActivity extends Activity {
    WifiManager mWifiMgr;
    WifiReceiver mReceiverWifi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        scanWifi();
    }

    private void scanWifi(){
        mWifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!mWifiMgr.isWifiEnabled()) {
            mWifiMgr.setWifiEnabled(true);
        }

        mReceiverWifi = new WifiReceiver();
        registerReceiver(mReceiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiMgr.startScan();
    }


    void vibrator(){
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> wifiList = mWifiMgr.getScanResults();
            for(int i = 0; i < wifiList.size(); i++){

                String fullName = wifiList.get(i).toString() ;
                Log.i("Wifi","Wifi:" + fullName);
                if(fullName.contains("flashair")){
                    vibrator();
                }
            }
        }
    }
}
