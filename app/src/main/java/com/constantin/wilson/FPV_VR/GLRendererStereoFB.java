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
import android.view.Surface;
import android.widget.Toast;

import com.google.vrtoolkit.cardboard.PhoneParams;
import com.google.vrtoolkit.cardboard.proto.Phone;
import com.google.vrtoolkit.cardboard.sensors.HeadTracker;

import java.nio.FloatBuffer;

import static android.content.Context.MODE_PRIVATE;

/*Rendering thread for front buffer rendering*/

public class GLRendererStereoFB implements GLSurfaceViewFB.IRendererEGL14/*,SurfaceTexture.OnFrameAvailableListener*/{
    private static final int QCOM_TILED=0,QCOM_BUT_NO_TILING=1,NO_QCOM=2;
    private static int GPU_MODE; //GPUMode of 0: QCOM tiled rendering (works perfectly)| 1:QCOM render direct to fb without tiling (works okay)| 2: No QCOM chipset, normal finish with glClear(not tested)
    public volatile boolean running;
    private boolean firstTime=true;
    private float distortionFactor;
    private UdpVideoReceiver mVideoReceiver;
    private HeadTracker mHeadTracker;
    private OSDReceiverRenderer mOSD;
    private SharedPreferences settings,phoneInfo;
    private GLProgramTexEx mGLProgramTexEx;
    private Context mContext;
    private CameraGimbalTracker mCGTracker;
    private VsyncHelper mVsyncHelper;
    private GLProgramColorClear mOGLProgramColorClear;
    private float[] mRightEyeViewM=new float[16];
    private float[] mLeftEyeViewM=new float[16];
    private float[] mProjM=new float[16];
    //private float[] mLeftEyeMVPM=new float[16];
    //private float[] mRightEyeMVPM=new float[16];
    private float[] tempEyeViewM=new float[16];
    private float[] mLeftEyeTranslate=new float[16];
    private float[] mRightEyeTranslate=new float[16];
    private int[] textures = new int[2];
    //public static boolean next_frame=true;
    private boolean osd=true;
    private boolean distortionCorrection=false;
    private boolean externalTextureDoubleBuffering=false;
    private float videoFormat=1.3333f;
    private float modelDistance =3.0f;
    private float videoDistance =5.7f;
    private float interpupilarryDistance=0.2f;
    private float viewportScale=1.0f;
    private int tesselationFactor=1; //tesselation of 2 means 4 quads;
    private int numberCanvasQuads =tesselationFactor*tesselationFactor;
    private boolean VRWorldTracking=true;
    private boolean CameraGimbalTracking=false;
    private int buffers[]=new int[1];
    private int mDisplay_x,mDisplay_y;
    private int leftViewportWidth;
    private int leftViewPortHeight;
    private int leftViewportX;
    private int leftViewPortY;
    private int rightViewportWidth;
    private int rightViewportHeight;
    private int rightViewportX;
    private int rightViewportY;
    private SurfaceTexture mSurfaceTexture;
    private Surface mDecoderSurface;
    //private boolean updateSurface = false;
    private float zaehlerFramerate=0;
    private double timeb=0;
    private double fps=0;
    private boolean thisEye;
    private int[] result=new int[1];
    private int[] eh=new int[1];
    private int iteration=0;
    private boolean OSDOnTopOfVideo;
    private DistortionData mDistortionData;

