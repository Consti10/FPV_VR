package com.constantin.wilson.FPV_VR;

import android.content.Context;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.renderscript.Matrix4f;

import com.google.vrtoolkit.cardboard.PhoneParams;
import com.google.vrtoolkit.cardboard.proto.Phone;
import com.google.vrtoolkit.cardboard.sensors.HeadTracker;
import com.google.vrtoolkit.cardboard.sensors.internal.Matrix3x3d;

/**
 * Created by Constantin on 08.12.2016.
 * Use your Android Phone's Acc/Gyro Sensors to get the current Head Position (very Similar to VR tracking) and send them
 * via an bidirectional wifibroadcast Link to your copter
 * not yet working TODO
 */

public class CameraGimbalTracker {
    private HeadTracker mHeadTracker; //Cardboard
    private Context mContext;
    private Thread mThread;
    public volatile boolean running;
    private float[] mHeadViewM=new float[16];
    private float[] tempM3x3=new float[3*3];
    private float[] xyz=new float[3]; //yaw pitch roll

    public CameraGimbalTracker(Context context){
        mContext=context;
        mHeadTracker=HeadTracker.createFromContext(mContext);
        mHeadTracker.setNeckModelEnabled(false);
        final Phone.PhoneParams phoneParams = PhoneParams.readFromExternalStorage();
        if (phoneParams != null) {
            this.mHeadTracker.setGyroBias(phoneParams.gyroBias);
        }

    }

    public void startSending(){
        mHeadTracker.startTracking();
        running=true;
        mThread=new Thread(){
            @Override
            public void run() {
                while (running){
                    getPosXYZ();
                    //Todo: send via udp btw Protocol
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                }
        };
        mThread.start();
    }
    public void stopSending(){
        running=false;
    }


    public float[] getPosXYZ(){
        mHeadTracker.getLastHeadView(mHeadViewM, 0);
        tempM3x3=m4x4To3x3(mHeadViewM);
        xyz[0]=(float)Math.atan2(tempM3x3[4-1],tempM3x3[0]);//R(2,1),R(1,1));
        xyz[1]=(float)Math.atan2(-tempM3x3[7-1],Math.sqrt(Math.pow(tempM3x3[8-1],2)+Math.pow(tempM3x3[9-1],2)));//-R(3,1),Math.sqrt(R(3,2)^2+R(3,3)^2)));
        xyz[2]=(float)Math.atan2(tempM3x3[8-1],tempM3x3[9-1]);//R(3,2),R(3,3));
        xyz[0]=(float)Math.toDegrees(xyz[0]);
        xyz[1]=(float)Math.toDegrees(xyz[1]);
        xyz[2]=(float)Math.toDegrees(xyz[2]);
        //printMatrix(xyz);
        return xyz;
    }

    private float[] m4x4To3x3(float[] Matrix){
        /*Takes a Matrix the form
        X,X,X,0
        X,X,X,0
        X,X,X,0
        0,0,0,0
        where only the X values are valid values representing a rotation matrix in 3 axis.
         */
        if(Matrix.length!=16){
            throw new IllegalArgumentException("Not enough space to write the result");
        }
        float[] temp3x4M=new float[3*4];
        for(int i=0;i<3*4;i++){
            temp3x4M[i]=Matrix[i];
        }
        float[] temp3x3M=new float[3*3];
        int counter=0;
        for(int i=0;i<3*4;i++){
            if(i==4-1 || i==(4*2)-1 || i==(4*3)-1){
                //we have a 0 value (w)
            }else {
                temp3x3M[counter]=temp3x4M[i];
                counter++;
            }
        }
        return temp3x3M;
    }

    private void printMatrix(float[] Matrix){
        String s="";
        for(int i=0;i<Matrix.length;i++){
            s+=Matrix[i]+";";
        }
        System.out.println(s);
    }

}
