package com.constantin.wilson.FPV_VR;

import static com.constantin.wilson.FPV_VR.VsyncHelper.nanoToMs;

/**
 * Created by Constantin on 29.04.2017.
 */

public class FrameTimes {
    private static boolean PRINT_FRAME_LOG=false;
    private static boolean PRINT_AVG_FRAME_LOG=true;
    private static boolean P_EYE_FAILED_WARNING=false;
    public static boolean P_VSYNC_WAIT_TIME=false;
    private double frameStart=0;
    private double leftEyeStart,rightEyeStart;
    private String gnuPlotS="#Elapsed Time|EyeCPUTime|EyeGPUTime|CPU+GPUTime";
    private double gnuElapsedTS=0;

    private int fc=0;
    private String frameLog;
    private String avgFrameLog;
    private static double frameTimeKullanz; //1ms

    public double nFrames=0,nFramesRF=0; //number of frames DT was smaller than rendering time (TEARING)
    //public double nLeftEyesToR=0,nLeftEyeRF=0,nRightEyesToR=0,nRightEyeRF=0;

    private double leftEyeCPUTime=0,leftEyeGPUTime=0,rightEyeCPUTime=0,rightEyeGPUTime=0,frameTime=0;
    private double leftEyeCPUTimeSum=0,leftEyeGPUTimeSum=0,rightEyeCPUTimeSum=0,rightEyeGPUTimeSum=0,frameTimeSum=0;

    private double leftEyeCPUTimeSumC=0,rightEyeCPUTimeSumC=0,leftEyeGPUTimeSumC=0,rightEyeGPUTimeSumC=0;

    private double vsyncStartWTSum=0,vsyncStartWTSumC=0,vsyncMiddleWTSum=0,vsyncMiddleWTSumC=0;

    private double nLeftEyesFailed=0,nRightEyesFailed=0;
    private double nLeftEyesToR=0,nRightEyesToR=0;

    private double nLeftEyesSkipped=0,nLeftEyesC=0;

    //Used for %frames failed
    public double frameRT=10.0;  //Rendering a frame takes 10ms = 100fps
    public double EyeRT=0.5*frameRT; //assume rendering a eye takes half as long as the whole frame

    double frameDT;

    private boolean thisRightEyeRendered=true,thisLeftEyeRendered;

    private double vsyncStartWaitTime,vsyncMiddleWaitTime;

    FrameTimes(long frameDTNS,long vsyncStartKullanzNS){
        this.frameDT=frameDTNS*VsyncHelper.nanoToMs;
        frameTimeKullanz=(vsyncStartKullanzNS*nanoToMs)+0.5;
        //this.EyeRT=EyeRT;
    }
    public void onFrameStart(){
        frameStart=getTimeMs();

    }
    public void onFrameStop(){
        nFrames++;
        frameTime=getTimeMs()-frameStart;
        frameTimeSum+=frameTime;
        if(frameTime>frameDT+frameTimeKullanz){
            nFramesRF++;
        }
        fc++;
        if(fc>=60){
            if(PRINT_AVG_FRAME_LOG){
                avgFrameLog="*------------------------------------------------------------------*\n"+
                        "Avg. RightEye CPU Time:"+rightEyeCPUTimeSum/rightEyeCPUTimeSumC+
                        " | Avg. RightEye GPU Time"+rightEyeGPUTimeSum/rightEyeGPUTimeSumC+
                        "\nAvg. LeftEye  CPU Time:"+leftEyeCPUTimeSum/leftEyeCPUTimeSumC+
                        " | Avg. LeftEye  GPU Time:"+leftEyeGPUTimeSum/leftEyeGPUTimeSumC+
                        "\nAvg. FrameTime:"+frameTimeSum/nFrames+
                        "\nAvg. vsyncStart Wait Time:"+vsyncStartWTSum/vsyncStartWTSumC+
                        " | Avg.vsyncMiddle Wait Time:"+vsyncMiddleWTSum/vsyncMiddleWTSumC+
                        "\n----    ----    ----    ----    ----    ----    ----    ----    "+
                        "\n%Frames failed:" + nFramesRF / (nFrames) * 100 +
                        "\n%Left Eyes skipped"+nLeftEyesSkipped/nLeftEyesC*100+
                        "\n%Right Eyes Failed:" + nRightEyesFailed/nRightEyesToR * 100 +
                        "\n%Left Eyes Failed:" + nLeftEyesFailed / nLeftEyesToR * 100 +
                        //"%Eyes CPU failed"+nEyesCPUTooSLow/(nRightEyes+nLeftEyes)*100+

                        "\n*------------------------------------------------------------------*";
                System.out.println(avgFrameLog);
            }
            fc=0;
        }
        if(PRINT_FRAME_LOG){
            if(!thisLeftEyeRendered){
                leftEyeCPUTime=0;
                leftEyeGPUTime=0;
                vsyncMiddleWaitTime=0;
            }
            frameLog="*------------------------------------------------------------------*\n";
            frameLog+="\nRight Eye rendered:"+thisRightEyeRendered+"Left Eye rendered:"+thisLeftEyeRendered;
            frameLog+="\nRightEye CPU Time:"+rightEyeCPUTime;
            frameLog+="\nRightEye GPU Time"+rightEyeGPUTime;
            frameLog+="\nRightEye CPU&GPU Time:"+(rightEyeCPUTime+rightEyeGPUTime);
            frameLog+="\nLeftEye CPU Time:"+leftEyeCPUTime;
            frameLog+="\nLeftEye GPU Time"+leftEyeGPUTime;
            frameLog+="\nLeftEye CPU&GPU Time:"+(leftEyeCPUTime+leftEyeGPUTime);
            double frameCPUGPUTime=rightEyeCPUTime+rightEyeGPUTime+leftEyeCPUTime+leftEyeGPUTime;
            frameLog+="\nFrameCPUGPUTime"+frameCPUGPUTime;
            frameLog+="\nVsyncStart wait time:"+vsyncStartWaitTime;
            frameLog+="\nVsyncMiddle wait time:"+vsyncMiddleWaitTime;
            frameLog+="\nFrameCPUGPUVsyncTime:"+(frameCPUGPUTime+vsyncStartWaitTime+vsyncMiddleWaitTime);
            frameLog+="\nFrameTime:"+frameTime;
            frameLog+="\n*------------------------------------------------------------------*";
            System.out.println(frameLog);
        }
    }

