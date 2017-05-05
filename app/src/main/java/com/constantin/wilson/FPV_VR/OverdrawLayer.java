package com.constantin.wilson.FPV_VR;

/**
 * Created by Constantin on 29.10.2016.
 */

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class OverdrawLayer {
    private OverlayTexturePicture mOverlayTexturePicture;
    private GLProgramTex mOGLProgramTex;
    private OSDSettings mOSDSettings;
    private int MODE;
    private int verticesB,uvB;
    private int i2=4;
    private int nQPU;
    private int nNQPU;
    private float videoDistance;
    private boolean OSDOnTopOfVideo;
    private float[] mMVPM=new float[16]; //stores results
    float[] mProjM=new float[16]; //has to be set once
    private boolean enable_model;

    public volatile boolean multithreadUnitReady=true;
    public volatile boolean unitToUpdate=false;
    public volatile FloatBuffer mMultiThreadSafeFloatBuffer= ByteBuffer.allocateDirect(
            (OverlayTexturePicture.nNumberQuadsPerUnit*6*2)*4) .order(ByteOrder.nativeOrder()).asFloatBuffer();;
    public volatile int mUnitNumber;

    public OverdrawLayer(int textureID,float videoFormat,float videoDistance,boolean OSDOnTopOfVideo,
                         float[] projM,int MODE,boolean enable_model,OSDSettings osdSettings,float ratio,DistortionData distortionData){
        mProjM=projM;
        mOSDSettings=osdSettings;
        this.MODE=MODE;
        this.videoDistance=videoDistance;
        this.OSDOnTopOfVideo=OSDOnTopOfVideo;
        this.enable_model=enable_model;
        int[] tempBuff=new int[2];
        GLES20.glGenBuffers(2, tempBuff, 0);
        verticesB=tempBuff[0];
        uvB=tempBuff[1];
        if(MODE== GLProgramTexEx.MODE_MONO){
            GeometryHelper.getOverdrawCoordsByFormatMono(verticesB,uvB,mOSDSettings,ratio);
        }else {
            GeometryHelper.getOverdrawCoordByFormat(videoFormat,videoDistance,OSDOnTopOfVideo,verticesB,uvB,mOSDSettings);
        }
        mOGLProgramTex=new GLProgramTex(textureID,MODE,distortionData);
    }

    public void updateOverlayUnitMultithreadBuffer(int unitNumber,float[] uvFloats){
        mMultiThreadSafeFloatBuffer.position(0);
        mMultiThreadSafeFloatBuffer.put(uvFloats);
        mMultiThreadSafeFloatBuffer.position(0);
        mUnitNumber=unitNumber;
        multithreadUnitReady=false;
        unitToUpdate=true;
    }

    //has to be called from OpenGL thread
    public void updateOverlayUnit(){
        //Update once every frame (at 60fps)
        nQPU=OverlayTexturePicture.nQuadsPerUnit;
        nNQPU=OverlayTexturePicture.nNumberQuadsPerUnit;
        if(unitToUpdate){
            //System.out.println("Hi from updateOverlayUnit");
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, uvB);
            GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER,(mUnitNumber*nQPU*6*2)*4,(nNQPU*6*2)*4, mMultiThreadSafeFloatBuffer);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
            unitToUpdate=false;
            multithreadUnitReady=true;
        }
    }

    public void drawOverlay(float[] viewM,float[] heightModelM) {
        mOGLProgramTex.beforeDraw(verticesB,uvB);
        if(true){
            mOGLProgramTex.draw(viewM,mProjM,0,OverlayTexturePicture.nQuadsPerUnit*6*14);
        }
        if(mOSDSettings.enable_height&&enable_model){
            Matrix.multiplyMM(mMVPM, 0, viewM, 0, heightModelM, 0);
            mOGLProgramTex.draw(mMVPM,mProjM,6*OverlayTexturePicture.nQuadsPerUnit*14,10*6);
        }
        mOGLProgramTex.afterDraw();
    }
    public void changeOSDVideoRatio(float ratio){
        if(MODE!= GLProgramTexEx.MODE_MONO) {
            GeometryHelper.getOverdrawCoordByFormat(ratio, videoDistance, OSDOnTopOfVideo, verticesB, uvB, mOSDSettings);
        }
    }
}

