package com.constantin.wilson.FPV_VR;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class GLActivityMono extends AppCompatActivity {
    private GLSurfaceView mGLView;
    private boolean RequestRenderer=false;
    private volatile boolean running=false;
    String TAG="";
    private GLRendererMono mRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature((Window.FEATURE_NO_TITLE));
        super.onCreate(savedInstanceState);
        mGLView = new MyGLSurfaceView(this);
        //mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(mGLView);

        if(RequestRenderer){
            mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            running=true;
            Thread requestThread=new Thread(){
                @Override
                public void run() {
                    while(running){
                        mGLView.requestRender();
                        try {Thread.sleep(0,10);} catch (InterruptedException e) {e.printStackTrace();}
                    }
                }
            };
            requestThread.start();
        }

    }
    @Override
    protected void onPause(){
        super.onPause();
        mGLView.onPause();
    }
    @Override
    protected void onResume(){
        super.onResume();
        mGLView.onResume();
    }

    private class MyGLSurfaceView extends GLSurfaceView /*implements GLSurfaceView.EGLWindowSurfaceFactory*/{
        private Context mContext;

        public MyGLSurfaceView(Context context) {
            super(context);
            mContext = context;
            setEGLContextClientVersion(2);
            mRenderer = new GLRendererMono(mContext);
            setRenderer(mRenderer);
        }

        @Override
        public void onPause() {
            super.onPause();
            //mRenderer.onPause();
            //System.out.println("OGLActivity" + "On Pause");
        }

        @Override
        public void onResume() {
            super.onResume();
            //mRenderer.onResume();
            //System.out.println("OGLActivity" + "On Resume");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            running=false;
            if(mRenderer!=null){
                mRenderer.onSurfaceDestroyed();
            }
            //System.out.println("OGLActivity" + "Surface Destroyed");
            mRenderer=null;
        }


        /*@Override
        public boolean onTouchEvent(MotionEvent e) {
            Log.d("", "Hello");
            if(e.getActionMasked()==MotionEvent.ACTION_UP){
                if(mRenderer != null){
                    mRenderer.onTap();
                }
            }
            return true;
        }*/

        /*@Override
        public EGLSurface createWindowSurface(EGL10 egl10, EGLDisplay eglDisplay, EGLConfig eglConfig, Object o) {
            egl10.eglCreateWindowSurface(eglDisplay,eglConfig,o,null);
            return null;
        }

        @Override
        public void destroySurface(EGL10 egl10, EGLDisplay eglDisplay, EGLSurface eglSurface) {
            egl10.eglDestroySurface(eglDisplay,eglSurface);
        }*/
        /*
        @Override
        public void onAccuracyChanged(Sensor sensor,int accuracy){
        }

        @Override
        public void onSensorChanged(SensorEvent event){
            float[] orientationValues=new float[3];
            float[] tempRotationM=new float[3];
            float[] rotationM=new float[4*4];
            switch(event.sensor.getType()){
                case Sensor.TYPE_ROTATION_VECTOR:
                    SensorManager.getRotationMatrixFromVector(rotationM, event.values);
                    //SensorManager.remapCoordinateSystem(tempRotationM,0,0,rotationM);
                    SensorManager.getOrientation(rotationM,orientationValues);

                    double x=-90-Math.toDegrees(orientationValues[2]);
                    mRenderer.angle_x=x;
                    //System.out.println("X: "+x);
                    double y=Math.toDegrees(orientationValues[0]);
                    mRenderer.angle_y=y;
                    //System.out.println("Y: "+y);
                    /*
                    double z=-2*Math.toDegrees(orientationValues[1]);
                    mRenderer.angle_z=z;
                    System.out.println("Z: "+z);
                    break;
                default: Log.w("OGLActivity","Unknown Sensor Event");

            }
        }*/
    }


}

