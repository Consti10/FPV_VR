package com.constantin.wilson.FPV_VR;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

//works at least somehow
/*
* the Phone GPS is used to calculate distance drone-phone*/

public class GPSHelper implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener{
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private volatile boolean newDataAvailable=false;
    private Location mCurrentLocation;

    public GPSHelper(Context context){
        mContext=context;
        if(mGoogleApiClient==null){
            mGoogleApiClient=new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }
    public void stop(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
        if(mGoogleApiClient!=null){
            mGoogleApiClient.disconnect();
        }
    }
    public boolean newDataAvailable(){
        return newDataAvailable;
    }

    public Location getCurrentLocation(){
        newDataAvailable=false;
        //System.out.println(mCurrentLocation.toString());
        System.out.println("Lat:"+mCurrentLocation.getLatitude()+" Lon:"+mCurrentLocation.getLongitude()+" Alt:"
                +mCurrentLocation.getAltitude()+" Accuracy:"+mCurrentLocation.getAccuracy()+" Provider"
                +mCurrentLocation.getProvider());
        if(mCurrentLocation!=null){
            return mCurrentLocation;
        }else{
            return null;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mCurrentLocation!=null){
            newDataAvailable=true;
        }else {
            System.out.println("couldnt get last Position");
        }
        try{
            LocationRequest locReq=LocationRequest.create();
            locReq.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            locReq.setInterval(1000);
            locReq.setFastestInterval(1000);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,locReq,this);
        }catch (Exception e){e.printStackTrace();}
    }
    @Override
    public void onLocationChanged(Location location){
        newDataAvailable=true;
        mCurrentLocation=location;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
