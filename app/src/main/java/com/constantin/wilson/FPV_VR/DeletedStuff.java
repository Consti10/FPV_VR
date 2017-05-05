package com.constantin.wilson.FPV_VR;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Choreographer;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.vrtoolkit.cardboard.PhoneParams;
import com.google.vrtoolkit.cardboard.proto.Phone;
import com.google.vrtoolkit.cardboard.sensors.HeadTracker;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGL10;

import static android.content.Context.MODE_PRIVATE;

public class DeletedStuff {

    //<uses-permission android:name="com.qti.permission.PROFILER" />
    /*
        double x=-1*Math.cos(Math.toRadians(angle_x))*Math.cos(Math.toRadians(90-angle_y));
        double y=-1*Math.sin(Math.toRadians(angle_x));
        double z=   Math.cos(Math.toRadians(angle_x))*Math.sin(Math.toRadians(90-angle_y));
        double x2=  Math.sin(Math.toRadians(angle_z));
        double y2=  Math.cos(Math.toRadians(angle_z));
        float drei_d_factor=0.1f;
        float r=12.0f;
        float eyeX = 0.0f;
        float eyeY = 0.0f;
        float eyeZ = 3.0f;
        float lookX =(float)(eyeX-(r*x));
        float lookY =(float)(eyeY-(r*y));;
        float lookZ =(float)(eyeZ-(r*z));
        float upX = (float)x2;
        float upY= (float)y2;
        float upZ = 0;
        Matrix.setLookAtM(mLeftEyeViewM  , 0, eyeX-drei_d_factor, eyeY,eyeZ, lookX, lookY,lookZ, upX, upY, upZ);
        Matrix.setLookAtM(mRightEyeViewM , 0, eyeX+drei_d_factor, eyeY,eyeZ, lookX, lookY,lookZ, upX, upY, upZ);*/

    //When looking straight forward,all angles are 0
        /*
        angle_x+=0.05f;
        if(angle_x>=360){angle_x=0;}
        System.out.println("angle_x" + angle_x);*/
        /*
        angle_y+=0.1f;
        if(angle_y>=360){angle_y=0;}
        System.out.println("angle_y" + angle_y);*/
        /*
        angle_z+=0.5f;
        if(angle_z>=360){angle_z=0;}*/
        /*
        float[] view=new float[16];

        for(int i=0;i<view.length;i++){
            view[i]=0;
        }
        for(int i=0;i<view.length;i++){
            System.out.print(""+view[i]+", ");
        }
        System.out.println("");
        mHeadTracker.getLastHeadView(view, 0);
        for(int i=0;i<view.length;i++){
            System.out.print(""+view[i]+", ");
        }
        System.out.println("");
        System.out.println("");*/

     /*mSurfaceTexture.updateTexImage();
        GLES20.glFinish();
        mSurfaceTexture.updateTexImage();
        GLES20.glFinish();
        mSurfaceTexture.updateTexImage();
        GLES20.glFinish();
        mSurfaceTexture.updateTexImage();
        GLES20.glFinish();*/
    //Log.w("renderer", "since last time: " + ((mSurfaceTexture.getTimestamp() - SurfaceTextureTimeB) / 1000));
    //SurfaceTextureTimeB=mSurfaceTexture.getTimestamp();
    //Log.w("GLRendererStereo","Time for updating:"+(System.currentTimeMillis()-timeBUpdate));
    //GLES20.glFinish();
    //GLES20.glFlush();
        /*if((System.currentTimeMillis()-timeBUpdate)>=12){
            Log.w("GLRendererStereo","Time for updating:"+(System.currentTimeMillis()-timeBUpdate));
        }*/



    /*if(presentationTime){
            EGLExt.eglPresentationTimeANDROID(EGL14.eglGetCurrentDisplay(),EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW),System.nanoTime());
        }
        if(headTracking && mHeadTracker!=null){
            mHeadTracker.getLastHeadView(tempEyeViewM, 0);
            Matrix.multiplyMM(mLeftEyeViewM, 0,  mLeftEyeTranslate,  0, tempEyeViewM, 0);
            Matrix.multiplyMM(mRightEyeViewM, 0, mRightEyeTranslate, 0, tempEyeViewM, 0);
        }
        if(osd) {
            mOSD.setupModelMatrices();
        }
        //GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        //Danger: getTimestamp can't be used to compare with System.nanoTime or System.currentTimeMillis
        //because it's zero point depends on the sources providing the image;
        mSurfaceTexture.updateTexImage();
        if(clearFramebuffer){
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        }
        //GLES20.glFinish();
        //GLES20.glFlush();
        mOGLProgramTexEx.beforeDraw(buffers[0]);
        if(enable_stereo_renderer){
            //draw left eye
            GLES20.glViewport(leftViewportX, leftViewPortY, leftViewportWidth, leftViewPortHeight);
            mOGLProgramTexEx.draw(mLeftEyeViewM,mProjM,numberCanvasTriangles);
            //draw right eye
            GLES20.glViewport(rightViewportX, rightViewportY, rightViewportWidth, rightViewportHeight);
            mOGLProgramTexEx.draw(mRightEyeViewM,mProjM,numberCanvasTriangles);
        }else{
            GLES20.glViewport(0, 0, mDisplay_x, mDisplay_y);
            mOGLProgramTexEx.draw(mLeftEyeViewM,mProjM,numberCanvasTriangles);
        }
        mOGLProgramTexEx.afterDraw();
        if(osd){
            if(enable_stereo_renderer){
                GLES20.glViewport(leftViewportX, leftViewPortY, leftViewportWidth, leftViewPortHeight);
                mOSD.drawLeftEye(mLeftEyeViewM);
                GLES20.glViewport(rightViewportX, rightViewportY, rightViewportWidth, rightViewportHeight);
                mOSD.drawRightEye(mRightEyeViewM);
            }else{
                GLES20.glViewport(0, 0, mDisplay_x, mDisplay_y);
                mOSD.drawLeftEye(mLeftEyeViewM);
            }
        }
        //System.out.println("Computing took:"+(System.currentTimeMillis()-calculatingTB));
        zaehlerFramerate++;
        if((System.currentTimeMillis()-timeb)>1000) {
            fps = (double)(zaehlerFramerate / 1.0f);
            System.out.println("OpenGL fps:"+fps);
            if(fps >3 && mVideoReceiver !=null && mOSD!=null){
                mVideoReceiver.tellOpenGLFps((long)fps);
                mOSD.mDecoder_fps= mVideoReceiver.getDecoderFps();
                mOSD.mOpenGL_fps=(int)fps;
            }
            timeb = System.currentTimeMillis();
            zaehlerFramerate = 0;
        }
        /*try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        /*if(!FULL_VERSION){
            double timeElapsed= System.currentTimeMillis();
            System.out.println("Time elapsed:"+timeElapsed);
        }*/
    //xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx


