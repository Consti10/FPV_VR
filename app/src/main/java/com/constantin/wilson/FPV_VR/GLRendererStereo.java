package com.constantin.wilson.FPV_VR;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLExt;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.preference.PreferenceManager;
import android.view.Surface;
import android.widget.Toast;

import com.google.vrtoolkit.cardboard.PhoneParams;
import com.google.vrtoolkit.cardboard.proto.Phone;
import com.google.vrtoolkit.cardboard.sensors.HeadTracker;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/*
* Render Modes: Side by Side and one View. */
public class GLRendererStereo implements GLSurfaceView.Renderer{
    private UdpVideoReceiver mVideoReceiver;
    private HeadTracker mHeadTracker;
    private OSDReceiverRenderer mOSD;
    private SharedPreferences settings;
    private GLProgramTexEx mGLProgramTexEx;
    private Context mContext;
    private CameraGimbalTracker mCGTracker;
    private float[] mRightEyeViewM=new float[16];
    private float[] mLeftEyeViewM=new float[16];
    private float[] mProjM=new float[16];
    private float[] tempEyeViewM=new float[16];
    private float[] mLeftEyeTranslate=new float[16];
    private float[] mRightEyeTranslate=new float[16];
    private int[] textures = new int[2];
    private float distortionFactor;
    //public static boolean next_frame=true;
    private boolean presentationTime;
    private boolean swapIntervallZero=true;
    private boolean osd=true;
    private boolean clearFramebuffer=false;
    private boolean distortionCorrection=false;
    private boolean externalTextureDoubleBuffering=false;
    private float videoFormat=1.3333f;
    private float modelDistance =3.0f;
    private float videoDistance =5.7f;
    private float interpupilarryDistance=0.2f;
    private float viewportScale=1.0f;
    private int tesselationFactor=1; //tesselation of 2 means 4 quads;
    private int numberCanvasQuads=tesselationFactor*tesselationFactor;
    private boolean VRWorldTracking=false;
    private boolean CameraGimbalTracking=false;
    private boolean OSDOnTopOfVideo;

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
    private double timeInit;
    private boolean FULL_VERSION=true;
    //private VsyncHelper mVsyncHelper;
    private boolean hello_available=false;
    private DistortionData mDistortionData;

