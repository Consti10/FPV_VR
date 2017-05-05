package com.constantin.wilson.FPV_VR;


import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GeometryHelper {

    private static float[] indices=new float[4*6*2];
    private static int kommastellen;
    private static int hundreds , tens , ones ;

    public static final int COORDS_PER_VERTEX = 3;
    public static float[] getTriangleCoords(){
        float[] kopterAndSideArrows={
                //Kopter(gleichschenkliges Dreieck und 4 weitere)
                0.0f, 0.0f, -0.6f,  // top
                1.0f, 0.0f, 0.0f, 1.0f, //Color red
                0.5196152f, 0.0f, 0.3f,  // bottom right
                0.0f, 0.0f, 1.0f, 1.0f,  //Color blue
                -0.5196152f, 0.0f, 0.3f,  // bottom left
                1.0f, 1.0f, 0.0f, 1.0f, //Color yellow
                //1
                -1.1f, 0.0f, -1.0f,
                1.0f, 0.0f, 0.0f, 1.0f, //Color red
                -1.0f, 0.0f, -1.0f,
                1.0f, 0.0f, 0.0f, 1.0f, //Color red
                //-1.05f, 0.0f, -1.1f,
                0.0f,0.0f,0.0f,
                1.0f, 0.0f, 0.0f, 1.0f, //Color red
                //2
                1.1f, 0.0f, -1.0f,
                1.0f, 0.0f, 0.0f, 1.0f, //Color red
                1.0f, 0.0f, -1.0f,
                1.0f, 0.0f, 0.0f, 1.0f, //Color red
                //1.05f, 0.0f, -1.1f,
                0.0f,0.0f,0.0f,
                1.0f, 0.0f, 0.0f, 1.0f, //Color red
                //3
                1.1f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,  //Color blue
                1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f,  //Color blue
                //1.05f, 0.0f, 1.1f,
                0.0f,0.0f,0.0f,
                0.0f, 0.0f, 1.0f, 1.0f,  //Color blue
                //4
                -1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f, //Color yellow
                -1.1f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f, //Color yellow
                //-1.05f, 0.0f, 1.1f,
                0.0f,0.0f,0.0f,
                1.0f, 1.0f, 0.0f, 1.0f, //Color yellow
                //Arrow representing Copter's height
                //left side
                -1.75f , 0.05f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f, //Color yellow
                -1.85f , 0.0f,   0.0f,
                1.0f, 1.0f, 0.0f, 1.0f, //Color yellow
                -1.75f ,-0.05f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f, //Color yellow
                //right side
                1.75f , 0.05f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f, //Color yellow
                1.85f , 0.00f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f, //Color yellow
                1.75f ,-0.05f, 0.0f,
                1.0f, 1.0f, 0.0f, 1.0f, //Color yellow

        };
        float[] homeArrowCoords= {
                -0.2f, 1.0f, 1.1f,
                0.0f, 0.1f, 0.0f, 1.0f, //Color dark green
                0.2f, 1.0f, 1.1f,
                0.0f, 0.1f, 0.0f, 1.0f, //Color dark green
                0.0f, 1.0f, 1.6f,
                0.0f, 1.0f, 0.0f, 1.0f, //Color green
        };

        float[] FloatReturn=new float[homeArrowCoords.length+kopterAndSideArrows.length+(18*42)];
        //coords for lines representing height
        float x=-2.1f,y=2.005f,z=0.00f,height=0.01f,width=0.25f;
        makeRectangle(FloatReturn, 0, x, y - 0.0f, z, height, width, 0.0f, 1.0f, 0.0f, 0.0f);
        makeRectangle(FloatReturn, (1 * 42), x + (width / 2), y - 0.5f, z, height, width / 2, 0.0f, 1.0f, 0.0f, 1.0f);
        makeRectangle(FloatReturn, (2 * 42), x, y - 1.0f, z, height, width, 0.0f, 1.0f, 0.0f, 1.0f);
        makeRectangle(FloatReturn, (3 * 42), x + (width / 2), y - 1.5f, z, height, width / 2, 0.0f, 1.0f, 0.0f, 1.0f);
        makeRectangle(FloatReturn, (4 * 42), x, y - 2.0f, z, height, width, 0.0f, 1.0f, 0.0f, 1.0f);
        makeRectangle(FloatReturn, (5 * 42), x + (width / 2), y - 2.5f, z, height, width / 2, 0.0f, 1.0f, 0.0f, 1.0f);
        makeRectangle(FloatReturn, (6 * 42), x, y - 3.0f, z, height, width, 0.0f, 1.0f, 0.0f, 1.0f);
        makeRectangle(FloatReturn, (7 * 42), x + (width / 2), y - 3.5f, z, height, width / 2, 0.0f, 1.0f, 0.0f, 1.0f);
        makeRectangle(FloatReturn, (8 * 42), x, y - 4.0f, z, height, width, 0.0f, 1.0f, 0.0f, 1.0f);
        makeRectangle(FloatReturn, (9 * 42), -x - width, y - 0.0f, z, height, width, 1.0f, 1.0f, 0.0f, 1.0f);
        makeRectangle(FloatReturn, (10 * 42), -x - width, y - 0.5f, z, height, width / 2, 0.0f, 1.0f, 0.0f, 1.0f);
        makeRectangle(FloatReturn, (11 * 42), -x - width, y - 1.0f, z, height, width, 0.0f, 1.0f, 0.0f, 1.0f);
        makeRectangle(FloatReturn, (12 * 42), -x - width, y - 1.5f, z, height, width / 2, 0.0f, 1.0f, 0.0f, 1.0f);
        makeRectangle(FloatReturn, (13 * 42), -x - width, y - 2.0f, z, height, width, 0.0f, 1.0f, 0.0f, 1.0f);
        makeRectangle(FloatReturn, (14 * 42), -x - width, y - 2.5f, z, height, width / 2, 0.0f, 1.0f, 0.0f, 1.0f);
        makeRectangle(FloatReturn, (15 * 42), -x - width, y - 3.0f, z, height, width, 0.0f, 1.0f, 0.0f, 1.0f);
        makeRectangle(FloatReturn, (16 * 42), -x - width, y - 3.5f, z, height, width / 2, 0.0f, 1.0f, 0.0f, 1.0f);
        makeRectangle(FloatReturn, (17 * 42), -x - width, y - 4.0f, z, height, width, 0.0f, 1.0f, 0.0f, 1.0f);
        int height_lines_length=18*42;

        int counter=0;
        for(int i=height_lines_length;i<height_lines_length+kopterAndSideArrows.length;i++){
            FloatReturn[i]=kopterAndSideArrows[counter];
            counter++;
        }
        counter=0;
        for(int i=height_lines_length+kopterAndSideArrows.length;i<height_lines_length+kopterAndSideArrows.length+homeArrowCoords.length;i++){
            FloatReturn[i]=homeArrowCoords[counter];
            counter++;
        }
        return FloatReturn;

    }

    //koords for the OSD Overdraw
    public static void getOverdrawCoordByFormat(float videoFormat,float videoDistance,boolean onTopVideo,int vb,int uvb,OSDSettings osdSettings){
        int nQuadsPerUnit=7;
        FloatBuffer bbVertices = ByteBuffer.allocateDirect(
                ((14*6*nQuadsPerUnit*3)+(10*6*3))*4) .order(ByteOrder.nativeOrder()).asFloatBuffer();
        FloatBuffer bbUV = ByteBuffer.allocateDirect(
                ((14*6*nQuadsPerUnit*2)+(10*6*2))*4) .order(ByteOrder.nativeOrder()).asFloatBuffer();

        //Texture atlas offset
        float offset=(1.0f/OverlayTexturePicture.number_of_units);
        float width=3,height=0.5f;
        float x=-4.6f,y=(((1.0f/videoFormat)*5.0f)+(2*height))+0.1f,z=-videoDistance;
        float videoW=10,videoH=10.0f * (1.0f / videoFormat);
        if(onTopVideo){
            x=-videoW*0.5f;y=videoH*0.5f;
        }else{
            x=-videoW*0.5f;y=videoH*0.5f+2*height;
        }
        float x0= x         , y0= y       , z0=z;
        float x1= x         , y1= y-height, z1=z;
        float x2=-(width/2) , y2= y       , z2=z;
        float x3=-(width/2) , y3= y-height, z3=z;
        float x4=-x-width   , y4= y       , z4=z;
        float x5=-x-width   , y5= y-height, z5=z;
        float x6= x      , y6= y-(2*height) , z6=z;
        float x7=-x-width, y7= y-(2*height) , z7=z;
        float x8= x         , y8=-y+(height*2), z8=z;
        float x9= x         , y9=-y+height     , z9=z;
        float x10=-(width/2),y10=-y+(height*2),z10=z;
        float x11=-(width/2),y11=-y+height    ,z11=z;
        float x12=-x-width  ,y12=-y+(height*2),z12=z;
        float x13=-x-width  ,y13=-y+height    ,z13=z;
        float[] verticesData=new float[2*6*nQuadsPerUnit*3];
        float[] uvData=new float[2*6*nQuadsPerUnit*2];
        //GLHelper.makeRectangle2(TriangleVerticesData,0 *30,x0 ,y0 ,z0 ,height,10,0,1);
        //GLHelper.makeRectangle2(TriangleVerticesData,1 *30,0 ,0 ,z0 ,height,10,0,1);
        //GLHelper.makeOverlayUnit(TriangleVerticesData,0 *30,0 ,0 ,z0 ,height,10,0,1);
        float stringO=OverlayTexturePicture.stringO;
        makeOverlayUnit(verticesData,0 *18*nQuadsPerUnit,uvData,0*12*nQuadsPerUnit,x0 ,y0 ,z0 ,height,width,stringO+0*3*offset,offset,true);
        makeOverlayUnit(verticesData,1 *18*nQuadsPerUnit,uvData,1*12*nQuadsPerUnit,x1 ,y1 ,z1 ,height,width,stringO+1*3*offset,offset,true);
        bbVertices.position(0);
        bbVertices.put(verticesData);
        bbUV.position(0);
        bbUV.put(uvData);
        makeOverlayUnit(verticesData,0 *18*nQuadsPerUnit,uvData,0*12*nQuadsPerUnit,x2 ,y2 ,z2 ,height,width,stringO+2*3*offset,offset,osdSettings.enable_latitude_longitude);
        makeOverlayUnit(verticesData,1 *18*nQuadsPerUnit,uvData,1*12*nQuadsPerUnit,x3 ,y3 ,z3 ,height,width,stringO+3*3*offset,offset,osdSettings.enable_latitude_longitude);
        bbVertices.position(2*18*nQuadsPerUnit);
        bbVertices.put(verticesData);
        bbUV.position(2*12*nQuadsPerUnit);
        bbUV.put(uvData);
        makeOverlayUnit(verticesData,0 *18*nQuadsPerUnit,uvData,0*12*nQuadsPerUnit,x4 ,y4 ,z4 ,height,width,stringO+4*3*offset,offset,osdSettings.enable_battery_life);
        makeOverlayUnit(verticesData,1 *18*nQuadsPerUnit,uvData,1*12*nQuadsPerUnit,x5 ,y5 ,z5 ,height,width,stringO+5*3*offset,offset,osdSettings.enable_voltage);
        bbVertices.position(4*18*nQuadsPerUnit);
        bbVertices.put(verticesData);
        bbUV.position(4*12*nQuadsPerUnit);
        bbUV.put(uvData);
        makeOverlayUnit(verticesData,0 *18*nQuadsPerUnit,uvData,0*12*nQuadsPerUnit,x6 ,y6 ,z6 ,height,width,stringO+6*3*offset,offset,osdSettings.enable_rssi);
        makeOverlayUnit(verticesData,1 *18*nQuadsPerUnit,uvData,1*12*nQuadsPerUnit,x7 ,y7 ,z7 ,height,width,stringO+7*3*offset,offset,osdSettings.enable_ampere);
        bbVertices.position(6*18*nQuadsPerUnit);
        bbVertices.put(verticesData);
        bbUV.position(6*12*nQuadsPerUnit);
        bbUV.put(uvData);
        makeOverlayUnit(verticesData,0 *18*nQuadsPerUnit,uvData,0*12*nQuadsPerUnit,x8 ,y8 ,z8 ,height,width,stringO+8*3*offset,offset,osdSettings.enable_X2);
        makeOverlayUnit(verticesData,1 *18*nQuadsPerUnit,uvData,1*12*nQuadsPerUnit,x9 ,y9 ,z9 ,height,width,stringO+9*3*offset,offset,osdSettings.enable_X3);
        bbVertices.position(8*18*nQuadsPerUnit);
        bbVertices.put(verticesData);
        bbUV.position(8*12*nQuadsPerUnit);
        bbUV.put(uvData);
        makeOverlayUnit(verticesData,0*18*nQuadsPerUnit,uvData,0*12*nQuadsPerUnit,x10,y10,z10,height,width,stringO+10*3*offset,offset,osdSettings.enable_speed);
        makeOverlayUnit(verticesData,1*18*nQuadsPerUnit,uvData,1*12*nQuadsPerUnit,x11,y11,z11,height,width,stringO+11*3*offset,offset,osdSettings.enable_X4);
        bbVertices.position(10*18*nQuadsPerUnit);
        bbVertices.put(verticesData);
        bbUV.position(10*12*nQuadsPerUnit);
        bbUV.put(uvData);
        makeOverlayUnit(verticesData,0*18*nQuadsPerUnit,uvData,0*12*nQuadsPerUnit,x12,y12,z12,height,width,stringO+12*3*offset,offset,osdSettings.enable_height);
        makeOverlayUnit(verticesData,1*18*nQuadsPerUnit,uvData,1*12*nQuadsPerUnit,x13,y13,z13,height,width,stringO+13*3*offset,offset,osdSettings.enable_height);
        bbVertices.position(12*18*nQuadsPerUnit);
        bbVertices.put(verticesData);
        bbUV.position(12*12*nQuadsPerUnit);
        bbUV.put(uvData);
        //for height info
        verticesData=new float[10*6*3];
        uvData=new float[10*6*2];
        float x14=-1.8f-0.5f,y14= 2.0625f  , z14=0;
        float x15=-1.8f-0.5f,y15= 1.0625f  , z15=0;
        float x16=-1.8f-0.5f,y16= 0.0625f  , z16=0;
        float x17=-1.8f-0.5f,y17=-0.9375f  , z17=0;
        float x18=-1.8f-0.5f,y18=-1.9375f  , z18=0;
        float x19= 2.1f,y19= 2.0625f  , z19=0;
        float x20= 2.1f,y20= 1.0625f  , z20=0;
        float x21= 2.1f,y21= 0.0625f  , z21=0;
        float x22= 2.1f,y22=-0.9375f  , z22=0;
        float x23= 2.1f,y23=-1.9375f  , z23=0;
        float heightO=OverlayTexturePicture.heightO;
        makeRectangle4(verticesData,0*6*3,uvData,0*6*2,x14,y14,z14,0.125f,0.5f,heightO+0*3*offset,offset*3);
        makeRectangle4(verticesData,1*6*3,uvData,1*6*2,x15,y15,z15,0.125f,0.5f,heightO+1*3*offset,offset*3);
        makeRectangle4(verticesData,2*6*3,uvData,2*6*2,x16,y16,z16,0.125f,0.5f,heightO+2*3*offset,offset*3);
        makeRectangle4(verticesData,3*6*3,uvData,3*6*2,x17,y17,z17,0.125f,0.5f,heightO+3*3*offset,offset*3);
        makeRectangle4(verticesData,4*6*3,uvData,4*6*2,x18,y18,z18,0.125f,0.5f,heightO+4*3*offset,offset*3);
        makeRectangle4(verticesData,5*6*3,uvData,5*6*2,x19,y19,z19,0.125f,0.5f,heightO+0*3*offset,offset*3);
        makeRectangle4(verticesData,6*6*3,uvData,6*6*2,x20,y20,z20,0.125f,0.5f,heightO+1*3*offset,offset*3);
        makeRectangle4(verticesData,7*6*3,uvData,7*6*2,x21,y21,z21,0.125f,0.5f,heightO+2*3*offset,offset*3);
        makeRectangle4(verticesData,8*6*3,uvData,8*6*2,x22,y22,z22,0.125f,0.5f,heightO+3*3*offset,offset*3);
        makeRectangle4(verticesData,9*6*3,uvData,9*6*2,x23,y23,z23,0.125f,0.5f,heightO+4*3*offset,offset*3);
        bbVertices.position(14*18*nQuadsPerUnit);
        bbVertices.put(verticesData);
        bbUV.position(14*12*nQuadsPerUnit);
        bbUV.put(uvData);

        bbVertices.position(0);
        bbUV.position(0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vb);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, bbVertices.capacity() * 4,
                bbVertices, GLES20.GL_DYNAMIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, uvb);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, bbUV.capacity() * 4,
                bbUV, GLES20.GL_DYNAMIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }
    public static void getOverdrawCoordsByFormatMono(int vb,int uvb,OSDSettings osdSettings,float ratio){
        //float ratio=1.7777f;
        System.out.println("Ratio:"+ratio);
        int nQuadsPerUnit=7;
        FloatBuffer bbVertices = ByteBuffer.allocateDirect(
                ((14*6*nQuadsPerUnit*3)+(10*6*3))*4) .order(ByteOrder.nativeOrder()).asFloatBuffer();
        FloatBuffer bbUV = ByteBuffer.allocateDirect(
                ((14*6*nQuadsPerUnit*2)+(10*6*2))*4) .order(ByteOrder.nativeOrder()).asFloatBuffer();
        //Texture atlas offset
        float offset=(1.0f/OverlayTexturePicture.number_of_units);
        float width=3,height=0.5f;
        //float x=-8.9f,y=(((1.0f/ratio)*6.8f)+(2*height))+0.1f,z=-5;
        //float x=-5*ratio,y=1.0f/ratio*8.9f,z=-5;
        float x=(-10.0f/2.0f)*ratio,y=5.0f,z=-5;

        float x0= x         , y0= y       , z0=z;
        float x1= x         , y1= y-height, z1=z;
        float x2=-(width/2) , y2= y       , z2=z;
        float x3=-(width/2) , y3= y-height, z3=z;
        float x4=-x-width   , y4= y       , z4=z;
        float x5=-x-width   , y5= y-height, z5=z;
        float x6= x      , y6= y-(2*height) , z6=z;
        float x7=-x-width, y7= y-(2*height) , z7=z;
        float x8= x         , y8=-y+(height*2), z8=z;
        float x9= x         , y9=-y+height     , z9=z;
        float x10=-(width/2),y10=-y+(height*2),z10=z;
        float x11=-(width/2),y11=-y+height    ,z11=z;
        float x12=-x-width  ,y12=-y+(height*2),z12=z;
        float x13=-x-width  ,y13=-y+height    ,z13=z;
        float[] verticesData=new float[2*6*nQuadsPerUnit*3];
        float[] uvData=new float[2*6*nQuadsPerUnit*2];
        //GLHelper.makeRectangle2(TriangleVerticesData,0 *30,x0 ,y0 ,z0 ,height,10,0,1);
        //GLHelper.makeRectangle2(TriangleVerticesData,1 *30,0 ,0 ,z0 ,height,10,0,1);
        //GLHelper.makeOverlayUnit(TriangleVerticesData,0 *30,0 ,0 ,z0 ,height,10,0,1);
        float stringO=OverlayTexturePicture.stringO;
        makeOverlayUnit(verticesData,0 *18*nQuadsPerUnit,uvData,0*12*nQuadsPerUnit,x0 ,y0 ,z0 ,height,width,stringO+0*3*offset,offset,true);
        makeOverlayUnit(verticesData,1 *18*nQuadsPerUnit,uvData,1*12*nQuadsPerUnit,x1 ,y1 ,z1 ,height,width,stringO+1*3*offset,offset,true);
        bbVertices.position(0);
        bbVertices.put(verticesData);
        bbUV.position(0);
        bbUV.put(uvData);
        makeOverlayUnit(verticesData,0 *18*nQuadsPerUnit,uvData,0*12*nQuadsPerUnit,x2 ,y2 ,z2 ,height,width,stringO+2*3*offset,offset,osdSettings.enable_latitude_longitude);
        makeOverlayUnit(verticesData,1 *18*nQuadsPerUnit,uvData,1*12*nQuadsPerUnit,x3 ,y3 ,z3 ,height,width,stringO+3*3*offset,offset,osdSettings.enable_latitude_longitude);
        bbVertices.position(2*18*nQuadsPerUnit);
        bbVertices.put(verticesData);
        bbUV.position(2*12*nQuadsPerUnit);
        bbUV.put(uvData);
        makeOverlayUnit(verticesData,0 *18*nQuadsPerUnit,uvData,0*12*nQuadsPerUnit,x4 ,y4 ,z4 ,height,width,stringO+4*3*offset,offset,osdSettings.enable_battery_life);
        makeOverlayUnit(verticesData,1 *18*nQuadsPerUnit,uvData,1*12*nQuadsPerUnit,x5 ,y5 ,z5 ,height,width,stringO+5*3*offset,offset,osdSettings.enable_voltage);
        bbVertices.position(4*18*nQuadsPerUnit);
        bbVertices.put(verticesData);
        bbUV.position(4*12*nQuadsPerUnit);
        bbUV.put(uvData);
        makeOverlayUnit(verticesData,0 *18*nQuadsPerUnit,uvData,0*12*nQuadsPerUnit,x6 ,y6 ,z6 ,height,width,stringO+6*3*offset,offset,osdSettings.enable_rssi);
        makeOverlayUnit(verticesData,1 *18*nQuadsPerUnit,uvData,1*12*nQuadsPerUnit,x7 ,y7 ,z7 ,height,width,stringO+7*3*offset,offset,osdSettings.enable_ampere);
        bbVertices.position(6*18*nQuadsPerUnit);
        bbVertices.put(verticesData);
        bbUV.position(6*12*nQuadsPerUnit);
        bbUV.put(uvData);
        makeOverlayUnit(verticesData,0 *18*nQuadsPerUnit,uvData,0*12*nQuadsPerUnit,x8 ,y8 ,z8 ,height,width,stringO+8*3*offset,offset,osdSettings.enable_X2);
        makeOverlayUnit(verticesData,1 *18*nQuadsPerUnit,uvData,1*12*nQuadsPerUnit,x9 ,y9 ,z9 ,height,width,stringO+9*3*offset,offset,osdSettings.enable_X3);
        bbVertices.position(8*18*nQuadsPerUnit);
        bbVertices.put(verticesData);
        bbUV.position(8*12*nQuadsPerUnit);
        bbUV.put(uvData);
        makeOverlayUnit(verticesData,0*18*nQuadsPerUnit,uvData,0*12*nQuadsPerUnit,x10,y10,z10,height,width,stringO+10*3*offset,offset,osdSettings.enable_speed);
        makeOverlayUnit(verticesData,1*18*nQuadsPerUnit,uvData,1*12*nQuadsPerUnit,x11,y11,z11,height,width,stringO+11*3*offset,offset,osdSettings.enable_X4);
        bbVertices.position(10*18*nQuadsPerUnit);
        bbVertices.put(verticesData);
        bbUV.position(10*12*nQuadsPerUnit);
        bbUV.put(uvData);
        makeOverlayUnit(verticesData,0*18*nQuadsPerUnit,uvData,0*12*nQuadsPerUnit,x12,y12,z12,height,width,stringO+12*3*offset,offset,osdSettings.enable_height);
        makeOverlayUnit(verticesData,1*18*nQuadsPerUnit,uvData,1*12*nQuadsPerUnit,x13,y13,z13,height,width,stringO+13*3*offset,offset,osdSettings.enable_height);
        bbVertices.position(12*18*nQuadsPerUnit);
        bbVertices.put(verticesData);
        bbUV.position(12*12*nQuadsPerUnit);
        bbUV.put(uvData);
        //for height info
        verticesData=new float[10*6*3];
        uvData=new float[10*6*2];
        float x14=-1.8f-0.5f,y14= 2.0625f  , z14=0;
        float x15=-1.8f-0.5f,y15= 1.0625f  , z15=0;
        float x16=-1.8f-0.5f,y16= 0.0625f  , z16=0;
        float x17=-1.8f-0.5f,y17=-0.9375f  , z17=0;
        float x18=-1.8f-0.5f,y18=-1.9375f  , z18=0;
        float x19= 2.1f,y19= 2.0625f  , z19=0;
        float x20= 2.1f,y20= 1.0625f  , z20=0;
        float x21= 2.1f,y21= 0.0625f  , z21=0;
        float x22= 2.1f,y22=-0.9375f  , z22=0;
        float x23= 2.1f,y23=-1.9375f  , z23=0;
        float heightO=OverlayTexturePicture.heightO;
        makeRectangle4(verticesData,0*6*3,uvData,0*6*2,x14,y14,z14,0.125f,0.5f,heightO+0*3*offset,offset*3);
        makeRectangle4(verticesData,1*6*3,uvData,1*6*2,x15,y15,z15,0.125f,0.5f,heightO+1*3*offset,offset*3);
        makeRectangle4(verticesData,2*6*3,uvData,2*6*2,x16,y16,z16,0.125f,0.5f,heightO+2*3*offset,offset*3);
        makeRectangle4(verticesData,3*6*3,uvData,3*6*2,x17,y17,z17,0.125f,0.5f,heightO+3*3*offset,offset*3);
        makeRectangle4(verticesData,4*6*3,uvData,4*6*2,x18,y18,z18,0.125f,0.5f,heightO+4*3*offset,offset*3);
        makeRectangle4(verticesData,5*6*3,uvData,5*6*2,x19,y19,z19,0.125f,0.5f,heightO+0*3*offset,offset*3);
        makeRectangle4(verticesData,6*6*3,uvData,6*6*2,x20,y20,z20,0.125f,0.5f,heightO+1*3*offset,offset*3);
        makeRectangle4(verticesData,7*6*3,uvData,7*6*2,x21,y21,z21,0.125f,0.5f,heightO+2*3*offset,offset*3);
        makeRectangle4(verticesData,8*6*3,uvData,8*6*2,x22,y22,z22,0.125f,0.5f,heightO+3*3*offset,offset*3);
        makeRectangle4(verticesData,9*6*3,uvData,9*6*2,x23,y23,z23,0.125f,0.5f,heightO+4*3*offset,offset*3);
        bbVertices.position(14*18*nQuadsPerUnit);
        bbVertices.put(verticesData);
        bbUV.position(14*12*nQuadsPerUnit);
        bbUV.put(uvData);

        bbVertices.position(0);
        bbUV.position(0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vb);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, bbVertices.capacity() * 4,
                bbVertices, GLES20.GL_DYNAMIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, uvb);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, bbUV.capacity() * 4,
                bbUV, GLES20.GL_DYNAMIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }
    public static float[] numberToIndices(float number) {
        /* Assuming the layout is as follows: 012345678900.1.2.3. usw*/
        /*can handle 3 places; numbers between 999 und 0.00 */
        boolean negative=false;
        if (number > 999) {
            //Log.d("osdhelper", "number too big");
            number=999;
        }
        if(number<0){
            number=-number;
            negative=true;
        }
        kommastellen=0;
        if(number<100){
            kommastellen++;
            if(number<10){
                kommastellen++;
            }
        }
        if(kommastellen==0){
            number=(int)number;
            hundreds = 0; tens = 0; ones = 0;
            while (number > 99) {
                number-=100;
                hundreds++;
            }
            while (number > 9) {
                number-=10;
                tens++;
            }
            ones = (int) number;
            //System.out.println("hundreds:" + hundreds + " tens:" + tens + " ones:" + ones);
            float u,v;
            v=0.0f;
            float width,height;
            int c=0;
            if(negative){
                u=(1.0f/OverlayTexturePicture.number_of_units)*20;
                width= (1.0f/OverlayTexturePicture.number_of_units);
                height=1.0f;
            }else {
                u=0.0f;
                /*Ugly HACK*/
                width=0;height=0;
            }
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] =u;
            indices[c++] =v+height;
            width= (1.0f/OverlayTexturePicture.number_of_units);
            height=1.0f;
            u=(1.0f/OverlayTexturePicture.number_of_units)*hundreds; //u-->x
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] =u;
            indices[c++] =v+height;
            u=(1.0f/OverlayTexturePicture.number_of_units)*tens; //u-->x
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] =u;
            indices[c++] =v+height;
            u=(1.0f/OverlayTexturePicture.number_of_units)*ones; //u-->x
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] =u;
            indices[c++] =v+height;
        }else
        if(kommastellen==1){
            float komma=number-((int)number);
            komma*=10;
            komma=(int)komma;
            number=(int)number;
            int tens=0,ones=0;
            while (number > 9) {
                number-=10;
                tens++;
            }
            ones = (int) number;
            float u,v;
            v=0.0f;
            float width,height;
            int c=0;
            if(negative){
                u=(1.0f/OverlayTexturePicture.number_of_units)*20;
                width= (1.0f/OverlayTexturePicture.number_of_units);
                height=1.0f;
            }else {
                u=0.0f;
                /*HACK*/
                width=0;height=0;
            }
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] =u;
            indices[c++] =v+height;
            width= (1.0f/OverlayTexturePicture.number_of_units);
            height=1.0f;
            u=(1.0f/OverlayTexturePicture.number_of_units)*tens; //u-->x
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] =u;
            indices[c++] =v+height;
            u=(1.0f/OverlayTexturePicture.number_of_units)*10+(1.0f/OverlayTexturePicture.number_of_units)*ones; //u-->x
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] =u;
            indices[c++] =v+height;
            u=(1.0f/OverlayTexturePicture.number_of_units)*komma; //u-->x
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] =u;
            indices[c++] =v+height;
        }else
        if(kommastellen==2){
            float komma=number-((int)number);
            komma*=100;
            komma=(int)komma;
            int komma1=0;
            while (komma > 9) {
                komma-=10;
                komma1++;
            }
            number=(int)number;
            int ones=0;
            ones = (int) number;
            float u,v;
            v=0.0f;
            float width,height;
            int c=0;
            if(negative){
                u=(1.0f/OverlayTexturePicture.number_of_units)*20;
                width= (1.0f/OverlayTexturePicture.number_of_units);
                height=1.0f;
            }else {
                u=0.0f;
                /*HACK*/
                width=0;height=0;
            }
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] =u;
            indices[c++] =v+height;
            u=(1.0f/OverlayTexturePicture.number_of_units)*10+(1.0f/OverlayTexturePicture.number_of_units)*ones; //u-->x
            v=0;
            width= (1.0f/OverlayTexturePicture.number_of_units);
            height=1.0f;
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] =u;
            indices[c++] =v+height;
            u=(1.0f/OverlayTexturePicture.number_of_units)*komma1; //u-->x
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] =u;
            indices[c++] =v+height;
            u=(1.0f/OverlayTexturePicture.number_of_units)*komma; //u-->x
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] = u;
            indices[c++] = v;
            indices[c++] = u+width;
            indices[c++] = v+height;
            indices[c++] =u;
            indices[c++] =v+height;
        }


        return  indices;
    }

    //taken from tinygps: https://github.com/mikalhart/TinyGPS/blob/master/TinyGPS.cpp#L296
    public static double distance_between(double lat1, double long1, double lat2, double long2) {
        // returns distance in meters between two positions, both specified
        // as signed decimal-degrees latitude and longitude. Uses great-circle
        // distance computation for hypothetical sphere of radius 6372795 meters.
        // Because Earth is no exact sphere, rounding errors may be up to 0.5%.
        // Courtesy of Maarten Lamers
        double delta = (long1-long2)*0.017453292519;
        double sdlong = Math.sin(Math.toRadians(delta));
        double cdlong = Math.cos(Math.toRadians(delta));
        lat1 = (lat1)*0.017453292519;
        lat2 = (lat2)*0.017453292519;
        double slat1 = Math.sin(Math.toRadians(lat1));
        double clat1 = Math.cos(Math.toRadians(lat1));
        double slat2 = Math.sin(Math.toRadians(lat2));
        double clat2 = Math.cos(Math.toRadians(lat2));
        delta = (clat1 * slat2) - (slat1 * clat2 * cdlong);
        delta = delta*delta;
        delta += (clat2 * sdlong)*(clat2 * sdlong);
        delta = Math.sqrt(delta);
        double denom = (slat1 * slat2) + (clat1 * clat2 * cdlong);
        delta = Math.toDegrees(Math.atan2(delta, denom));
        return delta * 6372795;
    }

    //taken from tinygps: https://github.com/mikalhart/TinyGPS/blob/master/TinyGPS.cpp#L321
    public static double course_to (double lat1, double long1, double lat2, double long2)
    {
        // returns course in degrees (North=0, West=270) from position 1 to position 2,
        // both specified as signed decimal-degrees latitude and longitude.
        // Because Earth is no exact sphere, calculated course may be off by a tiny fraction.
        // Courtesy of Maarten Lamers
        double dlon = (long2-long1)*0.017453292519;
        lat1 = (lat1)*0.017453292519;
        lat2 = (lat2)*0.017453292519;
        double a1 = Math.sin(Math.toRadians(dlon)) * Math.cos(Math.toRadians(lat2));
        double a2 = Math.sin(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(dlon));
        a2 = Math.cos(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) - a2;
        a2 = Math.atan2(a1, a2);
        while (a2 < 0.0)
        {
            a2 += Math.PI*2;
        }
        return Math.toDegrees(a2);
    }


    public static float[] getCanvasVert(float format,float distance,int tesselation){
        //F.e format=4:3
        //The x and z values stay,only the y values change for different video format's
        float x0=-5.0f , y0=(1.0f/format)*5.0f     , z0=-distance;
        if(tesselation==0){tesselation=1;}
        float[] TriangleVerticesData=new float[5*2*3*tesselation*tesselation];
        makeTesselatedRectangle(TriangleVerticesData,0,tesselation,x0,y0,z0,10,10.0f * (1.0f / format));
        return TriangleVerticesData;
    }
    public static float[] getCanvasVertMono(float ratio){
        //F.e format=4:3
        //The x and z values stay,only the y values change for different video format's
        //float x0=-5.0f , y0=(1.0f/format)*5.0f     , z0=-distance;
        //we need x,y,z=egal,u,v
        //nach links und rechts korrigieren
        float[] vertices={
                -1/ratio,1,0,0,0,
                -1/ratio,-1,0,0,1,
                1/ratio,-1,0,1,1,

                -1/ratio,1,0,0,0,
                1/ratio,-1,0,1,1,
                1/ratio,1,0,1,0
        };
        /*float x0=0.0f,y0=0.0f,z0=-2;
        float[] TriangleVerticesData=new float[5*2*3*1];
        makeTesselatedRectangle(TriangleVerticesData,0,1,x0,y0,z0,1,1);*/
        return vertices;
    }

    public static void makeTesselatedRectangle(float[] array,int arrayOffset,int tesselation,
                                               float x,float y,float z,float width,float height){
        //System.out.println("videoHeightVid"+height);
        int tesselationXDirection=tesselation;
        int tesselationYDirection=tesselation;
        float tesHeight=height/((float)tesselationXDirection);
        float tesWidth=width/((float)tesselationYDirection);
        float tesUVOffsetX=1.0f/(tesselationXDirection);
        float tesUVOffsety=1.0f/(tesselationYDirection);
        for(int i=0;i<tesselationXDirection;i++){
            float tx=x+(i*tesWidth);
            for(int i2=0;i2<tesselationYDirection;i2++){
                float ty=y-(i2*tesHeight);
                makeRectangle2(array,arrayOffset,tx,ty,z,tesHeight,tesWidth, (i*tesUVOffsetX), tesUVOffsetX,(i2*tesUVOffsety),tesUVOffsety);
                //makeRectangle2(array,arrayOffset,tx,ty,z,tesHeight,tesWidth,0,1,0,1);
                arrayOffset+=30;
            }
        }
    }
    public static void makeRectangle2(float[] array,int arrayOffset,float x,float y,float z,float height,float width,float xTexMin,float xTexMax,
                                      float yTexMin,float yTexMax){
        array[arrayOffset   ]=x;
        array[arrayOffset+ 1]=y;
        array[arrayOffset+ 2]=z;
        array[arrayOffset+ 3]=xTexMin;
        array[arrayOffset+ 4]=yTexMin;
        array[arrayOffset+ 5]=x+width;
        array[arrayOffset+ 6]=y;
        array[arrayOffset+ 7]=z;
        array[arrayOffset+ 8]=xTexMin+xTexMax;
        array[arrayOffset+ 9]=yTexMin;
        array[arrayOffset+10]=x+width;
        array[arrayOffset+11]=y-height;
        array[arrayOffset+12]=z;
        array[arrayOffset+13]=xTexMin+xTexMax;
        array[arrayOffset+14]=yTexMin+yTexMax;
        array[arrayOffset+15]=x;
        array[arrayOffset+16]=y;
        array[arrayOffset+17]=z;
        array[arrayOffset+18]=xTexMin;
        array[arrayOffset+19]=yTexMin;
        array[arrayOffset+20]=x+width;
        array[arrayOffset+21]=y-height;
        array[arrayOffset+22]=z;
        array[arrayOffset+23]=xTexMin+xTexMax;
        array[arrayOffset+24]=yTexMin+yTexMax;
        array[arrayOffset+25]=x;
        array[arrayOffset+26]=y-height;
        array[arrayOffset+27]=z;
        array[arrayOffset+28]=xTexMin;
        array[arrayOffset+29]=yTexMin+yTexMax;
    }
    public static void makeRectangle(float[] array,int arrayOffset,float x,float y,float z,float height,float width,float r,float g,float b,float a){
        array[arrayOffset   ]=x;
        array[arrayOffset+ 1]=y;
        array[arrayOffset+ 2]=z;
        array[arrayOffset+ 3]=r;
        array[arrayOffset+ 4]=g;
        array[arrayOffset+ 5]=b;
        array[arrayOffset+ 6]=a;
        array[arrayOffset+ 7]=x+width;
        array[arrayOffset+ 8]=y;
        array[arrayOffset+ 9]=z;
        array[arrayOffset+10]=r;
        array[arrayOffset+11]=g;
        array[arrayOffset+12]=b;
        array[arrayOffset+13]=a;
        array[arrayOffset+14]=x+width;
        array[arrayOffset+15]=y-height;
        array[arrayOffset+16]=z;
        array[arrayOffset+17]=r;
        array[arrayOffset+18]=g;
        array[arrayOffset+19]=b;
        array[arrayOffset+20]=a;
        array[arrayOffset+21]=x;
        array[arrayOffset+22]=y;
        array[arrayOffset+23]=z;
        array[arrayOffset+24]=r;
        array[arrayOffset+25]=g;
        array[arrayOffset+26]=b;
        array[arrayOffset+27]=a;
        array[arrayOffset+28]=x+width;
        array[arrayOffset+29]=y-height;
        array[arrayOffset+30]=z;
        array[arrayOffset+31]=r;
        array[arrayOffset+32]=g;
        array[arrayOffset+33]=b;
        array[arrayOffset+34]=a;
        array[arrayOffset+35]=x;
        array[arrayOffset+36]=y-height;
        array[arrayOffset+37]=z;
        array[arrayOffset+38]=r;
        array[arrayOffset+39]=g;
        array[arrayOffset+40]=b;
        array[arrayOffset+41]=a;
    }
    public static void makeRectangle3(float[] array,int arrayOffset,
                                      float x,float y,float z,
                                      float x2, float y2,float z2,
                                      float x3,float y3, float z3,
                                      float x4,float y4,float z4,
                                      float xOff,float offset){
        array[arrayOffset   ]=x;
        array[arrayOffset+ 1]=y;
        array[arrayOffset+ 2]=z;
        array[arrayOffset+ 3]=xOff;
        array[arrayOffset+ 4]=0.0f;
        array[arrayOffset+ 5]=x2;
        array[arrayOffset+ 6]=y2;
        array[arrayOffset+ 7]=z2;
        array[arrayOffset+ 8]=xOff+offset;
        array[arrayOffset+ 9]=0.0f;
        array[arrayOffset+10]=x3;
        array[arrayOffset+11]=y3;
        array[arrayOffset+12]=z3;
        array[arrayOffset+13]=xOff+offset;
        array[arrayOffset+14]=1.0f;
        array[arrayOffset+15]=x;
        array[arrayOffset+16]=y;
        array[arrayOffset+17]=z;
        array[arrayOffset+18]=xOff;
        array[arrayOffset+19]=0.0f;
        array[arrayOffset+20]=x3;
        array[arrayOffset+21]=y3;
        array[arrayOffset+22]=z3;
        array[arrayOffset+23]=xOff+offset;
        array[arrayOffset+24]=1.0f;
        array[arrayOffset+25]=x4;
        array[arrayOffset+26]=y4;
        array[arrayOffset+27]=z4;
        array[arrayOffset+28]=xOff;
        array[arrayOffset+29]=1.0f;
    }

    public static void makeOverlayUnit(float[] array,int arrayOffset,float[] array2,int arrayOffset2,
                                       float x,float y,float z,float height,float width,float xOff,float offset,boolean enabled){
        /*
        * 7 quads form one unit. if not enabled, they get rendered but don't show up because their uv coordinates set to 0
        */
        if(enabled){
            makeRectangle4(array,arrayOffset+0*18,array2 ,arrayOffset2+0*12,x+(0*width/7.0f) ,y ,z ,height,width/7,0,0);
            makeRectangle4(array,arrayOffset+1*18,array2 ,arrayOffset2+1*12,x+(1*width/7.0f) ,y ,z ,height,width/7,0,0);
            makeRectangle4(array,arrayOffset+2*18,array2 ,arrayOffset2+2*12,x+(2*width/7.0f) ,y ,z ,height,width/7,0,0);
            makeRectangle4(array,arrayOffset+3*18,array2 ,arrayOffset2+3*12,x+(3*width/7.0f) ,y ,z ,height,width/7,0,0);
            makeRectangle4(array,arrayOffset+4*18,array2 ,arrayOffset2+4*12,x+(4*width/7.0f) ,y ,z ,height,width/7,xOff+(offset*0),offset);
            makeRectangle4(array,arrayOffset+5*18,array2 ,arrayOffset2+5*12,x+(5*width/7.0f) ,y ,z ,height,width/7,xOff+(offset*1),offset);
            makeRectangle4(array,arrayOffset+6*18,array2 ,arrayOffset2+6*12,x+(6*width/7.0f) ,y ,z ,height,width/7,xOff+(offset*2),offset);
        }else{
            makeRectangle4(array,arrayOffset+0*18,array2 ,arrayOffset2+0*12,x+(0*width/6) ,y ,z ,0,0,0,0);
            makeRectangle4(array,arrayOffset+1*18,array2 ,arrayOffset2+1*12,x+(1*width/6) ,y ,z ,0,0,0,0);
            makeRectangle4(array,arrayOffset+2*18,array2 ,arrayOffset2+2*12,x+(2*width/6) ,y ,z ,0,0,0,0);
            makeRectangle4(array,arrayOffset+3*18,array2 ,arrayOffset2+3*12,x+(3*width/6) ,y ,z ,0,0,0,0);
            makeRectangle4(array,arrayOffset+4*18,array2 ,arrayOffset2+4*12,x+(4*width/6) ,y ,z ,0,0,0,0);
            makeRectangle4(array,arrayOffset+5*18,array2 ,arrayOffset2+5*12,x+(5*width/6) ,y ,z ,0,0,0,0);
            makeRectangle4(array,arrayOffset+6*18,array2 ,arrayOffset2+6*12,x+(6*width/7) ,y ,z ,0,0,0,0);
        }
        /*makeRectangle2(array,arrayOffset+0*30,x+(0*width/6) ,y ,z ,height,width/6,0   ,0.1f);
        makeRectangle2(array,arrayOffset+1*30,x+(1*width/6) ,y ,z ,height,width/6,0.1f,0.1f);
        makeRectangle2(array,arrayOffset+2*30,x+(2*width/6) ,y ,z ,height,width/6,0.2f,0.1f);
        makeRectangle2(array,arrayOffset+3*30,x+(3*width/6) ,y ,z ,height,width/6,0.3f,0.1f);
        makeRectangle2(array,arrayOffset+4*30,x+(4*width/6) ,y ,z ,height,width/6,0.4f,0.1f);
        makeRectangle2(array,arrayOffset+5*30,x+(5*width/6) ,y ,z ,height,width/6,0.5f,0.1f);*/
    }
    public static void makeRectangle4(float[] array,int arrayOffset,float[] array2,int arrayOffset2,
                                      float x,float y,float z,float height,float width,float xOff,float offset){
        array[arrayOffset   ]=x;
        array[arrayOffset+ 1]=y;
        array[arrayOffset+ 2]=z;
        array2[arrayOffset2   ]=xOff;
        array2[arrayOffset2+ 1]=0.0f;
        array[arrayOffset+ 3]=x+width;
        array[arrayOffset+ 4]=y;
        array[arrayOffset+ 5]=z;
        array2[arrayOffset2+ 2]=xOff+offset;
        array2[arrayOffset2+ 3]=0.0f;
        array[arrayOffset+ 6]=x+width;
        array[arrayOffset+ 7]=y-height;
        array[arrayOffset+ 8]=z;
        array2[arrayOffset2+ 4]=xOff+offset;
        array2[arrayOffset2+ 5]=1.0f;
        array[arrayOffset+ 9]=x;
        array[arrayOffset+10]=y;
        array[arrayOffset+11]=z;
        array2[arrayOffset2+ 6]=xOff;
        array2[arrayOffset2+ 7]=0.0f;
        array[arrayOffset+12]=x+width;
        array[arrayOffset+13]=y-height;
        array[arrayOffset+14]=z;
        array2[arrayOffset2+ 8]=xOff+offset;
        array2[arrayOffset2+ 9]=1.0f;
        array[arrayOffset+15]=x;
        array[arrayOffset+16]=y-height;
        array[arrayOffset+17]=z;
        array2[arrayOffset2+10]=xOff;
        array2[arrayOffset2+11]=1.0f;
    }

    public static void makeRoundVideoCanvas(float[] array,int arrayOffset,int steppSize,float height,float width){
        //4 rectangles
        int count=0;
        float r=(width/2);
        /*
        double distance_x=1-Math.cos(Math.toRadians((double)alpha_steppSize));
        distance_x=(distance_x*distance_x);
        double distance_z=Math.sin(Math.toRadians((double)alpha_steppSize));
        distance_z=(distance_z*distance_z);
        float texture_radius=(float)Math.sqrt(distance_x+distance_z);*/
        float alpha_steppSize=(180/steppSize);
        float texture_radius=1/(180/alpha_steppSize);
        for(int alpha=180;alpha>0;alpha-=alpha_steppSize){
            float x1,y1,z1,x2,y2,z2,x3,y3,z3,x4,y4,z4;
            x1=(float)(r*Math.cos(Math.toRadians((double)alpha)));
            y1=(height/2);
            z1=-(float)(r*Math.sin(Math.toRadians((double) alpha)));
            x2=(float)(r*Math.cos(Math.toRadians((double)alpha+alpha_steppSize)));
            y2=(height/2);
            z2=-(float)(r*Math.sin(Math.toRadians((double) alpha+alpha_steppSize)));
            x3=(float)(r*Math.cos(Math.toRadians((double)alpha+alpha_steppSize)));
            y3=-(height/2);
            z3=-(float)(r*Math.sin(Math.toRadians((double)alpha+alpha_steppSize)));
            x4=(float)(r*Math.cos(Math.toRadians((double)alpha)));
            y4=-(height/2);
            z4=-(float)(r*Math.sin(Math.toRadians((double) alpha)));
            makeRectangle3(array,arrayOffset+(count*5*6),x1,y1,z1,x2,y2,z2,x3,y3,z3,x4,y4,z4,count*texture_radius,texture_radius);
            count++;
        }
    }

    public static float[] getIcoSphereCoords(){
        float t = (float)((Math.sqrt(5) - 1)/2);
        float[][] icoshedronVertices = new float[][] {
                new float[] { -1,-t,0 },
                new float[] { 0,1,t },
                new float[] { 0,1,-t },
                new float[] { 1,t,0 },
                new float[] { 1,-t,0 },
                new float[] { 0,-1,-t },
                new float[] { 0,-1,t },
                new float[] { t,0,1 },
                new float[] { -t,0,1 },
                new float[] { t,0,-1 },
                new float[] { -t,0,-1 },
                new float[] { -1,t,0 },
        };
        for (float[] v : icoshedronVertices) {
            // Normalize the vertices to have unit length.
            float length = (float)Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);
            v[0] /= length;
            v[1] /= length;
            v[2] /= length;
        }
        for(int i1=0;i1<12;i1++){
            for(int i2=0;i2<3;i2++){
                icoshedronVertices[i1][i2]=6*icoshedronVertices[i1][i2];
            }
        }
        int[][] icoshedronFaces = new int[][] {
                { 3, 7, 1 },
                { 4, 7, 3 },
                { 6, 7, 4 },
                { 8, 7, 6 },
                { 7, 8, 1 },
                { 9, 4, 3 },
                { 2, 9, 3 },
                { 2, 3, 1 },
                { 11, 2, 1 },
                { 10, 2, 11 },
                { 10, 9, 2 },
                { 9, 5, 4 },
                { 6, 4, 5 },
                { 0, 6, 5 },
                { 0, 11, 8 },
                { 11, 1, 8 },
                { 10, 0, 5 },
                { 10, 5, 9 },
                { 0, 8, 6 },
                { 0, 10, 11 },
        };
        float[] vertices=new float[20*15];
        for(int i=0;i<20;i++){
            int index=icoshedronFaces[i][0];
            //first Triangle with tex. coords
            vertices[(i*15)+0]=icoshedronVertices[index][0];
            vertices[(i*15)+1]=icoshedronVertices[index][1];
            vertices[(i*15)+2]=icoshedronVertices[index][2];
            vertices[(i*15)+3]=0.0f;
            vertices[(i*15)+4]=1.0f;
            index=icoshedronFaces[i][1];
            vertices[(i*15)+5]=icoshedronVertices[index][0];
            vertices[(i*15)+6]=icoshedronVertices[index][1];
            vertices[(i*15)+7]=icoshedronVertices[index][2];
            vertices[(i*15)+8]=0.0f;
            vertices[(i*15)+9]=0.0f;
            index=icoshedronFaces[i][2];
            vertices[(i*15)+10]=icoshedronVertices[index][0];
            vertices[(i*15)+11]=icoshedronVertices[index][1];
            vertices[(i*15)+12]=icoshedronVertices[index][2];
            vertices[(i*15)+13]=1.0f;
            vertices[(i*15)+14]=0.0f;
        }

        return vertices;
    }
    public static void checkVecFrustrum(float[] viewM,float vecX,float vecY,float vecZ,float vecW){
        float[] vec={
                vecX,vecY,vecZ,vecW
        };
        float[] result=new float[4];
        Matrix.multiplyMV(result,0,viewM,0,vec,0);
        System.out.println("1) "+result[0]+" 2) "+result[1]+" 3) "+result[2]+" 4) "+result[3]);
    }


}