    /**
     private void receiveFromUDP() {
     int server_port = 5001;
     byte[] message = new byte[1024];
     //byte[] messageLong=new byte[1024];
     //int messageLongOffset=0;
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
     //System.out.println("Receiving OSD Data; Parsing required; length:"+p.getLength());
     //we have to parse Telemetry Data
     if(LTM){
     int ret=parseLTM(message,p.getLength());
     }
     if(FRSKY){
     int ret=parseFRSKY(message,p.getLength());
     //System.out.println("PARSE frsky");
     }
     //make Sure we have enough bytes when parsing the OSD data next time.
     //TODO: find a better approach
     //try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
     }else{exception=false;}
     }
     if (s != null) {
     s.close();
     s=null;
     }
     }*/

    /*if(countUp1){angle_z +=0.2;}else{angle_z -=0.2;}if(angle_z >=40){countUp1=false;}if(angle_z <=-40){countUp1=true;}
        //up_down
        if(countUp2){angle_x +=0.2;}else{angle_x -=0.2;}if(angle_x >=40){countUp2=false;}if(angle_x <=-40){countUp2=true;}
        //rotating vertically (only for Kopter)
        if(countUp3){angle_y +=0.2;}else{angle_y -=0.2;}if(angle_y >=40){countUp3=false;}if(angle_y <=-40){countUp3=true;}
        mHome_Arrow_angle_y+=0.4;if(mHome_Arrow_angle_y>=360){mHome_Arrow_angle_y=0;}
        mHeight_m+=0.1;if(mHeight_m>=100){mHeight_m=0;}*/
    /*<SwitchPreference
                android:key="formatStream"
                android:title="formatStream"
                android:summary="use exactely the 2 first received nalus to configure decoder. Default: OFF, only for special GPU's. If enabled,it is especially needed to start the rpi camera after your App Activity."
                android:defaultValue="false" />

                if(!mLowLagDecoder.formatStream){
                mLowLagDecoder.configureStartDecoder(MediaCodecFormatHelper.getRpiCsd0(),MediaCodecFormatHelper.getRpiCsd1());
                mLowLagDecoder.feedDecoder(n,len);
            }else{
                if(naluCount==1){
                    //set csd0
                    csd0=new byte[18];
                    for(int i=0;i<csd0.length;i++){
                        csd0[i]=n[i];
                    }
                    streamCsd0=ByteBuffer.wrap(csd0);
                }
                if(naluCount==2){
                    //set csd1 and configure decoder
                    csd1=new byte[18];
                    for(int i=0;i<csd1.length;i++){
                        csd1[i]=n[i];
                    }
                    streamCsd1=ByteBuffer.wrap(csd1);*/
    /*--------------------------------------------------------------------------------------------
        String servicePackage = "com.constantin.wilson.FPV_VR";
        String serviceClass = "com.constantin.wilson.FPV_VR.MyVrListenerService";
        ComponentName cn=new ComponentName(servicePackage,serviceClass);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                setVrModeEnabled(true,cn);
            } catch (PackageManager.NameNotFoundException e) {
                List<ApplicationInfo> installed = getPackageManager().getInstalledApplications(0);
                boolean isInstalled = false;
                for (ApplicationInfo app : installed) {
                    if (app.packageName.equals(servicePackage)) {
                        isInstalled = true;
                        break;
                    }
                }
                if (isInstalled) {
                    // Package is installed, but not enabled in Settings.  Let user enable it.
                    startActivity(new Intent(Settings.ACTION_VR_LISTENER_SETTINGS));
                } else {
                    // Package is not installed.  Send an intent to download this.
                    //sentIntentToLaunchAppStore(servicePackage);
                    System.out.println("Not installed");
                }
            }
        }*/
        /*PackageManager pm=getPackageManager();
        boolean b=getPackageManager().hasSystemFeature("android.software.vr.mode");
        System.out.println("Available:"+b);*/

    /*Thread reqThread=new Thread(){
            @Override
            public void run() {
                while (true){
                    try {Thread.sleep(0,10);} catch (InterruptedException e) {e.printStackTrace();}
                    mGLView.requestRender();
                }

            }
        };
        //reqThread.setPriority(Thread.MAX_PRIORITY);
        reqThread.start();*/
    //mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    //mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    //mGLView.requestRender();
    //Choreographer.getInstance().postFrameCallback(this);

