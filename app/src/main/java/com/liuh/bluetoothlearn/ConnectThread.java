package com.liuh.bluetoothlearn;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Date: 2018/2/26 13:51
 * Description:客户端类
 * 主要用来创建RFCOMM socket,并连接服务端
 * 先扫描周围的蓝牙设备,如果扫描到指定设备,则进行连接.
 * 连接过程主要在ConnectThread线程中进行,先创建socket,方式有两种.
 * 连接安全:createRfcommSocketToServiceRecord
 * 连接不安全:createInsecureRfcommSocketToServiceRecord
 *
 * 客户端socket会主动连接服务端,连接过程中会自动进行配对,需要双方同意才可以连接成功
 */

public class ConnectThread extends Thread {

    private static final String TAG = "ConnectThread";

    private final UUID SERVICE_UUID = UUID.randomUUID();
    private BluetoothSocket mSocket;
    BluetoothDevice mDevice;

    public ConnectThread(BluetoothDevice mDevice) {
        this.mDevice = mDevice;
        //得到一个BluetoothSocket
        try {
            mSocket = mDevice.createRfcommSocketToServiceRecord(SERVICE_UUID);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "创建BluetoothSocket失败");
            mSocket = null;
        }

    }

    @Override
    public void run() {
        Log.e(TAG, "Begin ConnectThread");
        try {
            //socket连接,该调用会阻塞,直到连接成功或失败
            mSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Connect fail");
            try {
                mSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
        //启动连接线程
//        connect(mSocket, mDevice);
    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Close mSocket fail");
        }

    }

}
