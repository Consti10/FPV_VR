package com.constantin.wilson.FPV_VR;

import android.content.SharedPreferences;

/**
 * Holds all Options for the OSD Overlay. Set once in OSDReceiverRenderer, then passed around
 */

public class OSDSettings {
    /*The 14 OSD elements*/
    public boolean enable_battery_life;
    public boolean enable_rssi;
    public boolean enable_distance;
    public boolean enable_height;
    public boolean enable_voltage;
    public boolean enable_ampere;
    public boolean enable_speed;
    public boolean enable_X4;
    public boolean enable_X2;
    public boolean enable_X3;
    public boolean enable_latitude_longitude;
    //
    public boolean enable_model;
    public boolean enable_yaw,enable_roll,enable_pitch;
    public boolean invert_yaw,invert_roll,invert_pitch;
    public boolean enable_home_arrow;
    public boolean enable_auto_home;
    //
    public boolean LTM,FRSKY,RSSI,MAVLINK;
    public int FRSKYPort,LTMPort,RSSIPort,MAVLINKPort;
    public float CELLS=3;
    public float CELL_MIN=3.2f;
    public float CELL_MAX=4.2f;

    OSDSettings(SharedPreferences settings){
        enable_battery_life=settings.getBoolean("enable_battery_life", true);
        enable_latitude_longitude =settings.getBoolean("enable_latitude_longitude", true);
        enable_rssi=settings.getBoolean("enable_rssi", true);
        enable_distance=settings.getBoolean("enable_distance", true);
        enable_height=settings.getBoolean("enable_height", true);
        enable_voltage=settings.getBoolean("enable_voltage", true);
        enable_ampere=settings.getBoolean("enable_ampere", true);
        enable_X3=settings.getBoolean("enable_x3", true);
        enable_speed=settings.getBoolean("enable_speed", true);
        enable_X4=settings.getBoolean("enable_x4", true);
        enable_latitude_longitude=settings.getBoolean("enable_latitude_longitude", true);;
        enable_X2=settings.getBoolean("enable_x2", true);

        enable_yaw=settings.getBoolean("enable_yaw", false);
        enable_roll=settings.getBoolean("enable_roll", true);
        enable_pitch=settings.getBoolean("enable_pitch", true);
        invert_yaw=settings.getBoolean("invert_yaw", false);
        invert_roll=settings.getBoolean("invert_roll", false);
        invert_pitch=settings.getBoolean("invert_pitch", false);
        enable_model=settings.getBoolean("enable_model", true);
        enable_home_arrow=settings.getBoolean("enable_home_arrow", true);
        enable_auto_home=settings.getBoolean("enable_auto_home",true);

        LTM=settings.getBoolean("LTM", true);
        FRSKY=settings.getBoolean("FRSKY", true);
        RSSI=settings.getBoolean("RSSI", true);
        MAVLINK=settings.getBoolean("MAVLINK",true);
        try{
            LTMPort= Integer.parseInt(settings.getString("LTMPort", "5001"));
        }catch (Exception e){e.printStackTrace();LTMPort=5001;}
        try{
            FRSKYPort= Integer.parseInt(settings.getString("FRSKYPort", "5002"));
        }catch (Exception e){e.printStackTrace();FRSKYPort=5002;}
        try{
            RSSIPort= Integer.parseInt(settings.getString("RSSIPort", "5003"));
        }catch (Exception e){e.printStackTrace();RSSIPort=5003;}
        try{
            MAVLINKPort= Integer.parseInt(settings.getString("MAVLINKPort", "5004"));
        }catch (Exception e){e.printStackTrace();MAVLINKPort=5004;}
        try{
            CELLS=Float.parseFloat(settings.getString("CELLS","3"));
        }catch (Exception e){e.printStackTrace();CELLS=3;}
        try{
            CELL_MIN=Float.parseFloat(settings.getString("CELL_MIN","3.2"));
        }catch (Exception e){e.printStackTrace();CELL_MIN=3.2f;}
        try{
            CELL_MAX=Float.parseFloat(settings.getString("CELL_MAX","4.2f"));
        }catch (Exception e){e.printStackTrace();CELL_MAX=4.2f;}
    }

}
