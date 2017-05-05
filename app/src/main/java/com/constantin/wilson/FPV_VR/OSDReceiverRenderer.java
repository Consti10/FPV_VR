package com.constantin.wilson.FPV_VR;


import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.preference.PreferenceManager;
import android.content.Context;
import android.widget.Toast;

import java.nio.FloatBuffer;

//call startReceiving to start OSD;
//Threads: 2, first for refreshing the Overdraw Canvas, second for receiving udp data
public class OSDReceiverRenderer {
    private GLProgramColor mGLProgramColor;
    private GPSHelper mGPSHelper;
    private OSDSettings mOSDSettings;
    private Thread circularRefreshThread;
    private UDPTelemetryReceiver mFRSKYReceiver,mLTMReceiver,mRSSIReceiver,mMAVLINKReceiver;
    public OverdrawLayer mOverdrawLayer;
    private Context mContext;
    private volatile boolean running=true;
    private final int mBytesPerFloat = 4;
    private int GLBuffers[] = new int[3]; //first one for kopter/side arrows
    private int i=0;
    private FloatBuffer kopterHArrowVerUV;
    private float[] mProjM=new float[16];
    private float[] mKopterModelM =new float[16];
    private float[] mHomeArrowModelM=new float[16];
    private float[] mSideArrowsM=new float[16];
    private float[] mHeightModelM=new float[16];
    private float[] mMVPM=new float[16];
    private float[] mWorldDistanceTranslationM=new float[16];
    private float[] scratch=new float[16];
    private float[] scratch2=new float[16];
    private float[] uv;
    //Circular refreshing
    private int circular_refresh_count=0;
    /*THOUGHTS:
    1) 5 Triangles,rotating in 3 axes, representing my airplane/kopter*/
    //Danger: when angle up_down=0 (=zero degree) ,you can't see the triangle;
    //in Degree
    private volatile float angle_x =0;
    private volatile float angle_z =0;
    private volatile float angle_y =0;
    /*
    2)Numbers:
    //Drawn on a simple "texture atlas". */
    public volatile int mDecoder_fps=0;
    public volatile int mOpenGL_fps=0;
    private volatile float mBattery_life_percentage=0; //Todo
    private volatile float mHeight_m=50;
    //3) Home Pfeil
    private volatile float mHome_Arrow_angle_y=0;

    private double mHomeLat=-0.5,mHomeLon=0.0,mHomeAtt;  //Lon:---->, Stripes ||||



