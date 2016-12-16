# FPV_VR

FPV_VR

How it works:
This Android app can receive a fpv video stream, decode it with ultra low latency and display it ether in a single window, side by side, or side by side in VR mode. 
Furthermore you can enable an OSD in the VR world.
To decode the video it uses the low-level Android MediaCodec API and for displaying the video on screen it uses ether the SurfaceView or TextureView pipeline
provided by Android, or when you want to use the app in VR mode and/or with OSD it uses OpenGL ES2.0 .
You can ether buy this app in the android Play Store https://play.google.com/store/apps/details?id=com.constantin.wilson.FPV_VR&hl=en, support developement and get active support with the app.
Or you can compile the source code from https://github.com/Consti10/FPV_VR for private use with android-studio.

Howto use with EZ-Wifibroadcast
(Your data will go trought he following pipeline):
Light->rpi cam->rpi h.264 encoder->wifibroadcast tx-> air(wifibradcast packets)->wifibradcast rx->usb-cable->smartphone-app->hw h.264 decoder of Smartphone->Screen
Step 1: Download FPV_VR app from https://play.google.com/store/apps/details?id=com.constantin.wilson.FPV_VR&hl=en or compile 
the source code yourself.
Step 2: Dwonload newest ez wifibroadcast image (at least 1.3 beta) from https://www.rcgroups.com/forums/showthread.php?t=2664393 and install on your ground/air pi's
Step 3: Enable "usb Tetehering" ( wifibroadcast.txt USB_TETHER=Y ) on your rx pi, rest leafe dafault
Step 4: connect your smartphone via usb cable to your RX! pi and open an usb hotspot in your smartphone settings (usb-tethering)
Step 5: Open "TestActvity". You should now receive nalu's (frames) on your smartphone 
PICTURE before
PICTURE after
Step 6: in your "Decoder Settings" set "receive from udp"
Step 7: Open one of the 3 Activities. You can now see the video. If not, try restarting your tx pi first, else continue with "decoder debuging"

How to use with any other fpv video streaming setup based on the pi camera (or any other camera): (AVANCED !)
(Your data will go trought he following pipeline):
Light->rpi cam->rpi h.264 encoder->network->smartphone->hardware h.264 decoder from Smartphone->Screen
You can pipe any raw .h264 stream to udp port 5000 and receive an Image. The same for the OSD data on Port 5001 (5001=LTM,5002=frsky,5003=wifibroadcast rssi). Example:
1)Setup a network between your rpi with camera and the smartphone.
2)execute raspivid -w 960 -h 810 -b 3000000 -fps 49 -t 0 -pf baseline -ih -g 49 -o - | socat -b 1024 - udp4-datagram:10.69.47.45:5000 on your rpi.
Of course you need to use your phone's ip address. You can find it in "test Activity" or running ifconfig usw.


Decoder Debugging:
Since the app uses really low-level functions to reduce latency it may be needed to adjust the app to your specific hw decoder. First, you should try 
out setting the Decoder to "sw decoder", disable formatRPI and check for an image. If you don't get an image with the sw decoder you are propably not receiving a correct video stream.
Then you can try out disabling multithread or enable "user debug" and last write me so I can fix your problem. However,on most modern Smartphones the encoder is now 
working out of the box (Galaxy S3,S5,S6,S5-S6 Edge, some smartphones with mali gpu's, and LG G3) 

OSD: The app does support LTM (Light-Telemetry-Protokoll) and FRSKY packets received on udp port 5001 bzw. 5002, and wifibroadcast rssi values on 50003. However, osd support is currently only succesfully tested with LTM packets.
in ez-wifibroadcast 1.3, you have to pipe the OSD data to udp ports yourself. This feature will follow soon.

Settings (example) for vr googles like Cardboard, Gear Vr usw:
video Distance & model Distance set to 10
tesselation set to 20
enable distortion correction
enable Head Tracking
rest can be left deafault.

Settings (example) for vr googles with a really low fov ( also refferd to as side-by-side video googles)
videoDistance & model Distance set to 5.7
tesselation to 1
disable Distortion Correction
disable Head Tracking

Settings (example) for usage without vr googles (without side-by-side) 
Disable stereo Rendering
disable Distortion correction
disable headtracking
video Distance to 5

Settings:
1. OpenGL Settings
1.1) Performance Hacks:
Independently from the decoder fps, the higher the OpenGl fps the lower latency you have.
1.1.1) swap intervall zero: increases openGl fps on most phones. default on 
1.1.2) presentationTime: basically has the same result like swap intervall,and some additional benefits. however,see description in app.
1.1.3) clearFramebuffer: on my Huawei this call blocks the pipeline for a short time, and disabling it has no negative effects. However,on Adreno gpu's this results in unexpected drawing issues. Default leave on.

