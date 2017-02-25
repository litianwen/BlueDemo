package com.example.administrator.lesson22;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2016/12/22.
 */

public class RWStream extends Thread {

    InputStream is;
    OutputStream os;

    private final BluetoothSocket socket;

    public RWStream(BluetoothSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();

            byte[] buf = new byte[1024];
            int len = 0;
            Log.e("TAG", "-----------开始读取----(is==null)   " + (is == null));
            while (socket.isConnected()) {
                while ((len = is.read(buf)) != -1) {
                    Log.e("TAG", "----------" + new String(buf, 0, len));
                }
            }
        } catch (Exception e) {
            Log.e("TAG", "-----------线程异常");
        }
    }


    public void write(String msg) {
        Log.e("TAG", "--------os!=null   " + (os != null));
        if (os != null) {
            try {
                os.write(msg.getBytes());
                os.flush();
            } catch (Exception e) {
                Log.e("TAG", "---写入--------异常");
            }
        }
    }
}
