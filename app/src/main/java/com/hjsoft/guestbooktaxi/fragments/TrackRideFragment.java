package com.hjsoft.guestbooktaxi.fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.hjsoft.guestbooktaxi.model.Distance;
import com.hjsoft.guestbooktaxi.model.Duration;
import com.hjsoft.guestbooktaxi.model.DurationPojo;
import com.hjsoft.guestbooktaxi.model.Element;
import com.hjsoft.guestbooktaxi.model.RideStopPojo;
import com.hjsoft.guestbooktaxi.model.Row;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 19/12/16.
 */
public class TrackRideFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener{


    Button btTrackRide;
    TextView tvCancelRide;
    SupportMapFragment mapFragment;
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
    LinearLayout llCont;
    boolean track=false;
    TextView tvMyLoc;
    Handler h;
    Runnable run;
    API REST_CLIENT;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    String requestId;
    ImageButton ibPhone;
    String driverMobile;
    ArrayList<CabData> list;
    CabData data;
    TextView tvArrivalTime,tvArrivalText;
    double curnt_lat,curnt_long;
    ProgressDialog progressDialog;
    BottomSheetDialogFragment myBottomSheet;
    DBAdapter dbAdapter;
    String pickupLoc,dropLoc,pickupLat,pickupLong;
    String rideDate;
    SimpleDateFormat dateFormat;
    LinearLayout llArrivalTime,llTrackRide;
    Handler handler,hStopTime,hOtp;
    Runnable r,rStopTime,rOtp;
    Marker cab;
    boolean firstDistanceCalculation=true;
    boolean trackingRide=false;
    RelativeLayout rlCont;
    TextView tvDriverName,tvVehRegNo,tvPickup,tvDrop,tvRid,tvDriverMobile;
    ImageView ivDriver;
    String driverName;
    ImageButton ibClose,ibDots;
    View vwBottomSheet;
    boolean OTPAuthenticated=false;
    SimpleDraweeView ivDriverPic;
    LatLng startPosition,finalPosition,currentPosition;
    boolean isMarkerRotating=false;
    double cpLat,cpLng;
    MediaPlayer mp;
    String companyId="CMP00001";
    View rootView;
    String stCategorySelected;
    String stLocalPkg="";
    TextView tvOTP;
    NotificationManager notificationManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fresco.initialize(getActivity().getApplicationContext());

        geocoder = new Geocoder(getContext(), Locale.getDefault());

        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

        pickupLoc=pref.getString("pickup_location",null);
        dropLoc=pref.getString("drop_location",null);
        pickupLat=pref.getString("pickup_lat",null);
        pickupLong=pref.getString("pickup_long",null);
        rideDate=dateFormat.format(new Date());

        REST_CLIENT= RestClient.get();

        dbAdapter=new DBAdapter(getActivity());
        dbAdapter=dbAdapter.open();

