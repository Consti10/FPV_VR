package com.constantin.wilson.FPV_VR;


import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.Distortion;



/*
OpenGL "Program* for drawing vertices with a normal texture
Now supports overdraw with alpha ether 0 or 1 without enabling Blending to use with GL_QCOM_writeonly_rendering
later on when implementing front buffer rendering
 */

public class GLProgramTex {
    public static final int MODE_MONO=0,MODE_STEREO=1,MODE_STEREO_DISTORTED=2;
    private int MODE;
    public int mProgram;
    public int mPositionHandle;
    public int mTextureHandle;
    public   int mMVMatrixHandle;
    public int mPMatrixHandle;
    public int mSamplerLoc;
    private float mDistortionFactor;
    private int mMVPMatrixHandle;
    private float[] mMVPM=new float[16];
    private int activeTexture;
    private int textureId;

    public GLProgramTex(int textureId, int renderMode,DistortionData distortionData){
        MODE=renderMode;
        activeTexture=GLES20.GL_TEXTURE0;
        this.textureId=textureId;
        OverlayTexturePicture mOverlayTexturePicture=new OverlayTexturePicture();
        switch (MODE){
            case MODE_MONO:
                mProgram= GLHelper.createProgram(getVertexShader3(), getFragmentShader3());
                mMVPMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uMVPMatrix");break;
            case MODE_STEREO:
                mProgram= GLHelper.createProgram(getVertexShader3(), getFragmentShader3());
                mMVPMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uMVPMatrix");break;
            case MODE_STEREO_DISTORTED:
                mProgram = GLHelper.createProgram(getVertexShaderDistorted(distortionData.getUndistortionCoeficients()), getFragmentShaderDistorted());
                mMVMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uMVMatrix");
                mPMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uPMatrix");break;
        }
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        mSamplerLoc = GLES20.glGetUniformLocation (mProgram, "sTexture" );
        GLHelper.checkGlError("glGetAttribLocation sTexture");
        GLES20.glActiveTexture(activeTexture);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLHelper.checkGlError("glBindTexture mTextureID");
        /*GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_GENERATE_MIPMAP_HINT,
                GLES20.GL_FALSE);*/
        /*GLES20.glEnable(GLES20.GL_BLEND);
        //GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_CONSTANT_ALPHA);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glDisable(GLES20.GL_BLEND);*/
        //GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0,mOverlayTexturePicture.draw());
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,mOverlayTexturePicture.draw(),0);
        mOverlayTexturePicture.recycle();
        /*GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);*/
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLHelper.checkGlError("end");
    }

    public void beforeDraw(int vertB,int uvB){
        //GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glUseProgram(mProgram);
        GLES20.glActiveTexture(activeTexture);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertB);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, uvB);
        GLES20.glEnableVertexAttribArray(mTextureHandle);
        GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false,
                0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        //doesn't take a textureId
        //GLES20.glUniform1i(mSamplerLoc, textureId);
        //GLES20.glUniform1i(mSamplerLoc,activeTexture);
        GLES20.glUniform1i(mSamplerLoc,0);
        GLHelper.checkGlError("beforeDraw");
    }
    public void draw(float[] modViewM,float[] projM,int trianglesOff,int numTriangles){
        switch (MODE){
            case MODE_MONO:
                Matrix.multiplyMM(mMVPM, 0, projM, 0, modViewM, 0);
                GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPM, 0);break;
            case MODE_STEREO:
                Matrix.multiplyMM(mMVPM, 0, projM, 0, modViewM, 0);
                GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPM, 0);break;
            case MODE_STEREO_DISTORTED:
                GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, modViewM, 0);
                GLES20.glUniformMatrix4fv(mPMatrixHandle, 1, false, projM, 0);
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, trianglesOff,numTriangles);
    }
    public  void  afterDraw(){
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureHandle);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        //GLES20.glDisable(GLES20.GL_BLEND);
    }
    /*
    public void setTexture(int textureUnit,int textureId,int uniformID,int textureType){
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+textureUnit);
        GLES20.glBindTexture(textureType,textureId);
        GLES20.glUniform1i(uniformID,textureUnit);
    }*/

    //Program for drawing overlay /overlayUnits and heights /for textures

    private String getVertexShaderDistorted(float[] distortionCoeficients){

        return "attribute vec4 aPosition;" +
                "attribute vec2 aTexCoord;" +
                "varying vec2 vTexCoord;" +
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
        return "precision mediump float;" +
                "varying vec2 vTexCoord;" +
                "uniform sampler2D sTexture;" +
                "void main() {" +
                //"  gl_FragColor = texture2D( sTexture, vTexCoord );" +
                "vec4 color=texture2D( sTexture, vTexCoord );"+
                "if(color.a>0.5){" +
                "  gl_FragColor =color;" +
                "}else{"+
                "discard;"+
                "}"+
                //"  gl_FragColor =vec4(0.5,0,0,1);" +
                "}";
    }


    //Program for drawing overlay /overlayUnits and heights /for textures
    private static String getVertexShader3(){
        return  "uniform mat4 uMVPMatrix;" +
                "attribute vec4 aPosition;" +
                 "attribute vec2 aTexCoord;" +
                 "varying vec2 vTexCoord;" +

                "void main() {" +
                "  gl_Position = uMVPMatrix * aPosition;" +
                "  vTexCoord = aTexCoord;" +
                "}";

    }
    private static String getFragmentShader3(){
        return "precision mediump float;" +
                "varying vec2 vTexCoord;" +
                "uniform sampler2D sTexture;" +
                "void main() {" +
                //"  gl_FragColor = texture2D( sTexture, vTexCoord );" +
                "vec4 color=texture2D( sTexture, vTexCoord );"+
                "if(color.a>0.5){" +
                "  gl_FragColor =color;" +
                "}else{"+
                "discard;"+
                "}"+
                //"  gl_FragColor =vec4(0.5,0,0,1);" +
                "}";
    }

}