    /*private EGLSurface mPixelBuffer;
    //private EGLSurface mMainSurface;
    private final GLSurfaceView.EGLWindowSurfaceFactory mWindowSurfaceFactory = new GLSurfaceView.EGLWindowSurfaceFactory() {
        @Override
        public void destroySurface(final EGL10 egl, final EGLDisplay display, final EGLSurface surface) {
            Log.v(TAG, "EGLWindowSurfaceFactory.destroySurface " + Integer.toHexString(surface.hashCode()));
            egl.eglDestroySurface(display, surface);
        }

        @Override
        public EGLSurface createWindowSurface(final EGL10 egl, final EGLDisplay display, final EGLConfig config,
                                              final Object ignoredNativeWindow) {
            //System.out.println("SURFACE CRETIOn");
            final int[] surfaceAttribs = {
                    //EGL10.EGL_WIDTH, 16,
                    //EGL10.EGL_HEIGHT, 16,
                    //EGL10.EGL_SURFACE_TYPE,QCOMHelper.EGL_MUTABLE_RENDER_BUFFER_BIT_KHR,
                    //EGL10.EGL_RENDER_BUFFER,EGL10.EGL_SINGLE_BUFFER,
                    EGL14.EGL_RENDER_BUFFER,EGL14.EGL_SINGLE_BUFFER,
                    //QCOMHelper.EGL_FRONT_BUFFER_AUTO_REFRESH_ANDROID, EGL14.EGL_TRUE,
                    EGL10.EGL_NONE
            };
            mPixelBuffer = egl.eglCreatePbufferSurface(display, config, surfaceAttribs);
            if (EGL10.EGL_NO_SURFACE == mPixelBuffer) {
                throw new IllegalStateException("Pixel buffer surface not created; egl error 0x"
                        + Integer.toHexString(egl.eglGetError()));
            }
            Log.v(TAG, "EGLWindowSurfaceFactory.createWindowSurface : " + Integer.toHexString(mPixelBuffer.hashCode()));
            return mPixelBuffer;
        }
    };

    private final GLSurfaceView.EGLConfigChooser mConfigChooser = new GLSurfaceView.EGLConfigChooser() {
        @Override
        public EGLConfig chooseConfig(final EGL10 egl, final EGLDisplay display) {
            final int[] numberConfigs = new int[1];
            if (!egl.eglGetConfigs(display, null, 0, numberConfigs)) {
                throw new IllegalStateException("Unable to retrieve number of egl configs available.");
            }
            final EGLConfig[] configs = new EGLConfig[numberConfigs[0]];
            if (!egl.eglGetConfigs(display, configs, configs.length, numberConfigs)) {
                throw new IllegalStateException("Unable to retrieve egl configs available.");
            }

            final int[] configAttribs = {
                    EGL10.EGL_ALPHA_SIZE, 8, // need alpha for the multi-pass
                    // timewarp compositor
                    EGL10.EGL_BLUE_SIZE, 8,
                    EGL10.EGL_GREEN_SIZE, 8,
                    EGL10.EGL_RED_SIZE, 8,
                    EGL10.EGL_DEPTH_SIZE, 0,
                    EGL10.EGL_SAMPLES, 0,
                    //EGL10.EGL_SURFACE_TYPE,QCOMHelper.EGL_MUTABLE_RENDER_BUFFER_BIT_KHR,
                    //EGL10.EGL_SURFACE_TYPE,EGL10.EGL_WINDOW_BIT,
                    EGL10.EGL_NONE
            };

            EGLConfig config = null;
            for (int i = 0; i < numberConfigs[0]; ++i) {
                final int[] value = new int[1];

                /*final int EGL_OPENGL_ES3_BIT_KHR = 0x0040;
                if (!egl.eglGetConfigAttrib(display, configs[i], EGL10.EGL_RENDERABLE_TYPE,
                        value)) {
                    Log.v(TAG, "eglGetConfigAttrib for EGL_RENDERABLE_TYPE failed");
                    continue;
                }
                if ((value[0] & EGL_OPENGL_ES3_BIT_KHR) != EGL_OPENGL_ES3_BIT_KHR) {
                    continue;
                }*/

