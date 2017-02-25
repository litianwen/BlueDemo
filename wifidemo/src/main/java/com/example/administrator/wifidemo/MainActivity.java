package com.example.administrator.wifidemo;

import android.Manifest;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    UdpUtils utils;

    private static final String TAG = "WifiDemo";
    ListView lv;
    WifiManager wifiManager;  //管理wifi
    ArrayAdapter<String> adapter;
    List<String> wifiSSIDs = new ArrayList<>();
    WifiUtil wifiUtil;

    //    WifiInfo             //wifi信息
    //    ScanResult            //扫描结果
    //    WifiConfiguration     //wifi配置 如果需要连接wifi 必须先配置好  加密方式 wifi SSID  password
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            utils = new UdpUtils(this);
            utils.start();
            utils.sendMessage("192.168.1.255", 7777, "asdfasdfasdf");
        } catch (SocketException e) {
            e.printStackTrace();
        }


        wifiUtil = new WifiUtil(this);
        lv = (ListView) findViewById(R.id.lv);
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wifiSSIDs);
        lv.setAdapter(adapter);
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //连接wifi
                ScanResult sr = scanResults.get(position);//获取点击的扫描信息
                final String SSID = sr.SSID;
                WifiConfiguration wifiConfiguration = new WifiConfiguration();
                final int type = wifiUtil.getType(sr.capabilities);
                if (type == 1) {
                    WifiConfiguration config = wifiUtil.createWifiInfo(SSID, "", type);
                    int networkId = wifiManager.addNetwork(config);
                    wifiManager.enableNetwork(networkId, true);
                } else {
                    //有密码
                    final EditText et = new EditText(MainActivity.this);
                    et.setHint("输入wifi密码");
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("设置密码")
                            .setView(et)
                            .setNeutralButton("取消", null)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    WifiConfiguration config = wifiUtil.createWifiInfo(SSID, et.getText().toString(), type);
                                    wifiUtil.addNetWork(config);
                                }
                            }).create().show();


                }
            }
        });
    }


    public void open(View v) {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    public void close(View v) {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    List<ScanResult> scanResults;

    public void scan(View v) {
        if (wifiSSIDs != null)
            wifiSSIDs.clear();
        wifiManager.startScan();
        if (scanResults != null)
            scanResults.clear();
        scanResults = wifiManager.getScanResults();
        for (ScanResult scanResult : scanResults) {
            Log.e(TAG, scanResult.toString());
            wifiSSIDs.add(scanResult.SSID);
        }
        //显示wifi名称到listviewS
        adapter.notifyDataSetChanged();
    }

    List<WifiConfiguration> configuredNetworks;

    public void getGood(View v) {
        if (wifiSSIDs != null)
            wifiSSIDs.clear();
        if (configuredNetworks != null)
            configuredNetworks.clear();
        configuredNetworks = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration configuredNetwork : configuredNetworks) {
            Log.e(TAG, configuredNetwork.toString());
            wifiSSIDs.add(configuredNetwork.SSID);
            //连接已有的wifi配置
            //int id = wifiManager.addNetwork(configuredNetwork);
            //wifiManager.enableNetwork(id, true);
        }
        adapter.notifyDataSetChanged();
    }

}
