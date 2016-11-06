package com.constantin.wilson.FPV_VR;


import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class MyOSDReceiverRendererHelper {

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
        OpenGLHelper.makeRectangle(FloatReturn, 0, x, y - 0.0f, z, height, width, 0.0f, 1.0f, 0.0f, 0.0f);
        OpenGLHelper.makeRectangle(FloatReturn, (1 * 42), x + (width / 2), y - 0.5f, z, height, width / 2, 0.0f, 1.0f, 0.0f, 1.0f);
        OpenGLHelper.makeRectangle(FloatReturn, (2 * 42), x, y - 1.0f, z, height, width, 0.0f, 1.0f, 0.0f, 1.0f);
        OpenGLHelper.makeRectangle(FloatReturn, (3 * 42), x + (width / 2), y - 1.5f, z, height, width / 2, 0.0f, 1.0f, 0.0f, 1.0f);
        OpenGLHelper.makeRectangle(FloatReturn, (4 * 42), x, y - 2.0f, z, height, width, 0.0f, 1.0f, 0.0f, 1.0f);
        OpenGLHelper.makeRectangle(FloatReturn, (5 * 42), x + (width / 2), y - 2.5f, z, height, width / 2, 0.0f, 1.0f, 0.0f, 1.0f);
        OpenGLHelper.makeRectangle(FloatReturn, (6 * 42), x, y - 3.0f, z, height, width, 0.0f, 1.0f, 0.0f, 1.0f);
        OpenGLHelper.makeRectangle(FloatReturn, (7 * 42), x + (width / 2), y - 3.5f, z, height, width / 2, 0.0f, 1.0f, 0.0f, 1.0f);
        OpenGLHelper.makeRectangle(FloatReturn, (8 * 42), x, y - 4.0f, z, height, width, 0.0f, 1.0f, 0.0f, 1.0f);
        OpenGLHelper.makeRectangle(FloatReturn, (9 * 42), -x - width, y - 0.0f, z, height, width, 1.0f, 1.0f, 0.0f, 1.0f);
        OpenGLHelper.makeRectangle(FloatReturn, (10 * 42), -x - width, y - 0.5f, z, height, width / 2, 0.0f, 1.0f, 0.0f, 1.0f);
        OpenGLHelper.makeRectangle(FloatReturn, (11 * 42), -x - width, y - 1.0f, z, height, width, 0.0f, 1.0f, 0.0f, 1.0f);
        OpenGLHelper.makeRectangle(FloatReturn, (12 * 42), -x - width, y - 1.5f, z, height, width / 2, 0.0f, 1.0f, 0.0f, 1.0f);
        OpenGLHelper.makeRectangle(FloatReturn, (13 * 42), -x - width, y - 2.0f, z, height, width, 0.0f, 1.0f, 0.0f, 1.0f);
        OpenGLHelper.makeRectangle(FloatReturn, (14 * 42), -x - width, y - 2.5f, z, height, width / 2, 0.0f, 1.0f, 0.0f, 1.0f);
        OpenGLHelper.makeRectangle(FloatReturn, (15 * 42), -x - width, y - 3.0f, z, height, width, 0.0f, 1.0f, 0.0f, 1.0f);
        OpenGLHelper.makeRectangle(FloatReturn, (16 * 42), -x - width, y - 3.5f, z, height, width / 2, 0.0f, 1.0f, 0.0f, 1.0f);
        OpenGLHelper.makeRectangle(FloatReturn, (17 * 42), -x - width, y - 4.0f, z, height, width, 0.0f, 1.0f, 0.0f, 1.0f);
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
    public static void getOverdrawCoordByFormat(float videoFormat,float videoDistance,int vb,int uvb
                                               ,boolean enable_battery_life,boolean enable_lattitude_longitude,
                                                boolean enable_rssi,boolean enable_X2,boolean enable_height,boolean enable_voltage,
                                                boolean enable_ampere,boolean enable_X3,boolean enable_speed,boolean enable_X4){
        FloatBuffer bbVertices = ByteBuffer.allocateDirect(
                ((14*6*6*3)+(10*6*3))*4) .order(ByteOrder.nativeOrder()).asFloatBuffer();
        FloatBuffer bbUV = ByteBuffer.allocateDirect(
                ((14*6*6*2)+(10*6*2))*4) .order(ByteOrder.nativeOrder()).asFloatBuffer();

        //Texture atlas offset
        float offset=(1.0f/OverlayTexturePicture.number_of_units);
        float width=3,height=0.5f;
        float x=-5.0f,y=(((1.0f/videoFormat)*5.0f)+(2*height))+0.0001f,z=-videoDistance;
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
        float[] verticesData=new float[2*6*6*3];
        float[] uvData=new float[2*6*6*2];
        //OpenGLHelper.makeRectangle2(TriangleVerticesData,0 *30,x0 ,y0 ,z0 ,height,10,0,1);
        //OpenGLHelper.makeRectangle2(TriangleVerticesData,1 *30,0 ,0 ,z0 ,height,10,0,1);
        //OpenGLHelper.makeOverlayUnit(TriangleVerticesData,0 *30,0 ,0 ,z0 ,height,10,0,1);
        float stringO=OverlayTexturePicture.stringO;
        OpenGLHelper.makeOverlayUnit(verticesData,0 *18*6,uvData,0*12*6,x0 ,y0 ,z0 ,height,width,stringO+0*3*offset,offset,true);
        OpenGLHelper.makeOverlayUnit(verticesData,1 *18*6,uvData,1*12*6,x1 ,y1 ,z1 ,height,width,stringO+1*3*offset,offset,true);
        bbVertices.position(0);
        bbVertices.put(verticesData);
        bbUV.position(0);
        bbUV.put(uvData);
        OpenGLHelper.makeOverlayUnit(verticesData,0 *18*6,uvData,0*12*6,x2 ,y2 ,z2 ,height,width,stringO+2*3*offset,offset,enable_lattitude_longitude);
        OpenGLHelper.makeOverlayUnit(verticesData,1 *18*6,uvData,1*12*6,x3 ,y3 ,z3 ,height,width,stringO+3*3*offset,offset,enable_lattitude_longitude);
        bbVertices.position(2*18*6);
        bbVertices.put(verticesData);
        bbUV.position(2*12*6);
        bbUV.put(uvData);
        OpenGLHelper.makeOverlayUnit(verticesData,0 *18*6,uvData,0*12*6,x4 ,y4 ,z4 ,height,width,stringO+4*3*offset,offset,enable_battery_life);
        OpenGLHelper.makeOverlayUnit(verticesData,1 *18*6,uvData,1*12*6,x5 ,y5 ,z5 ,height,width,stringO+5*3*offset,offset,enable_voltage);
        bbVertices.position(4*18*6);
        bbVertices.put(verticesData);
        bbUV.position(4*12*6);
        bbUV.put(uvData);
        OpenGLHelper.makeOverlayUnit(verticesData,0 *18*6,uvData,0*12*6,x6 ,y6 ,z6 ,height,width,stringO+6*3*offset,offset,enable_rssi);
        OpenGLHelper.makeOverlayUnit(verticesData,1 *18*6,uvData,1*12*6,x7 ,y7 ,z7 ,height,width,stringO+7*3*offset,offset,enable_ampere);
        bbVertices.position(6*18*6);
        bbVertices.put(verticesData);
        bbUV.position(6*12*6);
        bbUV.put(uvData);
        OpenGLHelper.makeOverlayUnit(verticesData,0 *18*6,uvData,0*12*6,x8 ,y8 ,z8 ,height,width,stringO+8*3*offset,offset,enable_X2);
        OpenGLHelper.makeOverlayUnit(verticesData,1 *18*6,uvData,1*12*6,x9 ,y9 ,z9 ,height,width,stringO+9*3*offset,offset,enable_X3);
        bbVertices.position(8*18*6);
        bbVertices.put(verticesData);
        bbUV.position(8*12*6);
        bbUV.put(uvData);
        OpenGLHelper.makeOverlayUnit(verticesData,0*18*6,uvData,0*12*6,x10,y10,z10,height,width,stringO+10*3*offset,offset,enable_speed);
        OpenGLHelper.makeOverlayUnit(verticesData,1*18*6,uvData,1*12*6,x11,y11,z11,height,width,stringO+11*3*offset,offset,enable_X4);
        bbVertices.position(10*18*6);
        bbVertices.put(verticesData);
        bbUV.position(10*12*6);
        bbUV.put(uvData);
        OpenGLHelper.makeOverlayUnit(verticesData,0*18*6,uvData,0*12*6,x12,y12,z12,height,width,stringO+12*3*offset,offset,enable_height);
        OpenGLHelper.makeOverlayUnit(verticesData,1*18*6,uvData,1*12*6,x13,y13,z13,height,width,stringO+13*3*offset,offset,enable_height);
        bbVertices.position(12*18*6);
        bbVertices.put(verticesData);
        bbUV.position(12*12*6);
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
        OpenGLHelper.makeRectangle4(verticesData,0*6*3,uvData,0*6*2,x14,y14,z14,0.125f,0.5f,heightO+0*3*offset,offset*3);
        OpenGLHelper.makeRectangle4(verticesData,1*6*3,uvData,1*6*2,x15,y15,z15,0.125f,0.5f,heightO+1*3*offset,offset*3);
        OpenGLHelper.makeRectangle4(verticesData,2*6*3,uvData,2*6*2,x16,y16,z16,0.125f,0.5f,heightO+2*3*offset,offset*3);
        OpenGLHelper.makeRectangle4(verticesData,3*6*3,uvData,3*6*2,x17,y17,z17,0.125f,0.5f,heightO+3*3*offset,offset*3);
        OpenGLHelper.makeRectangle4(verticesData,4*6*3,uvData,4*6*2,x18,y18,z18,0.125f,0.5f,heightO+4*3*offset,offset*3);
        OpenGLHelper.makeRectangle4(verticesData,5*6*3,uvData,5*6*2,x19,y19,z19,0.125f,0.5f,heightO+0*3*offset,offset*3);
        OpenGLHelper.makeRectangle4(verticesData,6*6*3,uvData,6*6*2,x20,y20,z20,0.125f,0.5f,heightO+1*3*offset,offset*3);
        OpenGLHelper.makeRectangle4(verticesData,7*6*3,uvData,7*6*2,x21,y21,z21,0.125f,0.5f,heightO+2*3*offset,offset*3);
        OpenGLHelper.makeRectangle4(verticesData,8*6*3,uvData,8*6*2,x22,y22,z22,0.125f,0.5f,heightO+3*3*offset,offset*3);
        OpenGLHelper.makeRectangle4(verticesData,9*6*3,uvData,9*6*2,x23,y23,z23,0.125f,0.5f,heightO+4*3*offset,offset*3);
        bbVertices.position(14*18*6);
        bbVertices.put(verticesData);
        bbUV.position(14*12*6);
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
        float[] indices = new float[3*6*2];
        if (number > 999) {
            Log.d("osdhelper", "number too big");
            number=999;
        }
        int kommastellen=0;
        if(number<100){
            kommastellen++;
            if(number<10){
                kommastellen++;
            }
        }
        if(kommastellen==0){
            number=(int)number;
            int hundreds = 0, tens = 0, ones = 0;
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
            float u=(1.0f/OverlayTexturePicture.number_of_units)*hundreds; //u-->x
            float v=0.0f;
            float width= (1.0f/OverlayTexturePicture.number_of_units);
            float height=1.0f;
            indices[0] = u;
            indices[1] = v;
            indices[2] = u+width;
            indices[3] = v;
            indices[4] = u+width;
            indices[5] = v+height;
            indices[6] = u;
            indices[7] = v;
            indices[8] = u+width;
            indices[9] = v+height;
            indices[10] =u;
            indices[11] =v+height;
            u=(1.0f/OverlayTexturePicture.number_of_units)*tens; //u-->x
            indices[12] = u;
            indices[13] = v;
            indices[14] = u+width;
            indices[15] = v;
            indices[16] = u+width;
            indices[17] = v+height;
            indices[18] = u;
            indices[19] = v;
            indices[20] = u+width;
            indices[21] = v+height;
            indices[22] =u;
            indices[23] =v+height;
            u=(1.0f/OverlayTexturePicture.number_of_units)*ones; //u-->x
            indices[24] = u;
            indices[25] = v;
            indices[26] = u+width;
            indices[27] = v;
            indices[28] = u+width;
            indices[29] = v+height;
            indices[30] = u;
            indices[31] = v;
            indices[32] = u+width;
            indices[33] = v+height;
            indices[34] =u;
            indices[35] =v+height;
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
            float u=(1.0f/OverlayTexturePicture.number_of_units)*tens; //u-->x
            float v=0.0f;
            float width= (1.0f/OverlayTexturePicture.number_of_units);
            float height=1.0f;
            indices[0] = u;
            indices[1] = v;
            indices[2] = u+width;
            indices[3] = v;
            indices[4] = u+width;
            indices[5] = v+height;
            indices[6] = u;
            indices[7] = v;
            indices[8] = u+width;
            indices[9] = v+height;
            indices[10] =u;
            indices[11] =v+height;
            u=(1.0f/OverlayTexturePicture.number_of_units)*10+(1.0f/OverlayTexturePicture.number_of_units)*ones; //u-->x
            indices[12] = u;
            indices[13] = v;
            indices[14] = u+width;
            indices[15] = v;
            indices[16] = u+width;
            indices[17] = v+height;
            indices[18] = u;
            indices[19] = v;
            indices[20] = u+width;
            indices[21] = v+height;
            indices[22] =u;
            indices[23] =v+height;
            u=(1.0f/OverlayTexturePicture.number_of_units)*komma; //u-->x
            indices[24] = u;
            indices[25] = v;
            indices[26] = u+width;
            indices[27] = v;
            indices[28] = u+width;
            indices[29] = v+height;
            indices[30] = u;
            indices[31] = v;
            indices[32] = u+width;
            indices[33] = v+height;
            indices[34] =u;
            indices[35] =v+height;
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
            float u=(1.0f/OverlayTexturePicture.number_of_units)*10+(1.0f/OverlayTexturePicture.number_of_units)*ones; //u-->x
            float v=0.0f;
            float width= (1.0f/OverlayTexturePicture.number_of_units);
            float height=1.0f;
            indices[0] = u;
            indices[1] = v;
            indices[2] = u+width;
            indices[3] = v;
            indices[4] = u+width;
            indices[5] = v+height;
            indices[6] = u;
            indices[7] = v;
            indices[8] = u+width;
            indices[9] = v+height;
            indices[10] =u;
            indices[11] =v+height;
            u=(1.0f/OverlayTexturePicture.number_of_units)*komma1; //u-->x
            indices[12] = u;
            indices[13] = v;
            indices[14] = u+width;
            indices[15] = v;
            indices[16] = u+width;
            indices[17] = v+height;
            indices[18] = u;
            indices[19] = v;
            indices[20] = u+width;
            indices[21] = v+height;
            indices[22] =u;
            indices[23] =v+height;
            u=(1.0f/OverlayTexturePicture.number_of_units)*komma; //u-->x
            indices[24] = u;
            indices[25] = v;
            indices[26] = u+width;
            indices[27] = v;
            indices[28] = u+width;
            indices[29] = v+height;
            indices[30] = u;
            indices[31] = v;
            indices[32] = u+width;
            indices[33] = v+height;
            indices[34] =u;
            indices[35] =v+height;
        }


        return  indices;
    }


}