    public GLRendererStereoFB(Context context){
        mContext =context;
        settings= PreferenceManager.getDefaultSharedPreferences(mContext);
        phoneInfo = context.getSharedPreferences("phoneinfo", MODE_PRIVATE);
        mDistortionData=new DistortionData(settings);
        try{
            videoFormat=Float.parseFloat(settings.getString("videoFormat","1.3333"));
        }catch(Exception e){e.printStackTrace();}
        osd=settings.getBoolean("osd", true);
        externalTextureDoubleBuffering=settings.getBoolean("externalTextureDoubleBuffering",false);
        try{
            videoDistance =Float.parseFloat(settings.getString("videoDistance","5.7"));
        }catch (Exception e){e.printStackTrace();
            videoDistance =5.7f;}
        if(videoDistance >14.99999f || videoDistance <1){
            videoDistance =5.7f;}
        try{
            distortionFactor =Float.parseFloat(settings.getString("distortionFactor","0.15"));
        }catch (Exception e){e.printStackTrace();
            distortionFactor =0.15f;}
        try{
            interpupilarryDistance=Float.parseFloat(settings.getString("interpupillaryDistance","0.2"));
        }catch (Exception e){e.printStackTrace();interpupilarryDistance=0.2f;}
        if(interpupilarryDistance>=1 || interpupilarryDistance<0.0f){interpupilarryDistance=0.2f;}
        try{
            modelDistance =Float.parseFloat(settings.getString("modelDistance","3.0"));
        }catch (Exception e){e.printStackTrace();modelDistance =3.0f;}
        if(modelDistance >14.99999f || modelDistance <2){modelDistance =3.0f;}
        try{
            viewportScale =Float.parseFloat(settings.getString("viewportScale","1.0"));
        }catch (Exception e){e.printStackTrace();viewportScale=1.0f;}
        if(viewportScale>1 || viewportScale <=0){viewportScale=1.0f;}
        VRWorldTracking=settings.getBoolean("VRWorldTracking", false);
        OSDOnTopOfVideo=settings.getBoolean("OSDOnTopOfVideo", false);
        CameraGimbalTracking=settings.getBoolean("CameraGimbalTracking",false);
        distortionCorrection=settings.getBoolean("distortionCorrection", true);
        try{
            tesselationFactor =(int)Float.parseFloat(settings.getString("tesselationFactor","1.0"));
            if(tesselationFactor<1){tesselationFactor=1;}
        }catch (Exception e){tesselationFactor=1;}
        if(distortionCorrection&&(tesselationFactor<20)){tesselationFactor=20;}
        if(!distortionCorrection&&tesselationFactor>1){tesselationFactor=1;}
        numberCanvasQuads =tesselationFactor*tesselationFactor;
        if(phoneInfo.getBoolean("GL_QCOM_tiled_rendering",false)){
            //The GPU supports tiled rendering extension. enable QCOM Tiled rendering
            GPU_MODE=QCOM_TILED;
        }else {
            if(phoneInfo.getBoolean("QCOM",false)){
                //QCOM GPU, but no tiling extension. Enable render direct to fb and clearSpecial
                GPU_MODE=QCOM_BUT_NO_TILING;
            }else {
                //For Exynos,IMGTEC GPU's.Not tested yet.
                GPU_MODE=NO_QCOM;
            }
        }
        mVsyncHelper=new VsyncHelper(mContext);
    }

