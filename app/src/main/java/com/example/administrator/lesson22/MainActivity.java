package com.example.administrator.lesson22;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 10213;
    private static final int REQUEST_ENABLE = 123;
    private static final int REQUEST_DISCOVER = 333;
    ListView lv;

    //2套 128  8 4 4 4 12
    public static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    List<BluetoothDevice> devices = new ArrayList<>();
    List<String> deviceNames = new ArrayList<>();
    ArrayAdapter<String> adapter;

    BluetoothAdapter bluetoothAdapter;

    //蓝牙连接，必须有一端作为服务端
    //手机连接设备，设备是服务端

    TextView tv_show;
    EditText et_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_show = (TextView) findViewById(R.id.tv_show);
        et_send = (EditText) findViewById(R.id.et_send);
        lv = (ListView) findViewById(R.id.lv);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceNames);
        lv.setAdapter(adapter);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断是否有了权限
        checkPermission();

        //服务器往客户端发消息

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                connServer(devices.get(position));
            }
        });
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //23
            int check = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (check != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "权限以获取", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "权限未获取", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void open(View v) {
        //不推荐
        //bluetoothAdapter.enable();
        //推荐
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == resultCode) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "蓝牙已开启", Toast.LENGTH_SHORT).show();
                getBondedDevices();
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "蓝牙未开启", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_DISCOVER) {
            //是否允许被扫描
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取设备的广播
            if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Toast.makeText(context, "蓝牙扫描完毕", Toast.LENGTH_SHORT).show();
            } else {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devices.add(device);
                deviceNames.add(TextUtils.isEmpty(device.getName()) ? "未命名" : device.getName());
                adapter.notifyDataSetChanged();
            }
        }
    };

    public void close(View v) {
        bluetoothAdapter.disable();
    }

    public void found(View v) {
        //300秒
        getBondedDevices();
        if (bluetoothAdapter.isDiscovering()) {
            Toast.makeText(this, "正在扫描，别急", Toast.LENGTH_SHORT).show();
        } else
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 600), REQUEST_DISCOVER);
    }

    private void getBondedDevices() {
        //获取所有已经绑定了的设备
        deviceNames.clear();
        devices.clear();
        if (bluetoothAdapter.getBondedDevices() != null) {
            List<BluetoothDevice> liset = new ArrayList<>(bluetoothAdapter.getBondedDevices());
            devices.addAll(liset);
            for (BluetoothDevice device : liset) {
                deviceNames.add(device.getName());
            }
        }
    }

    public void scan(View v) {
        if (bluetoothAdapter.isEnabled()) {
            //使用广播的方法去获取设备
            bluetoothAdapter.startDiscovery();
        } else {
            Toast.makeText(this, "请先开启蓝牙", Toast.LENGTH_SHORT).show();
        }
    }

    BlueServer server;

    public void createServer(View v) {
        server = new BlueServer(bluetoothAdapter);
        server.start();
        isServer = true;
    }

    boolean isServer = false;
    RWStream client;

    public void write(String msg) {
        if (isServer) {
            Log.e("TAG", "----------(server != null && server.getRwStream() != null)  " + (server != null && server.getRwStream() != null));
            if (server != null && server.getRwStream() != null) {
                server.getRwStream().write(msg);
            }
        } else {
            if (client != null) {
                client.write(msg);
            }
        }
    }

    private void connServer(BluetoothDevice device) {
        try {
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            client = new RWStream(socket);
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void send(View v) {
        String et = et_send.getText().toString();
        write(et);
    }
}
