package com.kingwaytek.wifinamefinder;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;

public class MainActivity extends Activity implements FlashAirWifiHelper.FlashAirWifiInter{
    protected static final String TAG = "MainActivity";
    FlashAirWifiHelper mFlashAirWifiHelper ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!FlashAirWifiHelper.isCurrentWifiFlashAirSsid(this)){
            mFlashAirWifiHelper = new FlashAirWifiHelper(this,this);
            mFlashAirWifiHelper.scanWifi();
        }
    }

    void vibrator(){
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
    }

    @Override
    public void onLose() {

    }

    @Override
    public void onFound() {
        vibrator();
    }

    @Override
    public void onScaning() {

    }
}