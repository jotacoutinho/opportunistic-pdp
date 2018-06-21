package com.example.jotacoutinho.opportunisticsensing.ui;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwitchCompat bluetoothSwitch = findViewById(R.id.switchBluetooth);
        SwitchCompat microphoneSwitch = findViewById(R.id.switchMicrophone);
        SwitchCompat gpsSwitch = findViewById(R.id.switchGPS);
        //Button startButton = findViewById(R.id.startButton);
        ImageView startButtonView = findViewById(R.id.startButtonView);

        final Toast bluetoothAlert = Toast.makeText(this.getApplicationContext(), "Bluetooth activated!", Toast.LENGTH_SHORT);
        final Toast microphoneAlert = Toast.makeText(this.getApplicationContext(), "Microphone activated!", Toast.LENGTH_SHORT);
        final Toast gpsAlert = Toast.makeText(this.getApplicationContext(), "GPS activated!", Toast.LENGTH_SHORT);

        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                   bluetoothAlert.show();
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
                startService(new Intent(getApplicationContext(), BackgroundService.class));
                finish();
            }
        });
    }
}
