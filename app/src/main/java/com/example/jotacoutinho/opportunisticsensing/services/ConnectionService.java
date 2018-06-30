package com.example.jotacoutinho.opportunisticsensing.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionService extends Service {
    String ip = "127.0.0.1";
    int port = 51337;
    Socket socket;
    boolean connected = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) throws SecurityException {

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
        }

        return START_STICKY;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //if(action == received)
            //serialize obj
            //send to server
        }
    };
}