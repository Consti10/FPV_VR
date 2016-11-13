package com.constantin.wilson.FPV_VR;


/* Die ex von Tex*/
//for exteronal eos texture

import android.opengl.GLES20;
import android.opengl.Matrix;

public class OGLProgramTexEx {
    private int mProgram;
    private int mPositionHandle;
    private int mTextureHandle;
    private int mMVPMatrixHandle;
    private int mMVMatrixHandle;
    private int mProjMatrixHandle;
    private int mSamplerLoc;
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private  boolean mDistortionCorrection;
    private float[] mMVPM=new float[16];

    public OGLProgramTexEx(boolean distortionCorrection){
        mDistortionCorrection=distortionCorrection;
        if(mDistortionCorrection){
            mProgram = OpenGLHelper.createProgram(getVertexShaderTesselated(), getFragmentShaderTesselated());
        }else{
            mProgram=OpenGLHelper.createProgram(getVertexShader(), getFragmentShader());
        }
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        OpenGLHelper.checkGlError("glGetAttribLocation aPosition");
        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        OpenGLHelper.checkGlError("glGetAttribLocation aTexCoord");
        mSamplerLoc = GLES20.glGetUniformLocation (mProgram, "sTexture" );
        if(mDistortionCorrection){
            mMVMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uMVMatrix");
            mProjMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uPMatrix");
        }else{
            mMVPMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uMVPMatrix");
        }
        OpenGLHelper.checkGlError("glGetAttribLocation uMatrices");
    }
    public void beforeDraw(int activeTexture,int textureID,int VertUvBuffer){
        GLES20.glUseProgram(mProgram);
        GLES20.glActiveTexture(activeTexture);
        GLES20.glBindTexture(0x8D65, textureID);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, VertUvBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, VertUvBuffer);
        GLES20.glEnableVertexAttribArray(mTextureHandle);
        GLES20.glVertexAttribPointer(mTextureHandle, 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, 3 * 4);
        GLES20.glUniform1i(mSamplerLoc, textureID);
    }
    public void draw(float[] modViewM,float[] projM,int numTriangles){
        if(mDistortionCorrection){
            GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, modViewM, 0);
            GLES20.glUniformMatrix4fv(mProjMatrixHandle, 1, false, projM, 0);
        }else{
            Matrix.multiplyMM(mMVPM, 0, projM, 0, modViewM, 0);
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPM, 0);
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6*numTriangles);
    }

    public void afterDraw(){
        //disable
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureHandle);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);
        GLES20.glBindTexture(0x8D65, 0);
    }


    public static String getVertexShaderTesselated(){
        return //"uniform mat4 uMVPMatrix;\n" +
                "attribute vec4 aPosition;\n" +
                "attribute vec4 aTexCoord;\n" +
                "varying vec2 vTexCoord;\n" +
                        //
                 "uniform mat4 uMVMatrix;" +
                 "uniform mat4 uPMatrix;" +
                "float r2;"+
                "vec2 _Undistortion=vec2(-0.18,0.0);"+
                        //"mat4 _Undistortion;"+
                        //"float ret=0.0;"+
                //"vec4 _Undistortion=vec4(-0.441,-0.156,0.0,0.0);"+
                //"float _NearClip=1.0;"+
                "float _MaxRadSq=2.45;"+
                "vec4 pos;"+
                "void main() {\n" +
                        /*"_Undistortion[1][1]=-0.0;"+
                        "_Undistortion[0][1]=-0.0;"+
                        "_Undistortion[3][0]=-0.0;"+
                        "_Undistortion[2][0]=-0.0;"+
                        "_Undistortion[1][0]=-0.0;"+
                        "_Undistortion[0][0]=-0.15;"+*/
                 "  pos=uMVMatrix * aPosition;"+
                 "  vTexCoord = (aTexCoord).xy;" +
                 "  r2=clamp(dot(pos.xy,pos.xy)/(pos.z*pos.z),0.0,_MaxRadSq);"+
                 "  pos.xy *=1.0+(_Undistortion.x+_Undistortion.y*r2)*r2;"+
                        /*"ret=r2*(ret+_Undistortion[1][1]);"  +
                        "ret=r2*(ret+_Undistortion[0][1]);"  +
                        "ret=r2*(ret+_Undistortion[3][0]);"  +
                        "ret=r2*(ret+_Undistortion[2][0]);"  +
                        "ret=r2*(ret+_Undistortion[1][0]);"  +
                        "ret=r2*(ret+_Undistortion[0][0]);"  +
                        "pos.xy *= 1.0+ret;"+*/
                        //"}" +
                 "  gl_Position=uPMatrix*pos;"+
                 "}";
    }
    public static String getFragmentShaderTesselated(){
        return "#extension GL_OES_EGL_image_external : require\n" +
                "precision mediump float;\n" +
                "varying vec2 vTexCoord;\n" +
                "uniform samplerExternalOES sTexture;\n" +
                "void main() {\n" +
                "  gl_FragColor = texture2D(sTexture, vTexCoord);\n" +
                //"  gl_FragColor =vec4(0.5,0,0,1);" +
                "}\n";
    }
    public static String getVertexShader(){
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
    public static String getFragmentShader(){
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
