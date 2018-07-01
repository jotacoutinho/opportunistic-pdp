package com.example.jotacoutinho.opportunisticsensing.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.jotacoutinho.opportunisticsensing.entity.SensingData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionService extends Service {
    String ip = "127.0.0.1";
    int port = 51337;
    Socket socket;
    boolean connected = false;
    private static ObjectOutputStream outputStream = null;
    private static ObjectInputStream inputStream = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) throws SecurityException {

        //if not connected, starts socket connection
        if (!connected) {
            Thread conn = new Thread() {
                @Override
                public void run() {
                    socket = new Socket();
                    try {
                        socket.connect(new InetSocketAddress(ip, port));
                        connected = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            conn.start();

            //setup for output stream
            try {
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                //inputStream = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return START_STICKY;
    }

    //this receiver handles the data object that will be sent to the server
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BackgroundService.BROADCAST)){
                SensingData data = (SensingData) intent.getSerializableExtra("data");
                if(connected){
                    try {
                        outputStream.writeObject(data);
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
}