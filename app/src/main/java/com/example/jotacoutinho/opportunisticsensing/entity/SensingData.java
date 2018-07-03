package com.example.jotacoutinho.opportunisticsensing.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class SensingData implements Serializable {
    public double latitude;
    public double longitude;
    public double altitude;
    public double micAmpl;
    public ArrayList<String> devices;

    public SensingData(double lat, double lon, double a, ArrayList<String> d, double m){
        this.latitude = lat;
        this.longitude = lon;
        this.altitude = a;
        this.devices = d;
        this.micAmpl = m;
    }

    @Override
    public String toString() {
        return "SensingData={latitude=" + latitude + ", longitude=" + longitude + ", altitude=" + altitude + ", devices=" + devices + ", micAmpl=" + micAmpl + "}";
    }
}
