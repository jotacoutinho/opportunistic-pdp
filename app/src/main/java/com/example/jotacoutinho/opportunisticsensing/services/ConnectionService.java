package com.example.jotacoutinho.opportunisticsensing.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.jotacoutinho.opportunisticsensing.entity.SensingData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PushbackReader;
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
        Log.i("OSApp", "init connection service");
        //if not connected, starts socket connection
        if (!connected) {
            Thread conn = new Thread() {
                @Override
                public void run() {
                    socket = new Socket();
                    try {
                        socket.connect(new InetSocketAddress(ip, port));
                        connected = true;
                        outputStream = new ObjectOutputStream(socket.getOutputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            conn.start();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BackgroundService.BROADCAST);
        registerReceiver(this.receiver, filter);

        return START_NOT_STICKY;
    }

    //this receiver handles the data object that will be sent to the server
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("OSApp", "broadcast received: " + action);
            if(action.equals(BackgroundService.BROADCAST)){
                SensingData data = (SensingData) intent.getSerializableExtra("data");
                Log.i("OSApp", "Received data: " + data.toString());
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