package com.constantin.wilson.FPV_VR;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class GLActivityStereoFB extends Activity /*implements View.OnTouchListener*/{
    private GLSurfaceViewFB mGLViewEGL14;
    String TAG="";
    private GLRendererStereoFB mRendererEGL14;
    //private GLRendererStereoFBLatencyTester mRendererEGL14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature((Window.FEATURE_NO_TITLE));
        super.onCreate(savedInstanceState);
        mGLViewEGL14 = new MyGLSurfaceViewEGL14(this);
        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }*/
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(mGLViewEGL14);
        mGLViewEGL14.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        //mGLViewEGL14.setOnTouchListener(this);

    }

    @Override
    protected void onPause(){
        super.onPause();
        mRendererEGL14.running=false;
        mGLViewEGL14.onPause();
    }
    @Override
    protected void onResume(){
        super.onResume();
        mGLViewEGL14.onResume();
    }
    //@Override
    public boolean onTouch(View v,MotionEvent e) {
        System.out.println("TOUCH");
        if(e.getActionMasked()==MotionEvent.ACTION_UP){
                if(mRendererEGL14!= null){
                    mRendererEGL14.onTap();
                }
        }
        return true;
    }

    private class MyGLSurfaceViewEGL14 extends GLSurfaceViewFB {
        private Context mContext;

        public MyGLSurfaceViewEGL14(Context context) {
            super(context);
            mContext = context;
            //setEGLContextClientVersion(2);
            //setEGLConfigChooser(8,8,8,0,0,0);
            //setEGLConfigChooser(mConfigChooser);
            //setEGLConfigChooser(false);
            //setEGLWindowSurfaceFactory(mWindowSurfaceFactory);
            //setEGLContextClientVersion(3);
            mRendererEGL14 = new GLRendererStereoFB(mContext);
            //mRendererEGL14 =new GLRendererStereoFBLatencyTester(mContext);
            setRenderer(mRendererEGL14);
        }

        @Override
        public void onPause() {
            super.onPause();
            //mRenderer.onPause();
            //System.out.println("OGLActivity" + "On Pause");
            if(mRendererEGL14 !=null){
                mRendererEGL14.running=false;
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            //mRenderer.onResume();
            //System.out.println("OGLActivity" + "On Resume");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if(mRendererEGL14!=null){
                mRendererEGL14.running=false;
                mRendererEGL14.onSurfaceDestroyed();
            }
        }
    }
}

