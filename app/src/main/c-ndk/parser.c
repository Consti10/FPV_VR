//
// Created by Constantin on 01.11.2016.
//

#include "jni.h"
#include "android/log.h"
#include "telemetry.c"
#include "telemetry.h"
#include "ltm.c"
//#include "frsky.h"
#include "frsky.c"

telemetry_data_t td;
/*uint8_t buf[1024];
size_t n;*/
frsky_state_t fs;

JNIEXPORT void JNICALL Java_com_constantin_wilson_FPV_1VR_MyOSDReceiverRenderer_init(
        JNIEnv * env,
        jobject obj) {
    telemetry_init(&td);
    return;
}

JNIEXPORT void JNICALL Java_com_constantin_wilson_FPV_1VR_MyOSDReceiverRenderer_parseLTM(
        JNIEnv * env,
        jobject obj,jbyteArray b,jint i) {
    jbyte *jbytePointer1=(*env)->GetByteArrayElements(env,b,NULL);
    ltm_read(&td, jbytePointer1, i);
    (*env)->ReleaseByteArrayElements(env,b,jbytePointer1,0);
}
JNIEXPORT void JNICALL Java_com_constantin_wilson_FPV_1VR_MyOSDReceiverRenderer_parseFRSKY(
        JNIEnv * env,
        jobject obj,jbyteArray b,jint i) {
    jbyte *jbytePointer1=(*env)->GetByteArrayElements(env,b,NULL);
    frsky_parse_buffer(&fs, &td, jbytePointer1, i);
    (*env)->ReleaseByteArrayElements(env,b,jbytePointer1,0);
}
/*JNIEXPORT void JNICALL Java_com_example_wilson_FPV_1VR_TestActivity_parseLTM(
        JNIEnv * env,
        jobject obj,jbyteArray b,jint i) {
    jbyte *jbytePointer1=(*env)->GetByteArrayElements(env,b,NULL);
    ltm_read(&td, jbytePointer1, i);
    (*env)->ReleaseByteArrayElements(env,b,jbytePointer1,0);
}
JNIEXPORT jint JNICALL Java_com_example_wilson_FPV_1VR_TestActivity_parseFRSKY(
        JNIEnv * env,
        jobject obj,jbyteArray b,jint i) {
    jbyte *jbytePointer1=(*env)->GetByteArrayElements(env,b,NULL);
    int test=frsky_parse_buffer(&fs, &td, jbytePointer1, i);
    (*env)->ReleaseByteArrayElements(env,b,jbytePointer1,0);
    return (jint) test;
}*/

JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_MyOSDReceiverRenderer_getLatitude(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) ((float)td.latitude); //double

}

JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_MyOSDReceiverRenderer_getLongitude(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) ((float)td.longitude); //double

}

JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_MyOSDReceiverRenderer_getVoltage(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.voltage;
}
JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_MyOSDReceiverRenderer_getAmpere(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.ampere;
}
JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_MyOSDReceiverRenderer_getBaroAltitude(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.baro_altitude;
}
JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_MyOSDReceiverRenderer_getAltitude(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.altitude;
}
JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_MyOSDReceiverRenderer_getSpeed(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.speed;
}

JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_MyOSDReceiverRenderer_getRoll(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.roll; //uint16 //TODO degree?
}
JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_MyOSDReceiverRenderer_getPitch(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.pitch; //uint16 //TODO degree?
}
JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_MyOSDReceiverRenderer_getYaw(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.yaw; //uint16 //TODO degree?
}
JNIEXPORT jfloat JNICALL Java_com_constantin_wilson_FPV_1VR_MyOSDReceiverRenderer_getRSSI(
        JNIEnv* env,
        jobject obj) {
    return (jfloat) td.rssi; //uint16 //TODO degree?
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