1.2) Stereo and VR Rendering
1.2.1) enable stereo Rendering; For people who want to use the app with video&osd, but without vr googles set to false
1.2.2) interpupilarry distance: leave default=2; when you don't want a stereo effect set to 0
1.2.3) tesselationFactor: s. Description in App
1.2.4) distortion correction: enable for cardboard,gear vr usw googles
1.2.5) viewport scale: leave default,it is better to change the video canvas distance

1.3) VideoFormat: for 720p: 1280/720=1.778; for 800*600 1.333 usw
1.4) video Distance: Distance eye to video canvas. For a smaller canvas: a higher Distance ( when you are using vr googles with a high fov set to ~10)
1.5) Head tracking: enable when you are using vr googles. Not for a gimbal on your kopter,but to look around in vr. You have to install google cardboard and configure Cardboard if you want to use tis feature
1.6) OSD on/off:

1.7 usw) OSD specific settings: don't forget to change your model distance when changing video canvas distance.
Rest: switch for the different elements. You could use both frsky and ltm parser at the same time.
Home Arrow is currently not working.
To use the "Battery %" you have to set your number of Lipo Cells and set cell_min and cell_max for your lipo.

2. Decoder Settings:
2.1) Data Source: ether receive raw .h264 stream on udp (for wifibroadcast usw) or parse a test .h264 file for testing
2.2) FileName1: file name for the .h264 test file (with .h264 on the end)
2.3) Decoder multithread: default on,disable when you have problems with the hw decoder.
2.4) select your decoder: i higly recommended the dafault hw decoder for streaming. Only use sw decoder when you have problems with the hw decoder.

3. Ground recording Settings: file name for ground recording and on/off switch.

4.) Debug Settings: Latency file with info about the last sessions,  and "user debug". Never leave userdebug on during flight streaming.

##############################################################################################################################################
OSD:
|---------------------------------------------------------|
|Decoder fps          |Latitude in deree  | %Battery      |
|OpenGL Renderer fps  |Longitude in degree| Voltage in V  |
|RSSI of receiver     |                   | Ampere in A   |
|---------------------------------------------------------|
|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|
|                                                         |
|                         Videostream                     |
|                                                         |
|+++++++++++++++++++++++++++++++++++++++++++++++++++++++++|
|---------------------------------------------------------|
|Distance to home in m|uav speed in km/h | Height in m    | <--Barometer
|X(for future use)    |X(for future use) | Height in m    | <--GPS; used for Height Ladder
|---------------------------------------------------------|


Decoder Fps: Should match the fps (+-1) of your rpi camera. 
OpenGL  Fps: The more OpenGl fps the lower latency you get, since there is no synchronisation between Decoder/Opengl. This might change when Front Buffer Rendering bzw. Single Buffer Rendering is available for Android 7.
RSSI: shows the wifibroadcast receiver rssi in dbm;
Latt: Lattitude in degree
Lon: Longitude in degree
%Battery: Calculate % Battery left by using the Voltage and the number of Cells in your Settings. Don't forget to change your number of Cells (4s LiPo: 4Cells), and- if needed- the max and min V of your single Lipo Cells  
V: Voltage in V
A: Ampere in A

