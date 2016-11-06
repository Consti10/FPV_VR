package com.constantin.wilson.FPV_VR;


import android.opengl.GLES20;
import android.opengl.Matrix;

public class OGLProgramTex {
    public int mProgram;
    public int mPositionHandle;
    public int mTextureHandle;
    public   int mMVMatrixHandle;
    public int mPMatrixHandle;
    public int mSamplerLoc;
    private boolean mDistortionCorrection;
    private int mMVPMatrixHandle;
    private float[] mMVPM=new float[16];

    public OGLProgramTex(boolean distortionCorrection){
        mDistortionCorrection =distortionCorrection;
        if(mDistortionCorrection){
            mProgram = OpenGLHelper.createProgram(getVertexShader3Tesselated(), getFragmentShader3Tesselated());
        }else{
            mProgram=OpenGLHelper.createProgram(getVertexShader3(), getFragmentShader3());
        }
        if(mDistortionCorrection){
            mMVMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uMVMatrix");
            mPMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uPMatrix");
        }else{
            mMVPMatrixHandle=GLES20.glGetUniformLocation(mProgram,"uMVPMatrix");
        }
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        mSamplerLoc = GLES20.glGetUniformLocation (mProgram, "sTexture" );
        OpenGLHelper.checkGlError("glGetAttribLocation sTexture");
    }
    public void beforeDraw(int textureId,int vertB,int uvB){
        GLES20.glUseProgram(mProgram);
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
        GLES20.glUniform1i(mSamplerLoc, 0);
    }
    public void draw(float[] modViewM,float[] projM,int trianglesOff,int numTriangles){
        if(mDistortionCorrection){
            GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, modViewM, 0);
            GLES20.glUniformMatrix4fv(mPMatrixHandle, 1, false, projM, 0);
        }else{
            Matrix.multiplyMM(mMVPM, 0, projM, 0, modViewM, 0);
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPM, 0);
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, trianglesOff,numTriangles);
    }
    public  void  afterDraw(){
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureHandle);
    }

    //Program for drawing overlay /overlayUnits and heights /for textures
    public static String getVertexShader3Tesselated(){
        return  //"uniform mat4 uMVPMatrix;" +
                "attribute vec4 aPosition;" +
                "attribute vec2 aTexCoord;" +
                "varying vec2 vTexCoord;" +

                "uniform mat4 uMVMatrix;" +
                 "uniform mat4 uPMatrix;" +
                "float r2;"+
                "vec2 _Undistortion=vec2(-0.18,0.0);"+
                //"float _NearClip=1.0;"+
                "float _MaxRadSq=2.45;"+
                "vec4 pos;"+
                //"float _RealProjection;"+
                //"float _FixProjection;"+
                /*"void main() {" +
                "  gl_Position = uMVPMatrix * aPosition;" +
                "  v_texCoord = a_texCoord;" +
                "}";*/
                "void main() {" +
                "  pos=uMVMatrix * aPosition;"+
                "  vTexCoord = aTexCoord;" +
                        "r2=clamp(dot(pos.xy,pos.xy)/(pos.z*pos.z),0.0,_MaxRadSq);"+
                        "pos.xy *=1.0+(_Undistortion.x+_Undistortion.y*r2)*r2;"+
                        //"if((x2-r2)!=0.0){ pos.xyz=vec3(0.0,0.0,100.0);}"+
                        "gl_Position=uPMatrix*pos;"+
                 //       "}" +
                "}";
    }
    public static String getFragmentShader3Tesselated(){
        return "precision mediump float;" +
                "varying vec2 vTexCoord;" +
                "uniform sampler2D sTexture;" +
                "void main() {" +
                "  gl_FragColor = texture2D( sTexture, vTexCoord );" +
                //"  gl_FragColor =vec4(0.5,0,0,1);" +
                "}";
    }

    //Program for drawing overlay /overlayUnits and heights /for textures
    public static String getVertexShader3(){
        return  "uniform mat4 uMVPMatrix;" +
                "attribute vec4 aPosition;" +
                 "attribute vec2 aTexCoord;" +
                 "varying vec2 vTexCoord;" +

                "void main() {" +
                "  gl_Position = uMVPMatrix * aPosition;" +
                "  vTexCoord = aTexCoord;" +
                "}";

    }
    public static String getFragmentShader3(){
        return "precision mediump float;" +
                "varying vec2 vTexCoord;" +
                "uniform sampler2D sTexture;" +
                "void main() {" +
                "  gl_FragColor = texture2D( sTexture, vTexCoord );" +
                //"  gl_FragColor =vec4(0.5,0,0,1);" +
                "}";
    }
}
