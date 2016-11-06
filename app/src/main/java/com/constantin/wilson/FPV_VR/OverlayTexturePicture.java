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

    public static int number_of_units=10+10+(5*3)+(14*3);
    public static float heightO=1.0f/number_of_units*20;
    public static float stringO=1.0f/number_of_units*35;
    private int unitWidth=50,unitHeight=50;
    private int atlasWidth=number_of_units*unitWidth;
    private int atlasHeight=unitHeight;

    public  Bitmap draw(){
        //Texture Atlas
        bmp=Bitmap.createBitmap(atlasWidth,atlasHeight, Bitmap.Config.ARGB_8888);
        bmp.setHasAlpha(true);
        bmp.eraseColor(Color.TRANSPARENT);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        c=new Canvas(bmp);
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
        //draw our height markers
        textPaint.setColor(Color.WHITE);
        c.drawText("100m",20*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText("75m", 23*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText("50m", 26*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText("25m", 29*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText("0m" , 32*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        //draw our 14 strings for overlay units
        textPaint.setColor(Color.WHITE);
        c.drawText("fpsD",35*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText("fpsGL",38*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":Lat",41*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":Lon",44*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText("%Bat.",47*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":V",50*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":Rssi",53*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":A",56*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":X",59*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":X",62*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":km/h",65*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(".X",68*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":m(Ba)",71*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        c.drawText(":m(g)",74*unitWidth,unitHeight-((int)(unitHeight/5)), textPaint);
        return bmp;
    }
}
