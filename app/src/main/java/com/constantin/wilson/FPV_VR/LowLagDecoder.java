package com.constantin.wilson.FPV_VR;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import java.nio.ByteBuffer;

/**
 * Created by Constantin on 26.10.2016.
 * Has a lot of features to reduce latency included,
 * most of them are found by experimenting
 * using MediaCodec API. some compability-problems with different chipsets
 */

public class LowLagDecoder {
    private SharedPreferences settings;
    private Surface mSurface;
    private Context mContext;
    private ByteBuffer[] inputBuffers;
    private ByteBuffer[] outputBuffers;
    private MediaCodec.BufferInfo info;
    private MediaCodec decoder;
    private MediaFormat format;

    public volatile boolean running=true;
    public boolean decoderConfigured=false;
    public int current_fps=0;
    public long averageWaitForInputBufferLatency=0;

    private boolean DecoderMultiThread=true;
    private boolean userDebug=false;
    private boolean formatRPI=false;
    //public boolean formatStream=false;
    private int zaehlerFramerate=0;
    private long timeB2=0;
    private ByteBuffer inputBuffer;
    private int fps;
    private int outputBufferIndex;
    private int inputBufferIndex;
    private long latency;
    private long fpsSum=0,fpsCount=0,averageDecoderfps=0;
    private long HWDecoderlatencySum=0;
    private int outputCount=0;
    private long averageHWDecoderLatency=0;
    private long presentationTimeMs=0;
    public int width=0,height=0;
    public volatile float lastVideoFormat;
    public volatile boolean videoFormatChanged;

    public LowLagDecoder(Surface surface,Context context){
        mContext=context;
        mSurface=surface;
        settings= PreferenceManager.getDefaultSharedPreferences(mContext);
        DecoderMultiThread=settings.getBoolean("decoderMultiThread", true);
        formatRPI=settings.getBoolean("formatRPI",true);
        try{
            lastVideoFormat=Float.parseFloat(settings.getString("videoFormat","1.3333"));
        }catch(Exception e){e.printStackTrace();}
        //formatStream=settings.getBoolean("formatStream",false);
        userDebug=settings.getBoolean("userDebug", false);
        if(userDebug){
            SharedPreferences.Editor editor=settings.edit();
            if(settings.getString("debugFile","").length()>=5000){
                editor.putString("debugFile","new Session !\n");
            }else{
                editor.putString("debugFile",settings.getString("debugFile","")+"\n\nnew Session !\n");
            }
            editor.commit();
            makeToast("user debug enabled. disable for max performance");
        }
    }

    public void configureStartDecoder(ByteBuffer csd0,ByteBuffer csd1) {
        info = new MediaCodec.BufferInfo();
        try {
            if(settings.getString("decoder","HW").equals("SW")){
                //This Decoder Seems to exist on most android devices,but is pretty slow
                decoder=MediaCodec.createByCodecName("OMX.google.h264.decoder");
            }else{
                decoder = MediaCodec.createDecoderByType("video/avc");
            }
        } catch (Exception e) {
            System.out.println("Error creating decoder");
            handleDecoderException(e, "create decoder");
            running=false;
            return;
        }
        System.out.println("Codec Info: " + decoder.getCodecInfo().getName());
        if(userDebug){ makeToast("Selected decoder: " + decoder.getCodecInfo().getName());}
        format = MediaFormat.createVideoFormat("video/avc", 1920, 1080);
        /*if(formatStream){
            format.setByteBuffer("csd-0",csd0);
            format.setByteBuffer("csd-1", csd1);
        }*/
        if(formatRPI){
            format.setByteBuffer("csd-0",MediaCodecFormatHelper.getRpiCsd0());
            format.setByteBuffer("csd-1", MediaCodecFormatHelper.getRpiCsd1());
        }
        try {
            //This configuration will be overwritten anyway when we put an sps into the buffer
            //But: My decoder agrees with this,but some may not; to be improved
            decoder.configure(format, mSurface, null, 0);
            if (decoder == null) {
                System.out.println("Can't configure decoder!");
                if(userDebug){makeToast("Can't configure decoder!");}
                running=false;
                return;
            }
        } catch (Exception e) {
            System.out.println("error config decoder");
            handleDecoderException(e,"configure decoder");
        }
        decoder.start();
        decoderConfigured=true;
        if(DecoderMultiThread){
            Thread thread2=new Thread(){
                @Override
                public void run() {while(running){checkOutput();}}
            };
            //thread2.setPriority(Thread.MAX_PRIORITY);
            thread2.setPriority(Thread.NORM_PRIORITY);
            thread2.start();
        }
    }
    public void stopDecoder(){
        //System.out.println("HELLO FROM STOPDECODING");
        running=false;
        if(decoder!=null){
            try {
                decoder.signalEndOfInputStream();
            }catch (Exception e){handleDecoderException(e,"signal end of input");}
            try {
                decoder.flush();
            }catch (Exception e){handleDecoderException(e,"stopdecoder");}
            try{
                decoder.stop();
            }catch (Exception e){handleDecoderException(e,"flushDecoder");}
            try {
                decoder.release();
            }catch (Exception e){handleDecoderException(e,"releaseDecoder");}
        }
    }