        mp=MediaPlayer.create(getActivity(),R.raw.beep);
        notificationManager =(NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT<23)
        {
            establishConnection();
        }
        else
        {
            if(getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {
                establishConnection();
            }
            else
            {
                if(shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    Toast.makeText(getActivity(),"Location Permission is required for this app to run!",Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_track_ride, container,false);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        llCont=(LinearLayout)rootView.findViewById(R.id.ftr_ll_cont);
        btTrackRide=(Button)rootView.findViewById(R.id.ftr_bt_track);
        tvCancelRide=(TextView) rootView.findViewById(R.id.ftr_tv_cancel);
        tvMyLoc=(TextView)rootView.findViewById(R.id.ftr_tv_place);
        ibPhone=(ImageButton)rootView.findViewById(R.id.ftr_ib_phone);
        tvArrivalTime=(TextView)rootView.findViewById(R.id.ftr_tv_arrival_time);
        myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
        llArrivalTime=(LinearLayout)rootView.findViewById(R.id.ftr_ll_arrival_time);
        llTrackRide=(LinearLayout)rootView.findViewById(R.id.ftr_ll_track_ride);
        rlCont=(RelativeLayout)rootView.findViewById(R.id.ftr_rl);
        tvArrivalText=(TextView)rootView.findViewById(R.id.ftr_tv_arrival_text);
        tvDriverName=(TextView)rootView.findViewById(R.id.ftr_tv_dname);
        tvVehRegNo=(TextView)rootView.findViewById(R.id.ftr_tv_veh_regno);
        tvPickup=(TextView)rootView.findViewById(R.id.ftr_tv_ploc);
        tvDrop=(TextView)rootView.findViewById(R.id.ftr_tv_dloc);
        ibClose=(ImageButton)rootView.findViewById(R.id.ftr_ib_close);
        ibDots=(ImageButton)rootView.findViewById(R.id.ftr_ib_dots);
        vwBottomSheet=(View)rootView.findViewById(R.id.ftr_rl_bottom_sheet);
        ivDriverPic=(SimpleDraweeView) rootView.findViewById(R.id.ftr_iv_driver);
        tvRid=(TextView)rootView.findViewById(R.id.ftr_tv_creq_title);
        tvDriverMobile=(TextView)rootView.findViewById(R.id.ftr_tv_gmobile);
        tvOTP=(TextView)rootView.findViewById(R.id.ftr_tv_otp);
        tvOTP.setVisibility(View.GONE);

        tvPickup.setText(pickupLoc);
        tvDrop.setText(dropLoc);

        llTrackRide.setVisibility(View.GONE);
        //llCont.setVisibility(View.GONE);
        rlCont.setVisibility(View.INVISIBLE);
        btTrackRide.setVisibility(View.GONE);

        final BottomSheetBehavior behavior = BottomSheetBehavior.from(vwBottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGing");
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


        list=(ArrayList<CabData>) getActivity().getIntent().getSerializableExtra("list");

        if(list!=null) {

            data = list.get(0);
            driverMobile=data.getPhoneNumber();
            tvVehRegNo.setText(data.getVehRegNo());
            tvDriverName.setText(data.getDriverName());
            driverName=data.getDriverName();
            driverMobile=data.getPhoneNumber();
            tvDriverMobile.setText(data.getPhoneNumber());

            String driverPic=data.getDriverPic();

            if(driverPic.equals(""))
            {

            }
            else
            {
                Uri imageUri = Uri.parse(driverPic);
                ivDriverPic.setImageURI(imageUri);
            }

        }

        Bundle d=getActivity().getIntent().getExtras();
        if(d!=null) {
            requestId = d.getString("requestId");
            stCategorySelected=d.getString("selectedCategory");
            stLocalPkg=d.getString("localPackage");

        }

        if(!(stLocalPkg.equals("")))
        {
            tvRid.setText("Ride Details - "+stLocalPkg);
        }

        //dbAdapter.insertUserRide(requestId,pickupLoc,dropLoc,rideDate,"cab yet to be arrived");
        //dbAdapter.insertUserRideStatus(requestId,"not arrived");

        if(list==null&&d==null)
        {
            Toast.makeText(getActivity(),"No Ongoing Rides",Toast.LENGTH_LONG).show();
        }

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

        ibPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+driverMobile));//GUEST NUMBER HERE...
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

                if (!OTPAuthenticated) {

                    Toast.makeText(getActivity(),"OTP Authentication in progress!",Toast.LENGTH_LONG).show();
                    btTrackRide.setAlpha(Float.parseFloat("0.5"));

                } else {

                    dbAdapter.updateUserRideStatus(requestId, "tracked");

                    trackingRide = true;
                    //llArrivalTime.setVisibility(View.GONE);
                    // llCont.setVisibility(View.VISIBLE);
                    rlCont.setVisibility(View.VISIBLE);
                    btTrackRide.setVisibility(View.GONE);

                    handler = new Handler();
                    r = new Runnable() {
                        @Override
                        public void run() {

                            try {
                                mMap.setMyLocationEnabled(false);
                            }catch (SecurityException e){e.printStackTrace();}


                            handler.postDelayed(this,20000);

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
                                            cl = cabLocData.get(0);

                                            LatLng cabLocLatLng = new LatLng(Double.parseDouble(cl.getLatitude()), Double.parseDouble(cl.getLongitude()));

                                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cabLocLatLng, 16));

                                            if (mMap != null) {
                                                if (cab == null) {
                                                    if(stCategorySelected.equals("Mini")||stCategorySelected.equals("Micra"))
                                                    {
                                                        cab = mMap.addMarker(new MarkerOptions().position(cabLocLatLng)
                                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mini_15)));
                                                    }
                                                    else {
                                                        cab = mMap.addMarker(new MarkerOptions().position(cabLocLatLng)
                                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.prime_15)));
                                                    }
                                                } else {
                                                    //cab.setPosition(cabLocLatLng);

                                                    startPosition = cab.getPosition();
                                                    finalPosition = new LatLng(Double.parseDouble(cl.getLatitude()), Double.parseDouble(cl.getLongitude()));
                                                    double toRotation = bearingBetweenLocations(startPosition, finalPosition);
                                                    rotateMarker(cab, (float) toRotation);
                                                    accelerateDecelerate();

                                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(finalPosition, 16));
                                                    mMap.getUiSettings().setMapToolbarEnabled(false);

//                                                    CameraPosition oldPos = mMap.getCameraPosition();
//
//                                                    CameraPosition pos = CameraPosition.builder(oldPos).bearing((float)toRotation).build();
//                                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));

                                                }
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
                                                else
                                                {
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

                                        if(rootView.isShown()) {
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
        });

