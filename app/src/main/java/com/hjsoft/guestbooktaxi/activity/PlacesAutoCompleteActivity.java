package com.hjsoft.guestbooktaxi.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.adapter.DBAdapter;
import com.hjsoft.guestbooktaxi.adapter.PlacesAutoCompleteAdapter;
import com.hjsoft.guestbooktaxi.listener.RecyclerItemClickListener;

import java.util.ArrayList;


public class PlacesAutoCompleteActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    protected GoogleApiClient mGoogleApiClient;

    private static  LatLngBounds BOUNDS_INDIA;
    //17.4186, 78.5444
    private EditText mAutocompleteView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    ImageButton ibClear,ibLeft;
    DBAdapter dbAdapter;
    double cityCenterLat,cityCenterLong;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        buildGoogleApiClient();
        setContentView(R.layout.activity_place);
        dbAdapter=new DBAdapter(getApplicationContext());
        dbAdapter=dbAdapter.open();

        pref =getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        cityCenterLat=Double.parseDouble(pref.getString("cityCenterLat",null));
        cityCenterLong=Double.parseDouble(pref.getString("cityCenterLong",null));
//        cityCenterLat=17.6868;
//        cityCenterLong=83.2185;

        //System.out.println("******** "+cityCenterLat+":"+cityCenterLong);

        BOUNDS_INDIA =new LatLngBounds(
                new LatLng(cityCenterLat,cityCenterLong), new LatLng(cityCenterLat,cityCenterLong));

//        BOUNDS_INDIA =new LatLngBounds(
//                new LatLng(17.6868,83.2185), new LatLng(17.6868,83.2185));

        mAutocompleteView = (EditText)findViewById(R.id.ap_et_place);
        ibClear=(ImageButton) findViewById(R.id.ap_ib_close);
        ibLeft=(ImageButton)findViewById(R.id.ap_ib_left);

        ibClear.setVisibility(View.GONE);

        mAutoCompleteAdapter =  new PlacesAutoCompleteAdapter(this, R.layout.row,
                mGoogleApiClient, BOUNDS_INDIA, null);

        mRecyclerView=(RecyclerView)findViewById(R.id.ap_rView);
        mLinearLayoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAutoCompleteAdapter);
       // delete.setOnClickListener(this);
        mAutocompleteView.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    ibClear.setVisibility(View.VISIBLE);
                    mAutoCompleteAdapter.getFilter().filter(s.toString());
                }else if(!mGoogleApiClient.isConnected()){
                    Toast.makeText(getApplicationContext(), "API_NOT_CONNECTED",Toast.LENGTH_SHORT).show();
                   // Log.e(Constants.PlacesTag,Constants.API_NOT_CONNECTED);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });


        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(final View view, final int position) {

                        final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);

                        if (item != null) {

                            final String placeId = String.valueOf(item.placeId);

                            view.setBackgroundColor(Color.parseColor("#e0e0e0"));

                        /*
                             Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
                         */

                            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                                    .getPlaceById(mGoogleApiClient, placeId);
                            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                                @Override
                                public void onResult(PlaceBuffer places) {

                                    if (places.getCount() == 1) {

                                        LatLng ll = places.get(0).getLatLng();
                                        //dbAdapter.insertUserLocation(placeId,String.valueOf(item.description),ll.latitude,ll.longitude);
                                        Intent i = new Intent();
                                        i.putExtra("lat", ll.latitude);
                                        i.putExtra("lng", ll.longitude);
                                        i.putExtra("loc", item.description);
                                        setResult(2, i);
                                        finish();

                                    } else {

                                        String place = dbAdapter.getUserLocationFromPosition(position);
                                        double place_lat = dbAdapter.getUserLocationLatFromPosition(position);
                                        double place_lng = dbAdapter.getUserLocationLngFromPosition(position);

                                        Intent i = new Intent();
                                        i.putExtra("lat", place_lat);
                                        i.putExtra("lng", place_lng);
                                        i.putExtra("loc", place);
                                        setResult(2, i);
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                })
        );

        ibClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAutocompleteView.setText(" ");
                ibClear.setVisibility(View.GONE);
                mAutoCompleteAdapter.getFilter().filter(" ");
            }
        });

        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long


        return super.onOptionsItemSelected(item);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v("Google API Callback", "Connection Done");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("Google API Callback", "Connection Suspended");
        Log.v("Code", String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v("Google API Callback","Connection Failed");
        Log.v("Error Code", String.valueOf(connectionResult.getErrorCode()));
        Toast.makeText(this, ".API_NOT_CONNECTED",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        /*
        if(v==delete){
            mAutocompleteView.setText("");
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()){
            Log.v("Google API","Connecting");
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()){
            Log.v("Google API","Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}