    private void checkOutput() {
        //System.out.println("CheckOutput Thread Priority:"+Thread.currentThread().getPriority());
        try {
            //Thread.sleep(10000000);
            outputBufferIndex = decoder.dequeueOutputBuffer(info, 0);
            if (outputBufferIndex >= 0) {
                //
                zaehlerFramerate++;
                if((System.currentTimeMillis()-timeB2)>1000) {
                    fps = (zaehlerFramerate );
                    current_fps=(int)fps;
                    timeB2 = System.currentTimeMillis();
                    zaehlerFramerate = 0;
                    //Log.w("ReceiverDecoderThread", "fps:" + fps);
                    fpsSum+=fps;
                    fpsCount++;
                    format=decoder.getOutputFormat();
                    width=format.getInteger(MediaFormat.KEY_WIDTH);
                    height=format.getInteger(MediaFormat.KEY_HEIGHT);
                    if(((float)width/height)!=lastVideoFormat){
                        videoFormatChanged=true;
                    }else {
                        videoFormatChanged=false;
                    }
                }
                latency=((System.nanoTime()-info.presentationTimeUs)/1000000);
                if(latency>=0 && latency<=400){
                    outputCount++;
                    //System.out.println("HWDecoder latency"+latency);
                    HWDecoderlatencySum+=latency;
                    averageHWDecoderLatency=HWDecoderlatencySum/outputCount;
                    //Log.w("checkOutput 1","hw decoder latency:"+latency);
                    //Log.w("checkOutput 2","Average HW decoder latency:"+averageHWDecoderLatency);
                }
                //on my device this code snippet from Moonlight is not needed,after testing I doubt if it is really working at all;
                //if(decoder.dequeueOutputBuffer(info, 0) >= 0){ Log.w("...","second available");}
                //for GLSurfaceView,to drop the latest frames except the newest one,the timestamp has to be near the VSYNC signal.
                //requires android 5
                decoder.releaseOutputBuffer(outputBufferIndex,System.nanoTime()); //needs api 21
                //decoder.releaseOutputBuffer(outputBufferIndex,true);
                /*We got an output buffer. The next one probably won't be available in the next 5ms, so we can
                safely sleep that long without hurting performance
                 */
                Thread.sleep(5);
            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED || outputBufferIndex==MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                Log.d("UDP", "output format / buffers changed");
            } else if(outputBufferIndex!=MediaCodec.INFO_TRY_AGAIN_LATER) {
                Log.d("dequeueOutputBuffer", "not normal");
                if(userDebug){
                    makeToast("dequeueOutputBuffer;" + "not normal;" + "number:"+outputBufferIndex);
                    makeDebugFile("dequeueOutputBuffer;" + "not normal;" + "number:" + outputBufferIndex);
                }

            }
        }catch(Exception e) {
            handleDecoderException(e,"checkOutput");
        }
        /*to improve performance,sleep 1ms. increases lag maximum 1ms and improves performance a lot*/
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public void feedDecoder(byte[] n, int len) {
        //System.out.println("FEED NALU");
        while (running) {
            try {
                inputBuffers = decoder.getInputBuffers();
                inputBufferIndex = decoder.dequeueInputBuffer(0);
                if (inputBufferIndex >= 0) {
                    inputBuffer = inputBuffers[inputBufferIndex];
                    inputBuffer.put(n, 0, len);
                    presentationTimeMs=System.nanoTime();
                    decoder.queueInputBuffer(inputBufferIndex, 0, len,presentationTimeMs,0);
                    break;
                }else if(inputBufferIndex!=MediaCodec.INFO_TRY_AGAIN_LATER){
                    if(userDebug){
                        makeToast("queueInputBuffer unusual: "+inputBufferIndex);
                        makeDebugFile("queueInputBuffer unusual: "+inputBufferIndex);
                    }
                }
                if(!DecoderMultiThread){
                    checkOutput();
                }
            } catch (Exception e) {
                handleDecoderException(e,"feedDecoder");
            }
        }
        /*try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

    }

    public void writeLatencyFile(long OpenGLFpsSum,int OpenGLFpsCount){
        //Todo: measure time between realeasing output buffer and rendering it onto Screen
        /*
        Display mDisplay=getWindowManager().getDefaultDisplay();
        long PresentationDeadlineMillis=mDisplay.getPresentationDeadlineNanos()/1000000;
        Log.w(TAG,"Display:"+PresentationDeadlineMillis);
         */

        String lf=settings.getString("latencyFile","ERROR");
        if(lf.length()>=1000 || lf.length()<=20){
            lf="These values only show the measured lag of the app; \n"+
                    "The overall App latency may be much more higher,because you have to add the 'input lag' of your phone-about 32-48ms on android \n"+
                    "Every 'time' values are in ms. \n";
        }
        if(fpsCount==0){fpsCount=1;}
        if(OpenGLFpsCount==0){OpenGLFpsCount=1;}
        averageDecoderfps=fpsSum/fpsCount;
        lf+="\n Average HW Decoder fps: "+(averageDecoderfps);
        if(OpenGLFpsSum>=0){lf+="\n OpenGL as Output;Average OpenGL FPS: "+(OpenGLFpsSum/(long)OpenGLFpsCount);}
        lf+="\n Average measured app Latency: "+(averageWaitForInputBufferLatency+averageHWDecoderLatency);
        lf+="\n Average time waiting for an input Buffer:"+averageWaitForInputBufferLatency;
        lf+="\n Average time HW decoding:"+averageHWDecoderLatency;
        lf+="\n ";
        SharedPreferences.Editor editor=settings.edit();
        editor.putString("latencyFile",lf);
        editor.commit();

    }

    private void handleDecoderException(Exception e,String tag){
        if(userDebug) {
            makeToast("Exception on "+tag+": ->exception file");
            if (e instanceof MediaCodec.CodecException) {
                MediaCodec.CodecException codecExc = (MediaCodec.CodecException) e;
                makeDebugFile("CodecException on " + tag + " :" + codecExc.getDiagnosticInfo());
            } else {
                makeDebugFile("Exception on "+tag+":"+Log.getStackTraceString(e));
            }
            try {Thread.sleep(100,0);} catch (InterruptedException e2) {e2.printStackTrace();}
        }
        //e.printStackTrace();
    }
    private void makeToast(final String message) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void makeDebugFile(String message){
        SharedPreferences.Editor editor=settings.edit();
        editor.putString("debugFile",message+settings.getString("debugFile",""));
        editor.commit();
    }
}
