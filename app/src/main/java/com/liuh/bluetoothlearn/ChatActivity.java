package com.liuh.bluetoothlearn;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private BluetoothDevice bluetoothDevice;
    private UUID uuid = UUID.fromString("00001106-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        bluetoothDevice = getIntent().getParcelableExtra("bluetoothDevice");
        if (bluetoothDevice != null) {
            sendMessage(bluetoothDevice);
        }
    }


    private void sendMessage(final BluetoothDevice bluetoothDevice) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OutputStream os = null;
                try {
                    BluetoothSocket socket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                    socket.connect();
                    os = socket.getOutputStream();
                    os.write("hiahiahia,let's chat.".getBytes());
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
