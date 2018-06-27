package com.example.jotacoutinho.opportunisticsensing.services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BackgroundService extends Service {

    private BluetoothAdapter adapter;
    private List<String> devicesList = new ArrayList<String>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // do your jobs here

        if(intent.getExtras() == null){
            return START_NOT_STICKY;
        }

        if(intent.getExtras().get("bt-enabled").equals(true)){
            adapter = BluetoothAdapter.getDefaultAdapter();

            adapter.startDiscovery();

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(btReceiver, filter);
        }


        Log.i("OSApp", "running background service");

        return START_STICKY;
    }

    private final BroadcastReceiver btReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("OSApp", "bt-service: onReceive");
            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                Log.i("OSApp", "bt-service: ACTION_FOUND");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName =  device.getName() + "\n" + device.getAddress();

                for(String d : devicesList){
                    if(!deviceName.equals(d)){
                        devicesList.add(deviceName);
                        Log.i("OSApp", "bt-service: FOUND NEW DEVICE " + deviceName);
                    }
                }
            }
        }
    };
}
