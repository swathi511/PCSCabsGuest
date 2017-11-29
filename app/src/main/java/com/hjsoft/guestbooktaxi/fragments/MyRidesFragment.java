package com.hjsoft.guestbooktaxi.fragments;

import android.app.ProgressDialog;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.SessionManager;
import com.hjsoft.guestbooktaxi.adapter.DBAdapter;
import com.hjsoft.guestbooktaxi.adapter.RecyclerAdapter;
import com.hjsoft.guestbooktaxi.model.AllRidesPojo;
import com.hjsoft.guestbooktaxi.model.FormattedAllRidesData;
import com.hjsoft.guestbooktaxi.model.UserRidesData;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 29/12/16.
 */
public class MyRidesFragment extends Fragment implements RecyclerAdapter.AdapterCallback {

    RecyclerView rView;
    RecyclerAdapter mAdapter;
    ArrayList<UserRidesData> rideData = new ArrayList<>();
    DBAdapter dbAdapter;
    TextView tvNoRides;
    ArrayList<AllRidesPojo> allRidesDataList;
    AllRidesPojo allRidesData;
    API REST_CLIENT;
    View rootView;
    BottomSheetDialogFragment myBottomSheet;
    ArrayList<FormattedAllRidesData> dataList=new ArrayList<>();
    Date date1;
    String guestProfileId;
    HashMap<String, String> user;
    SessionManager session;
    ImageView ivRetry;
    String companyId="CMP00001";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        REST_CLIENT= RestClient.get();
        dbAdapter = new DBAdapter(getContext());
        dbAdapter = dbAdapter.open();
        //rideData = dbAdapter.getAllRideEntries();
        session=new SessionManager(getActivity());

        user=session.getUserDetails();

        guestProfileId=user.get(SessionManager.KEY_PROFILE_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_my_rides, container, false);
        rView = (RecyclerView) rootView.findViewById(R.id.fmr_rview);

        tvNoRides = (TextView) rootView.findViewById(R.id.fmr_tv_no_rides);
        myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
        ivRetry=(ImageView)rootView.findViewById(R.id.fmr_iv_retry);

        tvNoRides.setVisibility(View.GONE);
        ivRetry.setVisibility(View.GONE);

        //mAdapter.notifyDataSetChanged();
        //  if (rideData.size() == 0) {
        //  tvNoRides.setVisibility(View.VISIBLE);
        //  }
        //Call<ArrayList<AllRidesPojo>> call=REST_CLIENT.getAllUserRides(guestProfileId);

        getAllRideDetails();

        ivRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ivRetry.setVisibility(View.GONE);
                getAllRideDetails();
            }
        });
        return rootView;
    }

    @Override
    public void onMethodCallback(final int position,ArrayList<FormattedAllRidesData> data) {

        Fragment frag = new SpecificRideFragment();
        Bundle args = new Bundle();
        args.putInt("position",position);
        args.putSerializable("list",data);
        frag.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content_frame, frag,"specific_ride");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void getAllRideDetails()
    {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.show();

        Call<ArrayList<AllRidesPojo>> call=REST_CLIENT.getUserRides(guestProfileId,"guest",companyId);
        call.enqueue(new Callback<ArrayList<AllRidesPojo>>() {
            @Override
            public void onResponse(Call<ArrayList<AllRidesPojo>> call, Response<ArrayList<AllRidesPojo>> response) {

                AllRidesPojo data;

                if(response.isSuccessful())
                {
                    progressDialog.dismiss();
                    allRidesDataList=response.body();

                    for(int i=0;i<allRidesDataList.size();i++)
                    {
                        data=allRidesDataList.get(i);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a",Locale.ENGLISH);
                        // SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
                        try {
                            date1 = dateFormat.parse(data.getRidedate());
                        }catch(ParseException e)
                        {e.printStackTrace();}

                        if(data.getStatusofride().equals("BOOKED")&&(data.getTravelType().equals("local")||data.getTravelType().equals("Packages"))&&(data.getBookingType().equals("AppBooking")||data.getBookingType().equals(""))&&(!(data.getVehicleCategory().equals(""))))
                        {
                            //data.getTravelPackage().equals("")

                        }
                        else {

                            //it means it is offline request...(so adding to the list)
                            if(data.getStatusofride().equals("")||data.getStatusofride().equals("NORESPONSE")||data.getStatusofride().equals("DECLINED"))
                            {

                            }
                            else {
                                dataList.add(new FormattedAllRidesData(date1, data.getRequestid(), data.getFromlocation(), data.getTolocation(), data.getVehicleCategory(),
                                        data.getVehicleType(), data.getDistancetravelled(), data.getStatusofride(), data.getRidestarttime(), data.getRidestoptime(),
                                        data.getTotalamount(), data.getDrivername(), data.getDriverpic(), data.getTravelType(), data.getBookingType(), data.getTravelpackage(), data.getDrivermobile(),data.getDriverBattaAmt(),data.getPaymentMode(),data.getOtherCharges()));
                            }

                            }
                    }
                }
                else {

                    progressDialog.dismiss();
                }

                if(dataList.size()!=0) {
                    Collections.sort(dataList,new Comparator<FormattedAllRidesData>()
                    {
                        public int compare(FormattedAllRidesData o1, FormattedAllRidesData o2) {
                            if (o1.getRideDate()==null||o2.getRideDate()==null)
                                return 0;
                            return o1.getRideDate().compareTo(o2.getRideDate());
                        }
                    });

                    Collections.reverse(dataList);
                    mAdapter = new RecyclerAdapter(getActivity(), dataList, rView);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    rView.setLayoutManager(mLayoutManager);
                    rView.setItemAnimator(new DefaultItemAnimator());
                    rView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }else
                {
                    tvNoRides.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<AllRidesPojo>> call, Throwable t) {

                progressDialog.dismiss();
                ivRetry.setVisibility(View.VISIBLE);

                if(myBottomSheet.isAdded())
                {
                    //return;
                }
                else
                {
                    if(rootView.isShown()) {

                        myBottomSheet.show(getChildFragmentManager(), myBottomSheet.getTag());
                    }
                }

            }
        });
    }

}
