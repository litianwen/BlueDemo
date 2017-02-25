package com.example.administrator.wifidemo;

import android.content.Context;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

/**
 * Created by Administrator on 2016/12/23.
 */

public class UdpUtils extends Thread {
    WifiManager.MulticastLock sendLock;
    WifiManager.MulticastLock revicerLock;

    //单播 1-1 组播 1-M 广播 针对所有
    //UDP
    public UdpUtils(Context context) throws SocketException {
        socket = new DatagramSocket(7777);
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        sendLock = manager.createMulticastLock("send");
        revicerLock = manager.createMulticastLock("receiver");
    }

    //收包
    DatagramSocket socket;

    @Override
    public void run() {
        //接受者
        super.run();
        //开始监听
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        while (true) {
            try {
                revicerLock.acquire();
                socket.receive(packet);
                revicerLock.release();
                //执行到这里
                String msg = new String(packet.getData(), 0, packet.getLength());
                System.out.println(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //发送包
    public void sendMessage(String address, int port, String msg) {
        //发送者
        SocketAddress inetAddress = new InetSocketAddress(address, port);
        try {
            sendLock.acquire();
            DatagramPacket packet = new DatagramPacket(msg.getBytes(), 0, msg.getBytes().length, inetAddress);
            socket.send(packet);
            sendLock.release();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