    public void onLeftEyeRenderingStart(){
        leftEyeStart=getTimeMs();

    }
    public void onRightEyeRenderingStart(){
        rightEyeStart=getTimeMs();

    }
    public void onRightEyeCPUStop(){
        rightEyeCPUTime=getTimeMs()-rightEyeStart;
        rightEyeCPUTimeSum+=rightEyeCPUTime;
        rightEyeCPUTimeSumC++;

    }
    public void onRightEyeGPUStop(){
        rightEyeGPUTime=getTimeMs()-rightEyeStart-rightEyeCPUTime;
        rightEyeGPUTimeSum+=rightEyeGPUTime;
        rightEyeGPUTimeSumC++;
        nRightEyesToR++;
        if(rightEyeCPUTime+rightEyeGPUTime>EyeRT){
            nRightEyesFailed++;
            if( P_EYE_FAILED_WARNING){System.out.println("Calc&Rendering right eye took too long. RT:"+(rightEyeCPUTime+rightEyeGPUTime)+"ms"+
                    "  %Right Eyes Failed:"+nRightEyesFailed/nRightEyesToR*100);
            }
        }
        /*xxxxxxxxxxxxxxxxxxxxxxxx
        gnuPlotS+="\n#right Eye\n";
        gnuPlotS+="\n"+(System.currentTimeMillis()-gnuElapsedTS);
        gnuPlotS+=" "+rightEyeCPUTime;
        gnuPlotS+=" "+rightEyeGPUTime;
        gnuPlotS+=" "+(rightEyeCPUTime+rightEyeGPUTime);*/


    }
    public void onLeftEyeCPUStop(){
        leftEyeCPUTime=getTimeMs()-leftEyeStart;
        leftEyeCPUTimeSum+=leftEyeCPUTime;
        leftEyeCPUTimeSumC++;
    }

    public void onLeftEyeGPUStop(){
        leftEyeGPUTime=getTimeMs()-leftEyeStart-leftEyeCPUTime;
        leftEyeGPUTimeSum+=leftEyeGPUTime;
        leftEyeGPUTimeSumC++;
        nLeftEyesToR++;
        if(leftEyeCPUTime+leftEyeGPUTime>EyeRT){
            nLeftEyesFailed++;
            if( P_EYE_FAILED_WARNING){System.out.println("Calc&Rendering left eye took too long. RT:"+(leftEyeCPUTime+leftEyeGPUTime)+"ms"+
                    "  %Left Eyes Failed:"+nLeftEyesFailed/nLeftEyesToR*100);
            }
        }

        /*xxxxxxxxxxxxxxxxxxxxxxxx
        gnuPlotS+="\n#left Eye\n";
        gnuPlotS+="\n"+(System.currentTimeMillis()-gnuElapsedTS);
        gnuPlotS+=" "+rightEyeCPUTime;
        gnuPlotS+=" "+rightEyeGPUTime;
        gnuPlotS+=" "+(rightEyeCPUTime+rightEyeGPUTime);
        if((System.currentTimeMillis()-gnuElapsedTS)>4000&&(System.currentTimeMillis()-gnuElapsedTS)<4400){
            System.out.println(gnuPlotS);
            Log.d("",gnuPlotS);
        }*/

    }
    public void onWaitUntilVsyncStart(double time){
        this.thisRightEyeRendered=true; //always gets rendered
        vsyncStartWaitTime=time;
        vsyncStartWTSum+=time;
        vsyncStartWTSumC++;

    }
    public void onWaitUntilVsyncMiddle(double time,boolean thisLeftEyeRendered){
        this.thisLeftEyeRendered=thisLeftEyeRendered;
        nLeftEyesC++;
        if(thisLeftEyeRendered==true){
            vsyncMiddleWaitTime=time;
            vsyncMiddleWTSum+=time;
            vsyncMiddleWTSumC++;
        }else {
            nLeftEyesSkipped++;
        }
        vsyncMiddleWaitTime=time;
    }

    //even though in ms the resolution is in ns
    public static double getTimeMs(){
        return System.nanoTime()*nanoToMs;
    }
}