    public OSDReceiverRenderer(Context context, int textureID, float[] projectionM, float videoFormat, float modelDistance,
                               float videoDistance, int MODE,boolean OSDOnTopOfVideo,float ratio,
                               DistortionData distortionData){
        mContext=context;
        SharedPreferences settings= PreferenceManager.getDefaultSharedPreferences(context);
        mOSDSettings=new OSDSettings(settings);
        if(!mOSDSettings.enable_auto_home){
            try{
                mHomeLat=Float.parseFloat(settings.getString("HOME_LAT","0"));
            }catch (Exception e){e.printStackTrace();mHomeLat=0;}
            try{
                mHomeLon=Float.parseFloat(settings.getString("HOME_LON","0"));
            }catch (Exception e){e.printStackTrace();mHomeLon=0;}
            try{
                mHomeAtt=Float.parseFloat(settings.getString("HOME_ATT","0"));
            }catch (Exception e){e.printStackTrace();mHomeAtt=0;}
        }else{
            mGPSHelper=new GPSHelper(mContext);
        }
        kopterHArrowVerUV = GLHelper.getFloatBuffer(GeometryHelper.getTriangleCoords());
        GLES20.glGenBuffers(1, GLBuffers, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, GLBuffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, kopterHArrowVerUV.capacity() * mBytesPerFloat,
                kopterHArrowVerUV, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        mGLProgramColor =new GLProgramColor(MODE,distortionData);
        mProjM=projectionM;
        Matrix.setIdentityM(mWorldDistanceTranslationM,0);
        Matrix.translateM(mWorldDistanceTranslationM,0,0.0f,0.0f,-modelDistance);
        mOverdrawLayer=new OverdrawLayer(textureID,videoFormat,videoDistance,OSDOnTopOfVideo,mProjM,MODE,mOSDSettings.enable_model,
                mOSDSettings,ratio,distortionData);
        init();
        if(mOSDSettings.LTM){mLTMReceiver=new UDPTelemetryReceiver(mOSDSettings.LTMPort,500, UDPTelemetryReceiver.LTM);}
        if(mOSDSettings.FRSKY){mFRSKYReceiver=new UDPTelemetryReceiver(mOSDSettings.FRSKYPort,500, UDPTelemetryReceiver.FRSKY);}
        if(mOSDSettings.RSSI){mRSSIReceiver=new UDPTelemetryReceiver(mOSDSettings.RSSIPort,500, UDPTelemetryReceiver.RSSI);}
        if(mOSDSettings.MAVLINK){mMAVLINKReceiver=new UDPTelemetryReceiver(mOSDSettings.MAVLINKPort,500, UDPTelemetryReceiver.MAVLINK);}
        circularRefreshThread=new Thread(){
            @Override
            public void run() {
                refreshCircular();}
        };
    }
    public void startReceiving(){
        running=true;
        if(mOSDSettings.FRSKY&&mFRSKYReceiver!=null){
            mFRSKYReceiver.startReceiving();
        }
        if(mOSDSettings.LTM&&mLTMReceiver!=null){
            mLTMReceiver.startReceiving();
        }
        if(mOSDSettings.RSSI&&mRSSIReceiver!=null){
            mRSSIReceiver.startReceiving();
        }
        if(mOSDSettings.MAVLINK&&mMAVLINKReceiver!=null){
            mMAVLINKReceiver.startReceiving();
        }
        circularRefreshThread.setPriority(Thread.MIN_PRIORITY);
        circularRefreshThread.start();
    }
    public void stopReceiving(){
        running=false;
        if(mGPSHelper!=null){
            mGPSHelper.stop();
        }
        if(mFRSKYReceiver!=null){
            mFRSKYReceiver.stopReceiving();
        }
        if(mLTMReceiver!=null){
            mLTMReceiver.stopReceiving();
        }
        if(mRSSIReceiver!=null){
            mRSSIReceiver.stopReceiving();
        }
    }

    public void refreshCircular(){
        while(running){
            //System.out.println("CircularRefreshThread Priority:"+Thread.currentThread().getPriority());
            //System.out.println("CircularRefresh Process Priority"+ Process.getThreadPriority(Process.myTid()));
            i++;
            if(i>100){i=0;}
            circular_refresh_count++;
            switch(circular_refresh_count){
                case  1: if(! true)                      {circular_refresh_count++;}else{break;};
                case  2: if(! true)                      {circular_refresh_count++;}else{break;};
                case  3: if(!mOSDSettings.enable_latitude_longitude){circular_refresh_count++;}else{break;};
                case  4: if(!mOSDSettings.enable_latitude_longitude){circular_refresh_count++;}else{break;};
                case  5: if(!mOSDSettings.enable_battery_life)       {circular_refresh_count++;}else{break;};
                case  6: if(!mOSDSettings.enable_voltage)            {circular_refresh_count++;}else{break;};
                case  7: if(!mOSDSettings.enable_rssi)               {circular_refresh_count++;}else{break;};
                case  8: if(!mOSDSettings.enable_ampere)             {circular_refresh_count++;}else{break;};
                case  9: if(!mOSDSettings.enable_distance)           {circular_refresh_count++;}else{break;};
                case 10: if(!mOSDSettings.enable_X3)                 {circular_refresh_count++;}else{break;};
                case 11: if(!mOSDSettings.enable_speed)              {circular_refresh_count++;}else{break;};
                case 12: if(!mOSDSettings.enable_X4)                 {circular_refresh_count++;}else{break;};
                case 13: if(!mOSDSettings.enable_height)             {circular_refresh_count++;}else{break;};
                case 14: if(!mOSDSettings.enable_height)             {circular_refresh_count=0;};break;

            }
            switch(circular_refresh_count){
                case  1: createUnitVerticesDataAndUpdateBuffer(0, mDecoder_fps);break;
                case  2: createUnitVerticesDataAndUpdateBuffer(1, mOpenGL_fps);break;
                case  3: createUnitVerticesDataAndUpdateBuffer(2, getLatitude()); break;
                case  4: createUnitVerticesDataAndUpdateBuffer(3, getLongitude()); break;
                case  5: createUnitVerticesDataAndUpdateBuffer(4, getBatLifePerc(getVoltage())); break;
                case  6: createUnitVerticesDataAndUpdateBuffer(5, getVoltage()       );break;
                case  7: createUnitVerticesDataAndUpdateBuffer(6, getWBRSSI() );break;
                case  8: createUnitVerticesDataAndUpdateBuffer(7, getAmpere()) ;break;
                case  9: createUnitVerticesDataAndUpdateBuffer(8, (float) GeometryHelper.distance_between(mHomeLat,mHomeLon,
                        getLatitude(),getLongitude()));break;
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
        if(mOSDSettings.enable_auto_home&&mGPSHelper.newDataAvailable()){
            Location mLoc=mGPSHelper.getCurrentLocation();
            if(mLoc!=null){
                mHomeLat=mLoc.getLatitude();
                mHomeLon=mLoc.getLongitude();
                mHomeAtt=mLoc.getAltitude();
            }
        }
        uv= GeometryHelper.numberToIndices(value);
        while (true){
            boolean b;
            synchronized (this){
                b=mOverdrawLayer.multithreadUnitReady;
            }
            if(b){mOverdrawLayer.updateOverlayUnitMultithreadBuffer(unitNumber,uv);break;}
            try{Thread.sleep(5);}catch(Exception e){};
        }
    }

    public void setupModelMatrices(){
        if(mOSDSettings.enable_pitch){angle_x=getPitch();}
        if(mOSDSettings.enable_yaw){angle_y=getYaw();}
        if(mOSDSettings.enable_roll){angle_z=getRoll();}
        if(mOSDSettings.invert_yaw){angle_y*=-1;}
        if(mOSDSettings.invert_roll){angle_z*=-1;}
        if(mOSDSettings.invert_pitch){angle_x*=-1;}
        mHeight_m=getAltitude(); //or baro altitude ?
        if(mHeight_m==0){mHeight_m=getBaroAltitude();}
        mHome_Arrow_angle_y=(float) GeometryHelper.course_to(mHomeLat,mHomeLon,getLatitude(),getLongitude());
        mHome_Arrow_angle_y+=180;
        //Setup ModelMatrices(needs only be done one Time per Frame)
        Matrix.setIdentityM(scratch,0);
        //side arrow
        Matrix.multiplyMM(mSideArrowsM,0,mWorldDistanceTranslationM,0,scratch,0);
        //rotate for copter
        Matrix.rotateM(scratch, 0, angle_x, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(scratch, 0, angle_y, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(scratch, 0, angle_z, 0.0f, 0.0f, 1.0f);
        //Kopter
        Matrix.multiplyMM(mKopterModelM,0,mWorldDistanceTranslationM,0,scratch,0);
        //Lines and their canvases
        float translate_height=(-50.0f+mHeight_m)/25.0f;
        Matrix.setIdentityM(scratch2, 0);
        Matrix.translateM(scratch2, 0, 0.0f, -translate_height, 0);
        //Matrix.multiplyMM(temp, 0, scratch, 0, scratch2, 0);
        //Matrix.multiplyMM(mHeightModelM, 0,mWorldDistanceTranslationM, 0, temp, 0);
        Matrix.multiplyMM(mHeightModelM, 0,mWorldDistanceTranslationM, 0, scratch2, 0);
        //home arrow
        Matrix.setIdentityM(scratch2, 0);
        Matrix.rotateM(scratch2, 0, mHome_Arrow_angle_y, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mHomeArrowModelM,0,mWorldDistanceTranslationM,0,scratch2,0);
        //OSD
        /*Matrix.setIdentityM(scratch2,0);
        Matrix.multiplyMM(mOSDModelM,0,mWorldDistanceTranslationM,0,scratch2,0);*/
    }
    public void updateOverlayUnit(){
        mOverdrawLayer.updateOverlayUnit();
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
        mGLProgramColor.beforeDraw(GLBuffers[0]);

        if(mOSDSettings.enable_height){
            Matrix.multiplyMM(mMVPM, 0, viewM, 0, mHeightModelM, 0);
            mGLProgramColor.draw(mMVPM,mProjM,0,18*6);
        }
        if(mOSDSettings.enable_model){
            Matrix.multiplyMM(mMVPM, 0, viewM, 0, mKopterModelM, 0);
            mGLProgramColor.draw(mMVPM,mProjM,18*6,5*3);
        }
        if(mOSDSettings.enable_height){
            //mOGLProgramColor.draw(mMVPM,mProjM,(18*6)+(5*3), 2*3);
            Matrix.multiplyMM(mMVPM, 0, viewM, 0, mSideArrowsM, 0);
            mGLProgramColor.draw(mMVPM,mProjM,(18*6)+(5*3), 2*3);
        }
        if(mOSDSettings.enable_home_arrow){
            Matrix.multiplyMM(mMVPM, 0, viewM, 0, mHomeArrowModelM, 0);
            mGLProgramColor.draw(mMVPM,mProjM,(18*6)+(7*3), 3);
        }
        mGLProgramColor.afterDraw();
    }
    public void changeOSDVideoRatio(float ratio){
        mOverdrawLayer.changeOSDVideoRatio(ratio);
    }

    static{
        System.loadLibrary("parser");
    }
    public static native int parseLTM(byte[] b,int i);
    public static native int parseFRSKY(byte[] b,int i);
    public static native int setWBRSSI(float rssi);
    public static native int parseMAVLINK(byte[] b,int i);
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
    private static native float getWBRSSI();
    public static native void setAffinity(int cpu);

    private void makeToast(final String message) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public float getBatLifePerc(float voltage){
        if(mOSDSettings.CELLS==0){mOSDSettings.CELLS=3;}
        if((mOSDSettings.CELL_MAX-mOSDSettings.CELL_MIN)==0){return 0;}
        if(voltage==0){return 0;}
        float ret=((voltage/mOSDSettings.CELLS)-mOSDSettings.CELL_MIN)/(mOSDSettings.CELL_MAX-mOSDSettings.CELL_MIN)*100;
        if(ret<0){
            ret=-ret;
        }
        return ret;
    }
}
