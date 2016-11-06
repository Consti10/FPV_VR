package com.constantin.wilson.FPV_VR;

/**
 * Created by Constantin on 29.10.2016.
 */

import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class OverdrawLayer {
    private OverlayTexturePicture mOverlayTexturePicture;
    private OGLProgramTex mOGLProgramTex;
    private int mTextureID;
    private int verticesB,uvB;
    private int i2=4;
    private boolean enable_height;
    private float[] mMVPM=new float[16]; //stores results
    float[] mProjM=new float[16]; //has to be set once

    public volatile boolean multithreadUnitReady=true;
    public volatile boolean unitToUpdate=false;
    public volatile FloatBuffer mMultiThreadSafeFloatBuffer= ByteBuffer.allocateDirect(
            (3*6*2)*4) .order(ByteOrder.nativeOrder()).asFloatBuffer();;
    public volatile int mUnitNumber;

    public OverdrawLayer(int[] textures,float videoFormat,float videoDistance,
                         float[] projM,boolean distortionCorrection,boolean enable_battery_life,
                         boolean enable_lattitude_longitude,boolean enable_rssi,boolean enable_X2,boolean enable_height,
                         boolean enable_voltage, boolean enable_ampere,boolean enable_X3,boolean enable_speed,
                         boolean enable_X4){
        mOverlayTexturePicture=new OverlayTexturePicture();
        mProjM=projM;
        this.enable_height=enable_height;
        int[] tempBuff=new int[2];
        GLES20.glGenBuffers(2, tempBuff, 0);
        verticesB=tempBuff[0];
        uvB=tempBuff[1];
        MyOSDReceiverRendererHelper.getOverdrawCoordByFormat(videoFormat,videoDistance,verticesB,uvB,
                enable_battery_life,enable_lattitude_longitude,enable_rssi,enable_X2,enable_height,
                enable_voltage,enable_ampere,enable_X3,enable_speed,enable_X4);
        mOGLProgramTex=new OGLProgramTex(distortionCorrection);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);
        OpenGLHelper.checkGlError("glBindTexture mTextureID");
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        /*GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_GENERATE_MIPMAP_HINT,
                GLES20.GL_FALSE);*/
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_CONSTANT_ALPHA);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, mOverlayTexturePicture.draw());
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
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
    private void updateOverlayUnit(){
        if(unitToUpdate){
            //System.out.println("Hi from updateOverlayUnit");
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, uvB);
            GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER,(mUnitNumber*6*6*2)*4,(3*6*2)*4, mMultiThreadSafeFloatBuffer);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
            unitToUpdate=false;
            multithreadUnitReady=true;
        }
    }

    public void drawOverlay(float[] viewM,float[] heightModelM) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //Update once every frame (at 60fps)
        if(i2<=1){
            i2=2;
            updateOverlayUnit();
        }else{i2-=1;}
        mOGLProgramTex.beforeDraw(mTextureID,verticesB,uvB);
        if(true){
            mOGLProgramTex.draw(viewM,mProjM,0,6*6*14);
        }
        if(enable_height){
            Matrix.multiplyMM(mMVPM, 0, viewM, 0, heightModelM, 0);
            mOGLProgramTex.draw(mMVPM,mProjM,6*6*14,10*6);
        }
        mOGLProgramTex.afterDraw();
        GLES20.glDisable(GLES20.GL_BLEND);
    }
}

