package com.example.jotacoutinho.opportunisticsensing.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jotacoutinho.opportunisticsensing.R;
import com.example.jotacoutinho.opportunisticsensing.services.BackgroundService;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 1;
    private final static int REQUEST_ENABLE_MIC = 2;
    private final static int REQUEST_ENABLE_GPS = 3;

    private boolean isBtEnabled = false;
    private boolean isMicEnabled = false;
    private boolean isGpsEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent intent = new Intent(getApplicationContext(), BackgroundService.class);

        SwitchCompat bluetoothSwitch = findViewById(R.id.switchBluetooth);
        SwitchCompat microphoneSwitch = findViewById(R.id.switchMicrophone);
        SwitchCompat gpsSwitch = findViewById(R.id.switchGPS);
        //Button startButton = findViewById(R.id.startButton);
        ImageView startButtonView = findViewById(R.id.startButtonView);

        startButtonView.getLayoutParams().height = 135;
        startButtonView.getLayoutParams().height = 135;

        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                    if(!adapter.isEnabled()){
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                    else{
                        isBtEnabled = true;
                        final Toast bluetoothAlert = Toast.makeText(getApplicationContext(), "Bluetooth activated!", Toast.LENGTH_SHORT);
                        bluetoothAlert.show();
                    }
                } else {
                    intent.putExtra("bt-enabled", false);
                }
            }
        });

        microphoneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            REQUEST_ENABLE_MIC);
                } else {
                    intent.putExtra("mic-enabled", false);
                }
            }
        });

        gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    LocationManager manager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                    try{
                        isGpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    } catch(Exception ex){}
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_ENABLE_GPS);
                } else {
                    intent.putExtra("gps-enabled", false);
                }
            }
        });

        startButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBtEnabled){
                    intent.putExtra("bt-enabled", true);
                } else {
                    intent.putExtra("bt-enabled", false);
                }
                if(isMicEnabled){
                    intent.putExtra("mic-enabled", true);
                } else {
                    intent.putExtra("mic-enabled", false);
                }
                if(isGpsEnabled){
                    intent.putExtra("gps-enabled", true);
                } else {
                    intent.putExtra("gps-enabled", false);
                }
                startService(intent);

                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                isBtEnabled = true;
            }  else {
                isBtEnabled = false;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode) {
            case REQUEST_ENABLE_MIC:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    final Toast micAlert = Toast.makeText(this.getApplicationContext(), "Microphone activated!", Toast.LENGTH_SHORT);
                    micAlert.show();
                    isMicEnabled = true;
                }  else {
                    isMicEnabled = false;
                }
                break;
            case REQUEST_ENABLE_GPS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    isGpsEnabled = true;
                    final Toast gpsAlert = Toast.makeText(this.getApplicationContext(), "GPS activated!", Toast.LENGTH_SHORT);
                    gpsAlert.show();
                }  else {
                    isGpsEnabled = false;
                }
                break;
        }
    }
}