                /*if (!egl.eglGetConfigAttrib(display, configs[i], EGL10.EGL_SURFACE_TYPE, value)) {
                    Log.v(TAG, "eglGetConfigAttrib for EGL_SURFACE_TYPE failed");
                    continue;
                }
                if ((value[0]
                        & (EGL10.EGL_WINDOW_BIT | EGL10.EGL_PBUFFER_BIT)) != (EGL10.EGL_WINDOW_BIT
                        | EGL10.EGL_PBUFFER_BIT)) {
                    continue;
                }*/
    //System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
    /*int j = 0;
    for (; configAttribs[j] != EGL10.EGL_NONE; j += 2) {
        if (!egl.eglGetConfigAttrib(display, configs[i], configAttribs[j], value)) {
            Log.v(TAG, "eglGetConfigAttrib for " + configAttribs[j] + " failed");
            continue;
        }
        if (value[0] != configAttribs[j + 1]) {
            break;
        }
    }
    if (configAttribs[j] == EGL10.EGL_NONE) {
        config = configs[i];
        break;
    }
}
            /*config=configs[10];

            for(int i=0;i<configs.length;i++){
                final int[] value=new int[1];
                if(!egl.eglGetConfigAttrib(display,configs[i],EGL10.EGL_SURFACE_TYPE,value)){
                    Log.v(TAG," failed");
                    continue;
                }
                if(value[0]!=QCOMHelper.EGL_MUTABLE_RENDER_BUFFER_BIT_KHR){
                    System.out.println("NOT");
                }else {
                    System.out.println("TRUE");
                }
            }*/
/*return config;
        }
        };*/
    //double tb=VsyncHelper.getTimeMs();
        /*long sync= GLES30.glFenceSync(GLES30.GL_SYNC_GPU_COMMANDS_COMPLETE,0);
        int res=GLES30.GL_UNSIGNALED;
        res=GLES30.glClientWaitSync(sync,GLES30.GL_SYNC_FLUSH_COMMANDS_BIT,1000000000);
        GLES30.glDeleteSync(sync);*/
        /*if(res==GLES30.GL_ALREADY_SIGNALED){
            System.out.println("GL_ALREADY_SIGNALED");
        }else if(res==GLES30.GL_TIMEOUT_EXPIRED){
            System.out.println("GL_TIMEOUT_EXPIRED");
        }else if(res==GLES30.GL_CONDITION_SATISFIED){
            System.out.println("GL_CONDITION_SATISFIED");
        }else if(res==GLES30.GL_WAIT_FAILED){
            System.out.println("GL_WAIT_FAILED");
        }else{
            System.out.println("NOTHING");
        }*/
    //System.out.println("ClientWait took:"+(VsyncHelper.getTimeMs()-tb));
    // EGL14.eglWaitClient();
    //EGL14.eglWaitGL(); //same like glFinish as comp.spec.
    //GLES20.glFinish();
        /*long sync=GLES30.glFenceSync(GLES30.GL_SYNC_GPU_COMMANDS_COMPLETE,0);
        GLES30.glWaitSync(sync,0,10); //doesn't block
        IntBuffer ib=;
        GLES30.glGetSynciv(sync,GLES30.GL_SYNC_STATUS, 4, null, ib);
        boolean finished = syncStatus == GLES30.GL_SIGNALED;
        //GLES30.glFinish();
        GLES30.glDeleteSync(sync);*/
    //GLES20.glFinish();
    /*double ts=getTimeMs();
        long vsync=lastTimeVsyncOccuredNS;
        long nanoTime=System.nanoTime();
        int c=0;
        while (nanoTime-vsync>accurateFrameDT){
            vsync+=accurateFrameDT;
            c++;
        }
        lastTimeVsyncOccuredNS=vsync;
        double vsyncPosX=(System.nanoTime()-vsync)*nanoToMs;
        vsyncPosX+=1;
        while (vsyncPosX>FrameDT){
            vsyncPosX-=FrameDT;
            c++;
        }
        return vsyncPosX;*/

    /*double d=getTimeMs()-(l*nanoToMs);
        if(d>2){
            System.out.println(d);
        }*/
    //System.out.println("doFrame()");
        /*synchronized (this){
            lastTimeVsyncOccuredNS=l;
        }
        //lastTimeVsyncOccured=l*nanoToMs;*/
        /*if(vsync1==0){
            vsync1=l;
        }else if(vsync2==0){
            vsync2=l;
            synchronized (this){
                accurateFrameDT=vsync2-vsync1;
                //accurateFrameDT=16642852;
            }
            //System.out.println(""+accurateFrameDT);
        }*/
    private String getVertexShaderDistortedCardboard(){
        return //"uniform mat4 uMVPMatrix;\n" +
                "attribute vec4 aPosition;\n" +
                        "attribute vec4 aTexCoord;\n" +
                        "varying vec2 vTexCoord;\n" +
                        "varying vec2 isInRange;"+
                        "uniform mat4 uMVMatrix;" +
                        "uniform mat4 uPMatrix;" +
                        "float r2;"+
                        "float near;"+
                        //"vec2 _Undistortion=vec2(-0.3,0.0);"+
                        "vec2 _Undistortion=vec2(-0.55,0.34);"+
                        //"vec2 _Undistortion=vec2(-0.2,0.0);"+
                        //"mat4 _Undistortion;"+
                        //"float ret=0.0;"+
                        //"vec4 _Undistortion=vec4(-0.441,-0.156,0.0,0.0);"+
                        //"float _NearClip=1.0;"+
                        "float _MaxRadSq=1.5;"+
                        "float _MaxVisibleRadius=0.8;"+
                        "float x;"+
                        "float r;"+
                        "vec4 pos;"+
                        "void main() {\n" +
                        "near=(-(uPMatrix[2][2] + 1.0) / uPMatrix[3][2]);"+
                        "  pos=uMVMatrix * aPosition;"+
                        "  vTexCoord = (aTexCoord).xy;" +
                        "if(pos.z<near){"+
                        //"  r2=clamp(dot(pos.xy,pos.xy)/(pos.z*pos.z),0.0,_MaxRadSq);"+
                        "  r2=dot(pos.xy,pos.xy)/(pos.z*pos.z);"+
                        "  pos.xy *=1.0+(_Undistortion.x+_Undistortion.y*r2)*r2;"+
                        "  r2=dot(pos.xy,pos.xy)/(pos.z*pos.z);"+
                        "r=sqrt(r2);"+
                        "if(r>_MaxVisibleRadius){"+
                        //Todo: Set to a value, where the final position
                        //"x=_MaxVisibleRadius/sqrt(dot(pos.xy,pos.xy)/(pos.z*pos.z));"+
                        "x=_MaxVisibleRadius/r;"+
                        "pos.xy*=x;"+
                        //"blub=1.0+(_Undistortion.x+_Undistortion.y*_MaxRadSq)*_MaxRadSq;"+
                        //"bla=1.0+(_Undistortion.x+_Undistortion.y*r2)*r2;"+
                        //"x=pos.x*blub;"+
                        //"y=pos.y*blub;"+
                        //"pos.x;"+
                        //"pos.xy=vec2(2.0,2.0);"+
                        //"isInRange.x=0.0;" +
                        "}else{"+
                        //"  pos.xy *=1.0+(_Undistortion.x+_Undistortion.y*r2)*r2;"+
                        "}"+
                        "}"+
                        "  gl_Position=uPMatrix*pos;"+
                        "}";
    }
    private static String getFragmentShaderDistortedCardboard() {
        return "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;\n" +
                "varying vec2 vTexCoord;\n" +
                "uniform samplerExternalOES sTexture;\n" +
                "void main() {\n" +
                "  gl_FragColor = texture2D(sTexture, vTexCoord);\n" +
                "}\n";
        //"  gl_FragColor =vec4(0.5,0,0,1);" +
    }

