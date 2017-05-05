package com.constantin.wilson.FPV_VR;

/**
 * Created by Constantin on 11.12.2016.
 */

public class QCOMHelper {
    public static int GL_CPU_OPTIMIZED_QCOM = 0x8FB1;
    public static int GL_GPU_OPTIMIZED_QCOM = 0x8FB2;
    public static int GL_RENDER_DIRECT_TO_FRAMEBUFFER_QCOM = 0x8FB3;
    public static int GL_DONT_CARE = 0x1100;
    public static int GL_BINNING_CONTROL_HINT_QCOM = 0x8FB0;
    public static int WRITEONLY_RENDERING_QCOM=0x8823;

    public static int EGL_MUTABLE_RENDER_BUFFER_BIT_KHR=0x00001000;
    public static int EGL_FRONT_BUFFER_AUTO_REFRESH_ANDROID=0x314C;

    static{
        System.loadLibrary("qcom");
    }
    public static native void glStartTilingQCOM(int x,int y,int width,int height);
    public static native void glEndTilingQCOM();
    public static native void TilingPrepare();
}


