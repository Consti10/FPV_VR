package com.constantin.wilson.FPV_VR;


/*Thoughts on creating an scan line racing/chasing renderer
* Eye-alternating front buffer rendering
* compatible with v.d.d.c.*/

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.opengl.EGL14;
import android.os.HardwarePropertiesManager;
import android.os.Looper;
import android.util.Log;
import android.view.Choreographer;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class VsyncHelper implements Choreographer.FrameCallback{
    private FrameTimes mFrameTimes;
    private static boolean USE_NATIVE=false;
    private static boolean RELEASE=true; //disables all debugging relevant code, but increases Performance
    public static boolean SKIP_EYE=true;
    private Context mContext;
    public static double nanoToMs=0.000001; //10^-6
    public static double msToNano=(long)(1.0/nanoToMs);
    /*Change 02.01.2017: Values in nanoseconds/long*/
    private static long vsyncStartKullanzNS=(long)(0.5*msToNano); //maximum "jumps" about 0.5ms (couldn't they implement doFrame more accurate ?
    private long frameDTNS;
    /*There are 2 Threads that will access the following variable :
    * 1)The OpenGL Render Thread
    * 2)The VSYNC Frame Callback
    * However,because the Callback only reads,and the Render Thread only writes, volatile is enough*/
    private volatile long lastTimeVsyncOccuredNS=0;
    private long l,l2;
    //|-----------!-----------|
    //|           !           |
    //|           !           |   => Vsync Direction
    //|-----------!-----------| ->x-Direction (Direction the VSYNC travels along; we aren't interested in the VSYNC y pos.
    //   left Eye ! right Eye                  x ranges between 0 and FrameDTNS
    //TO Do's : front/back porch
    //public double FrameDT =16.666666; //a display Refresh Time of 16ms== 60fps Refresh rate
    //public double EyeDT = FrameDT*0.5; //Half the display time
    //private long exactDTNS;
    //private long exactDTNSSum=0,exactDTNSCount=0;
    private double ts;

    public VsyncHelper(Context context){
        mContext=context;
        Display d=((WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE)).getDefaultDisplay();
        float refreshRating=d.getRefreshRate();
        //double x=d.getPresentationDeadlineNanos();
        //System.out.println("X:"+x*nanoToMs);
        //System.out.println("refreshRate:"+refreshRating);
        //FrameDTNS=(long)(16.6666666*msToNano);
        frameDTNS=16652500; //For my nexus 5x. Avg. of 1min adding all vsyncs
        Choreographer.getInstance().postFrameCallback(this);
        if(USE_NATIVE){
            initNative();
        }
        mFrameTimes=new FrameTimes(frameDTNS,vsyncStartKullanzNS);
    }
    public void onFrameStart(){
        if(RELEASE){return;}
        mFrameTimes.onFrameStart();
    }
    public void onFrameStop(){
        if(RELEASE){return;}
        mFrameTimes.onFrameStop();
    }

    public void onLeftEyeRenderingStart(){
        if(RELEASE){return;}
        mFrameTimes.onLeftEyeRenderingStart();
    }
    public void onRightEyeRenderingStart(){
        if(RELEASE){return;}
        mFrameTimes.onRightEyeRenderingStart();
    }
    public void onRightEyeCPUStop(){
        if(RELEASE){return;}
        mFrameTimes.onRightEyeCPUStop();
    }
    public void onRightEyeGPUStop(){
        if(RELEASE){return;}
        mFrameTimes.onRightEyeGPUStop();
    }
    public void onLeftEyeCPUStop(){
        if(RELEASE){return;}
        mFrameTimes.onLeftEyeCPUStop();
    }
    public void onLeftEyeGPUStop(){
        if(RELEASE){return;}
        mFrameTimes.onLeftEyeGPUStop();
    }
    public void waitUntilVsyncStart(){
        if(!USE_NATIVE){
            //System.out.println("n left eyes skipped"+nLeftEyesSkipped+" n right eyes skipped:"+nRightEyesSkipped);
            ts=getTimeMs();
            long l=getVsyncPosX();
            while (l>vsyncStartKullanzNS/2&&l<frameDTNS-vsyncStartKullanzNS/2){
                l=getVsyncPosX();
            }
            /*long timeToWait=frameDTNS-getVsyncPosX();
            long timeToStart=System.nanoTime()+timeToWait;*/
            /*long l=lastTimeVsyncOccuredNS;
            long currTime=System.nanoTime();
            while (currTime-l>frameDTNS){
                l+=frameDTNS;
            }
            long timeToStart=l;
            while (System.nanoTime()<timeToStart){
                //wait
            }*/
            if(!RELEASE){
                double time=getTimeMs()-ts;
                mFrameTimes.onWaitUntilVsyncStart(time);
                if(mFrameTimes.P_VSYNC_WAIT_TIME){
                    System.out.println("time waiting for new vsync:"+time);
                }
            }
        }
        else {
            mFrameTimes.onWaitUntilVsyncStart(0);
            waitUntilVsyncStartNative();
        }
    }
    public boolean waitUntilVsyncMiddle(){
        if(!USE_NATIVE){
            if(getVsyncPosX()>(frameDTNS/2)){
                mFrameTimes.onWaitUntilVsyncMiddle(0,false);
                return SKIP_EYE;
            }
            double ts=getTimeMs();
            while (getVsyncPosX()<(frameDTNS/2)){
                //wait
            }
            double time=getTimeMs()-ts;
            mFrameTimes.onWaitUntilVsyncMiddle(time,true);
            return !SKIP_EYE;}
        else {
            boolean ret=waitUntilVsyncMiddleNative();
            if(ret==SKIP_EYE){
                mFrameTimes.onWaitUntilVsyncMiddle(0,false);
            }else {
                mFrameTimes.onWaitUntilVsyncMiddle(0,true);
            }
            return ret;
        }

    }
    //even though in ms the resolution is in ns
    public static double getTimeMs(){
        return System.nanoTime()*nanoToMs;
    }
    /*Problem:
    the callback may be invoked mor than 10ms after the vsync actually happened. In this case the timestamp is still
    accurate, but the time at when doFrame was invoked is definitely not !
    use the timestamp only to compensate for "drift"; when the old timestamp is more than FrameDT (f.e. 16.666ms) old, subtract
    this value and stil get accurate vsync Position
     */
    @Override
    public void doFrame(long l){
        //System.out.println("VSYNC");
        if(USE_NATIVE){
            //Assume,it takes almost no time to invoke the ndk function. Can't use java times with ndk unfortunately
            long diff=System.nanoTime()-l;
            doFrameNative(diff);
        }else {
            /*long diff=l-lastTimeVsyncOccuredNS;
            if(diff>16*msToNano&&diff<17*msToNano){
                exactDTNSSum+=diff;
                exactDTNSCount++;
                System.out.println("DisplayTime"+(exactDTNSSum/exactDTNSCount));
            }*/
            lastTimeVsyncOccuredNS=l;
        }
        /*if(System.nanoTime()-ts>0.1*msToNano){
            System.out.println("InvokeTime:"+(System.nanoTime()-ts)*nanoToMs);
        }*/
        Choreographer.getInstance().postFrameCallback(this);
    }
    public long getVsyncPosX(){
        l=lastTimeVsyncOccuredNS;
        l2=System.nanoTime()-l;
        //the CPU takes at least 1.5ms; makes Application more tearing resistent,but adds as many lag:
        //l2+=1.5*nanoToMs;
        //the calc&rendering is faster than the display;vsync offset to reduce lag without tearing:
        l2+=(frameDTNS-1*msToNano); //1ms seems to be the max. vsync offset to reduce lag without tearing
        //assume crating&rendering never takes longer than 8.3-2=6.3ms;
        /*int maxVal=(int)(l2/FrameDTNS);
        l2-=(maxVal*FrameDTNS);*/
        //int c=0;
        while (l2>=frameDTNS){
            //c++;
            l2-=frameDTNS;
        }
        //if(c>5){System.out.println("C:"+c);}
        //System.out.println(""+(l2*nanoToMs));
        return l2;
    }

    static{
        System.loadLibrary("VsyncHelperNative");
    }
    public static native void initNative();
    public static native boolean waitUntilVsyncMiddleNative();
    public static native void waitUntilVsyncStartNative();
    public static native void doFrameNative(long l);
}