    private String getVertexShaderDistortedCardboard2(){
        return //"uniform mat4 uMVPMatrix;\n" +
                "attribute vec4 aPosition;\n" +
                        "attribute vec4 aTexCoord;\n" +
                        "varying vec2 vTexCoord;\n" +
                        "uniform mat4 uMVMatrix;" +
                        "uniform mat4 uPMatrix;" +
                        "float r2;"+
                        //"vec2 _Undistortion=vec2(-0.55,0.34);"+ //Cardboard Coefficients. OK
                        //"vec2 _Undistortion=vec2(-0.441, 0.156f);"+ //Default library Coefficients. OK
                        "vec2 _Undistortion=vec2(-0.39110273, 0.16881901);"+ //Default library inverse Coefficients.
                        "float _MaxRadSq=1.5;"+
                        "float _MaxVisibleRadius=0.8;"+
                        "float x;"+
                        "float r;"+
                        "vec4 pos;"+
                        "void main() {\n" +
                        "pos=uMVMatrix * aPosition;"+
                        "vTexCoord = (aTexCoord).xy;" +

                        /*"r2=dot(pos.xy,pos.xy)/(pos.z*pos.z);"+
                        "pos.xy *=1.0+(_Undistortion.x+_Undistortion.y*r2)*r2;"+

                        "r2=dot(pos.xy,pos.xy)/(pos.z*pos.z);"+
                        "r=sqrt(r2);"+
                        "if(r>_MaxVisibleRadius){"+
                        //"x=_MaxVisibleRadius/sqrt(dot(pos.xy,pos.xy)/(pos.z*pos.z));"+
                        " x=_MaxVisibleRadius/r;"+
                        " pos.xy*=x;"+
                        "}"+
                        "gl_Position=uPMatrix*pos;"+
                        "}";*/
                        "r2=clamp(dot(pos.xy,pos.xy)/(pos.z*pos.z),0.0,_MaxRadSq);"+
                        "pos.xy *=1.0+(_Undistortion.x+_Undistortion.y*r2)*r2;"+
                        "gl_Position=uPMatrix*pos;"+
                        "}";
    }
    private static String getFragmentShaderDistortedCardboard2() {
        return "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;\n" +
                "varying vec2 vTexCoord;\n" +
                "uniform samplerExternalOES sTexture;\n" +
                "void main() {\n" +
                "  gl_FragColor = texture2D(sTexture, vTexCoord);\n" +
                "}\n";
        //"  gl_FragColor =vec4(0.5,0,0,1);" +
    }
    //"vec2 _Undistortion=vec2(-0.55,0.34);"+ //Cardboard Coefficients. OK
    //"vec2 _Undistortion=vec2(-0.441, 0.156f);"+ //Default library Coefficients. OK
    //red outside radius
    private String getVertexShaderDistorted(float[] distortionCoeficients){
        return "attribute vec4 aPosition;\n" +
                "attribute vec4 aTexCoord;\n" +
                "varying vec2 vTexCoord;\n" +
                "uniform mat4 uMVMatrix;" +
                "uniform mat4 uPMatrix;" +
                "float r2;"+
                "vec4 pos;"+
                "float ret;"+
                "varying float isInRange;"+
                "float _MaxRadSq="+distortionCoeficients[0]+";"+
                //There is no vec6 data type. Therefore, we use 1 vec4 and 1 vec2. Vec4 holds k1,k2,k3,k4 and vec6 holds k5,k6
                "vec4 _Undistortion=vec4("+distortionCoeficients[1]+","+distortionCoeficients[2]+","+distortionCoeficients[3]+","+distortionCoeficients[4]+");"+
                "vec2 _Undistortion2=vec2("+distortionCoeficients[5]+","+distortionCoeficients[6]+");"+
                "void main() {\n" +
                "  pos=uMVMatrix * aPosition;"+
                "  vTexCoord = (aTexCoord).xy;" +
                "  r2=clamp(dot(pos.xy,pos.xy)/(pos.z*pos.z),0.0,_MaxRadSq);"+
                //"  pos.xy *=1.0+(_Undistortion.x+_Undistortion.y*r2+_Undistortion.z*r2*r2+_Undistortion.w*r2*r2*r2)*r2;"+
                "ret = 0.0;"+
                "ret = r2 * (ret + _Undistortion2.y);"+
                "ret = r2 * (ret + _Undistortion2.x);"+
                "ret = r2 * (ret + _Undistortion.w);"+
                "ret = r2 * (ret + _Undistortion.z);"+
                "ret = r2 * (ret + _Undistortion.y);"+
                "ret = r2 * (ret + _Undistortion.x);"+
                "pos.xy*=1.0+ret;"+
                "  gl_Position=uPMatrix*pos;"+
                "if(r2==_MaxRadSq){isInRange=0.0;}else{isInRange=1.0;}"+
                "}";
    }


