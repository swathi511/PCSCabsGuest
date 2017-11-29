package com.hjsoft.guestbooktaxi.fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import com.hjsoft.guestbooktaxi.activity.RideCancelActivity;
import com.hjsoft.guestbooktaxi.activity.RideInProgressActivity;
import com.hjsoft.guestbooktaxi.activity.RideStopActivity;
import com.hjsoft.guestbooktaxi.adapter.DBAdapter;
import com.hjsoft.guestbooktaxi.model.BookCabPojo;
import com.hjsoft.guestbooktaxi.model.CabData;
import com.hjsoft.guestbooktaxi.model.CabLocationPojo;
import com.hjsoft.guestbooktaxi.model.CancelPojo;
import com.hjsoft.guestbooktaxi.model.FormattedAllRidesData;
import com.hjsoft.guestbooktaxi.model.RideStopPojo;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 10/1/17.
 */
public class SpecificRideOngoingFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {

    Button btTrackRide;
    TextView tvCancelRide,tvMyLoc,tvArrivalTime;
    SupportMapFragment mapFragment;
    LinearLayout llCont,llArrivalTime;
    BottomSheetDialogFragment myBottomSheet;
    ImageButton ibPhone;
    int position;
    ArrayList<FormattedAllRidesData> dataList=new ArrayList<>();
    DBAdapter dbAdapter;
    FormattedAllRidesData data;
    String requestId,status;
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
    Handler handler,h,hStopTime,hOtp;
    Runnable r,run,rStopTime,rOtp;
    Marker cab;
    String driverMobile,driverName;
    API REST_CLIENT;
    boolean track=false;
    View rootView;
    RelativeLayout rlCont;
    TextView tvDriverName,tvVehRegNo,tvPickup,tvDrop,tvDriverMobile;
    ImageButton ibClose,ibDots;
    View vwBottomSheet;
    boolean OTPAuthenticated=false;
    LatLng startPosition,finalPosition,currentPosition;
    boolean isMarkerRotating=false;
    double cpLat,cpLng;
    String companyId="CMP00001";
    SimpleDraweeView ivDriverPic;
    String driverPic;
    String stCategorySelected;
    TextView tvRid,tvOTP;
    NotificationManager notificationManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Fresco.initialize(getActivity().getApplicationContext());
        notificationManager =(NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

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

        stCategorySelected=data.getVehicleCategory();
        status=dbAdapter.getUserStatus(requestId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_specific_ride_ongoing, container,false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        llCont=(LinearLayout)rootView.findViewById(R.id.fsro_ll_cont);
        btTrackRide=(Button)rootView.findViewById(R.id.fsro_bt_track);
        tvCancelRide=(TextView) rootView.findViewById(R.id.fsro_tv_cancel);
        tvMyLoc=(TextView)rootView.findViewById(R.id.fsro_tv_place);
        ibPhone=(ImageButton)rootView.findViewById(R.id.fsro_ib_phone);
        tvArrivalTime=(TextView)rootView.findViewById(R.id.fsro_tv_arrival_time);
        myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
        llArrivalTime=(LinearLayout)rootView.findViewById(R.id.fsro_ll_arrival_time);
        //llTrackRide=(LinearLayout)rootView.findViewById(R.id.fsro_ll_track_ride);
        rlCont=(RelativeLayout)rootView.findViewById(R.id.fsro_rl);
        tvDriverName=(TextView)rootView.findViewById(R.id.fsro_tv_dname);
        tvVehRegNo=(TextView)rootView.findViewById(R.id.fsro_tv_veh_regno);
        tvPickup=(TextView)rootView.findViewById(R.id.fsro_tv_ploc);
        tvDrop=(TextView)rootView.findViewById(R.id.fsro_tv_dloc);
        ibClose=(ImageButton)rootView.findViewById(R.id.fsro_ib_close);
        ibDots=(ImageButton)rootView.findViewById(R.id.fsro_ib_dots);
        vwBottomSheet=(View)rootView.findViewById(R.id.fsro_rl_bottom_sheet);
        ivDriverPic=(SimpleDraweeView) rootView.findViewById(R.id.fsro_iv_driver);
        tvRid=(TextView)rootView.findViewById(R.id.fsro_tv_creq_title);
        tvDriverMobile=(TextView)rootView.findViewById(R.id.fsro_tv_gmobile);
        tvOTP=(TextView)rootView.findViewById(R.id.fsro_tv_otp);
        tvOTP.setVisibility(View.GONE);

        if(data.getTravelPackage().equals(""))
        {

        }
        else {
            tvRid.setText("Ride Details - "+data.getTravelPackage());
        }


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

        tvPickup.setText(data.getFromLocation());
        tvDrop.setText(data.getToLocation());
        tvDriverName.setText(driverName);
        tvVehRegNo.setText(data.getVehicleCategory());
        tvDriverMobile.setText(data.getDriverMobile());
        //tvDriverMobile.setText(data.getP);

      //  llTrackRide.setVisibility(View.INVISIBLE);
        //llCont.setVisibility(View.GONE);
        rlCont.setVisibility(View.INVISIBLE);
        llArrivalTime.setVisibility(View.GONE);
        btTrackRide.setVisibility(View.GONE);

        driverPic=data.getDriverPic();

        if(driverPic.equals(""))
        {

        }
        else
        {
            Uri imageUri = Uri.parse(driverPic);
            ivDriverPic.setImageURI(imageUri);
        }



        if(status.equals(""))
        {
            //Toast.makeText(getActivity(),"Oops! Error Tracking the Ride",Toast.LENGTH_SHORT).show();
            dbAdapter.insertUserRideStatus(requestId,"not arrived");
        }

        status=dbAdapter.getUserStatus(requestId);

        switch(status)
        {
            case "not arrived":

                showCancelFunctionality();

                break;
            case "arrived":

                //showTrackCabFunctionality();

                JsonObject v=new JsonObject();
                v.addProperty("requestid",requestId);
                v.addProperty("companyid",companyId);

                Call<BookCabPojo> call=REST_CLIENT.getNotification(v);
                call.enqueue(new Callback<BookCabPojo>() {
                    @Override
                    public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                        if(response.isSuccessful())
                        {
                            BookCabPojo msg1=response.body();

                            String m[]=msg1.getMessage().split("-");

                            tvOTP.setVisibility(View.VISIBLE);

                            tvOTP.setText(" OTP : "+m[1]);
                        }



                    }

                    @Override
                    public void onFailure(Call<BookCabPojo> call, Throwable t) {

                    }
                });




                checkOTPstatus();
                llArrivalTime.setVisibility(View.VISIBLE);
                tvArrivalTime.setText("Cab has arrived!");
                break;
            case "tracked":

                showingTrackCab();

                break;
        }

        ibPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"));//GUEST NUMBER HERE...
                startActivity(intent);
            }
        });


        tvCancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertCancelRide();

            }
        });

        btTrackRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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


    public void showCancelFunctionality()
    {
        llArrivalTime.setVisibility(View.VISIBLE);

        checkForArrivalNotification();
    }

    public void showTrackCabFunctionality()
    {
        //if(llArrivalTime.isShown())
       // {
          //  llArrivalTime.setVisibility(View.GONE);
      //  }
        tvArrivalTime.setText("Driver waiting at your location.");
      //  llTrackRide.setVisibility(View.VISIBLE);
        btTrackRide.setVisibility(View.VISIBLE);
       // checkForDutyFinish();
        checkOTPstatus();
    }

    public void showingTrackCab()
    {
       // if(llTrackRide.isShown())
      //  {
       //     llTrackRide.setVisibility(View.VISIBLE);
      //  }

        //ibPhone.setVisibility(View.GONE);

        trackingRide=true;
        //888 llCont.setVisibility(View.VISIBLE);
        //888 llCont.setVisibility(View.VISIBLE);
        OTPAuthenticated=true;
        trackingAndDutyFinishFunctionality();
        checkForDutyFinish();
    }

    public  void checkForArrivalNotification(){

        h=new Handler();
        run=new Runnable() {
            @Override
            public void run() {

                h.postDelayed(this,15000);

                JsonObject v=new JsonObject();
                v.addProperty("requestid",requestId);
                v.addProperty("companyid",companyId);

                Call<BookCabPojo> call=REST_CLIENT.getNotification(v);
                call.enqueue(new Callback<BookCabPojo>() {
                    @Override
                    public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                        if(myBottomSheet.isAdded())
                        {
                            myBottomSheet.dismiss();

                        }

                        if(response.isSuccessful())
                        {
                            BookCabPojo msg1=response.body();

                            String m[]=msg1.getMessage().split("-");

                            tvOTP.setVisibility(View.VISIBLE);

                            tvOTP.setText("OTP : "+m[1]);

                            if(m[0].equals("Cab arrived at your pickup location. OTP "))
                            {
                                //alertDialog.dismiss();
                                dbAdapter.updateUserRideStatus(requestId,"arrived");
                                tvArrivalTime.setText("Cab has arrived!");
                                //tvCancelRide.setVisibility(View.GONE);
                               // llArrivalTime.setVisibility(View.GONE);
                               // llTrackRide.setVisibility(View.VISIBLE);

                                h.removeCallbacks(run);
                                btTrackRide.setVisibility(View.VISIBLE);
                                btTrackRide.setAlpha(Float.parseFloat("0.5"));
                                checkOTPstatus();

                                String msgText = driverName+" has arrived, Waiting at your location.";


                               /* NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(getActivity())
                                                .setSmallIcon(R.drawable.car_image)
                                                .setContentTitle("PCS Cabs")
                                                .setContentText("Cab has Arrived!");

                                int mNotificationId = 001;
// Gets an instance of the NotificationManager service
                                NotificationManager mNotifyMgr =
                                        (NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);
// Builds the notification and issues it.
                                mNotifyMgr.notify(mNotificationId, mBuilder.build());*/


                                android.app.Notification.Builder builder = new Notification.Builder(getActivity());
                                builder.setContentTitle("PCS Cabs")
                                        .setContentText("Cab has Arrived !")
                                        .setSmallIcon(R.drawable.car_image).setAutoCancel(true)
                                        .setStyle(new Notification.BigTextStyle().bigText(msgText));

                                /*Intent myIntent = new Intent(getActivity(), RideInProgressActivity.class);
                                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                PendingIntent pendingIntent = PendingIntent.getActivity(
                                        getActivity(),
                                        0,
                                        myIntent, 0);
                                builder.setContentIntent(pendingIntent);
*/

                                //Notification notification = new Notification.BigTextStyle(builder).bigText(msgText).build();

                                notificationManager.notify(0,builder.build());



                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BookCabPojo> call, Throwable t) {


                        if(myBottomSheet.isAdded())
                        {
                            //return;

                        }
                        else {
                            myBottomSheet.show(getChildFragmentManager(), myBottomSheet.getTag());
                        }
                    }
                });
            }
        };

        h.post(run);
    }


    public void establishConnection(){

        buildGoogleApiClient();
        buildLocationSettingsRequest();
        entered=true;
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

    public void alertCancelRide()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_cancel_ride, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);

        Button ok=(Button) dialogView.findViewById(R.id.acr_bt_yes);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(OTPAuthenticated)
                {
                    hOtp.removeCallbacks(rOtp);
                }


                alertDialog.dismiss();

                final JsonObject v=new JsonObject();
                v.addProperty("requestid",requestId);
                v.addProperty("companyid",companyId);
                Call<BookCabPojo> call1=REST_CLIENT.doCancel(v);
                call1.enqueue(new Callback<BookCabPojo>() {
                    @Override
                    public void onResponse(Call<BookCabPojo> call1, Response<BookCabPojo> response) {

                        if(response.isSuccessful())
                        {
                            Call<List<CancelPojo>> call=REST_CLIENT.sendCancelStatus(v);
                            call.enqueue(new Callback<List<CancelPojo>>() {
                                @Override
                                public void onResponse(Call<List<CancelPojo>> call, Response<List<CancelPojo>> response) {

                                    List<CancelPojo> msgList;
                                    CancelPojo msg;

                                    if (response.isSuccessful())
                                    {
                                        msgList=response.body();

                                        for(int j=0;j<msgList.size();j++)
                                        {
                                            msg=msgList.get(0);

                                            String cancelDetails=msg.getCancelmessage();
                                            String[] cancelFeeDetails=cancelDetails.split("-");
                                            String cancelFee=cancelFeeDetails[0];
                                            String paymentMode="none";

                                            if(cancelFee.equals("0"))
                                            {

                                            }
                                            else
                                            {
                                                if(msg.getPaycash().equals("0"))
                                                {
                                                    paymentMode="wallet";
                                                    String walletAmount=msg.getWalletamount();
                                                    String timeUpdated=java.text.DateFormat.getTimeInstance().format(new Date());

                                                    if(dbAdapter.isWalletPresent()>0)
                                                    {
                                                        dbAdapter.updateWalletAmount(walletAmount,timeUpdated);

                                                    }
                                                    else {

                                                        dbAdapter.insertWalletAmount(walletAmount,timeUpdated);
                                                    }
                                                }
                                                else {

                                                    paymentMode="cash";
                                                }
                                            }

                                            ArrayList<CabData> cabAcceptedData = new ArrayList<CabData>();
                                            cabAcceptedData.add(new CabData(" ", " ",
                                                    data.getDriverMobile(), data.getVehicleCategory()," "," ",data.getDriverName(),data.getDriverPic()," "));


                                            if(h!=null) {
                                                h.removeCallbacks(run);
                                            }
                                            Intent i=new Intent(getActivity(), RideCancelActivity.class);
                                            i.putExtra("list",cabAcceptedData);
                                            i.putExtra("cancelFee",cancelFee);
                                            i.putExtra("requestId",requestId);
                                            i.putExtra("paymentMode",paymentMode);
                                            startActivity(i);
                                            getActivity().finish();

                                        }
                                    }

                                }

                                @Override
                                public void onFailure(Call<List<CancelPojo>> call, Throwable t) {

                                }
                            });

                        }



                    }

                    @Override
                    public void onFailure(Call<BookCabPojo> call1, Throwable t) {

                    }
                });
                //checkForArrivalNotification();
//                JsonObject v=new JsonObject();
//                v.addProperty("requestid",requestId);
//                v.addProperty("companyid",companyId);






               /* Call<BookCabPojo> call=REST_CLIENT.sendCancelStatus(v);
                call.enqueue(new Callback<BookCabPojo>() {
                    @Override
                    public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                        BookCabPojo msg;
                        if(response.isSuccessful())
                        {
                            msg=response.body();

                            if(msg.getMessage().equals("updated"))
                            {
                                //dbAdapter.updateUserRideEntry(requestId,"cancelled");
                                h.removeCallbacks(run);
                                Intent i=new Intent(getActivity(),HomeActivity.class);
                                startActivity(i);
                                getActivity().finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BookCabPojo> call, Throwable t) {

                    }
                });*/

            }
        });

        Button no=(Button) dialogView.findViewById(R.id.acr_bt_no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

    }

    public void trackingAndDutyFinishFunctionality()
    {
        if(!OTPAuthenticated)
        {

        }
        else {
            dbAdapter.updateUserRideStatus(requestId, "tracked");

            trackingRide = true;

            llCont.setVisibility(View.VISIBLE);
            rlCont.setVisibility(View.VISIBLE);
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

                            if (myBottomSheet.isAdded()) {
                                myBottomSheet.dismiss();
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

                            if (myBottomSheet.isAdded()) {
                                //return;
                            } else {
                                if (rootView.isShown()) {
                                    myBottomSheet.show(getChildFragmentManager(), myBottomSheet.getTag());
                                }
                            }
                        }
                    });
                }
            };
            handler.post(r);
        }

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

    public void checkOTPstatus(){

        hOtp=new Handler();
        rOtp=new Runnable() {
            @Override
            public void run() {

                hOtp.postDelayed(this,30000);

                JsonObject v=new JsonObject();
                v.addProperty("requestid",requestId);
                v.addProperty("companyid",companyId);

                Call<BookCabPojo> call=REST_CLIENT.checkOTP(v);
                call.enqueue(new Callback<BookCabPojo>() {
                    @Override
                    public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                        if(response.isSuccessful())
                        {
                            hOtp.removeCallbacks(rOtp);
                            checkForDutyFinish();
                            OTPAuthenticated=true;
                            llArrivalTime.setVisibility(View.GONE);
                           // ibPhone.setVisibility(View.GONE);
                            btTrackRide.setVisibility(View.VISIBLE);
                            btTrackRide.setAlpha(Float.parseFloat("1"));
                        }
                    }

                    @Override
                    public void onFailure(Call<BookCabPojo> call, Throwable t) {

                    }
                });

            }
        };

        hOtp.post(rOtp);
    }


    @Override
    public void onDetach() {
        super.onDetach();

        if(handler!=null) {
            handler.removeCallbacks(r);
        }
        if(h!=null) {
            h.removeCallbacks(run);
        }
        if(hStopTime!=null) {
            hStopTime.removeCallbacks(rStopTime);
        }

        if(hOtp!=null)
        {
            hOtp.removeCallbacks(rOtp);
        }

        notificationManager.cancel(0);

    }
}
