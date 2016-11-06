package com.constantin.wilson.FPV_VR;


import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.preference.PreferenceManager;
import android.content.Context;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.FloatBuffer;

//call startReceiving to start OSD;
//Threads: 2, first for refreshing the Overdraw Canvas, second for receiving udp data
public class MyOSDReceiverRenderer {
    private OGLProgramColor mOGLProgramColor;
    private DatagramSocket s = null;
    private Thread circularRefreshThread,receiveFromUDPThread;
    private volatile boolean running=true;
    private boolean countUp1=true,countUp2=true,countUp3=true,countUp4true;
    private final int mBytesPerFloat = 4;
    private int buffer[] = new int[3]; //first one for kopter/side arrows
    int i=0;
    public OverdrawLayer mOverdrawLayer;
    private FloatBuffer kopterHArrowVerUV;
    public int mDisplay_x,mDisplay_y;
    float[] mProjM=new float[16];
    float[] mKopterModelM =new float[16];
    float[] mHomeArrowModelM=new float[16];
    private float[] mHeightModelM=new float[16];
    private float[] mMVPM=new float[16];
    private float[] mWorldDistanceTranslationM=new float[16];
    private float[] scratch=new float[16];
    private float[] scratch2=new float[16];
    //Circular refreshing
    private int circular_refresh_count=0;
    //Booleans for OSD
    private boolean LTM,FRSKY;
    private boolean enable_model;
    private boolean invert_yaw,invert_roll,invert_pitch;
    private boolean enable_home_arrow;
    private boolean enable_battery_life;
    private boolean enable_latitude_longitude;
    private boolean enable_rssi;
    private boolean enable_X2;
    private boolean enable_height;
    private boolean enable_voltage;
    private boolean enable_ampere;
    private boolean enable_X3;
    private boolean enable_speed;
    private boolean enable_X4;
    /*THOUGHTS:
    1) 5 Triangles,rotating in 3 axes, representing my airplane/kopter*/
    //Danger: when angle up_down=0 (=zero degree) ,you can't see the triangle;
    //in Degree
    private volatile float angle_x =1.0f;
    private volatile float angle_z =30;
    private volatile float angle_y =0;
    /*
    2)Numbers:
    //Drawn on a simple "texture atlas". */
    public volatile int mDecoder_fps=49;
    public volatile int mOpenGL_fps=60;
    private volatile float mBattery_life_percentage=0; //Todo
    private volatile float mHeight_m=50;
    //3) Home Pfeil
    private volatile float mHome_Arrow_angle_y=0;


