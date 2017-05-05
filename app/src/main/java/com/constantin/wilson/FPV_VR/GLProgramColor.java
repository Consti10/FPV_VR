package com.constantin.wilson.FPV_VR;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.Distortion;


/*
* OpenGL "Program* for drawing simple coloured vertices
*/
public class GLProgramColor {
    public static final int MODE_MONO=0,MODE_STEREO=1,MODE_STEREO_DISTORTED=2;
    private int MODE;
    private float mDistortionFactor;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVMatrixHandle;
    private int mPMatrixHandle;
    private int mProgram;
    private int mMVPMatrixHandle;
    private float[] mMVPM=new float[16];

    public GLProgramColor(int renderMode,DistortionData distortionData) {
        MODE=renderMode;
        switch (MODE){
            case MODE_MONO:
                mProgram= GLHelper.createProgram(getVertexShader2(), getFragmentShader2());
                mMVPMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uMVPMatrix");break;
            case MODE_STEREO:
                mProgram= GLHelper.createProgram(getVertexShader2(), getFragmentShader2());
                mMVPMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uMVPMatrix");break;
            case MODE_STEREO_DISTORTED:
                mProgram = GLHelper.createProgram(getVertexShaderDistorted(distortionData.getUndistortionCoeficients()), getFragmentShaderDistorted());
                mMVMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uMVMatrix");
                mPMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uPMatrix");break;
        }
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        GLHelper.checkGlError("glGetAttribLocation OGProgramColor");
    }
    public void beforeDraw(int buffer){
        GLES20.glUseProgram(mProgram);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3/*3vertices*/, GLES20.GL_FLOAT, false, 7*4, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer);
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, 4/*rgba*/,
                GLES20.GL_FLOAT, false, 7*4, 3 * 4);
    }
    public void draw(float[] modViewM, float[] projM,int trianglesOff,int numberTriangles){
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
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, trianglesOff, numberTriangles);
    }
    public void afterDraw(){
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }


    //Program for drawing the kopter,home arrow and height lines/for colours

    private String getVertexShaderDistorted(float[] distortionCoeficients){

        return "attribute vec4 aPosition;"+
                "attribute vec4 aColor;"+
                "varying vec4 vColor;"+
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
                "  vColor = aColor;" +
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
        return "precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a
                // precision in the fragment shader.
                + "varying vec4 vColor;          \n"		// This is the color from the vertex shader interpolated across the
                // triangle per fragment.
                + "void main()                    \n"		// The entry point for our fragment shader.
                + "{                              \n"
                + "   gl_FragColor = vColor;     \n"		// Pass the color directly through the pipeline.
                + "}                              \n";
    }

    private static String getVertexShader2(){
        return  "uniform mat4 uMVPMatrix;"+
                "attribute vec4 aPosition;"+
                        "attribute vec4 aColor;"+
                        "varying vec4 vColor;"+

                "void main()                   \n"+
                "{                              \n"+
                "   vColor = aColor;          \n"+
                // It will be interpolated across the triangle.
                "   gl_Position = uMVPMatrix* aPosition;    \n"+
                "}                             \n";
    }
    private static String getFragmentShader2(){
        return "precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a
                // precision in the fragment shader.
                + "varying vec4 vColor;          \n"		// This is the color from the vertex shader interpolated across the
                // triangle per fragment.
                + "void main()                    \n"		// The entry point for our fragment shader.
                + "{                              \n"
                + "   gl_FragColor = vColor;     \n"		// Pass the color directly through the pipeline.
                + "}                              \n";
    }

}


