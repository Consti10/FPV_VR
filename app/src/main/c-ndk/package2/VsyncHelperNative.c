//
// Created by Constantin on 29.04.2017.
//

#include "nanoseconds.h"
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <stdbool.h>
#include <stdint.h>
#include <math.h>
#include <assert.h>
#include <jni.h>
#include <android/log.h>

static bool SKIP_EYE=true;
static bool P_VSYNC_WAIT_TIME=false;

static double nanoToMs=0.000001; //10^-6
static double msToNano=(long)(1.0/0.000001);

double vsyncWaitTimeSum=0,vsyncWaitTimeC=0,vsyncMiddleWTSum=0,vsyncMiddleC=0;

double tsMS=0;

volatile ksNanoseconds lastTimeVsyncOccuredNS,l,l2;
ksNanoseconds frameDTNS,eyeDTNS;

ksNanoseconds getVsyncPosX();

JNIEXPORT void JNICALL Java_com_constantin_wilson_FPV_1VR_VsyncHelper_doFrameNative(JNIEnv * env,jobject obj,jlong l){
    //lastTimeVsyncOccuredNS=(ksNanoseconds)l;
    //__android_log_print(ANDROID_LOG_ERROR, "FPV_VR", "l %f",(float)l*nanoToMs);
    /*Since we cannot compare System.nanoTime (java) and the c clock_gettime() values, this is a hacky approach for calculating
     * the vsync timestamp.*/
    lastTimeVsyncOccuredNS=getTimeNS()-(ksNanoseconds)l;

}
JNIEXPORT void JNICALL Java_com_constantin_wilson_FPV_1VR_VsyncHelper_initNative(JNIEnv * env,jobject obj){
    frameDTNS=(ksNanoseconds )(16.6*msToNano);
    eyeDTNS=(ksNanoseconds )((long)frameDTNS/2.0); //TODO resolution conversions
    //frameDTNS=16600000;
}


JNIEXPORT bool JNICALL Java_com_constantin_wilson_FPV_1VR_VsyncHelper_waitUntilVsyncMiddleNative(JNIEnv * env,jobject obj){
    if(getVsyncPosX()>eyeDTNS){
        return SKIP_EYE;
    }
    tsMS=getTimeMS();
    while (getVsyncPosX()<eyeDTNS){
        //wait
    }
    vsyncMiddleWTSum+=getTimeMS()-tsMS;
    vsyncMiddleC++;
    return !SKIP_EYE;
}

JNIEXPORT void JNICALL Java_com_constantin_wilson_FPV_1VR_VsyncHelper_waitUntilVsyncStartNative(JNIEnv * env,jobject obj){
    tsMS=getTimeMS();
    while (getVsyncPosX()>(500000)) {
        //
    }
    double time=getTimeMS()-tsMS;
    if(P_VSYNC_WAIT_TIME){
        //println("time waiting for new vsync:"+time);
    }
    vsyncWaitTimeSum+=time;
    vsyncWaitTimeC++;
}

ksNanoseconds getVsyncPosX() {
    l=lastTimeVsyncOccuredNS;
    //the CPU takes at least 1ms; makes Application more tearing resistent,but adds as many lag
    //l-=1*msToNano;
    //l+=8*msToNano;
    //l-=(frameDTNS-3*msToNano); //3ms seems to be the max. vsync offset to reduce lag without tearing
    //assume crating&rendering never takes longer than 8.3-3=5.3ms;
    l2=getTimeNS()-l;
    //int c=0;
    while (l2>=frameDTNS){
        //c++;
        //__android_log_print(ANDROID_LOG_ERROR, "FPV_VR", "counter %f",(float)c);
        l2-=frameDTNS;
    }
    //System.out.println("C:"+c);
    //System.out.println(""+(l2*nanoToMs));
    //__android_log_print(ANDROID_LOG_ERROR, "FPV_VR", "VsyncPosXMS %f",(float)l2*nanoToMs);
    //printlnAndroid("blub");
    return l2;
}