        return rootView;
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==REQUEST_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                establishConnection();

            } else {
                Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_LONG).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void checkForDistanceCalculation(){

        firstDistanceCalculation=false;

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Getting Cab Arrival Time");
        progressDialog.show();

        String urlString="https://maps.googleapis.com/maps/api/distancematrix/json?" +
                "origins="+data.getLatitude()+","+data.getLongitude()+"&destinations="+pickupLat+","+pickupLong+"&key=AIzaSyB0z7WOiu8JIcf1fKj2LqiI7MVRmX5ZwR8";

        Call<DurationPojo> call=REST_CLIENT.getDistanceDetails(urlString);
        call.enqueue(new Callback<DurationPojo>() {
            @Override
            public void onResponse(Call<DurationPojo> call, Response<DurationPojo> response) {

                DurationPojo d;

                if(response.isSuccessful())
                {
                    d=response.body();
                    List<Row> r=d.getRows();
                    Row r1;
                    for(int a=0;a<r.size();a++)
                    {
                        r1=r.get(a);
                        List<Element> e=r1.getElements();

                        Element e1;

                        for(int b=0;b<e.size();b++)
                        {
                            e1=e.get(b);
                            Distance t1=e1.getDistance();

                            Duration t2=e1.getDuration();

                            tvArrivalTime.setText(t2.getText());
                            progressDialog.dismiss();
                        }
                    }
                }
                else
                {
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<DurationPojo> call, Throwable t) {

                progressDialog.dismiss();
            }
        });
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
                                mp.start();
                                //alertDialog.dismiss();
                                dbAdapter.updateUserRideStatus(requestId,"arrived");
                                // llArrivalTime.setVisibility(View.GONE);
                                tvArrivalText.setText("Cab has Arrived !");
                                //tvArrivalText.setTextColor(Color.parseColor("#0067de"));
                                tvArrivalTime.setVisibility(View.GONE);
                                // tvCancelRide.setVisibility(View.GONE);
                                // llTrackRide.setVisibility(View.VISIBLE);
                                btTrackRide.setVisibility(View.VISIBLE);

                                h.removeCallbacks(run);

                                checkOTPstatus();

                                /*

                                NotificationCompat.Builder mBuilder =
                                        new NotificationCompat.Builder(getActivity())
                                                .setSmallIcon(R.drawable.car_image)
                                                .setContentTitle("HJ Taxi")
                                                .setContentText("Cab has Arrived!");

                                int mNotificationId = 001;
// Gets an instance of the NotificationManager service
                                NotificationManager mNotifyMgr =
                                        (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
// Builds the notification and issues it.
                                mNotifyMgr.notify(mNotificationId, mBuilder.build());*/
                                //dbAdapter.updateUserRideEntry(requestId,"ongoing");

                                /*String msgText = "Cab has Arrived! "
                                        + "\n"+"Driver: "+driverName+""
                                        + "\n"+"Mobile Number: "+driverMobile+"";*/

                                String msgText = driverName+" ("+data.getVehRegNo()+") has arrived, Waiting at your location.";

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
                                builder.setContentIntent(pendingIntent);*/
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

                            if(rootView.isShown()) {
                                myBottomSheet.show(getChildFragmentManager(), myBottomSheet.getTag());
                            }
                        }
                    }
                });
            }
        };

        h.post(run);
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

                        if(response.isSuccessful())
                        {
                            dataList=response.body();

                            data=dataList.get(0);

                            if(data.getDistancetravelled().equals(""))
                            {

                            }
                            else {
                                hStopTime.removeCallbacks(rStopTime);
                                Intent i = new Intent(getActivity(),RideStopActivity.class);
                                i.putExtra("requestId", requestId);
                                i.putExtra("driverName",driverName);
                                i.putExtra("driverNumber",driverMobile);
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

    public void establishConnection(){

        buildGoogleApiClient();
        buildLocationSettingsRequest();
        entered=true;
        checkForArrivalNotification();
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
            handler.removeCallbacks(r);
        }

      /*  if(OTPAuthenticated)
        {
            hStopTime.removeCallbacks(rStopTime);
        }
        */
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
            handler.post(r);
        }

      /*  if(OTPAuthenticated)
        {
            hStopTime.post(rStopTime);
        }
        */
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
                else {
                    tvMyLoc.setText("-");
                }
            } catch (IOException e) {
                e.printStackTrace();
                complete_address = "Unable to get the location details";
                tvMyLoc.setText(complete_address);
            }
            // mMap.addMarker(new MarkerOptions().position(lastLoc)
            //  .icon(BitmapDescriptorFactory.fromResource(R.drawable.sedan)));
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

        if(firstDistanceCalculation) {
            checkForDistanceCalculation();
        }
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
                            ibPhone.setVisibility(View.GONE);
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
                //checkForArrivalNotification();
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
                                            cabAcceptedData.add(new CabData(data.getProfileId(), data.getVehRegNo(),
                                                    data.getPhoneNumber(), data.getCabCat(), data.getLatitude(),data.getLongitude(),data.getDriverName(),data.getDriverPic(),data.getDutyPerform()));

                                            // dbAdapter.updateUserRideEntry(requestId,"cancelled");
                                            h.removeCallbacks(run);
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
//                            Call<BookCabPojo> call=REST_CLIENT.sendCancelStatus(v);
//                            call.enqueue(new Callback<BookCabPojo>() {
//                                @Override
//                                public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {
//
//                                    BookCabPojo msg;
//                                    if(response.isSuccessful())
//                                    {
//                                        msg=response.body();
//
//                                        String cancelDetails=msg.getMessage();
//                                        String[] cancelFeeDetails=cancelDetails.split("-");
//                                        String cancelFee=cancelFeeDetails[0];
//
//
//                                           // Toast.makeText(getActivity(), "Cancellation charges Rs. " + cancelFee + " added to next ride.", Toast.LENGTH_LONG).show();
//                                            ArrayList<CabData> cabAcceptedData = new ArrayList<CabData>();
//                                            cabAcceptedData.add(new CabData(data.getProfileId(), data.getVehRegNo(),
//                                                    data.getPhoneNumber(), data.getCabCat(), data.getLatitude(),data.getLongitude(),data.getDriverName(),data.getDriverPic()));
//
//                                        // dbAdapter.updateUserRideEntry(requestId,"cancelled");
//                                            h.removeCallbacks(run);
//                                            Intent i=new Intent(getActivity(), RideCancelActivity.class);
//                                            i.putExtra("list",cabAcceptedData);
//                                            i.putExtra("cancelFee",cancelFee);
//                                            i.putExtra("requestId",requestId);
//                                            startActivity(i);
//                                            getActivity().finish();
//
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<BookCabPojo> call, Throwable t) {
//
//                                }
//                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<BookCabPojo> call1, Throwable t) {

                    }
                });
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
    }
}