package com.constantin.wilson.FPV_VR;

import android.opengl.GLES20;

/**
 * Created by Constantin on 30.12.2016.
 * Since a normal glClear flushes the pipeline in Adreno and therefore cannot be used, this Program clears the whole
 * viewport specified at the call time with blue/black
 */

public class GLProgramColorClear {
    private int mPositionHandle;
    private int mProgram;
    private int buffer;

    public GLProgramColorClear() {
        int buffers[]=new int[1];
        GLES20.glGenBuffers(1, buffers, 0);
        buffer=buffers[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        float[] vertices={
                -1,1,0,1,
                -1,-1,0,1,
                1,-1,0,1,

                -1,1,0,1,
                1,-1,0,1,
                 1,1,0,1
        };
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
                GLHelper.getFloatBuffer(vertices).capacity() * 4,
                GLHelper.getFloatBuffer(vertices),
                GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        mProgram= GLHelper.createProgram(getVertexShader(), getFragmentShader());
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GLHelper.checkGlError("glGetAttribLocation OGProgramColorClear");
    }
    public void beforeDraw(){
        GLES20.glUseProgram(mProgram);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 4, GLES20.GL_FLOAT, false,4*4, 0);
    }
    public void draw(){
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0,6);
    }
    public void afterDraw(){
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }



    public static String getVertexShader(){
        return //"uniform mat4 uMVPMatrix;"+
                "attribute vec4 aPosition;"+
                "void main()                   \n"+
                "{                              \n"+
                //"   gl_Position = uMVPMatrix* aPosition;    \n"+
                "   gl_Position = aPosition;    \n"+
                //"   gl_Position = vec4(aPosition.xy,0.0,1.0);    \n"+
                //"   gl_Position.xy = aPosition.xy;    \n"+
                "}                             \n";
    }
    public static String getFragmentShader(){
        return "precision mediump float;       \n"
                + "void main()                    \n"
                + "{                              \n"
                +"  gl_FragColor =vec4(0.0,0,0.05,1);"
                + "}                              \n";
    }
}