    private static String getFragmentShaderDistorted(){
        return "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;\n" +
                "varying vec2 vTexCoord;\n" +
                "varying float isInRange;"+
                "uniform samplerExternalOES sTexture;\n" +
                "void main() {\n" +
                "if(isInRange==1.0){"+
                "  gl_FragColor = texture2D(sTexture, vTexCoord);\n" +
                "}else{gl_FragColor = vec4(0.5,0,0,1);}"+
                //"  gl_FragColor =vec4(0.5,0,0,1);" +
                "}\n";
    }
    //end
    //slight blurr for greater values
    private String getVertexShaderDistorted2(float[] distortionCoeficients){
        return "attribute vec4 aPosition;\n" +
                "attribute vec4 aTexCoord;\n" +
                "varying vec2 vTexCoord;\n" +
                "uniform mat4 uMVMatrix;" +
                "uniform mat4 uPMatrix;" +
                "float r2;"+
                "vec4 pos;"+
                "float ret;"+
                "varying float isInRange;"+
                "float _MaxRadSq="+distortionCoeficients[0]+";"+
                //There is no vec6 data type. Therefore, we use 1 vec4 and 1 vec2. Vec4 holds k1,k2,k3,k4 and vec6 holds k5,k6
                "vec4 _Undistortion=vec4("+distortionCoeficients[1]+","+distortionCoeficients[2]+","+distortionCoeficients[3]+","+distortionCoeficients[4]+");"+
                "vec2 _Undistortion2=vec2("+distortionCoeficients[5]+","+distortionCoeficients[6]+");"+
                "void main() {\n" +
                "  pos=uMVMatrix * aPosition;"+
                "  vTexCoord = (aTexCoord).xy;" +
                "  r2=clamp(dot(pos.xy,pos.xy)/(pos.z*pos.z),0.0,_MaxRadSq);"+
                //"  pos.xy *=1.0+(_Undistortion.x+_Undistortion.y*r2+_Undistortion.z*r2*r2+_Undistortion.w*r2*r2*r2)*r2;"+
                "ret = 0.0;"+
                "ret = r2 * (ret + _Undistortion2.y);"+
                "ret = r2 * (ret + _Undistortion2.x);"+
                "ret = r2 * (ret + _Undistortion.w);"+
                "ret = r2 * (ret + _Undistortion.z);"+
                "ret = r2 * (ret + _Undistortion.y);"+
                "ret = r2 * (ret + _Undistortion.x);"+
                "pos.xy*=1.0+ret;"+
                "  gl_Position=uPMatrix*pos;"+
                "if(r2==_MaxRadSq){isInRange=0.0;}else{isInRange=1.0;}"+
                "}";
    }
    private static String getFragmentShaderDistorted2(){
        return "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;\n" +
                "varying vec2 vTexCoord;\n" +
                "varying float isInRange;"+
                "uniform samplerExternalOES sTexture;\n" +
                "void main() {\n" +
                "  gl_FragColor = texture2D(sTexture, vTexCoord);\n" +
                "if(isInRange==0.0){"+
                "gl_FragColor.xyz*=0.5;}"+
                //"  gl_FragColor =vec4(0.5,0,0,1);" +
                "}\n";
    }

