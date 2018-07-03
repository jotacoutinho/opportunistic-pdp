package com.example.jotacoutinho.opportunisticsensing.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.jotacoutinho.opportunisticsensing.R;
import com.example.jotacoutinho.opportunisticsensing.entity.SensingData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackgroundService extends Service {

    private BluetoothAdapter adapter;
    Thread micRecordingThread;
    Thread connectionThread;
    public MediaRecorder recorder = null;
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;
    private Long lastTimestamp = 0l;

    public static final String BROADCAST = "data-broadcast";
    public static boolean isOSRunning = false;

    private ArrayList<String> devicesList = new ArrayList<String>();
    double latitude, longitude, altitude, amplitude = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() throws SecurityException {
        super.onCreate();

        isOSRunning = true;

        //first timestamp
        lastTimestamp = System.currentTimeMillis()/1000;

        //starting connection service
        final Intent connIntent = new Intent(getApplicationContext(), ConnectionService.class);
        startService(connIntent);

        //thread to handle server-client connection (send collected data)
        connectionThread = new Thread(){
            @Override
            public void run() {
                while(isOSRunning){
                    //if (!((System.currentTimeMillis()/1000 + 15) - lastTimestamp >= 15)) {
                    try {
                        Thread.sleep(15000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //get maxAmplitude and reset recorder
                        amplitude = recorder.getMaxAmplitude();

                        recorder.stop();
                        recorder.release();
                        recorder = null;
                        micRecordingThread.start();

                        SensingData lastSensing = new SensingData(latitude, longitude, altitude, devicesList, amplitude);
                        Log.i("OSApp", lastSensing.toString());

                        Intent intent = new Intent(BROADCAST);
                        intent.putExtra("data", lastSensing);
                        sendBroadcast(intent);
                    }
            }
        };
        connectionThread.start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) throws SecurityException {
//        if(intent.getExtras() == null){
//            Log.i("OSApp", "no options selected");
//            return START_NOT_STICKY;
//        }

        // period control
        lastTimestamp = System.currentTimeMillis()/1000;

        //setup for bluetooth discovery
        if(intent.getExtras().get("bt-enabled").equals(true)){
            adapter = BluetoothAdapter.getDefaultAdapter();
            adapter.startDiscovery();

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(btReceiver, filter);
        }

        //setup for microphone recording thread
        if(intent.getExtras().get("mic-enabled").equals(true)){
            if(recorder == null){
                micRecordingThread = new Thread() {
                    @Override
                    public void run() {
                        recorder = new MediaRecorder();
                        try{
                            //configuring recorder to save file osappsensoring.3gp at /storage/emulated/0/Android/data/com.example.jotacoutinho.opportunisticsensing/cache
                            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                            recorder.setAudioSamplingRate(8000);
                            recorder.setAudioEncodingBitRate(12200);
                            recorder.setOutputFile(getExternalCacheDir().getAbsolutePath() + "/osappsensoring.3gp");

                            recorder.prepare();
                            recorder.start();
                            //this method returns the max value since its last call, so we need to call it a first time
                            Log.i("OSApp", "first call max ampl: " + recorder.getMaxAmplitude());
                        } catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                };
                micRecordingThread.start();
            }
        }

        //setup for gps coordinates
        if(intent.getExtras().get("gps-enabled").equals(true)){
            if(locationManager == null && locationListener == null){
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationListener = new MyLocationListener();
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, locationListener);
            }
        }

        Log.i("OSApp", "running background service");

        return START_STICKY;
    }

    //this receiver handles the list of discovered devices to populate the list
    private final BroadcastReceiver btReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_FOUND) && !intent.getExtras().isEmpty()){
                Log.i("OSApp", "bt-service: ACTION_FOUND");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(device == null){
                    return;
                } else{
                    String deviceName =  device.getAddress();

                    if(devicesList.isEmpty()){
                        devicesList.add(deviceName);
                    }

                    for(String d : devicesList){
                        if(!deviceName.equals(d)){
                            devicesList.add(deviceName);
                            Log.i("OSApp", "bt-service: FOUND NEW DEVICE " + deviceName);
                        }
                    }
                }
            }
        }
    };

    //this listener gets updated coordinates every 4s (in theory, but the service runs in background, which is called whenever the OS wants).
    public class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            altitude = location.getAltitude();

            Log.i("OSApp", "Current Latitude: " + latitude + "/ Current Longitude: " + longitude + "/ Current Altitude: " + altitude);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    }
}
