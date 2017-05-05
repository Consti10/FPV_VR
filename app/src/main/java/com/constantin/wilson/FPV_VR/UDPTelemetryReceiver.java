package com.constantin.wilson.FPV_VR;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by Constantin on 22.11.2016.
 */

public class UDPTelemetryReceiver {
    private int server_port,port_timeout;
    private byte[] message = new byte[1024];
    private Thread receiverThread;
    private DatagramPacket p=null;
    private DatagramSocket s = null;
    private boolean exception=false;
    private volatile boolean running=false;
    private int parser;
    public static int LTM=0;
    public static int FRSKY=1;
    public static int RSSI=2;
    public static int MAVLINK=3;

    public UDPTelemetryReceiver(int server_port, int port_timeout, int parser){
        this.server_port=server_port;
        this.port_timeout=port_timeout;
        this.parser=parser;
        p = new DatagramPacket(message, message.length);
    }

    private void receive(){
        try {s = new DatagramSocket(server_port);
            s.setSoTimeout(port_timeout);
        } catch (SocketException e) {e.printStackTrace();}

        while (running && s != null) {
            try {
                s.receive(p);
            } catch (IOException e) {
                exception=true;
                if(! (e instanceof SocketTimeoutException)){
                    e.printStackTrace();
                }
            }
            if(!exception){
                //System.out.println("Receiving OSD Data; Parsing required; length:"+p.getLength());
                //we have to parse Telemetry Data
                if(parser==LTM){
                    int ret= OSDReceiverRenderer.parseLTM(message,p.getLength());
                    //System.out.println("Parse LTM");
                }else if(parser==FRSKY){
                    int ret= OSDReceiverRenderer.parseFRSKY(message,p.getLength());
                    //System.out.println("PARSE frsky");
                }else if(parser==RSSI){
                    /*float is 32 bit long (4 bytes) or 4*8 bit;
                    * excpects 4 bytes in little-endian representing a float value*/
                    /*
                    if(p.getLength()<4){
                        //
                    }else{
                        byte[] fb=new byte[4];
                        fb[0]=message[0];fb[1]=message[1];fb[2]=message[2];fb[3]=message[3];
                        float rssi=ByteBuffer.wrap(fb).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                        //System.out.println("RSSI:"+rssi);
                        int ret=OSDReceiverRenderer.setWBRSSI(rssi);
                        //System.out.printf("byte 0:"+Byte.valueOf(fb[0])+"byte 1:"+Byte.valueOf(fb[1])+
                          //      "byte 2:"+Byte.valueOf(fb[2])+"byte 3:"+Byte.valueOf(fb[3]));
                    }
                    //System.out.println("length:"+p.getLength());*/
                    /*uint8_t is 1 byte long and ranges between -128 to 128; It represents the signal strength.*/
                    //should work
                    byte ByteUint8_t=message[0];
                    float rssi=(float) ByteUint8_t;
                    //System.out.printf("byte:"+Byte.valueOf(ByteUint8_t)+"  float:"+rssi);
                    if(p.getLength()>0){int ret= OSDReceiverRenderer.setWBRSSI(rssi);}

                }else if(parser==MAVLINK){
                    int ret= OSDReceiverRenderer.parseMAVLINK(message,p.getLength());
                    System.out.printf("MAVLINK");
                }
                //make Sure we have enough bytes when parsing the OSD data next time.
                //TODO: find a better approach
                //try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
            }else{exception=false;}
        }
        if (s != null) {
            s.close();
            s=null;
        }
    }

    public void startReceiving(){
        running=true;
        receiverThread=new Thread(){
            @Override
            public void run() {
                receive();}
        };
        receiverThread.start();

    }
    public void stopReceiving(){
        running=false;
    }

}
