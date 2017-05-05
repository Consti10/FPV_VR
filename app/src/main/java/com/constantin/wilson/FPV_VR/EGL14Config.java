package com.constantin.wilson.FPV_VR;

import android.content.SharedPreferences;
import android.opengl.EGL14;

import static android.content.Context.MODE_PRIVATE;
import static com.constantin.wilson.FPV_VR.QCOMHelper.EGL_MUTABLE_RENDER_BUFFER_BIT_KHR;

/**
 * Created by Constantin on 01.01.2017.
 * The EGL Config class that- with MyGLSurfaceViewFB- enables front buffer rendering
 */

public class EGL14Config
{
    /** Extension for surface recording */
    private static final int	EGL_RECORDABLE_ANDROID	= 0x3142;

    /**
     * Chooses a valid EGL Config for EGL14
     *
     * @param eglDisplay
     *            EGL14 Display
     * @param MSAA
     *            True to enable 4 msaa
     * @return Resolved config
     */
    public static android.opengl.EGLConfig chooseConfig(final android.opengl.EGLDisplay eglDisplay, final boolean MSAA)
    {
        //Only enable MSAA in FBR mode when using QCOM Tiled
        final int[] attribList = { EGL14.EGL_RED_SIZE, 8, //
                EGL14.EGL_GREEN_SIZE, 8, //
                EGL14.EGL_BLUE_SIZE, 8, //
                EGL14.EGL_ALPHA_SIZE, 8, //
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT, //
                /*I think this should be the place where to set the "mutable" for Surface Type.
                * However, on my hardware the appl. renders to the front buffer, when this is leaved out,
                * and EGL_RENDER BUFFER is set to single Buffer in createWindowSurface*/
                EGL14.EGL_SAMPLE_BUFFERS, 1,
                EGL14.EGL_SAMPLES, 4,  // This is for 4x MSAA.
                EGL14.EGL_NONE, 0, //
                EGL14.EGL_NONE };
        if(!MSAA){
            attribList[11]=0;
            attribList[13]=1;
        }
        /*
        final int[] attribList = { EGL14.EGL_RED_SIZE, 8, //
                EGL14.EGL_GREEN_SIZE, 8, //
                EGL14.EGL_BLUE_SIZE, 8, //
                EGL14.EGL_ALPHA_SIZE, 8, //
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT, //
                EGL14.EGL_SURFACE_TYPE,EGL14.EGL_WINDOW_BIT|EGL_MUTABLE_RENDER_BUFFER_BIT_KHR,

                EGL14.EGL_SAMPLE_BUFFERS, 1,
                EGL14.EGL_SAMPLES, 4,  // This is for 4x MSAA.

                EGL14.EGL_NONE, 0, //
                EGL14.EGL_NONE };*/
        /*if (recordable == true)
        {
            attribList[attribList.length - 3] = EGL14Config.EGL_RECORDABLE_ANDROID;
            attribList[attribList.length - 2] = 1;
        }*/

        android.opengl.EGLConfig[] configList = new android.opengl.EGLConfig[1];
        final int[] numConfigs = new int[1];

        if (EGL14.eglChooseConfig(eglDisplay, attribList, 0, configList, 0, configList.length, numConfigs, 0) == false)
        {
            throw new RuntimeException("failed to find valid RGB8888 EGL14 EGLConfig");
        }

        return configList[0];
    }
}