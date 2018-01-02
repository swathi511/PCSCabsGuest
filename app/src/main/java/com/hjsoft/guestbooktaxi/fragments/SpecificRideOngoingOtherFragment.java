package com.hjsoft.guestbooktaxi.fragments;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.activity.HomeActivity;
import com.hjsoft.guestbooktaxi.activity.RideStopActivity;
import com.hjsoft.guestbooktaxi.adapter.DBAdapter;
import com.hjsoft.guestbooktaxi.model.BookCabPojo;
import com.hjsoft.guestbooktaxi.model.CabLocationPojo;
import com.hjsoft.guestbooktaxi.model.FormattedAllRidesData;
import com.hjsoft.guestbooktaxi.model.RideStopPojo;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 9/3/17.
 */
public class SpecificRideOngoingOtherFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {

    Button btTrackRide;
    TextView tvMyLoc;
    SupportMapFragment mapFragment;
    BottomSheetDialogFragment myBottomSheet;
    ImageButton ibPhone;
    int position;
    ArrayList<FormattedAllRidesData> dataList=new ArrayList<>();
    DBAdapter dbAdapter;
    FormattedAllRidesData data;
    String requestId;
    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected boolean mRequestingLocationUpdates;
    boolean entered=false;
    final static int REQUEST_LOCATION = 199;
    protected Location mLastLocation;
    double latitude,longitude;
    Geocoder geocoder;
    List<Address> addresses;
    LatLng lastLoc;
    String complete_address;
    double curnt_lat,curnt_long;
    boolean trackingRide=false;
    Handler handler,hStopTime;
    Runnable r,rStopTime;
    Marker cab;
    String driverMobile,driverName;
    API REST_CLIENT;
    boolean track=false;
    View rootView;
    TextView tvDriverName,tvVehRegNo,tvPickup,tvDrop,tvDriverMobile;
    ImageButton ibClose,ibDots;
    View vwBottomSheet;
    LatLng startPosition,finalPosition,currentPosition;
    boolean isMarkerRotating=false;
    double cpLat,cpLng;
    String companyId="CMP00001";
    SimpleDraweeView ivDriverPic;
    String driverPic;
    RelativeLayout rlTrackRide;
    String stCategorySelected;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        Fresco.initialize(getActivity().getApplicationContext());

        geocoder = new Geocoder(getContext(), Locale.getDefault());

        establishConnection();

        dbAdapter=new DBAdapter(getContext());
        dbAdapter=dbAdapter.open();
        REST_CLIENT= RestClient.get();

        position=getArguments().getInt("position");
        dataList=(ArrayList<FormattedAllRidesData>) getArguments().getSerializable("list");
        //Toast.makeText(getActivity(),"value "+position,Toast.LENGTH_LONG).show();
        data=dataList.get(position);

        requestId=data.getRequestId();
        driverName=data.getDriverName();
        driverMobile=data.getDriverMobile();
        stCategorySelected=data.getVehicleCategory();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_specific_ride_ongoing_other, container,false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        btTrackRide=(Button)rootView.findViewById(R.id.fsroo_bt_track);
        tvMyLoc=(TextView)rootView.findViewById(R.id.fsroo_tv_place);
        ibPhone=(ImageButton)rootView.findViewById(R.id.fsroo_ib_phone);
        myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
        tvDriverName=(TextView)rootView.findViewById(R.id.fsroo_tv_dname);
        tvVehRegNo=(TextView)rootView.findViewById(R.id.fsroo_tv_veh_regno);
        tvPickup=(TextView)rootView.findViewById(R.id.fsroo_tv_ploc);
        tvDrop=(TextView)rootView.findViewById(R.id.fsroo_tv_dloc);
        ibClose=(ImageButton)rootView.findViewById(R.id.fsroo_ib_close);
        ibDots=(ImageButton)rootView.findViewById(R.id.fsroo_ib_dots);
        vwBottomSheet=(View)rootView.findViewById(R.id.fsroo_rl_bottom_sheet);
        ivDriverPic=(SimpleDraweeView) rootView.findViewById(R.id.fsroo_iv_driver);
        rlTrackRide=(RelativeLayout)rootView.findViewById(R.id.fsroo_rl);
        tvDriverMobile=(TextView)rootView.findViewById(R.id.fsroo_tv_gmobile);

        tvPickup.setText(data.getFromLocation());
        tvDrop.setText(data.getToLocation());
        tvDriverName.setText(driverName);
        tvVehRegNo.setText(data.getVehicleCategory());
        rlTrackRide.setVisibility(View.GONE);
        tvDriverMobile.setText(data.getDriverMobile());

