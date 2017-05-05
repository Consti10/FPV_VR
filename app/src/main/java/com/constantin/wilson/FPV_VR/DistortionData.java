package com.constantin.wilson.FPV_VR;

import android.content.SharedPreferences;
import android.os.Debug;

import com.google.vrtoolkit.cardboard.Distortion;

/**
 * Created by Constantin on 23.04.2017.
 * class for creating coefficients for vertex displacement distortion correction (k1..k6 and _MaxRadSq)
 *r2=1.5 means about a circle with diameter left-viewp. to right viewp. Even almost a litlebit more
 * r2=1.0 means about a circle with diameter I would consider to be roughly true for vr. Maybe 80% of left-right diameter
 */

public class DistortionData {
    public Distortion mDistortion;
    public static final int CARDBOARD_DISTORTION = 0, GEARVR_DISTORTION = 1, DAYDREAM_DISTORTION = 2;
    public float distortionFactor;
    public float mFOV;
    //private float[] coefficients;

    public DistortionData(SharedPreferences settings) {
        mDistortion = new Distortion();
        switch(settings.getString("headset","CV2")){
            //Cardboard Version 1
            case "CV1":mDistortion.setCoefficients(new float[]{0.441f, 0.156f});
                mFOV=40;break;
            //Cardboard Version 2
            case "CV2":mDistortion.setCoefficients(new float[]{0.34f, 0.55f});
                mFOV=60;break;
            //GearVR
            case "GVR":mDistortion.setCoefficients(new float[]{0.215f,0.215f});
                mFOV=45;break;
            //Daydream
            case "DD":mDistortion.setCoefficients(new float[]{0.42f,0.51f});
                mFOV=45;break;
            case "Manually":float k1,k2;
                mFOV=180;
                try{
                    k1 =Float.parseFloat(settings.getString("k1","0.15"));
                    k2 =Float.parseFloat(settings.getString("k2","0.15"));
                }catch (Exception e){e.printStackTrace();k1=0;k2=0;}
                mDistortion.setCoefficients(new float[]{k1,k2});break;
        }
        //float[] coefficients={0.215f,0.215f}; //GearVr ?
        //float[] coefficients={0.42f,0.51f}; //Daydream ?
        //Data from gvr_unity_sdk:
        //float[] coefficients = {0.441f, 0.156f}; //Cardboard v1; confirmed by DesignLab file
        //float[] coefficients={0.34f, 0.55f}; //Cardboard v2
        //float[] coefficients = {0.1f, 0.1f};
        //mDistortion.setCoefficients(coefficients);
    }

    //returns _MaxRadSq and k1,k2,k3,k4,k5,k6
    public float[] getUndistortionCoeficients() {
        float[] results = new float[6+1];
        //float maxFovHalfAngle = (float)(50.0f * Math.PI / 180.0f);
        float maxFovHalfAngle = (float) (mFOV * Math.PI / 180.0f);
        float maxRadiusLens = mDistortion.distortInverse(maxFovHalfAngle);
        //maxRadiusLens=maxFovHalfAngle;
        //System.out.println(maxRadiusLens);
        //maxRadiusLens=1.0f;
        Distortion inverse = mDistortion.getApproximateInverseDistortion(maxRadiusLens,6);
        System.out.println("radius" + maxRadiusLens);
        System.out.println(inverse.toString());
        results[0] = maxRadiusLens;
        results[1] = inverse.getCoefficients()[0];
        results[2] = inverse.getCoefficients()[1];
        results[3] = inverse.getCoefficients()[2];
        results[4] = inverse.getCoefficients()[3];
        results[5] = inverse.getCoefficients()[4];
        results[6] = inverse.getCoefficients()[5];
        //results[0]=-0.18f;
        //results[1]=0;
        //results[2]=2.45f;
        return results;
    }

}