    /*private class VsyncHelper implements Choreographer.FrameCallback{
        private static boolean RELEASE=false; //disables all debugging relevant code, but increases Performance
        private static boolean PRINT_FRAME_LOG=false;
        private static boolean PRINT_AVG_FRAME_LOG=true;
        private static boolean P_EYE_FAILED_WARNING=false;
        private static boolean P_FRAME_FAILED_WARNING=false;
        private static boolean P_AVG_FAILED=false;
        private static boolean P_VSYNC_WAIT_TIME=false;
        public static boolean RENDERING_IN_TIME=true;
        public static boolean SKIP_EYE=true;
        private int fc=0;
        private String frameLog;
        private String avgFrameLog;
        private Context mContext;
        private static double vsyncKullanz=0.5;
        public static double nanoToMs=0.000001; //10^-6
        public static double msToNano=(long)(1.0/nanoToMs);
        //Values in milliseconds. for exact timings use getTimeMs
    /*Change 02.01.2017: Values in nanoseconds/long*/
        /*public double FrameRT=10.0;  //Rendering a frame takes 10ms = 100fps
        public double EyeRT=0.5*FrameRT; //assume rendering a eye takes half as long as the whole frame
        public double FrameDT =16.666666; //a display Refresh Time of 16ms== 60fps Refresh rate; gets assigned with exact value in VsyncHelper()
        private long FrameDTNS=(long)(16.6666666*msToNano);
        public double EyeDT = FrameDT*0.5; //Half the display time
        public double nFrames=0,nFramesRF=0; //number of frames DT was smaller than rendering time (TEARING)
        public double nLeftEyesToR=0,nLeftEyeRF=0,nRightEyesToR=0,nRightEyeRF=0;
        private double leftEyeCPUTime=0,leftEyeGPUTime=0,rightEyeCPUTime=0,rightEyeGPUTime=0,frameTime=0;
        private double leftEyeCPUTimeSum=0,leftEyeGPUTimeSum=0,rightEyeCPUTimeSum=0,rightEyeGPUTimeSum=0,frameTimeSum=0;
        private double vsyncWaitTimeSum=0,vsyncWaitTimeC=0,vsyncMiddleWTSum=0,vsyncMiddleC=0;
        /*There are 2 Threads that will access the following variable :
        * 1)The OpenGL Render Thread
        * 2)The VSYNC Frame Callback
        * However,because the Callback only reads,and the Render Thread only writes, volatile is enough
        */
        /*private volatile long lastTimeVsyncOccuredNS=0;
        private long l,l2;
        //|-----------!-----------|
        //|           !           |
        //|           !           |   => Vsync Direction
        //|-----------!-----------| ->x-Direction (Direction the VSYNC travels along; we aren't interested in the VSYNC y pos.
        //   left Eye ! right Eye                  x ranges between 0 and 16.666
    /*double leftEyeVsyncMin;
    double leftEyeVsyncMax;
    double rightEyeVsyncMin;
    double rightEyeVsyncMax;
    double xDirVsyncTravelsPerMs;*/
        /*private double EyeDTMinusEyeRT;
        private double frameStart=0;
        private double leftEyeStart,rightEyeStart;
        private String gnuPlotS="#Elapsed Time|EyeCPUTime|EyeGPUTime|CPU+GPUTime";
        private double gnuElapsedTS=0;

        public VsyncHelper(Context context){
            mContext=context;
            Display d=((WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE)).getDefaultDisplay();
            float refreshRating=d.getRefreshRate();
            //double x=d.getPresentationDeadlineNanos();
            //System.out.println("X:"+x*nanoToMs);
            //System.out.println("refreshRate:"+refreshRating);
            FrameDT=1000.0/refreshRating;
            FrameDT=16.6666666666*1.0;
            FrameDTNS=(long)(16.6666666*msToNano);
            EyeDT=FrameDT*0.5;
            FrameRT=16.66666*1;
            EyeRT=FrameRT*0.5;
            EyeDTMinusEyeRT=EyeDT-EyeRT;
            frameStart=getTimeMs()*2;
            Choreographer.getInstance().postFrameCallback(this);
            gnuElapsedTS=System.currentTimeMillis();
        }
        public void onRenderingFrameStart(){
            frameStart=getTimeMs();
        }
        public void onRenderingFrameStop(){
            if(RELEASE){return;}
            nFrames++;
            frameTime=getTimeMs()-frameStart;
            frameTimeSum+=frameTime;
            if(frameTime>FrameDT+vsyncKullanz){
                nFramesRF++;
                if(P_FRAME_FAILED_WARNING){System.out.println("Rendering frame took too long. FT:"+frameTime+"ms"+
                        "  Number of failed frames:"+((int)nFramesRF)+"  %Frames failed:"+nFramesRF/nFrames*100);
                }
            }
            fc++;
            if(fc>=60){
                if(P_AVG_FAILED) {
                    System.out.println("*xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx*\n" +
                            "%Frames failed:" + nFramesRF / (nFrames) * 100 + "\n" +
                            "%Right Eyes Failed:" + nRightEyeRF / nRightEyesToR * 100 + "\n" +
                            "%Left Eyes Failed:" + nLeftEyeRF / nLeftEyesToR * 100 +"\n"+
                            //"%Eyes CPU failed"+nEyesCPUTooSLow/(nRightEyes+nLeftEyes)*100+
                            "\n*xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx*"
                    );
                }
                if(PRINT_AVG_FRAME_LOG){
                    avgFrameLog="*------------------------------------------------------------------*\n"+
                            "Avg. RightEye CPU Time:"+rightEyeCPUTimeSum/nRightEyesToR+" "+
                            "Avg. RightEye GPU Time"+rightEyeGPUTimeSum/nRightEyesToR+
                            "\nAvg. LeftEye CPU Time:"+leftEyeCPUTimeSum/nLeftEyesToR+" "+
                            "Avg. LeftEye GPU Time:"+leftEyeGPUTimeSum/nLeftEyesToR+
                            "\nAvg. FrameTime:"+frameTimeSum/nFrames+
                            "\nAvg. VSYNC Wait Time:"+vsyncWaitTimeSum/vsyncWaitTimeC+
                            "\nAvg.vsyncMiddle Wait Time:"+vsyncMiddleWTSum/vsyncMiddleC+
                            "\n*------------------------------------------------------------------*";
                    System.out.println(avgFrameLog);
                }
                fc=0;
            }
            if(PRINT_FRAME_LOG){
                frameLog="*------------------------------------------------------------------*\n";
                frameLog+="\nRightEye CPU Time:"+rightEyeCPUTime;
                frameLog+="\nRightEye GPU Time"+rightEyeGPUTime;
                frameLog+="\nLeftEye CPU Time:"+leftEyeCPUTime;
                frameLog+="\nLeftEye GPU Time"+leftEyeGPUTime;
                frameLog+="\nFrameCPUGPUTime"+frameTime;
                frameLog+="\n*------------------------------------------------------------------*";
                System.out.println(frameLog);
            }
        }

        public void onLeftEyeRenderingStart(){leftEyeStart=getTimeMs();}
        public void onRightEyeRenderingStart(){
            rightEyeStart=getTimeMs();
        }
        public void onRightEyeCPUStop(){
            if(RELEASE){return;}
            rightEyeCPUTime=getTimeMs()-rightEyeStart;
            rightEyeCPUTimeSum+=rightEyeCPUTime;
        }
        public void onRightEyeGPUStop(){
            if(RELEASE){return;}
            nRightEyesToR++;
            rightEyeGPUTime=getTimeMs()-rightEyeStart-rightEyeCPUTime;
            rightEyeGPUTimeSum+=rightEyeGPUTime;
            if(rightEyeCPUTime+rightEyeGPUTime>EyeRT){
                nRightEyeRF++;
                if( P_EYE_FAILED_WARNING){System.out.println("Calc&Rendering right eye took too long. RT:"+(rightEyeCPUTime+rightEyeGPUTime)+"ms"+
                        "  %Right Eyes Failed:"+nRightEyeRF/nRightEyesToR*100);
                }
            }
        /*xxxxxxxxxxxxxxxxxxxxxxxx
        gnuPlotS+="\n#right Eye\n";
        gnuPlotS+="\n"+(System.currentTimeMillis()-gnuElapsedTS);
        gnuPlotS+=" "+rightEyeCPUTime;
        gnuPlotS+=" "+rightEyeGPUTime;
        gnuPlotS+=" "+(rightEyeCPUTime+rightEyeGPUTime);*/

