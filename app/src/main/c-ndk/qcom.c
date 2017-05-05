//
// Created by Constantin on 29.12.2016.
//
#define _GNU_SOURCE
#include <string.h>
#include <assert.h>
#include <stdlib.h>
#include <unistd.h>
#include <jni.h>
#include <GLES2/gl2ext.h>
#include <GLES2/gl2.h>
#include <EGL/egl.h>
#include <android/log.h>

//#include "Log.h"

PFNGLSTARTTILINGQCOMPROC	glStartTilingQCOM_;
PFNGLENDTILINGQCOMPROC		glEndTilingQCOM_;

JNIEXPORT void JNICALL Java_com_constantin_wilson_FPV_1VR_QCOMHelper_TilingPrepare(JNIEnv * env,jobject obj) {
    glStartTilingQCOM_ = (PFNGLSTARTTILINGQCOMPROC)eglGetProcAddress("glStartTilingQCOM");
    glEndTilingQCOM_ = (PFNGLENDTILINGQCOMPROC)eglGetProcAddress("glEndTilingQCOM");
}
JNIEXPORT void JNICALL Java_com_constantin_wilson_FPV_1VR_QCOMHelper_glStartTilingQCOM(JNIEnv * env,jobject obj,jint x,jint y,jint width,jint height) {
    glStartTilingQCOM_( x,y,width,height, 0 );
    return;
}
JNIEXPORT void JNICALL Java_com_constantin_wilson_FPV_1VR_QCOMHelper_glEndTilingQCOM(JNIEnv * env, jobject obj) {
    glEndTilingQCOM_( GL_COLOR_BUFFER_BIT0_QCOM );
    return;
}

