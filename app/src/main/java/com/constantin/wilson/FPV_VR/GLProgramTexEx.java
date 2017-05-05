package com.constantin.wilson.FPV_VR;
import android.content.SharedPreferences;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.Distortion;
/* Die ex von Tex*/
//for exteronal eos texture
/*For all Modes: x,y,z,u,v. In MONO mode the z=0 and w=1 in vertex shader*/

public class GLProgramTexEx {
    public static final int MODE_MONO=0,MODE_STEREO=1,MODE_STEREO_DISTORTED=2;
    private int MODE;
    private int mProgram;
    private int mPositionHandle;
    private int mTextureHandle;
    private int mMVPMatrixHandle;
    private int mMVMatrixHandle;
    private int mProjMatrixHandle;
    private int activeTexture;
    public int textureId;
    private int mSamplerLoc;
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private float[] mMVPM=new float[16];

    public GLProgramTexEx(int textureId, int renderMode, DistortionData distortionData){
        MODE=renderMode;
        activeTexture= GLES20.GL_TEXTURE1;
        this.textureId=textureId;
        switch (MODE){
            case MODE_MONO:
                mProgram= GLHelper.createProgram(getVertexShaderMono(), getFragmentShaderMono());break;
            case MODE_STEREO:
                mProgram= GLHelper.createProgram(getVertexShaderStereo(), getFragmentShaderStereo());break;
            case MODE_STEREO_DISTORTED:
                mProgram = GLHelper.createProgram(getVertexShaderDistorted(distortionData.getUndistortionCoeficients()), getFragmentShaderDistorted());
        }
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GLHelper.checkGlError("glGetAttribLocation aPosition");
        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        GLHelper.checkGlError("glGetAttribLocation aTexCoord");
        mSamplerLoc = GLES20.glGetUniformLocation (mProgram, "sTexture" );
        switch (MODE){
            case MODE_MONO:
                break;
            case MODE_STEREO:
                mMVPMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uMVPMatrix");break;
            case MODE_STEREO_DISTORTED:
                mMVMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uMVMatrix");
                mProjMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uPMatrix");break;
        }
        //I don't know why,but it seems like when you use both external and normal textures,you have to use normal textures for the first,
        //and the external texture for the second unit; bug ?
        //Probably fixed; I didn't handle the OpenGl samplers correctely;
        GLES20.glActiveTexture(activeTexture);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textureId);
        GLHelper.checkGlError("glBindTexture mTextureID");
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        /*GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_NEAREST);*/
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,0);
        GLHelper.checkGlError("end");
    }
    public void beforeDraw(int VertUvBuffer){
        GLES20.glUseProgram(mProgram);
        GLES20.glActiveTexture(activeTexture);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, VertUvBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, 0);
        GLES20.glEnableVertexAttribArray(mTextureHandle);
        GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, 3 * 4);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);
        //GLES20.glUniform1i(mSamplerLoc, textureId);
        //GLES20.glUniform1i(mSamplerLoc, activeTexture);
        //GLES20.glUniform1i(mSamplerLoc, 1); //it should work this way in a tutorial from github); it does on
        //handy but not in emulator
        GLHelper.checkGlError("beforeDraw");
    }
    public void draw(float[] modViewM,float[] projM,int numVertices){
        switch (MODE){
            case MODE_MONO:
                break;
            case MODE_STEREO:
                Matrix.multiplyMM(mMVPM, 0, projM, 0, modViewM, 0);
                GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPM, 0);break;
            case MODE_STEREO_DISTORTED:
                GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, modViewM, 0);
                GLES20.glUniformMatrix4fv(mProjMatrixHandle, 1, false, projM, 0);break;
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, numVertices);
    }

    public void afterDraw(){
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureHandle);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
    }


    private String getVertexShaderDistorted(float[] distortionCoeficients){
        return "attribute vec4 aPosition;\n" +
                "attribute vec4 aTexCoord;\n" +
                "varying vec2 vTexCoord;\n" +
                "uniform mat4 uMVMatrix;" +
                "uniform mat4 uPMatrix;" +
                "float r2;"+
                "vec4 pos;"+
                "float ret;"+
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
                "}";
    }


    private static String getFragmentShaderDistorted(){
        return "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;\n" +
                "varying vec2 vTexCoord;\n" +
                "uniform samplerExternalOES sTexture;\n" +
                "void main() {\n" +
                "  gl_FragColor = texture2D(sTexture, vTexCoord);\n" +
                //"  gl_FragColor =vec4(0.5,0,0,1);" +
                "}\n";
    }
    private static String getVertexShaderStereo(){
        return "uniform mat4 uMVPMatrix;\n" +
                "attribute vec4 aPosition;\n" +
                "attribute vec4 aTexCoord;\n" +
                "varying vec2 vTexCoord;\n" +

                "void main() {\n" +
                "  gl_Position = uMVPMatrix*aPosition;\n" +
                //"  gl_Position = aPosition;\n" +
                "  vTexCoord = (aTexCoord).xy;\n" +
                //"  vTextureCoord = aTextureCoord;\n" +
                //"  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                "}\n";
    }
    private static String getFragmentShaderStereo(){
        return "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;\n" +
                "varying vec2 vTexCoord;\n" +
                "uniform samplerExternalOES sTexture;\n" +
                "void main() {\n" +
                "  gl_FragColor = texture2D(sTexture, vTexCoord);\n" +
                //"  gl_FragColor =vec4(0.5,0,0,1);" +
                "}\n";
    }
    private static String getVertexShaderMono(){
        return "attribute vec4 aPosition;\n" +
                "attribute vec2 aTexCoord;\n" +
                "varying vec2 vTexCoord;\n" +

                "void main() {\n" +
                //"  gl_Position = uMVPMatrix*aPosition;\n" +
                "  gl_Position.xy = aPosition.xy;\n" +
                "  gl_Position.zw=vec2(0.0,1.0);\n"+
                "  vTexCoord = (aTexCoord).xy;\n" +
                //"  vTextureCoord = aTextureCoord;\n" +
                //"  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                "}\n";
    }
    private static String getFragmentShaderMono(){
        return "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;\n" +
                "varying vec2 vTexCoord;\n" +
                "uniform samplerExternalOES sTexture;\n" +
                "void main() {\n" +
                "  gl_FragColor = texture2D(sTexture, vTexCoord);\n" +
                //"  gl_FragColor =vec4(0.5,0,0,1);" +
                "}\n";
    }
}