        /*}
        public void onLeftEyeCPUStop(){
            if(RELEASE){return;}
            leftEyeCPUTime=getTimeMs()-leftEyeStart;
            leftEyeCPUTimeSum+=leftEyeCPUTime;
        }
        public void onLeftEyeGPUStop(){
            if(RELEASE){return;}
            nLeftEyesToR++;
            leftEyeGPUTime=getTimeMs()-leftEyeStart-leftEyeCPUTime;
            leftEyeGPUTimeSum+=leftEyeGPUTime;
            if(leftEyeCPUTime+leftEyeGPUTime>EyeRT){
                nLeftEyeRF++;
                if( P_EYE_FAILED_WARNING){System.out.println("Calc&Rendering left eye took too long. RT:"+(leftEyeCPUTime+leftEyeGPUTime)+"ms"+
                        "  %Left Eyes Failed:"+nLeftEyeRF/nLeftEyesToR*100);
                }
            }
        /*xxxxxxxxxxxxxxxxxxxxxxxx
        gnuPlotS+="\n#left Eye\n";
        gnuPlotS+="\n"+(System.currentTimeMillis()-gnuElapsedTS);
        gnuPlotS+=" "+rightEyeCPUTime;
        gnuPlotS+=" "+rightEyeGPUTime;
        gnuPlotS+=" "+(rightEyeCPUTime+rightEyeGPUTime);
        if((System.currentTimeMillis()-gnuElapsedTS)>4000&&(System.currentTimeMillis()-gnuElapsedTS)<4400){
            System.out.println(gnuPlotS);
            Log.d("",gnuPlotS);
        }*/

        }
        /*wait until the cpu creation & rendering will just finish in Time; needs really accurate predictions and thread btw. clock speed controll*/
        /*public void waitUntilLastNSRightEye(){
            double timeWeHaveToStartRendering=frameStart+EyeDTMinusEyeRT;
            if(false){
                System.out.println("timeOffset we waited for rightEye:"+(timeWeHaveToStartRendering-getTimeMs()));
            }
            while(getTimeMs()<timeWeHaveToStartRendering){
                //wait;
            }
        }
        public void waitUntilLastNSLeftEye(){
            double timeWeHaveToStartRendering=frameStart+EyeDT+EyeDTMinusEyeRT;
            if(false){
                System.out.println("timeOffset we waited for leftEye:"+(timeWeHaveToStartRendering-getTimeMs()));
            }
            while(getTimeMs()<timeWeHaveToStartRendering){
                //wait;
            }
        }
        public boolean waitUntilVsyncMiddle(){
            if(getVsyncPosX()>(FrameDT/2.0)){
                return SKIP_EYE;
            }
            double ts=getTimeMs();
            while (getVsyncPosX()<(FrameDT/2.0)){
                //wait
            }
            vsyncMiddleWTSum+=getTimeMs()-ts;
            vsyncMiddleC++;
            return !SKIP_EYE;
        }
        public void waitUntilVsyncStart(){
            //System.out.println("n left eyes skipped"+nLeftEyesSkipped+" n right eyes skipped:"+nRightEyesSkipped);
            double ts=getTimeMs();
            while (getVsyncPosX()>0.5){
                //
            }
            double time=getTimeMs()-ts;
            if(P_VSYNC_WAIT_TIME){
                System.out.println("time waiting for new vsync:"+time);
            }
            vsyncWaitTimeSum+=time;
            vsyncWaitTimeC++;
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
        /*@Override
        public void doFrame(long l){
            //System.out.println("VSYNC");
            lastTimeVsyncOccuredNS=l;
            Choreographer.getInstance().postFrameCallback(this);
        }
        public double getVsyncPosX(){
            l=lastTimeVsyncOccuredNS;
            //the CPU takes at least 1ms; makes Application more tearing resistent,but adds as many lag
            //l-=1*msToNano;
            //l+=8*msToNano;
            //l-=(FrameDTNS-3*msToNano); //3ms seems to be the max. vsync offset to reduce lag without tearing
            //assume crating&rendering never takes longer than 8.3-3=5.3ms;
            l2=System.nanoTime()-l;
            //int c=0;
            while (l2>=FrameDTNS){
                //c++;
                l2-=FrameDTNS;
            }
            //System.out.println("C:"+c);
            //System.out.println(""+(l2*nanoToMs));
            return (l2*nanoToMs);
        }

    }

}*/
