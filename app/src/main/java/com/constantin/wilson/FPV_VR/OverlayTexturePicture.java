package com.constantin.wilson.FPV_VR;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * Created by Constantin on 29.10.2016.
 */

public class OverlayTexturePicture {
    private Canvas c;
    private Paint textPaint;
    private Bitmap bmp;

    public static int number_of_units=10+10+1+(5*3)+(14*3); //36+42=78
    public static float heightO=1.0f/number_of_units*21;
    public static float stringO=1.0f/number_of_units*36;
    public static int nQuadsPerUnit=7;
    public static int nNumberQuadsPerUnit=4;
    private int unitWidth=50,unitHeight=50;
    private int atlasWidth=number_of_units*unitWidth;
    private int atlasHeight=unitHeight;

    public  Bitmap draw(){
        //Texture Atlas
        bmp=Bitmap.createBitmap(atlasWidth,atlasHeight, Bitmap.Config.ARGB_8888);
        bmp.setHasAlpha(true);
        c=new Canvas(bmp);
        bmp.eraseColor(Color.TRANSPARENT);
        textPaint=new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(unitHeight);
        //Draw our number Atlas
        c.drawText("0",0*unitWidth,unitHeight-(unitHeight/5),textPaint);
        c.drawText("1",1*unitWidth,unitHeight-(unitHeight/5),textPaint);
        c.drawText("2",2*unitWidth,unitHeight-(unitHeight/5),textPaint);
        c.drawText("3",3*unitWidth,unitHeight-(unitHeight/5),textPaint);
        c.drawText("4",4*unitWidth,unitHeight-(unitHeight/5),textPaint);
        c.drawText("5",5*unitWidth,unitHeight-(unitHeight/5),textPaint);
        c.drawText("6",6*unitWidth,unitHeight-(unitHeight/5),textPaint);
        c.drawText("7",7*unitWidth,unitHeight-(unitHeight/5),textPaint);
        c.drawText("8",8*unitWidth,unitHeight-(unitHeight/5),textPaint);
        c.drawText("9",9*unitWidth,unitHeight-(unitHeight/5),textPaint);
        //
        c.drawText("0.",10*unitWidth,unitHeight-(unitHeight/5),textPaint);
        c.drawText("1.",11*unitWidth,unitHeight-(unitHeight/5),textPaint);
        c.drawText("2.",12*unitWidth,unitHeight-(unitHeight/5),textPaint);
        c.drawText("3.",13*unitWidth,unitHeight-(unitHeight/5),textPaint);
        c.drawText("4.",14*unitWidth,unitHeight-(unitHeight/5),textPaint);
        c.drawText("5.",15*unitWidth,unitHeight-(unitHeight/5),textPaint);
        c.drawText("6.",16*unitWidth,unitHeight-(unitHeight/5),textPaint);
        c.drawText("7.",17*unitWidth,unitHeight-(unitHeight/5),textPaint);
        c.drawText("8.",18*unitWidth,unitHeight-(unitHeight/5),textPaint);
        c.drawText("9.",19*unitWidth,unitHeight-(unitHeight/5),textPaint);
        //draw our "-"
        textPaint.setTextSize(unitHeight+30);
        c.drawText(" -",20*unitWidth,unitHeight-(unitHeight/9),textPaint);
        textPaint.setTextSize(unitHeight);
        //draw our height markers
        textPaint.setColor(Color.WHITE);
        c.drawText("100m",21*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText("75m", 24*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText("50m", 27*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText("25m", 30*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText("0m" , 33*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        //draw our 14 strings for overlay units
        textPaint.setColor(Color.WHITE);
        c.drawText("fpsD",36*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText("fpsGL",39*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":Lat",42*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":Lon",45*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText("%Bat.",48*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":V",51*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":Rssi",54*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":A",57*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":m(Ho)",60*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":X",63*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":km/h",66*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(".X",69*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":m(Ba)",72*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":m(G)",75*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        return bmp;
    }
    public void recycle(){
        bmp.recycle();
    }
}
