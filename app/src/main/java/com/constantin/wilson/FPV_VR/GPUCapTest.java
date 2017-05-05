package com.constantin.wilson.FPV_VR;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/*Test the Capabilities of the GPU and other Phone specific stuff. Only run once, when the App is run the first time.
* Settings are stored as SharedPreferences in phoneinfo.xml*/
public class GPUCapTest extends AppCompatActivity {
    SharedPreferences settings,phoneInfo;
    String GPUCap;
    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        GLSurfaceView mGLView;
        mGLView = new GLSurfaceView(this);
        mGLView.setEGLContextClientVersion(2);
        mGLView.setRenderer(new CheckGLRenderer());
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mGLView.requestRender();
        setContentView(mGLView);
    }
    private class CheckGLRenderer implements GLSurfaceView.Renderer{
        @Override
        public void onSurfaceCreated(GL10 glUnused, EGLConfig config){
            GPUCap= GLES20.glGetString(GLES20.GL_EXTENSIONS);
            //System.out.println(GPUCap);
            phoneInfo = getSharedPreferences("phoneinfo", MODE_PRIVATE);
            settings= PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor=phoneInfo.edit();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                editor.putBoolean("MinAndroid7",true);
                editor.commit();
            }else{
                editor.putBoolean("MinAndroid7",false);
                editor.commit();
            }
            if(GPUCap.contains("GL_QCOM_tiled_rendering")){
                editor.putBoolean("GL_QCOM_tiled_rendering",true);
                editor.commit();
                editor.putBoolean("MSAA",true);
                editor.commit();
            }else{
                editor.putBoolean("GL_QCOM_tiled_rendering",false);
                editor.commit();
                editor.putBoolean("MSAA",false);
                editor.commit();
            }
            if(GPUCap.contains("QCOM")){
                editor.putBoolean("QCOM",true);
                editor.commit();
            }
            if(phoneInfo.getBoolean("MinAndroid7",false)&&phoneInfo.getBoolean("GL_QCOM_tiled_rendering",false)){
                SharedPreferences.Editor editor2=settings.edit();
                editor2.putBoolean("FBR",true);
                editor2.commit();
            }
            editor.putBoolean("FirstStart",false);
            editor.commit();
            finish();
        }
        @Override
        public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        }
        @Override
        public void onDrawFrame(GL10 glUnused) {
        }
    }
}