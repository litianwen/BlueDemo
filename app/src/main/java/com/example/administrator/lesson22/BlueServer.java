package com.example.administrator.lesson22;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Administrator on 2016/12/22.
 */

public class BlueServer extends Thread {

    RWStream rwStream;

    public RWStream getRwStream() {
        return rwStream;
    }

    private final BluetoothAdapter adapter;

    public BlueServer(BluetoothAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void run() {
        super.run();
        try {
            BluetoothServerSocket socket = adapter.listenUsingRfcommWithServiceRecord("server", MainActivity.uuid);
            Log.e("TAG", "--------------->>开始监听客户端连接");
            BluetoothSocket client = socket.accept();
            Log.e("TAG", "--------------->>有客户端接入");
            rwStream = new RWStream(client);
            rwStream.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