        final BottomSheetBehavior behavior = BottomSheetBehavior.from(vwBottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGING....");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_HIDDEN");
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheetCallback", "slideOffset: " + slideOffset);

            }
        });


        driverPic=data.getDriverPic();

        if(driverPic.equals(""))
        {

        }
        else
        {
            Uri imageUri = Uri.parse(driverPic);
            ivDriverPic.setImageURI(imageUri);
        }


        ibPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+driverMobile));//GUEST NUMBER HERE...
                startActivity(intent);
            }
        });


        btTrackRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rlTrackRide.setVisibility(View.VISIBLE);
                trackingAndDutyFinishFunctionality();
            }
        });


        ibClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vwBottomSheet.setVisibility(View.GONE);

            }
        });

        ibDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vwBottomSheet.setVisibility(View.VISIBLE);
            }
        });

        return rootView;
    }

    public void establishConnection(){

        buildGoogleApiClient();
        buildLocationSettingsRequest();
        entered=true;
        checkForDutyFinish();
    }

    @Override
    public void onStart() {

        super.onStart();

        if(Build.VERSION.SDK_INT>=23)
        {
            if(!entered)
            {
            }
            else
            {
                mGoogleApiClient.connect();
                //super.onStart();
            }
        }
        else
        {
            mGoogleApiClient.connect();
            // super.onStart();
        }
    }

    @Override
    public void onStop() {

        if(mGoogleApiClient!=null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mGoogleApiClient!=null) {
            if (mGoogleApiClient.isConnected()) {
                stopLocationUpdates();

            }
        }

        if(trackingRide) {

            if(handler!=null) {
                handler.removeCallbacks(r);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(entered)
        {
            if(mGoogleApiClient.isConnecting()||mGoogleApiClient.isConnected())
            {
            }
            else {
                mGoogleApiClient.connect();
            }
        }

        if(mGoogleApiClient!=null) {

            if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }

        if(trackingRide)
        {

            if(handler!=null)
            {
                handler.post(r);
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        createLocationRequest();
    }

    protected void buildLocationSettingsRequest() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {

                    case LocationSettingsStatusCodes.SUCCESS:
                        //Location Settings Satisfied
                        startLocationUpdates();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to resolve it
                        break;
                }
            }
        });
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(45000);//45 sec
        mLocationRequest.setFastestInterval(30000);//5 sec
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {

        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
            mRequestingLocationUpdates=true;
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mapFragment.getMapAsync(this);
        if (mLastLocation == null) {
            try {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        if(mRequestingLocationUpdates)
        {
            startLocationUpdates();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        curnt_lat=location.getLatitude();
        curnt_long=location.getLongitude();

        if(track) {

            latitude = location.getLatitude();
            longitude = location.getLongitude();
            lastLoc = new LatLng(latitude, longitude);
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if(addresses.size()!=0) {
                    String address = addresses.get(0).getAddressLine(0);
                    String add1 = addresses.get(0).getAddressLine(1);
                    String add2 = addresses.get(0).getAddressLine(2);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    complete_address = address + " " + add1 + " " + add2;
                    tvMyLoc.setText(complete_address);
                }
                else
                {
                    tvMyLoc.setText("-");
                }
            } catch (IOException e) {
                e.printStackTrace();
                complete_address = "Unable to get the location details";
                tvMyLoc.setText(complete_address);
            }
            //  mMap.addMarker(new MarkerOptions().position(lastLoc)
            // .icon(BitmapDescriptorFactory.fromResource(R.drawable.sedan)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLoc, 16));
            mMap.getUiSettings().setMapToolbarEnabled(false);
        }



    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        lastLoc = new LatLng(latitude, longitude);
        curnt_lat=latitude;
        curnt_long=longitude;

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        try {
            mMap.setMyLocationEnabled(true);
        }catch (SecurityException e){e.printStackTrace();}

        /*mMap.addMarker(new MarkerOptions().position(lastLoc)
                .title("Current Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_image)));*/
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(lastLoc)
                .zoom(16)
                //.bearing(30).tilt(45)
                .build()));
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

    }

    public void trackingAndDutyFinishFunctionality()
    {
        btTrackRide.setVisibility(View.GONE);

        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(this,20000);

                if(mMap!=null) {
                    try {
                        mMap.setMyLocationEnabled(false);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }

                Call<List<CabLocationPojo>> call = REST_CLIENT.getCabLocation(requestId,companyId);
                call.enqueue(new Callback<List<CabLocationPojo>>() {
                    @Override
                    public void onResponse(Call<List<CabLocationPojo>> call, Response<List<CabLocationPojo>> response) {

                        if(myBottomSheet!=null) {

                            if (myBottomSheet.isAdded()) {
                                myBottomSheet.dismiss();
                            }
                        }
                        CabLocationPojo cl;
                        List<CabLocationPojo> cabLocData;
                        if (response.isSuccessful()) {
                            cabLocData = response.body();

                            for (int i = 0; i < cabLocData.size(); i++) {
                                cl = cabLocData.get(i);

                                LatLng cabLocLatLng = new LatLng(Double.parseDouble(cl.getLatitude()), Double.parseDouble(cl.getLongitude()));

                                if (mMap != null) {
                                    if (cab == null) {
                                        if(stCategorySelected.equals("mini")||stCategorySelected.equals("micra"))
                                        {
                                            cab = mMap.addMarker(new MarkerOptions().position(cabLocLatLng)
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mini_15)));
                                        }
                                        else {
                                            cab = mMap.addMarker(new MarkerOptions().position(cabLocLatLng)
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.prime_15)));
                                        }
                                    } else {
                                        // cab.setPosition(cabLocLatLng);
                                        startPosition = cab.getPosition();
                                        finalPosition = new LatLng(Double.parseDouble(cl.getLatitude()), Double.parseDouble(cl.getLongitude()));
                                        double toRotation = bearingBetweenLocations(startPosition, finalPosition);
                                        rotateMarker(cab, (float) toRotation);
                                        accelerateDecelerate();

//                                            CameraPosition oldPos = mMap.getCameraPosition();
//
//                                            CameraPosition pos = CameraPosition.builder(oldPos).bearing((float)toRotation).build();
//                                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));

                                    }
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cabLocLatLng, 16));
                                }
                                try {
                                    addresses = geocoder.getFromLocation(Double.parseDouble(cl.getLatitude()), Double.parseDouble(cl.getLongitude()), 1);
                                    if(addresses.size()!=0) {
                                        String address = addresses.get(0).getAddressLine(0);
                                        String add1 = addresses.get(0).getAddressLine(1);
                                        String add2 = addresses.get(0).getAddressLine(2);
                                        String city = addresses.get(0).getLocality();
                                        String state = addresses.get(0).getAdminArea();

                                        if(add1!=null&&add2!=null) {
                                            complete_address = address + " " + add1 + " " + add2;
                                        }
                                        else {
                                            complete_address=address;
                                        }

                                        tvMyLoc.setText(complete_address);
                                    }
                                    else {
                                        tvMyLoc.setText("-");
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    complete_address = "Unable to get the location details";
                                    tvMyLoc.setText(complete_address);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CabLocationPojo>> call, Throwable t) {

                        if(myBottomSheet!=null) {

                            if (myBottomSheet.isAdded()) {
                                //return;
                            } else {
                                if (rootView.isShown()) {
                                    myBottomSheet.show(getChildFragmentManager(), myBottomSheet.getTag());
                                }
                            }
                        }
                    }
                });
            }
        };
        handler.post(r);
    }

    private double bearingBetweenLocations(LatLng latLng1,LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }

    private void rotateMarker(final Marker marker, final float toRotation) {
        if(!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 500;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }

    private void accelerateDecelerate()
    {
        final Handler handler = new Handler();

        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 5000;
        final boolean hideMarker = false;



        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;

                cpLat=startPosition.latitude * (1 - t) + finalPosition.latitude * t;
                cpLng= startPosition.longitude * (1 - t) + finalPosition.longitude * t;

                currentPosition = new LatLng(cpLat,cpLng);

                cab.setPosition(currentPosition);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        cab.setVisible(false);
                    } else {
                        cab.setVisible(true);
                    }
                }
            }
        });
    }

    public void checkForDutyFinish()
    {
        hStopTime=new Handler();
        rStopTime=new Runnable() {
            @Override
            public void run() {

                hStopTime.postDelayed(this,15000);
                Call<List<RideStopPojo>> call=REST_CLIENT.getRideStopData(requestId,companyId,"guest");
                call.enqueue(new Callback<List<RideStopPojo>>() {
                    @Override
                    public void onResponse(Call<List<RideStopPojo>> call, Response<List<RideStopPojo>> response) {


                        List<RideStopPojo> dataList;
                        RideStopPojo data;

                        //System.out.println();

                        if(response.isSuccessful())
                        {
                            dataList=response.body();

                            data=dataList.get(0);

                            if(data.getDistancetravelled().equals(""))
                            {}
                            else {
                                hStopTime.removeCallbacks(rStopTime);
                                Intent i = new Intent(getActivity(), RideStopActivity.class);
                                i.putExtra("requestId", requestId);
                                i.putExtra("driverName",driverName);
                                i.putExtra("driverMobile",driverMobile);
                                startActivity(i);
                                getActivity().finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RideStopPojo>> call, Throwable t) {

                    }
                });
            }
        };
        hStopTime.post(rStopTime);
    }


    @Override
    public void onDetach() {
        super.onDetach();

        if(handler!=null) {
            handler.removeCallbacks(r);
        }

        if(hStopTime!=null) {
            hStopTime.removeCallbacks(rStopTime);
        }

    }
}