    public MyOSDReceiverRenderer(Context context, int[] textures,float[] leftEyeViewM,float[] rightEyeViewM,float[] projectionM,float videoFormat,float modelDistance,float videoDistance,boolean distortionCorrection){
        SharedPreferences settings= PreferenceManager.getDefaultSharedPreferences(context);
        LTM=settings.getBoolean("ltm", false);
        FRSKY=settings.getBoolean("frsky", false);
        invert_yaw=settings.getBoolean("invert_yaw", false);
        invert_roll=settings.getBoolean("invert_roll", false);
        invert_pitch=settings.getBoolean("invert_pitch", false);
        enable_model=settings.getBoolean("enable_model", true);
        enable_home_arrow=settings.getBoolean("enable_home_arrow", true);
        enable_battery_life=settings.getBoolean("enable_battery_life", true);
        enable_latitude_longitude =settings.getBoolean("enable_latitude_longitude", true);
        enable_rssi=settings.getBoolean("enable_rssi", true);
        enable_X2=settings.getBoolean("enable_x2", true);
        enable_height=settings.getBoolean("enable_height", true);
        enable_voltage=settings.getBoolean("enable_voltage", true);
        enable_ampere=settings.getBoolean("enable_ampere", true);
        enable_X3=settings.getBoolean("enable_x3", true);
        enable_speed=settings.getBoolean("enable_speed", true);
        enable_X4=settings.getBoolean("enable_x4", true);
        kopterHArrowVerUV = OpenGLHelper.getFloatBuffer(MyOSDReceiverRendererHelper.getTriangleCoords());
        GLES20.glGenBuffers(1, buffer, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, kopterHArrowVerUV.capacity() * mBytesPerFloat,
                kopterHArrowVerUV, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        mOGLProgramColor =new OGLProgramColor(distortionCorrection);
        mProjM=projectionM;
        Matrix.setIdentityM(mWorldDistanceTranslationM,0);
        Matrix.translateM(mWorldDistanceTranslationM,0,0.0f,0.0f,-modelDistance);
        mOverdrawLayer=new OverdrawLayer(textures,videoFormat,videoDistance,mProjM,distortionCorrection,
                enable_battery_life, enable_latitude_longitude,enable_rssi,enable_X2,enable_height,enable_voltage,
                enable_ampere,enable_X3,enable_speed,enable_X4);
        init();
        receiveFromUDPThread=new Thread(){
            @Override
            public void run() {
                receiveFromUDP();}
        };
        circularRefreshThread=new Thread(){
            @Override
            public void run() {
                refreshCircular();}
        };
    }
    public void startReceiving(){
        running=true;
        receiveFromUDPThread.start();
        circularRefreshThread.start();
    }
    public void stopReceiving(){
        running=false;
    }

    public void refreshCircular(){
        while(running){
            i++;
            if(i>100){i=0;}
            circular_refresh_count++;
            switch(circular_refresh_count){
                case  1: if(! true)                      {circular_refresh_count++;}else{break;};
                case  2: if(! true)                      {circular_refresh_count++;}else{break;};
                case  3: if(!enable_latitude_longitude){circular_refresh_count++;}else{break;};
                case  4: if(!enable_latitude_longitude){circular_refresh_count++;}else{break;};
                case  5: if(! enable_battery_life)       {circular_refresh_count++;}else{break;};
                case  6: if(! enable_voltage)            {circular_refresh_count++;}else{break;};
                case  7: if(! enable_rssi)               {circular_refresh_count++;}else{break;};
                case  8: if(! enable_ampere)             {circular_refresh_count++;}else{break;};
                case  9: if(! enable_X2)                 {circular_refresh_count++;}else{break;};
                case 10: if(! enable_X3)                 {circular_refresh_count++;}else{break;};
                case 11: if(! enable_speed)              {circular_refresh_count++;}else{break;};
                case 12: if(! enable_X4)                 {circular_refresh_count++;}else{break;};
                case 13: if(! enable_height)             {circular_refresh_count++;}else{break;};
                case 14: if(! enable_height)             {circular_refresh_count=0;};break;

            }
            switch(circular_refresh_count){
                case  1: createUnitVerticesDataAndUpdateBuffer(0, mDecoder_fps);break;
                case  2: createUnitVerticesDataAndUpdateBuffer(1, mOpenGL_fps);break;
                case  3: createUnitVerticesDataAndUpdateBuffer(2, getLatitude()); break;
                case  4: createUnitVerticesDataAndUpdateBuffer(3, getLongitude()); break;
                case  5: createUnitVerticesDataAndUpdateBuffer(4, mBattery_life_percentage); break;
                case  6: createUnitVerticesDataAndUpdateBuffer(5, getVoltage()       );break;
                case  7: createUnitVerticesDataAndUpdateBuffer(6, getRSSI() );break;
                case  8: createUnitVerticesDataAndUpdateBuffer(7, getAmpere()) ;break;
                case  9: createUnitVerticesDataAndUpdateBuffer(8, i );break;
                case 10: createUnitVerticesDataAndUpdateBuffer(9, i );break;
                case 11: createUnitVerticesDataAndUpdateBuffer(10, getSpeed() );break;
                case 12: createUnitVerticesDataAndUpdateBuffer(11, i);break;
                case 13: createUnitVerticesDataAndUpdateBuffer(12, getBaroAltitude());break;
                case 14: createUnitVerticesDataAndUpdateBuffer(13, getAltitude() );circular_refresh_count=0;break;
                default:break;
            }
        }
    }
    //called by refreshCircular thread
    public void createUnitVerticesDataAndUpdateBuffer(int unitNumber, float value){
        float[] uv=MyOSDReceiverRendererHelper.numberToIndices(value);
        while (true){
            if(mOverdrawLayer.multithreadUnitReady){mOverdrawLayer.updateOverlayUnitMultithreadBuffer(unitNumber,uv);break;}
            try{Thread.sleep(5);}catch(Exception e){};
        }
    }

    private void receiveFromUDP() {
        int server_port = 5001;
        byte[] message = new byte[1024];
        DatagramPacket p = new DatagramPacket(message, message.length);
        boolean exception=false;
        try {s = new DatagramSocket(server_port);
            s.setSoTimeout(500);
        } catch (SocketException e) {e.printStackTrace();}
        while (running && s != null) {
            try {
                s.receive(p);
            } catch (IOException e) {
                exception=true;
                if(! (e instanceof SocketTimeoutException)){
                    e.printStackTrace();
                }
            }
            if(!exception){
                System.out.println("Receiving OSD Data; Parsing required; length:"+p.getLength());
                //we have to parse Telemetry Data
                if(LTM){
                    parseLTM(message,p.getLength());
                }
                if(FRSKY){
                    parseFRSKY(message,p.getLength());
                }
            }else{exception=false;}
        }
        if (s != null) {
            s.close();
            s=null;
        }
    }

    public void setupModelMatrices(){
        angle_x=getPitch();
        angle_y=getYaw();
        angle_z=getRoll();
        if(invert_yaw){angle_y*=-1;}
        if(invert_roll){angle_z*=-1;}
        if(invert_pitch){angle_x*=-1;}
        mHeight_m=getAltitude(); //or baro altitude ?
        /*if(countUp1){angle_z +=0.2;}else{angle_z -=0.2;}if(angle_z >=40){countUp1=false;}if(angle_z <=-40){countUp1=true;}
        //up_down
        if(countUp2){angle_x +=0.2;}else{angle_x -=0.2;}if(angle_x >=40){countUp2=false;}if(angle_x <=-40){countUp2=true;}
        //rotating vertically (only for Kopter)
        if(countUp3){angle_y +=0.2;}else{angle_y -=0.2;}if(angle_y >=40){countUp3=false;}if(angle_y <=-40){countUp3=true;}
        mHome_Arrow_angle_y+=0.4;if(mHome_Arrow_angle_y>=360){mHome_Arrow_angle_y=0;}
        mHeight_m+=0.1;if(mHeight_m>=100){mHeight_m=0;}*/
        //Setup ModelMatrices(needs only be done one Time per Frame)
        Matrix.setIdentityM(scratch,0);
        Matrix.rotateM(scratch, 0, angle_x, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(scratch, 0, angle_y, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(scratch, 0, angle_z, 0.0f, 0.0f, 1.0f);
        //Lines and their canvases
        float translate_height=(-50.0f+mHeight_m)/25.0f;
        float[] temp=new float[16];
        Matrix.setIdentityM(scratch2, 0);
        Matrix.translateM(scratch2, 0, 0.0f, -translate_height, 0);
        Matrix.multiplyMM(temp, 0, scratch, 0, scratch2, 0);
        Matrix.multiplyMM(mHeightModelM, 0,mWorldDistanceTranslationM, 0, temp, 0);
        //Kpter and side arrows
        Matrix.multiplyMM(mKopterModelM,0,mWorldDistanceTranslationM,0,scratch,0);
        //home arrow
        Matrix.setIdentityM(scratch2, 0);
        Matrix.rotateM(scratch2, 0, mHome_Arrow_angle_y, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mHomeArrowModelM,0,mWorldDistanceTranslationM,0,scratch2,0);
        //OSD
        /*Matrix.setIdentityM(scratch2,0);
        Matrix.multiplyMM(mOSDModelM,0,mWorldDistanceTranslationM,0,scratch2,0);*/
    }

    public void drawLeftEye(float[] mLeftEyeViewM){
        draw(mLeftEyeViewM);
        mOverdrawLayer.drawOverlay(mLeftEyeViewM,mHeightModelM);

    }

    public void drawRightEye(float[] mRightEyeViewM){
        draw(mRightEyeViewM);
        mOverdrawLayer.drawOverlay(mRightEyeViewM,mHeightModelM);
    }

    //will be called from the openGl context;
    //we have to make sure it is multithreading-save
    public void draw(float[] viewM){
        mOGLProgramColor.beforeDraw(buffer[0]);

        if(enable_height){
            Matrix.multiplyMM(mMVPM, 0, viewM, 0, mHeightModelM, 0);
            mOGLProgramColor.draw(mMVPM,mProjM,0,18*6);
        }
        if(enable_model){
            Matrix.multiplyMM(mMVPM, 0, viewM, 0, mKopterModelM, 0);
            mOGLProgramColor.draw(mMVPM,mProjM,18*6,5*3);
        }
        if(enable_height){
            //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, (18*6)+(5*3), 2*3);
            mOGLProgramColor.draw(mMVPM,mProjM,(18*6)+(5*3), 2*3);
        }
        if(enable_home_arrow){
            Matrix.multiplyMM(mMVPM, 0, viewM, 0, mHomeArrowModelM, 0);
            mOGLProgramColor.draw(mMVPM,mProjM,(18*6)+(7*3), 3);
        }
        mOGLProgramColor.afterDraw();
        /*byte[] message=new byte[1024];
        for(int i=0;i<message.length;i++){
            message[i]=100;
        }
        //0x24 0x54 0x47 0xFF 0xFF 0xFF 0xFF 0xFF 0xFF 0xFF 0xFF 0xFF 0xFF 0xFF 0xFF 0xFF  0xFF   0xC0
        message[0]=0x24;
        message[1]=0x54;
        message[2]=0x47;
        message[3]=0x00;
        message[4]=0x00;
        message[5]=0x00;
        message[6]=0x00;
        message[7]=0x00;
        message[8]=0x00;
        message[9]=0x00;
        message[10]=0x00;
        message[11]=0x00;
        message[12]=0x00;
        message[13]=0x00;
        message[14]=0x00;
        message[15]=0x00;
        message[16]=0x00;
        message[17]=0x00;
        message[18]=0x00;
        parseLTM(message,message.length);*/
    }

    static{
        System.loadLibrary("parser");
    }
    public static native void parseLTM(byte[] b,int length);
    public static native void parseFRSKY(byte[] b,int length);
    private static native void init();
    private static native float getVoltage();
    private static native float getAmpere();
    private static native float getBaroAltitude();
    private static native float getAltitude();
    private static native float getLongitude();
    private static native float getLatitude();
    private static native float getSpeed();
    private static native float getRoll();
    private static native float getPitch();
    private static native float getYaw();
    private static native float getRSSI();


}
