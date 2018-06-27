package com.example.jotacoutinho.opportunisticsensing.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
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


        final Toast microphoneAlert = Toast.makeText(this.getApplicationContext(), "Microphone activated!", Toast.LENGTH_SHORT);
        final Toast gpsAlert = Toast.makeText(this.getApplicationContext(), "GPS activated!", Toast.LENGTH_SHORT);

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
                        intent.putExtra("bt-enabled", true);
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
                    microphoneAlert.show();
                }
            }
        });

        gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    gpsAlert.show();
                }
            }
        });

        startButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                final Toast bluetoothAlert = Toast.makeText(getApplicationContext(), "Bluetooth activated!", Toast.LENGTH_SHORT);
                bluetoothAlert.show();
            }
        }
    }
}
