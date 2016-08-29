package com.kingwaytek.wifinamefinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

public class FlashAirWifiHelper extends BroadcastReceiver {

    WifiManager mWifiMgr;
    Context mContext ;
    final static String WIFI_SSID_PREX = "flashair";
    FlashAirWifiInter mCallback ;

    public interface FlashAirWifiInter{
        void onLose();
        void onFound();
        void onScaning();
    }

    public FlashAirWifiHelper(Context context,FlashAirWifiInter callback){
        mContext = context;
        mCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mCallback.onScaning();

        Log.i(MainActivity.TAG,"SSID scaning...");
        List<ScanResult> wifiList = mWifiMgr.getScanResults();

        boolean bFound = false ;
        for(int i = 0; i < wifiList.size(); i++){
            ScanResult scanResult = wifiList.get(i);
            String fullName = wifiList.get(i).toString();
            if(fullName.contains(WIFI_SSID_PREX)){
                Log.i(MainActivity.TAG,"found flashair wifi,enable FLASHAIR");
                if(!isCurrentWifiFlashAirSsid(mContext)) {
                    enableWifi(scanResult.SSID);
                }
                bFound = true ;
                mCallback.onFound();
                break;
            }
        }

        if(!bFound){
            Log.i(MainActivity.TAG,"not found flashair lose...");
            mCallback.onLose();
        }
    }

    public static boolean isCurrentWifiFlashAirSsid(Context context){
        String wifiSsid = getCurrentSsid(context);
        return wifiSsid.contains(WIFI_SSID_PREX) ;
    }

    void scanWifi(){
        mWifiMgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (!mWifiMgr.isWifiEnabled()) {
            mWifiMgr.setWifiEnabled(true);
        }

        mContext.registerReceiver(this, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiMgr.startScan();
    }

    public static String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        return ssid;
    }

    public void enableWifi(final String SSID){
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", SSID);
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(mContext.WIFI_SERVICE);
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }
}