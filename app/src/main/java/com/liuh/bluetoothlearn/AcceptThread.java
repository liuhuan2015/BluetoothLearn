package com.liuh.bluetoothlearn;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;


/**
 * Date: 2018/2/25 15:43
 * Description: android蓝牙设备之间可以通过SDP协议建立连接进行通信,通信方式类似于平常使用的socket
 * 首先创建BluetoothServerSocket,BluetoothAdapter中提供了两种创建BluetoothServerSocket的方式
 * <p>
 * 连接安全的方式: 通过listenUsingRfcommWithServiceRecord创建RFCOMM Bluetooth socket,需要进行配对
 * 连接不安全的方式: 通过listenUsingInsecureRfcommWithServiceRecord创建RFCOMM Bluetooth socket,连接时不需要进行配对
 * <p>
 * 其中的uuid需要服务器端和客户端进行统一
 */

public class AcceptThread extends Thread {

    private static final String TAG = "AcceptThread";
    private final String SERVICE_NAME = "bt_connect";
    private final UUID SERVICE_UUID = UUID.randomUUID();

    private BluetoothAdapter mAdapter;

    //本地服务套接字
    private final BluetoothServerSocket mServerSocket;
    private int mState;//蓝牙连接状态


    public AcceptThread() {

        BluetoothServerSocket tmp = null;
        //创建一个新的侦听服务器套接字
        try {
            tmp = mAdapter.listenUsingRfcommWithServiceRecord(SERVICE_NAME, SERVICE_UUID);
        } catch (IOException e) {
//            e.printStackTrace();
            Log.e(TAG, "listen filed");
        }
        mServerSocket = tmp;

    }

    @Override
    public void run() {
        BluetoothSocket socket = null;
        //无限循环,直到连接成功
        while (mState != STATE_CONNECTED) {

            try {
                socket = mServerSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "accept() failed", e);
                break;
            }

            //如果连接被接受
            if (socket != null) {


            }


        }


        super.run();
    }
}