    public GLRendererStereo(Context context){
        mContext =context;
        settings= PreferenceManager.getDefaultSharedPreferences(mContext);
        mDistortionData=new DistortionData(settings);
        try{
            videoFormat=Float.parseFloat(settings.getString("videoFormat","1.3333"));
        }catch(Exception e){e.printStackTrace();}
        presentationTime=settings.getBoolean("presentationTime", true);
        osd=settings.getBoolean("osd", true);
        swapIntervallZero=settings.getBoolean("swapIntervallZero",false);
        clearFramebuffer=settings.getBoolean("clearFramebuffer",true);
        externalTextureDoubleBuffering=settings.getBoolean("externalTextureDoubleBuffering",false);
        try{
            videoDistance =Float.parseFloat(settings.getString("videoDistance","5.7"));
        }catch (Exception e){e.printStackTrace();
            videoDistance =5.7f;}
        try{
            distortionFactor =Float.parseFloat(settings.getString("distortionFactor","0.15"));
        }catch (Exception e){e.printStackTrace();
            distortionFactor =0.15f;}
        if(videoDistance >14.99999f || videoDistance <1){
            videoDistance =5.7f;}
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
        CameraGimbalTracking=settings.getBoolean("CameraGimbalTracking",false);
        distortionCorrection=settings.getBoolean("distortionCorrection", true);
        try{
            tesselationFactor =(int)Float.parseFloat(settings.getString("tesselationFactor","1.0"));
            if(tesselationFactor<1){tesselationFactor=1;}
        }catch (Exception e){tesselationFactor=1;}
        if(distortionCorrection&&(tesselationFactor<20)){tesselationFactor=20;}
        if(!distortionCorrection&&tesselationFactor>1){tesselationFactor=1;}
        numberCanvasQuads=tesselationFactor*tesselationFactor;
        OSDOnTopOfVideo=settings.getBoolean("OSDOnTopOfVideo", false);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        GLES20.glClearColor(0.0f, 0.0f, 0.07f, 0.0f);
        GLES20.glGenBuffers(1, buffers, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        FloatBuffer fb= GLHelper.getFloatBuffer(GeometryHelper.getCanvasVert(videoFormat, videoDistance,tesselationFactor));
        fb.position(0);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, fb.capacity() * 4, fb,
                GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        //we have to create the texture for the overdraw,too
        GLES20.glGenTextures(2, textures, 0);
        if(distortionCorrection){
            mGLProgramTexEx =new GLProgramTexEx(textures[0], GLProgramTexEx.MODE_STEREO_DISTORTED,mDistortionData);
        }else {
            mGLProgramTexEx =new GLProgramTexEx(textures[0], GLProgramTexEx.MODE_STEREO,null);
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
                    videoDistance, GLProgramTexEx.MODE_STEREO_DISTORTED,OSDOnTopOfVideo,0.0f,mDistortionData);
        }else{
            mOSD=new OSDReceiverRenderer(mContext,textures[1],mProjM,videoFormat, modelDistance,
                    videoDistance, GLProgramTexEx.MODE_STEREO,OSDOnTopOfVideo,0.0f,mDistortionData);
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
        if(swapIntervallZero){
            EGL14.eglSwapInterval(EGL14.eglGetCurrentDisplay(), 0);
        }
        timeInit=System.currentTimeMillis();
        //GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height){
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
        //mVsyncHelper=new VsyncHelper(mContext);

    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        checkVideoRatio();
        /*EGL14.eglSurfaceAttrib(EGL14.eglGetCurrentDisplay(),EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW),
                EGL14.EGL_SURFACE_TYPE,EGL14.EGL_SINGLE_BUFFER);**/
        if(presentationTime){
            EGLExt.eglPresentationTimeANDROID(EGL14.eglGetCurrentDisplay(),EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW),System.nanoTime());
        }
        if(VRWorldTracking && mHeadTracker!=null){
            mHeadTracker.getLastHeadView(tempEyeViewM, 0);
            Matrix.multiplyMM(mLeftEyeViewM, 0,  mLeftEyeTranslate,  0, tempEyeViewM, 0);
            Matrix.multiplyMM(mRightEyeViewM, 0, mRightEyeTranslate, 0, tempEyeViewM, 0);
        }
        if(osd) {
            mOSD.setupModelMatrices();
            mOSD.updateOverlayUnit();
        }
        if(clearFramebuffer){
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);
        }
        mSurfaceTexture.updateTexImage();
        //draw left eye
        GLES20.glViewport(leftViewportX, leftViewPortY, leftViewportWidth, leftViewPortHeight);
        mGLProgramTexEx.beforeDraw(buffers[0]);
        mGLProgramTexEx.draw(mLeftEyeViewM,mProjM,numberCanvasQuads*6);
        mGLProgramTexEx.afterDraw();
        if(osd){mOSD.drawLeftEye(mLeftEyeViewM);}
        //draw right eye
        GLES20.glViewport(rightViewportX, rightViewportY, rightViewportWidth, rightViewportHeight);
        mGLProgramTexEx.beforeDraw(buffers[0]);
        mGLProgramTexEx.draw(mRightEyeViewM,mProjM,numberCanvasQuads*6);
        mGLProgramTexEx.afterDraw();
        if(osd){mOSD.drawRightEye(mRightEyeViewM);}
        //System.out.println("Computing took:"+(System.currentTimeMillis()-calculatingTB));
        plotFPS();
        //GLES20.glFinish();
        /*while(!hello_available){
            try {Thread.sleep(1);} catch (InterruptedException e) {}
        }
        hello_available=false;*/
        //xxxxxxxxxxxxxxxxxxxxxxxxxxxxx

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
        //System.out.println("Hello from SurfaceDestroyed");
    }

    public void onTap(){
        if(mVideoReceiver !=null){
            mVideoReceiver.next_frame=true;
            /*
            angle_z+=10;
            System.out.println("Angle_z"+angle_z);*/
        }
    }
    private void plotFPS(){
        zaehlerFramerate++;
        if((System.currentTimeMillis()-timeb)>1000) {
            fps = (double) (zaehlerFramerate / 1.0f);
            System.out.println("OpenGL fps:" + fps);
            if (fps > 3 && mVideoReceiver != null && mOSD != null) {
                mVideoReceiver.tellOpenGLFps((long) fps);
                mOSD.mDecoder_fps = mVideoReceiver.getDecoderFps();
                mOSD.mOpenGL_fps = (int) fps;
            }
            timeb = System.currentTimeMillis();
            zaehlerFramerate = 0;
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
}
