package com.constantin.wilson.FPV_VR;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private EditTextPreference mFNVSP,mFNP,mdFP;
    private SwitchPreference mFBRP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        //mFNVSP = (EditTextPreference) findPreference("fileNameVideoSource");
        mFNP = (EditTextPreference) findPreference("fileName");
        mFBRP = (SwitchPreference) findPreference("FBR");
        mdFP=(EditTextPreference)findPreference("distortionFactor");
        //mFNVSP = (SwitchPreference) findPreference("ChimeOn15Past");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //System.out.println("CHANGED");
        if(key.equals("fileNameVideoSource")||key.equals("fileName")){
            String fileNameVideoSource=sharedPreferences.getString("fileNameVideoSource","rpi960mal810.h264");
            String fileName=sharedPreferences.getString("fileName", "mGroundRecording.h264");
            if(fileNameVideoSource.equals(fileName)){
                Toast.makeText(this,"You mustn't select same file for video source and ground recording",Toast.LENGTH_LONG).show();
                boolean different=false;
                while(!different){
                    fileName=("X"+fileName);
                    if( ! fileNameVideoSource.equals(fileName)){
                        different=true;
                    }
                }
                /*SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("fileName",fileName);
                editor.commit();*/
                mFNP.setText(fileName);
            }
        }
        if(key.equals("LTMPort")||key.equals("FRSKYPort")||key.equals("RSSIPort")){
            int LTMPort,FRSKYPort,RSSIPort;
            try{
                LTMPort= Integer.parseInt(sharedPreferences.getString("LTMPort", "5001"));
            }catch (Exception e){e.printStackTrace();LTMPort=5001;}
            try {
                FRSKYPort = Integer.parseInt(sharedPreferences.getString("FRSKYPort", "5002"));
            }catch (Exception e){e.printStackTrace();FRSKYPort=5002;}
            try {
                RSSIPort = Integer.parseInt(sharedPreferences.getString("RSSIPort", "5003"));
            }catch (Exception e){e.printStackTrace();RSSIPort=5003;}
            if(LTMPort==FRSKYPort){
                Toast.makeText(this,"You mustn'select the same port for frsky and ltm",Toast.LENGTH_LONG).show();
            }
            if(RSSIPort==FRSKYPort){
                Toast.makeText(this,"You mustn'select the same port for rssi and frsky",Toast.LENGTH_LONG).show();
            }
            if(RSSIPort==LTMPort){
                Toast.makeText(this,"You mustn'select the same port for rssi and ltm",Toast.LENGTH_LONG).show();
            }
        }

        if(key.equals("FBR")){
            SharedPreferences phoneInfo = getSharedPreferences("phoneinfo", MODE_PRIVATE);
            boolean fbr=sharedPreferences.getBoolean("FBR",false);
            if(fbr==false){
                if(phoneInfo.getBoolean("MinAndroid7",false)&&phoneInfo.getBoolean("GL_QCOM_tiled_rendering",false)){
                    Toast.makeText(this,"You disabled FBR, though your Smartphone probably supports it",Toast.LENGTH_LONG).show();
                }
            }
            if(fbr=true){
                if(!phoneInfo.getBoolean("MinAndroid7",false)){
                    Toast.makeText(this,"The App cannot render into the Front Buffer without Android" +
                            " 7 or higher. Disabling FBR",Toast.LENGTH_LONG).show();
                    /*SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putBoolean("FBR",false);
                    editor.commit();*/
                    mFBRP.setChecked(false);
                }
                if(!phoneInfo.getBoolean("GL_QCOM_tiled_rendering",false) && phoneInfo.getBoolean("MinAndroid7",false)){
                    Toast.makeText(this,"This Smartphone has Android 7 but no QCOMM chipset. FBR works " +
                            "best on QCOMM chipsets,but might also work on this phone. Disable, when you have visual " +
                            "problems",Toast.LENGTH_LONG).show();
                }
            }
        }
        if(key.equals("distortionFactor")){
            float distortionFactor;
            try{
                distortionFactor= Float.parseFloat(sharedPreferences.getString("distortionFactor", "0.15"));
            }catch (Exception e){distortionFactor=0.15f;}
            if(distortionFactor>0.18){
                Toast.makeText(this,"You mustnt select a value higher than 0.18",Toast.LENGTH_LONG).show();
                distortionFactor=0.18f;
                mdFP.setText(""+distortionFactor);
            }
        }
        /*if(key.equals("FolderPointer")){
            System.out.println("FOLDER mmmmmmmmmmmm");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                    + "/FPV_VR_GroundRecordings/");
            intent.setDataAndType(uri, "text/csv");
            startActivity(Intent.createChooser(intent, "Open folder"));
        }*/
    }

}
