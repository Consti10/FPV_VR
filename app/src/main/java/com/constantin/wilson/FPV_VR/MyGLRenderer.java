package com.constantin.wilson.FPV_VR;


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

import com.google.vrtoolkit.cardboard.PhoneParams;
import com.google.vrtoolkit.cardboard.proto.Phone;
import com.google.vrtoolkit.cardboard.sensors.HeadTracker;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private UdpStreamReceiver mDecoder;
    private HeadTracker mHeadTracker;
    private MyOSDReceiverRenderer mOSD;
    private SharedPreferences settings;
    private OGLProgramTexEx mOGLProgramTexEx;
    private Context mContext;
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
    private boolean enable_stereo_renderer=true;
    private boolean unlimitedOGLFps;
    private boolean swapIntervallZero=true;
    private boolean osd=true;
    private boolean distortionCorrection=false;
    private float videoFormat=1.3333f;
    private float modelDistance =3.0f;
    private float videoDistance =5.7f;
    private float interpupilarryDistance=0.2f;
    private float viewportScale=1.0f;
    private int tesselationFactor=1; //tesselation of 2 means 4 quads;
    private int numberCanvasTriangles=tesselationFactor*tesselationFactor;
    private boolean headTracking=true;
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static int GL_TEXTURE_EXTERNAL_OES = 0x8D65;
    private int mTextureID;
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
    private int zaehlerFramerate=0;
    private double timeb=0;
    private double fps=0;
    private double timeInit;
    private boolean FULL_VERSION=true;

    public MyGLRenderer(Context context){
        mContext =context;
        settings= PreferenceManager.getDefaultSharedPreferences(mContext);
        try{
            videoFormat=Float.parseFloat(settings.getString("videoFormat","1.3333"));
        }catch(Exception e){e.printStackTrace();}
        unlimitedOGLFps=settings.getBoolean("unlimitedOGLFps", false);
        osd=settings.getBoolean("osd", false);
        swapIntervallZero=settings.getBoolean("swapIntervallZero",true);
        try{
            videoDistance =Float.parseFloat(settings.getString("videoDistance","5.7"));
        }catch (Exception e){e.printStackTrace();
            videoDistance =5.7f;}
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
        headTracking=settings.getBoolean("headTracking", false);
        enable_stereo_renderer=settings.getBoolean("enable_stereo_renderer", true);
        distortionCorrection=settings.getBoolean("distortionCorrection", false);
        try{
            tesselationFactor =(int)Float.parseFloat(settings.getString("tesselationFactor","1.0"));
            if(tesselationFactor<1){tesselationFactor=1;}
        }catch (Exception e){tesselationFactor=1;}
        if(distortionCorrection&&(tesselationFactor<20)){tesselationFactor=20;}
        numberCanvasTriangles=tesselationFactor*tesselationFactor;
        if(!enable_stereo_renderer){headTracking=false;distortionCorrection=false;tesselationFactor=1;
            numberCanvasTriangles=tesselationFactor*tesselationFactor;}
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        GLES20.glClearColor(0.0f, 0.0f, 0.07f, 0.0f);
        GLES20.glGenBuffers(1, buffers, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
                OpenGLHelper.getFloatBuffer(getCanvasVertByFormatTeselation(videoFormat, videoDistance,tesselationFactor)).capacity() * 4,
                OpenGLHelper.getFloatBuffer(getCanvasVertByFormatTeselation(videoFormat, videoDistance,tesselationFactor)),
                GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        mOGLProgramTexEx=new OGLProgramTexEx(distortionCorrection);
        //we have to create the texture for the overdraw,too
        GLES20.glGenTextures(2, textures, 0);
        mTextureID = textures[0];
        //I don't know why,but it seems like when you use both external and normal textures,you have to use normal textures for the first,
        //and the external texture for the second unit; bug ?
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
        OpenGLHelper.checkGlError("glBindTexture mTextureID");
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        //mSurfaceTexture = new SurfaceTexture(mTextureID);
        //Enable double buffering,because MediaCodec and OpenGL don't have any synchronisation ?
        //For me,it seems like db hasn't really implemented or has no effect;
        //disable ,because at least it should (not tested) add lag
        mSurfaceTexture = new SurfaceTexture(mTextureID,false);
        mDecoderSurface=new Surface(mSurfaceTexture);
        mDecoder=new UdpStreamReceiver(mDecoderSurface,5000, mContext);
        mDecoder.startDecoding();
        mOSD=new MyOSDReceiverRenderer(mContext,textures,mLeftEyeViewM,mRightEyeViewM,mProjM,videoFormat, modelDistance, videoDistance,distortionCorrection);
        mOSD.startReceiving();
        mHeadTracker=HeadTracker.createFromContext(mContext);
        mHeadTracker.setNeckModelEnabled(true);
        final Phone.PhoneParams phoneParams = PhoneParams.readFromExternalStorage();
        if (phoneParams != null) {
            this.mHeadTracker.setGyroBias(phoneParams.gyroBias);
        }
        if (headTracking) {
            mHeadTracker.startTracking();
        }
        if(swapIntervallZero){
            EGL14.eglSwapInterval(EGL14.eglGetCurrentDisplay(), 0);
        }
        timeInit=System.currentTimeMillis();
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height){
        //Setup Matrices
        if(enable_stereo_renderer){
            float ratio = (float) (width / 2) / height;
            Matrix.frustumM(mProjM, 0, -ratio, ratio, -1, 1, 1.0f, 15.0f);
            Matrix.setLookAtM(mLeftEyeViewM, 0, -(interpupilarryDistance / 2), 0.0f, 0.0f, 0.0f, 0.0f, -12, 0.0f, 1.0f, 0.0f);
            Matrix.setLookAtM(mRightEyeViewM, 0, (interpupilarryDistance / 2), 0.0f, 0.0f, 0.0f, 0.0f, - 12, 0.0f, 1.0f, 0.0f);
        }else{
            float ratio = (float) (width) / height;
            Matrix.frustumM(mProjM, 0, -ratio, ratio, -1, 1, 1.0f, 15.0f);
            //we use the left eye view matrix as normal view matrix
            Matrix.setLookAtM(mLeftEyeViewM, 0, 0, 0.0f, 0.0f, 0.0f, 0.0f, -12, 0.0f, 1.0f, 0.0f);
        }
        Matrix.setIdentityM(mLeftEyeTranslate, 0);
        Matrix.setIdentityM(mRightEyeTranslate, 0);
        Matrix.translateM(mLeftEyeTranslate, 0, (interpupilarryDistance / 2), 0.0f, 0);
        Matrix.translateM(mRightEyeTranslate, 0, -(interpupilarryDistance / 2), 0.0f, 0);
        GLES20.glViewport(0, 0, width, height);
        mDisplay_x=width;
        mDisplay_y=height;
        mOSD.mDisplay_x=width;
        mOSD.mDisplay_y=height;
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
    public void onDrawFrame(GL10 glUnused) {
        double calculatingTB=System.currentTimeMillis();
        //Tell android this frame should have been displayed at the time it was created
        if(unlimitedOGLFps){
            EGLExt.eglPresentationTimeANDROID(EGL14.eglGetCurrentDisplay(),EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW),System.nanoTime());
        }
        if(headTracking && mHeadTracker!=null){
            mHeadTracker.getLastHeadView(tempEyeViewM, 0);
            Matrix.multiplyMM(mLeftEyeViewM, 0,  mLeftEyeTranslate,  0, tempEyeViewM, 0);
            Matrix.multiplyMM(mRightEyeViewM, 0, mRightEyeTranslate, 0, tempEyeViewM, 0);
        }
        //GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        //Danger: getTimestamp can't be used to compare with System.nanoTime or System.currentTimeMillis
        //because it's zero point depends on the sources providing the image;
        mSurfaceTexture.updateTexImage();
        //GLES20.glFinish();
        //GLES20.glFlush();
        mOGLProgramTexEx.beforeDraw(GLES20.GL_TEXTURE1,mTextureID,buffers[0]);
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
            mOSD.setupModelMatrices();
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
            if(fps >3 && mDecoder!=null && mOSD!=null){
                mDecoder.tellOpenGLFps((long)fps);
                mOSD.mDecoder_fps=mDecoder.getDecoderFps();
                mOSD.mOpenGL_fps=(int)fps;
            }
            timeb = System.currentTimeMillis();
            zaehlerFramerate = 0;
        }
        if(!FULL_VERSION){
            double timeElapsed= System.currentTimeMillis();
            System.out.println("Time elapsed:"+timeElapsed);
        }
    }
    public void onSurfaceDestroyed() {
        if(mOSD!=null){
            mOSD.stopReceiving();
        }
        if(mDecoder!=null){
            mDecoder.stopDecoding();
        }
        if(mHeadTracker!=null){
            mHeadTracker.stopTracking();
        }
    }


    public void onTap(){
        if(mDecoder!=null){
            mDecoder.next_frame=true;
            /*
            angle_z+=10;
            System.out.println("Angle_z"+angle_z);*/
        }
    }
    private float[] getCanvasVertByFormatTeselation(float format,float distance,int tesselation){
        //F.e format=4:3
        //The x and z values stay,only the y values change for different video format's
        float x0=-5.0f , y0=(1.0f/format)*5.0f     , z0=-distance;
        float[] TriangleVerticesData=new float[5*6*tesselation*tesselation];
        OpenGLHelper.makeTesselatedRectangle(TriangleVerticesData,0,tesselation,x0,y0,z0,10.0f * (1.0f / format),10);
        /*OpenGLHelper.makeRectangle3(TriangleVerticesData, 0,
                x0, y0, z0,
                x0 + 10, y0, z0,
                x0 + 10, y0 - (10.0f * (1.0f / format)), z0,
                x0, y0 - (10.0f * (1.0f / format)), z0,
                0.0f, 1.0f
        );*/
        return TriangleVerticesData;
    }

}