    @Override
    public void onSurfaceCreated()
    {
        GLES20.glClearColor(0.0f, 0.0f, 0.07f, 0.0f);
        GLES20.glGenBuffers(1, buffers, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
                GLHelper.getFloatBuffer(GeometryHelper.getCanvasVert(videoFormat, videoDistance,tesselationFactor)).capacity() * 4,
                GLHelper.getFloatBuffer(GeometryHelper.getCanvasVert(videoFormat, videoDistance,tesselationFactor)),
                GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        mOGLProgramColorClear=new GLProgramColorClear();
        //we have to create the texture for the overdraw,too
        GLES20.glGenTextures(2, textures, 0);
        if(distortionCorrection){
            mGLProgramTexEx =new GLProgramTexEx(textures[0], GLProgramTexEx.MODE_STEREO_DISTORTED,mDistortionData);
        }else {
            mGLProgramTexEx =new GLProgramTexEx(textures[0], GLProgramTexEx.MODE_STEREO,mDistortionData);
        }
        //mSurfaceTexture = new SurfaceTexture(mTextureID);
        //Enable double buffering,because MediaCodec and OpenGL don't have any synchronisation ?
        //For me,it seems like db hasn't really implemented or has no effect;
        //disable ,because at least it should (not tested) add lag
        //mSurfaceTexture = new SurfaceTexture(mGLProgramTexEx.textureId,false);
        mSurfaceTexture = new SurfaceTexture(mGLProgramTexEx.textureId,externalTextureDoubleBuffering);
        //mSurfaceTexture.setOnFrameAvailableListener(this);
        mDecoderSurface=new Surface(mSurfaceTexture);
        mVideoReceiver =new UdpVideoReceiver(mDecoderSurface,5000, mContext);
        mVideoReceiver.startDecoding();
        if(distortionCorrection){
            mOSD=new OSDReceiverRenderer(mContext,textures[1],mProjM,videoFormat, modelDistance,
                    videoDistance, GLProgramTexEx.MODE_STEREO_DISTORTED,OSDOnTopOfVideo,0,mDistortionData);
        }else{
            mOSD=new OSDReceiverRenderer(mContext,textures[1],mProjM,videoFormat, modelDistance,
                    videoDistance, GLProgramTexEx.MODE_STEREO,OSDOnTopOfVideo,0,mDistortionData);
        }
        mOSD.startReceiving();
        if (VRWorldTracking) {
            mHeadTracker=HeadTracker.createFromContext(mContext);
            mHeadTracker.setNeckModelEnabled(true);
            final Phone.PhoneParams phoneParams = PhoneParams.readFromExternalStorage();
            if (phoneParams != null) {
                this.mHeadTracker.setGyroBias(phoneParams.gyroBias);
            }
            mHeadTracker.startTracking();
        }
        if(CameraGimbalTracking){
            if(VRWorldTracking){
                makeToast("You mustn't enable VRWorld tracking and Camera Gimbal Tracking at the same time; Disabling Camera Gimbal Tracking");
                CameraGimbalTracking=false;
            }else {
                mCGTracker=new CameraGimbalTracker(mContext);
                mCGTracker.startSending();
            }
        }
        if(GPU_MODE==QCOM_TILED){
            QCOMHelper.TilingPrepare();
        }
        /*on FPV_VR without decoder running it first has an negative effect,
        * but after some time it has a really positive effect häh ? (enabled: fps after some seconds: 180)
        * but it fluctuates a lot so i can't be sure*/
        /*has a positive effect*/
        if(GPU_MODE==QCOM_BUT_NO_TILING){
            //GLES20.glEnable(QCOMHelper.GL_BINNING_CONTROL_HINT_QCOM);
            //GLES20.glHint(QCOMHelper.GL_BINNING_CONTROL_HINT_QCOM,QCOMHelper.GL_RENDER_DIRECT_TO_FRAMEBUFFER_QCOM);
            GLES20.glEnable(QCOMHelper.WRITEONLY_RENDERING_QCOM);
        }
        //GLES20.glEnable(QCOMHelper.WRITEONLY_RENDERING_QCOM);
        //GLES20.glHint(QCOMHelper.GL_BINNING_CONTROL_HINT_QCOM,QCOMHelper.GL_GPU_OPTIMIZED_QCOM);
        //GLES20.glHint(QCOMHelper.GL_BINNING_CONTROL_HINT_QCOM,QCOMHelper.GL_RENDER_DIRECT_TO_FRAMEBUFFER_QCOM);
        //GLES20.glHint(QCOMHelper.GL_BINNING_CONTROL_HINT_QCOM,QCOMHelper.GL_CPU_OPTIMIZED_QCOM);
        /*GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glDepthMask(true);*/
        //GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
    }

    @Override
    public void onSurfaceChanged(int width, int height){
        //Setup Matrices
        float ratio = (float) (width / 2) / height;
        Matrix.frustumM(mProjM, 0, -ratio, ratio, -1, 1, 1.0f, 15.0f);
        Matrix.setLookAtM(mLeftEyeViewM, 0, -(interpupilarryDistance / 2), 0.0f, 0.0f, 0.0f, 0.0f, -12, 0.0f, 1.0f, 0.0f);
        Matrix.setLookAtM(mRightEyeViewM, 0, (interpupilarryDistance / 2), 0.0f, 0.0f, 0.0f, 0.0f, - 12, 0.0f, 1.0f, 0.0f);

        Matrix.setIdentityM(mLeftEyeTranslate, 0);
        Matrix.setIdentityM(mRightEyeTranslate, 0);
        Matrix.translateM(mLeftEyeTranslate, 0, (interpupilarryDistance / 2), 0.0f, 0);
        Matrix.translateM(mRightEyeTranslate, 0, -(interpupilarryDistance / 2), 0.0f, 0);
        GLES20.glViewport(0, 0, width, height);
        mDisplay_x=width;
        mDisplay_y=height;
        leftViewportWidth=(int)((mDisplay_x/2*viewportScale));
        leftViewPortHeight=(int)((mDisplay_y*viewportScale));
        leftViewportX=(int)(((mDisplay_x/2)-leftViewportWidth)/2);
        leftViewPortY=(int)((mDisplay_y-leftViewPortHeight)/2);
        rightViewportWidth=leftViewportWidth;
        rightViewportHeight=leftViewPortHeight;
        rightViewportX=(mDisplay_x/2)+leftViewportX;
        rightViewportY=leftViewPortY;

    }

    @Override
    public void onDrawFrame() {
        running=true;
        /*EGL14.eglSurfaceAttrib(EGL14.eglGetCurrentDisplay(),EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW),
                EGL14.EGL_SURFACE_TYPE,EGL14.EGL_SINGLE_BUFFER);
        EGL14.eglSwapBuffers(EGL14.eglGetCurrentDisplay(),EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW));*/
        if(!EGL14.eglSurfaceAttrib(EGL14.eglGetCurrentDisplay(),EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW),
                QCOMHelper.EGL_FRONT_BUFFER_AUTO_REFRESH_ANDROID, EGL14.EGL_TRUE)) {
            System.out.println("Cannot set front buffer Auto refresh");
        }
        //Swap Once (first Frame) and return; needed for initialisations
        if(firstTime){
            firstTime=false;
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            EGL14.eglSwapBuffers(EGL14.eglGetCurrentDisplay(),EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW));
            return;
        }
        OSDReceiverRenderer.setAffinity(2); //core 3
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        Process.setThreadPriority(-20);//Highest linux priority;
        System.out.println("process Priority:"+Process.getThreadPriority(Process.myTid()));
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            int[] cores=Process.getExclusiveCores();
            System.out.println("Cores:"+cores.length);
        }*/
        checkVideoRatio();
        while (running){
            drawBinocularFrameInSync();
        }
    }

    private void drawBinocularFrameInSync(){
        mVsyncHelper.onFrameStart();
        mVsyncHelper.waitUntilVsyncStart();
        mVsyncHelper.onRightEyeRenderingStart();
        drawRightEye(); //Creates Rendering Data on cpu side
        mVsyncHelper.onRightEyeCPUStop();
        mSurfaceTexture.updateTexImage();
        switch (GPU_MODE){
            case QCOM_TILED: QCOMHelper.glEndTilingQCOM();break;
            case QCOM_BUT_NO_TILING: GLES20.glFinish();break;
            case NO_QCOM:GLES20.glFinish();break;
        }
        //now the GPU finished rendering
        mVsyncHelper.onRightEyeGPUStop();
        thisEye=mVsyncHelper.waitUntilVsyncMiddle();
        if(thisEye!=VsyncHelper.SKIP_EYE){
            /*we can render the left eye. If the last eye didn't finish in time, we skip it,and draw the other one instead*/
            mVsyncHelper.onLeftEyeRenderingStart();
            if(osd) {mOSD.setupModelMatrices();}
            drawLeftEye();
            mVsyncHelper.onLeftEyeCPUStop();
            mSurfaceTexture.updateTexImage();
            switch (GPU_MODE){
                case QCOM_TILED: QCOMHelper.glEndTilingQCOM();break;
                case QCOM_BUT_NO_TILING: GLES20.glFinish();break;
                case NO_QCOM:GLES20.glFinish();break;
            }
            mVsyncHelper.onLeftEyeGPUStop();
        }
        plotFPSAndCheck();
        /*if(running){
            EGL14.eglSwapBuffers(EGL14.eglGetCurrentDisplay(),EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW));
        }*/
        mVsyncHelper.onFrameStop();
    }

    private void drawRightEye(){
        GLES20.glViewport(rightViewportX, rightViewportY, rightViewportWidth, rightViewportHeight);
        switch (GPU_MODE){
            case QCOM_TILED:
                QCOMHelper.glStartTilingQCOM(rightViewportX, rightViewportY, rightViewportWidth, rightViewportHeight);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_STENCIL_BUFFER_BIT);
                mOSD.setupModelMatrices();break;
            case QCOM_BUT_NO_TILING:
                GLES20.glScissor(rightViewportX, rightViewportY, rightViewportWidth, rightViewportHeight);
                clearSpecial();
                mOSD.setupModelMatrices();break;
            case NO_QCOM:
                GLES20.glScissor(rightViewportX, rightViewportY, rightViewportWidth, rightViewportHeight);
                GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT| GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_STENCIL_BUFFER_BIT);
                mOSD.setupModelMatrices();break;
        }
        updateHeadPos();
        mGLProgramTexEx.beforeDraw(buffers[0]);
        mGLProgramTexEx.draw(mRightEyeViewM,mProjM, numberCanvasQuads*6);
        mGLProgramTexEx.afterDraw();
        if(osd){mOSD.drawRightEye(mRightEyeViewM);}
    }
    private void drawLeftEye(){
        GLES20.glViewport(leftViewportX, leftViewPortY, leftViewportWidth, leftViewPortHeight);
        switch (GPU_MODE){
            case QCOM_TILED:
                QCOMHelper.glStartTilingQCOM(leftViewportX, leftViewPortY, leftViewportWidth, leftViewPortHeight);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_STENCIL_BUFFER_BIT);
                mOSD.updateOverlayUnit();break;
            case QCOM_BUT_NO_TILING:
                GLES20.glScissor(leftViewportX, leftViewPortY, leftViewportWidth, leftViewPortHeight);
                clearSpecial();
                mOSD.updateOverlayUnit();break;
            case NO_QCOM:
                GLES20.glScissor(leftViewportX, leftViewPortY, leftViewportWidth, leftViewPortHeight);
                GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT| GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_STENCIL_BUFFER_BIT);
                mOSD.updateOverlayUnit();break;
        }
        updateHeadPos();
        mGLProgramTexEx.beforeDraw(buffers[0]);
        mGLProgramTexEx.draw(mLeftEyeViewM,mProjM, numberCanvasQuads*6);
        mGLProgramTexEx.afterDraw();
        if(osd){mOSD.drawLeftEye(mLeftEyeViewM);}
    }
    private void updateHeadPos(){
        if(VRWorldTracking && mHeadTracker!=null){
            mHeadTracker.getLastHeadView(tempEyeViewM, 0);
            Matrix.multiplyMM(mLeftEyeViewM, 0,  mLeftEyeTranslate,  0, tempEyeViewM, 0);
            Matrix.multiplyMM(mRightEyeViewM, 0, mRightEyeTranslate, 0, tempEyeViewM, 0);
        }
    }
    private void clearSpecial(){
        mOGLProgramColorClear.beforeDraw();
        mOGLProgramColorClear.draw();
        mOGLProgramColorClear.afterDraw();
    }
    private void finishFenceAndDoData(){
        eh[0]=result.length;
        result[0]=GLES30.GL_UNSIGNALED;
        long sync=GLES30.glFenceSync(GLES30.GL_SYNC_GPU_COMMANDS_COMPLETE,0);
        iteration=0;
        while(result[0]!=GLES30.GL_SIGNALED){
            /*here we can do some gl stuff. Be careful:only small work at each iteration*/
            if(iteration==0){if(osd) {mOSD.updateOverlayUnit();}}//takes bit longer;experimental;includes GL calls
            if(iteration==1){if(osd){mOSD.setupModelMatrices();}}//takes max 2ms at 1Ghz
            /*overall,probably takes no longer than 4ms*/
            GLES30.glGetSynciv(sync,GLES30.GL_SYNC_STATUS/*pname*/,
                    4*8/*buffsize*/,
                    eh/*int[] length*/, 0/*lengthOffset*/,
                    result/*values*/,0/*valuesOffset*/);
            //System.out.println("unsignalled"+result[0]);
            iteration++;
        }
        GLES30.glDeleteSync(sync);
    }

    public void onSurfaceDestroyed() {
        if(mOSD!=null){
            mOSD.stopReceiving();
        }
        if(mVideoReceiver !=null){
            mVideoReceiver.stopDecoding();
        }
        if(mHeadTracker!=null){
            mHeadTracker.stopTracking();
        }
        if(mCGTracker!=null){
            mCGTracker.stopSending();
        }
        mOSD=null;
        mVideoReceiver=null;
        mHeadTracker=null;
        mCGTracker=null;
        //System.out.println("Hello from SurfaceDestroyed");
    }
    private void plotFPSAndCheck(){
        zaehlerFramerate++;
        if((System.currentTimeMillis()-timeb)>1000) {
            fps = (double) (zaehlerFramerate / ((System.currentTimeMillis()-timeb)*0.001));
            System.out.println("OpenGL fps:" + fps);
            if (fps > 3 && mVideoReceiver != null && mOSD != null) {
                mVideoReceiver.tellOpenGLFps((long) fps);
                mOSD.mDecoder_fps = mVideoReceiver.getDecoderFps();
                mOSD.mOpenGL_fps = (int) fps;
            }
            timeb = System.currentTimeMillis();
            zaehlerFramerate = 0;
            checkVideoRatio();
        }
    }
    private void checkVideoRatio(){
        if(mVideoReceiver.videoRatioChanged()){
            videoFormat=mVideoReceiver.getVideoRatio();
            makeToast("Video Ratio Changed");
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
            FloatBuffer fb= GLHelper.getFloatBuffer(GeometryHelper.getCanvasVert(videoFormat, videoDistance,tesselationFactor));
            fb.position(0);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, fb.capacity() * 4, fb,
                    GLES20.GL_STATIC_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
            mOSD.changeOSDVideoRatio(videoFormat);
            mVideoReceiver.setLastVideoRatio(videoFormat);
        }
    }
    /*@Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        System.out.println("HELLO AVAILABLE");
        //hello_available=true;
    }*/
    private void makeToast(final String message) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onDestroy() {
    }
    public void onTap(){
        if(mVideoReceiver !=null){
            mVideoReceiver.next_frame=true;
            /*
            angle_z+=10;
            System.out.println("Angle_z"+angle_z);*/
        }
    }
}


