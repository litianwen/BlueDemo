package com.example.administrator.wifidemo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Administrator on 2016/12/23.
 */

public class TcpUtils extends Thread {

    ServerSocket serverSocket;

    public TcpUtils() {
        try {
            serverSocket = new ServerSocket(7777);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //客户端连接
//        try {
//            Socket socket = new Socket("192.168.1.211",7777);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void run() {
        super.run();
        try {
            while (true) {
                Socket socket = serverSocket.accept();
//                socket.getInputStream();
//                socket.getOutputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
