//
// Created by Constantin on 01.11.2016.
//

#define _GNU_SOURCE
#include "jni.h"
#include "android/log.h"
#include "telemetry.c"
#include "telemetry.h"
#include "ltm.c"
#include "frsky.c"
#include "mavlink.c"
#include "../../../../../../../AppData/Local/Android/sdk/ndk-bundle/platforms/android-21/arch-mips64/usr/include/sched.h"
#include <sys/syscall.h>
#include <pthread.h>
#include <sched.h>
#include <GLES2/gl2ext.h>
#include <EGL/egl.h>

telemetry_data_t td;
/*uint8_t buf[1024];
size_t n;*/
frsky_state_t fs;
//PFNGLSTARTTILINGQCOMPROC	glStartTilingQCOM_;
//PFNGLENDTILINGQCOMPROC		glEndTilingQCOM_;

JNIEXPORT void JNICALL Java_com_constantin_wilson_FPV_1VR_OSDReceiverRenderer_init(
        JNIEnv * env,
        jobject obj) {
    telemetry_init(&td);
    return;
}
JNIEXPORT jint JNICALL Java_com_constantin_wilson_FPV_1VR_OSDReceiverRenderer_parseLTM(
        JNIEnv * env,
        jobject obj,jbyteArray b,jint i) {
    jbyte *jbytePointer1=(*env)->GetByteArrayElements(env,b,NULL);
    int ret=ltm_read(&td, jbytePointer1, i);
    (*env)->ReleaseByteArrayElements(env,b,jbytePointer1,0);
    return (jint)ret;
}
JNIEXPORT jint JNICALL Java_com_constantin_wilson_FPV_1VR_OSDReceiverRenderer_parseFRSKY(
        JNIEnv * env,
        jobject obj,jbyteArray b,jint i) {
    jbyte *jbytePointer1=(*env)->GetByteArrayElements(env,b,NULL);
    int ret=frsky_parse_buffer(&fs, &td, jbytePointer1, i);
    (*env)->ReleaseByteArrayElements(env,b,jbytePointer1,0);
    return (jint)ret;
}
JNIEXPORT jint JNICALL Java_com_constantin_wilson_FPV_1VR_OSDReceiverRenderer_setWBRSSI(
        JNIEnv * env,
        jobject obj,jfloat rssi) {
    //int ret=frsky_parse_buffer(&fs, &td, jbytePointer1, i);
    int ret=1;
    td.WBrssi=rssi;
    return (jint)ret;
}
JNIEXPORT jint JNICALL Java_com_constantin_wilson_FPV_1VR_OSDReceiverRenderer_parseMAVLINK(
        JNIEnv * env,
        jobject obj,jbyteArray b,jint i) {
    jbyte *jbytePointer1=(*env)->GetByteArrayElements(env,b,NULL);
    int ret=mavlink_read(&td, jbytePointer1, i);
    (*env)->ReleaseByteArrayElements(env,b,jbytePointer1,0);
    return (jint)ret;
}



JNIEXPORT jint JNICALL Java_com_constantin_wilson_FPV_1VR_TestActivity_parseLTM(
        JNIEnv * env,
        jobject obj,jbyteArray b,jint i) {
    jbyte *jbytePointer1=(*env)->GetByteArrayElements(env,b,NULL);
    int ret=ltm_read(&td, jbytePointer1, i);
    (*env)->ReleaseByteArrayElements(env,b,jbytePointer1,0);
    return (jint)ret;
}
JNIEXPORT jint JNICALL Java_com_constantin_wilson_FPV_1VR_TestActivity_parseFRSKY(
        JNIEnv * env,
        jobject obj,jbyteArray b,jint i) {
    jbyte *jbytePointer1=(*env)->GetByteArrayElements(env,b,NULL);
    int ret=frsky_parse_buffer(&fs, &td, jbytePointer1, i);
    (*env)->ReleaseByteArrayElements(env,b,jbytePointer1,0);
    return (jint)ret;
}

/*JNIEXPORT jbyteArray JNICALL Java_com_constantin_wilson_FPV_1VR_MyOSDReceiverRenderer_test(
        JNIEnv* env,
        jobject obj) {
    mavlink_message_t msg;
    mavlink_msg_attitude_pack(0,0,&msg,0,0,0,0,0,0,0);
    //__android_log_print(ANDROID_LOG_ERROR, "FPV_VR", "HELLO test");
    uint8_t bytes[msg.len];
    for(int i=0;i<msg.len;i++){
    }
    return (jbyteArray) (msg); //double
}*/


JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_OSDReceiverRenderer_getLatitude(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) ((float)td.latitude); //double

}

JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_OSDReceiverRenderer_getLongitude(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) ((float)td.longitude); //double

}

JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_OSDReceiverRenderer_getVoltage(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.voltage;
}
JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_OSDReceiverRenderer_getAmpere(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.ampere;
}
JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_OSDReceiverRenderer_getBaroAltitude(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.baro_altitude;
}
JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_OSDReceiverRenderer_getAltitude(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.altitude;
}
JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_OSDReceiverRenderer_getSpeed(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.speed;
}

JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_OSDReceiverRenderer_getRoll(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.roll; //uint16 //TODO degree?
}
JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_OSDReceiverRenderer_getPitch(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.pitch; //uint16 //TODO degree?
}
JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_OSDReceiverRenderer_getYaw(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.yaw; //uint16 //TODO degree?
}
/*JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_MyOSDReceiverRenderer_getRSSI(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.rssi; //uint16 //TODO degree?
}*/

JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_OSDReceiverRenderer_getWBRSSI(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.WBrssi; //float !!
}

JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_MyOSDReceiverRenderer_getAirspeed(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.airspeed; //uint16 //TODO
}
JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_MyOSDReceiverRenderer_getSats(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.sats; //uint16 //TODO
}


/*Has nothing to do with OSD, but for not having to bind another file/class via ndk, this funktion is declared here,too*/
JNIEXPORT void JNICALL Java_com_constantin_wilson_FPV_1VR_OSDReceiverRenderer_setAffinity(JNIEnv * env,
                                   jobject obj,jint core){
    //int mMask=(jint)mask;
    //int mask=-1;//THREAD_AFFINITY_BIG_CORES
    //mask=1;
    cpu_set_t  cpuset;
    CPU_ZERO(&cpuset);       //clears the cpuset
    CPU_SET( core, &cpuset); //set CPU x on cpuset*/

    int err,syscallres;
    pid_t pid=gettid();
    //syscallres=syscall(__NR_sched_setaffinity,pid, sizeof(mask),&mask);
    syscallres=syscall(__NR_sched_setaffinity,pid, sizeof(cpuset),&cpuset);
    if(syscallres){
        err=errno;
        __android_log_print(ANDROID_LOG_ERROR, "FPV_VR", "setThread Affinity:",err);
    }
}

