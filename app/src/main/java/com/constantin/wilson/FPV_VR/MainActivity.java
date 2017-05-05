package com.constantin.wilson.FPV_VR;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;


/*Constantin Geier, 28.12.2015;
 */
public class MainActivity extends AppCompatActivity {
    Context context;
    SharedPreferences settings,phoneInfo;
    Intent mSurfaceViewI;
    Intent mOGLIMono;
    Intent mOGLIStereo;
    Intent mOGLIStereoFB;
    Intent mTextureViewI;
    Intent mSettingsI;
    Intent mTestActivityIntent;
    private volatile boolean TexView=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        settings= PreferenceManager.getDefaultSharedPreferences(context);
        phoneInfo = getSharedPreferences("phoneinfo", MODE_PRIVATE);
        if(phoneInfo.getBoolean("FirstStart",true)){
            getAllPermissionsIfNotYetGranted();
            onFirstStart();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        getAllPermissionsIfNotYetGranted();

        Switch mTVOrSVSwitch=(Switch) findViewById(R.id.switch1);
        mTVOrSVSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                TexView=b;
            }
        });
        Button mStartVideostreamOnlyB=(Button)findViewById(R.id.button1);
        mSurfaceViewI=new Intent();
        mSurfaceViewI.setClass(this, SurfaceViewActivity.class);
        mTextureViewI=new Intent();
        mTextureViewI.setClass(this, TextureViewActivity.class);
        mStartVideostreamOnlyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TexView){
                    startActivity(mTextureViewI);
                }else {
                    startActivity(mSurfaceViewI);
                }
            }
        });
        Button mStartOGLMonoB=(Button)findViewById(R.id.button2);
        mOGLIMono=new Intent();
        mOGLIMono.setClass(this, GLActivityMono.class);
        mStartOGLMonoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mOGLIMono);
            }
        });
        Button mStartOGLPB=(Button)findViewById(R.id.button3);
        mOGLIStereo=new Intent();
        mOGLIStereo.setClass(this, GLActivityStereo.class);
        mOGLIStereoFB =new Intent();
        mOGLIStereoFB.setClass(this,GLActivityStereoFB.class);
        mStartOGLPB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(settings.getBoolean("FBR", false)){
                    startActivity(mOGLIStereoFB);
                }else {
                    startActivity(mOGLIStereo);
                }
            }
        });


        Button mTestActivityB=(Button)findViewById(R.id.button4);
        mTestActivityIntent=new Intent();
        mTestActivityIntent.setClass(this, TestActivity.class);
        mTestActivityB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mTestActivityIntent);
            }
        });
        Button mSettingsB=(Button)findViewById(R.id.button5);
        mSettingsI=new Intent();
        mSettingsI.setClass(this, SettingsActivity.class);
        mSettingsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mSettingsI);
            }
        });

    }
    @Override
    protected void onResume(){
        super.onResume();
    }

    private void makeToast(final String message) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }
    private void onFirstStart(){
        //also sets first start preference to false and checks for Android 7
        Intent i=new Intent();
        i.setClass(context,GPUCapTest.class);
        startActivity(i);
    }
    private void getAllPermissionsIfNotYetGranted(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }
    }
}
