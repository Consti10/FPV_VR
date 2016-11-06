package com.constantin.wilson.FPV_VR;

import android.opengl.GLES20;
import android.opengl.Matrix;


public class OGLProgramColor {

    private int mPositionHandle;
    private int mColorHandle;
    private int mMVMatrixHandle;
    private int mPMatrixHandle;
    private int mProgram;
    private boolean mDistortionCorrection;
    private int mMVPMatrixHandle;
    private float[] mMVPM=new float[16];

    public OGLProgramColor(boolean distortionCorrection) {
        mDistortionCorrection=distortionCorrection;
        if(mDistortionCorrection){
            mProgram = OpenGLHelper.createProgram(getVertexShader2Tesselated(), getFragmentShader2Tesselated());
        }else{
            mProgram=OpenGLHelper.createProgram(getVertexShader2(), getFragmentShader2());
        }
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        if(mDistortionCorrection){
            mMVMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uMVMatrix");
            mPMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uPMatrix");
        }else{
            mMVPMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uMVPMatrix");
        }
        OpenGLHelper.checkGlError("glGetAttribLocation OGProgramColor");
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
        if(mDistortionCorrection){
            GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, modViewM, 0);
            GLES20.glUniformMatrix4fv(mPMatrixHandle, 1, false, projM, 0);
        }else{
            Matrix.multiplyMM(mMVPM, 0, projM, 0, modViewM, 0);
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPM, 0);
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, trianglesOff, numberTriangles);
    }
    public void afterDraw(){
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
        if(mDistortionCorrection){
            GLES20.glDisableVertexAttribArray(mMVMatrixHandle);
            GLES20.glDisableVertexAttribArray(mPMatrixHandle);
        }else{
            GLES20.glDisableVertexAttribArray(mMVPMatrixHandle);
        }
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }


    //Program for drawing the kopter,home arrow and height lines/for colours
    public static String getVertexShader2Tesselated(){
        return //"uniform mat4 uMVPMatrix;"+
                "attribute vec4 aPosition;"+
                 "attribute vec4 aColor;"+
                "varying vec4 vColor;"+

                "uniform mat4 uMVMatrix;" +
                "uniform mat4 uPMatrix;" +
                "float r2;"+
                "vec2 _Undistortion=vec2(-0.18,0.0);"+
                //"float _NearClip=1.0;"+
                "float _MaxRadSq=2.45;"+
                "vec4 pos;"+
                /*
                "void main()                   \n"+
                "{                              \n"+
                "   vColor = aColor;          \n"+
                // It will be interpolated across the triangle.
                "   gl_Position = uMVPMatrix* aPosition;    \n"+
                "}                             \n";    */
                        "void main() {" +
                        "  pos=uMVMatrix * aPosition;"+
                        "  vColor = aColor;" +
                        //"if(pos.z<=-_NearClip){"   +
                        "  r2=clamp(dot(pos.xy,pos.xy)/(pos.z*pos.z),0.0,_MaxRadSq);"+
                        "  pos.xy *=1.0+(_Undistortion.x+_Undistortion.y*r2)*r2;"+
                        //"}" +
                        "  gl_Position=uPMatrix*pos;"+
                        "}";
    }
    public static String getFragmentShader2Tesselated(){
        return "precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a
                // precision in the fragment shader.
                + "varying vec4 vColor;          \n"		// This is the color from the vertex shader interpolated across the
                // triangle per fragment.
                + "void main()                    \n"		// The entry point for our fragment shader.
                + "{                              \n"
                + "   gl_FragColor = vColor;     \n"		// Pass the color directly through the pipeline.
                + "}                              \n";
    }
    //Program for drawing the kopter,home arrow and height lines/for colours
    public static String getVertexShader2(){
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
    public static String getFragmentShader2(){
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