m(Ho): distance to Home. Let the App Calculate your Home position ether automatically by using the in-build gps or set HomeLon,Lat automatically
:X: For future use.
:km/h :uav Speed in km/h
:X : for future use
m(Ba): Height in m from your Barometer if transmitted; 
m(g): Height in m from your GPS if transmitted; used for height Ladder.

Your Copter's Yaw,Pitch and Roll is represented by a simple object of 5 coloured Triangles

Default Values: fpsD=0;fpsGL=0;Lat=0;Lon=0;%Batt=0;V=0;A=0;m(Ho)=0; if you don't transmit your Copters Position it will be 999 because the App will calculate the distance between your Phone and the North Pole (Lat.0,Lon.0);
X=counter;km/h=0;X=counter;m(Ba)=0;m(g)=20;
#######################################################################################################################################
Known Issues and Tipps:
Since the App needs a lot of RAM and cpu/gpu, close any "background apps", especially games
Restarting your OpenGL activity too often and fast may result in an out of memory app crash. Give your OS time to garbage collect the previous activity.
Always leave your Activity via the "back" button. else you might have to restart your app.

On the galaxy s3 you have to disable "format rpi" and start your activity before your tx pi 
#######################################################################################################################################
FAQ:
1) Why is it impossible to use my Smartphone Wlan adapter with wifibroadcast:
-Android doesn't support "Monitor Mode", so it is hard to port wifibroadcast to Android OS. If you are really familiar with Android Custom ROM's / linux you could theoretically do it, info: https://befinitiv.wordpress.com/2015/04/18/porting-wifibroadcast-to-android/

2) how high is the lag of the App ?
For my Smartphone (Huawei Ascend p7, mali 450 mp gpu, ~3 years old hw) the overall latency glas-to-glas is about 130ms. This splits up to: ~60ms Lag App, ~20ms Wifibroadcast, ~50ms Camera. However, the Lag can be lower on Smartphone with AMOLED displays and is lower on modern smartphones.
You can find a "latency File" in the App-Settings.
The "overall measured lag of the app" shouldn't be higher than 20ms (on my Smartphone: 12ms); when this value is higher, your hw decoder is probably buffering frames, a hw manufacturer's feature for smooth playback that isn't good for live streaming.
WATCH OUT! These values are only meaningfull when you receive the Stream via UDP, since "receive from file" has no synchronisation.
The Rest of the Lag is induced by "Android's SurfaceFlinger",  which can add as high as 3 "display-frames (3*16ms) latency.
*UPDATE 29.03.2016: Setting swap intervall to zero reduces lag by 1/2 more frame on my device,and increases OpenGL fps*
This is a big Problem of Android (and any other OS). 
When I started developing this App ~1 Year ago it seemed like there won't be any way around Android's "Display Lag". 
However, as of 2016 November 12., this problem gets some more attention because of VR applications, (Google Daydream, Gear VR) and might
be solved in Android 7. When I have a Phone that supports Android Nougat and is 'daydream-ready' I'l probably be able to reduce the lag of the app drastically.
At the Time the App is, as far as it goes for a "non-broadcom-hw-engineerer", optimized for real-time. And I am proud to say that that this App is at least as fast as the "Game Stream" deals by nVidia or Stream (reference Values for Gamestream from here, interesting video:
https://www.youtube.com/watch?v=a9-bdXUC0j0

3) my Phone disconnects the hotspot to the pi after a short time:
The Phone gets automatically charged by your pi,which can be a problem. Provide suficient power to your ground pi or take a look at:
https://www.raspberrypi.org/forums/viewtopic.php?f=29&t=100244

4) Why Isn't the App a normal "Cardboard App"
Cardboard's "Distortion Renderer" would increase Lag again. The App uses some of the Cardboard-Api's f.e "HeadTracker", But uses a completely other approach to distort the image.
(vertex displacement distortion correction)
