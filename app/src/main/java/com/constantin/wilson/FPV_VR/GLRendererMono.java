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

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class GLRendererMono implements GLSurfaceView.Renderer/*,SurfaceTexture.OnFrameAvailableListener*/{
    private UdpVideoReceiver mVideoReceiver;
    private OSDReceiverRenderer mOSD;
    private SharedPreferences settings;
    private GLProgramTexEx mGLProgramTexEx;
    private Context mContext;
    private SurfaceTexture mSurfaceTexture;
    private Surface mDecoderSurface;
    private float[] mLeftEyeViewM=new float[16];
    private float[] mProjM=new float[16];
    private float ratio;
    private int[] textures = new int[2];
    private boolean presentationTime;
    private boolean swapIntervallZero=true;
    private boolean osd=true;
    private boolean clearFramebuffer=false;
    private boolean externalTextureDoubleBuffering=false;
    private float videoFormat=1.3333f;
    private float modelDistance =3.0f;
    private int buffers[]=new int[1];
    private int mDisplay_x,mDisplay_y;
    private float zaehlerFramerate=0;
    private double timeb=0;
    private double fps=0;

    public GLRendererMono(Context context){
        mContext =context;
        settings= PreferenceManager.getDefaultSharedPreferences(mContext);
        try{
            videoFormat=Float.parseFloat(settings.getString("videoFormat","1.3333"));
        }catch(Exception e){e.printStackTrace();}
        presentationTime=settings.getBoolean("presentationTime", true);
        osd=settings.getBoolean("osd", true);
        swapIntervallZero=settings.getBoolean("swapIntervallZero",false);
        clearFramebuffer=settings.getBoolean("clearFramebuffer",true);
        externalTextureDoubleBuffering=settings.getBoolean("externalTextureDoubleBuffering",false);
        try{
            modelDistance =Float.parseFloat(settings.getString("modelDistance","3.0"));
        }catch (Exception e){e.printStackTrace();modelDistance =3.0f;}
        if(modelDistance >14.99999f || modelDistance <2){modelDistance =3.0f;}
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        GLES20.glClearColor(0.0f, 0.0f, 0.07f, 0.0f);
        //we have to create the texture for the overdraw,too
        GLES20.glGenTextures(2, textures, 0);
        mGLProgramTexEx =new GLProgramTexEx(textures[0], GLProgramTexEx.MODE_MONO,null);
        //mSurfaceTexture = new SurfaceTexture(mTextureID);
        //Enable double buffering,because MediaCodec and OpenGL don't have any synchronisation ?
        //For me,it seems like db hasn't really implemented or has no effect;
        //disable ,because at least it should (not tested) add lag
        //mSurfaceTexture = new SurfaceTexture(mGLProgramTexEx.textureId,false);
        mSurfaceTexture = new SurfaceTexture(mGLProgramTexEx.textureId,externalTextureDoubleBuffering);
        mDecoderSurface=new Surface(mSurfaceTexture);
        mVideoReceiver =new UdpVideoReceiver(mDecoderSurface,5000, mContext);
        mVideoReceiver.startDecoding();
        if(swapIntervallZero){
            EGL14.eglSwapInterval(EGL14.eglGetCurrentDisplay(), 0);
        }

    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height){
        //Setup Matrices
        ratio = ((float)width)/height;
        mDisplay_x=width;
        mDisplay_y=height;
        Matrix.frustumM(mProjM, 0, -ratio, ratio, -1, 1, 1.0f, 15.0f);
        //we use the left eye view matrix as normal view matrix
        Matrix.setLookAtM(mLeftEyeViewM, 0, 0, 0.0f, 0.0f, 0.0f, 0.0f, -12, 0.0f, 1.0f, 0.0f);
        GLES20.glGenBuffers(1, buffers, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        FloatBuffer fb= GLHelper.getFloatBuffer(GeometryHelper.getCanvasVertMono(ratio/videoFormat));
        fb.position(0);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, fb.capacity() * 4, fb,
                GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        mOSD=new OSDReceiverRenderer(mContext,textures[1],mProjM,ratio, modelDistance, 3.5f, GLProgramTexEx.MODE_MONO,
                true,ratio,null);
        mOSD.startReceiving();
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        checkVideoRatio();
        if(presentationTime){
            EGLExt.eglPresentationTimeANDROID(EGL14.eglGetCurrentDisplay(),EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW),System.nanoTime());
        }
        if(osd) {
            mOSD.setupModelMatrices();
            mOSD.updateOverlayUnit();
        }
        if(clearFramebuffer){
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);
        }
        mSurfaceTexture.updateTexImage();
        //for simlicity we are using the left eye View matrix
        mGLProgramTexEx.beforeDraw(buffers[0]);
        mGLProgramTexEx.draw(null,null,6);
        mGLProgramTexEx.afterDraw();
        if(osd){mOSD.drawLeftEye(mLeftEyeViewM);}
        plotFPS();

    }
    public void onSurfaceDestroyed() {
        if(mOSD!=null){
            mOSD.stopReceiving();
        }
        if(mVideoReceiver !=null){
            mVideoReceiver.stopDecoding();
        }
        mOSD=null;
        mVideoReceiver=null;
        //System.out.println("Hello from SurfaceDestroyed");
    }

    /*public void onTap(){
        if(mVideoReceiver !=null){
            mVideoReceiver.next_frame=true;
        }
    }*/
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
            FloatBuffer fb= GLHelper.getFloatBuffer(GeometryHelper.getCanvasVertMono(ratio/videoFormat));
            fb.position(0);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, fb.capacity() * 4, fb,
                    GLES20.GL_STATIC_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
            mVideoReceiver.setLastVideoRatio(videoFormat);
        }
    }

    private void makeToast(final String message) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

