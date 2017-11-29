package com.hjsoft.guestbooktaxi.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.activity.HomeActivity;
import com.hjsoft.guestbooktaxi.model.OutStationPojo;
import com.hjsoft.guestbooktaxi.model.ServiceLocationPojo;
import com.hjsoft.guestbooktaxi.model.TariffRatePojo;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 3/1/17.
 */
public class RateCardFragment extends Fragment {

    ImageButton ibCity,ibCategory,ibTravelType;
    TextView tvCategory,tvCity,tvTravelType,tvLocal,tvOutstation;
    String stCategory="Mini";
    String stTravelType="Local";
    API REST_CLIENT;
    String stCity;
    TextView tvBaseFare,tvBelow,tvAbove,tvRideTime,tvMinFare,tvPeakTime,tvPerKmRate;
    BottomSheetDialogFragment myBottomSheet;
    View rootView;
    String companyId="CMP00001";
    TextView tvLoc1,tvLoc2,tvLoc3,tvLoc4,tvLoc5;
    String city;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    int j=0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        REST_CLIENT= RestClient.get();
        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        //getTariffRate();
        city=pref.getString("city",null);
        stCity=city;
        getFareEstimates();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_rate_card, container, false);

        ibCity = (ImageButton)rootView.findViewById(R.id.frc_ib_city);
        ibCategory=(ImageButton)rootView.findViewById(R.id.frc_ib_category);
        //ibTravelType=(ImageButton)rootView.findViewById(R.id.frc_ib_travel_type);
        tvBaseFare=(TextView) rootView.findViewById(R.id.frc_tv_base_fare);
        tvBelow=(TextView)rootView.findViewById(R.id.frc_tv_below);
        tvAbove=(TextView) rootView.findViewById(R.id.frc_tv_above);
        tvMinFare=(TextView)rootView.findViewById(R.id.frc_tv_min_fare);
        tvRideTime=(TextView)rootView.findViewById(R.id.frc_tv_ride_time_charges);
        tvPeakTime=(TextView)rootView.findViewById(R.id.frc_tv_peak_time);
        tvPerKmRate=(TextView)rootView.findViewById(R.id.frc_tv_per_km_fare);
        myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
        tvLocal=(TextView)rootView.findViewById(R.id.frc_tv_local);
        tvOutstation=(TextView)rootView.findViewById(R.id.frc_tv_outstation);

        tvCategory=(TextView)rootView.findViewById(R.id.frc_tv_category);
        tvCity=(TextView)rootView.findViewById(R.id.frc_tv_city);
        tvCity.setText(city);
        // tvTravelType=(TextView)rootView.findViewById(R.id.frc_tv_travel_type);

        ibCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_locations, null);

                dialogBuilder.setView(dialogView);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                tvLoc1=(TextView)dialogView.findViewById(R.id.ale_tv_loc1);
                tvLoc2=(TextView)dialogView.findViewById(R.id.ale_tv_loc2);
                tvLoc3=(TextView)dialogView.findViewById(R.id.ale_tv_loc3);
                tvLoc4=(TextView)dialogView.findViewById(R.id.ale_tv_loc4);
                tvLoc5=(TextView)dialogView.findViewById(R.id.ale_tv_loc5);

                tvLoc1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        tvLoc1.setTextColor(Color.parseColor("#000000"));


                        city=tvLoc1.getText().toString().trim();
                        tvCity.setText(city);
                        stCity=city;
                        alertDialog.dismiss();
                        getFareEstimates();
                    }
                });

                tvLoc2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        tvLoc2.setTextColor(Color.parseColor("#000000"));

                        city=tvLoc2.getText().toString().trim();
                        tvCity.setText(city);

                        stCity=city;

                        alertDialog.dismiss();
                        getFareEstimates();
                    }
                });

                tvLoc3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        tvLoc3.setTextColor(Color.parseColor("#000000"));


                        city=tvLoc3.getText().toString().trim();
                        tvCity.setText(city);

                        stCity=city;


                        alertDialog.dismiss();
                        getFareEstimates();
                    }
                });

                tvLoc4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        tvLoc4.setTextColor(Color.parseColor("#000000"));

                        city=tvLoc4.getText().toString().trim();
                        tvCity.setText(city);

                        stCity=city;


                        alertDialog.dismiss();
                        getFareEstimates();

                    }
                });

                tvLoc5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        tvLoc5.setTextColor(Color.parseColor("#000000"));


                        city=tvLoc5.getText().toString().trim();
                        tvCity.setText(city);

                        stCity=city;


                        alertDialog.dismiss();
                        getFareEstimates();
                    }
                });



                Call<List<ServiceLocationPojo>> call=REST_CLIENT.getServiceLocations("CMP00001");
                call.enqueue(new Callback<List<ServiceLocationPojo>>() {
                    @Override
                    public void onResponse(Call<List<ServiceLocationPojo>> call, Response<List<ServiceLocationPojo>> response) {

                        ServiceLocationPojo data;
                        List<ServiceLocationPojo> dataList;

                        if(response.isSuccessful())
                        {
                            dataList=response.body();
                            j=0;

                            for(int i=0;i<dataList.size();i++)
                            {
                                data=dataList.get(i);
                                switch (j)
                                {
                                    case 0:tvLoc1.setText(data.getLocation());
                                        tvLoc1.setVisibility(View.VISIBLE);
                                        j++;
                                        break;
                                    case 1:tvLoc2.setText(data.getLocation());
                                        tvLoc2.setVisibility(View.VISIBLE);
                                        j++;
                                        break;
                                    case 2:tvLoc3.setText(data.getLocation());
                                        tvLoc3.setVisibility(View.VISIBLE);
                                        j++;
                                        break;
                                    case 3:tvLoc4.setText(data.getLocation());
                                        tvLoc4.setVisibility(View.VISIBLE);
                                        j++;
                                        break;
                                    case 4:tvLoc5.setText(data.getLocation());
                                        tvLoc5.setVisibility(View.VISIBLE);
                                        j++;
                                        break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ServiceLocationPojo>> call, Throwable t) {

                        Toast.makeText(getActivity(),"Check Internet COnnection",Toast.LENGTH_LONG).show();

                    }
                });


            }
        });

        ibCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_cab_categories, null);
                dialogBuilder.setView(dialogView);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                RadioButton mini=(RadioButton)dialogView.findViewById(R.id.acc_rb_mini);
                RadioButton micra=(RadioButton)dialogView.findViewById(R.id.acc_rb_micra);
                RadioButton sedan=(RadioButton)dialogView.findViewById(R.id.acc_rb_sedan);

                switch(stCategory)
                {
                    case "Mini":
                        mini.setTextColor(Color.parseColor("#2679cc"));
                        micra.setTextColor(Color.parseColor("#414040"));
                        sedan.setTextColor(Color.parseColor("#414040"));
                        break;
                    case "Suv":
                        mini.setTextColor(Color.parseColor("#414040"));
                        sedan.setTextColor(Color.parseColor("#414040"));
                        micra.setTextColor(Color.parseColor("#2679cc"));
                        break;
                    case "Sedan":
                        micra.setTextColor(Color.parseColor("#414040"));
                        mini.setTextColor(Color.parseColor("#414040"));
                        sedan.setTextColor(Color.parseColor("#2679cc"));
                        break;
                }

                RadioGroup rgList=(RadioGroup)dialogView.findViewById(R.id.acc_rg_list);
                rgList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {

                        switch (i)
                        {
                            case R.id.acc_rb_mini:
                                //Toast.makeText(getActivity(),"Mini clicked",Toast.LENGTH_LONG).show();
                                stCategory="Mini";
                                tvCategory.setText("Mini");
                                alertDialog.dismiss();
                                getFareEstimates();
                                break;
                            case R.id.acc_rb_micra:
                                //Toast.makeText(getActivity(),"Micra clicked",Toast.LENGTH_LONG).show();
                                stCategory="Suv";
                                tvCategory.setText("Suv ");
                                alertDialog.dismiss();
                                getFareEstimates();
                                break;
                            case R.id.acc_rb_sedan:
                                // Toast.makeText(getActivity(),"Sedan clicked",Toast.LENGTH_LONG).show();
                                stCategory="Sedan";
                                tvCategory.setText("Sedan");
                                alertDialog.dismiss();
                                getFareEstimates();
                                break;
                        }
                        // alertDialog.dismiss();
                    }
                });
            }
        });

        tvLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvLocal.setTextColor(Color.parseColor("#0067de"));
                tvOutstation.setTextColor(Color.parseColor("#414040"));
                tvLocal.setBackground(getResources().getDrawable(R.drawable.rect_blue_stroke_bg_nc));
                tvOutstation.setBackground(getResources().getDrawable(R.drawable.rect_ash_stroke_bg_single_line));
                stTravelType="Local";
                // tvTravelType.setText("Local");
                getFareEstimates();
            }
        });

        tvOutstation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvOutstation.setTextColor(Color.parseColor("#0067de"));
                tvLocal.setTextColor(Color.parseColor("#414040"));
                tvOutstation.setBackground(getResources().getDrawable(R.drawable.rect_blue_stroke_bg_nc));
                tvLocal.setBackground(getResources().getDrawable(R.drawable.rect_ash_stroke_bg_single_line));
                stTravelType="OutStation";
                // tvTravelType.setText("OutStation");
                getFareEstimates();

            }
        });

        /*

        ibTravelType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_travel_type, null);
                dialogBuilder.setView(dialogView);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                RadioButton local=(RadioButton)dialogView.findViewById(R.id.att_rb_local);
                RadioButton outStation=(RadioButton)dialogView.findViewById(R.id.att_rb_out_station);

                switch(stTravelType)
                {
                    case "Local":
                        local.setTextColor(Color.parseColor("#2679cc"));
                        outStation.setTextColor(Color.parseColor("#414040"));
                        break;
                    case "OutStation":
                        local.setTextColor(Color.parseColor("#414040"));
                        outStation.setTextColor(Color.parseColor("#2679cc"));
                        break;
                }

                RadioGroup rgList=(RadioGroup)dialogView.findViewById(R.id.acc_rg_list);
                rgList.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {

                        switch (i)
                        {
                            case R.id.att_rb_local:
                                //Toast.makeText(getActivity(),"Mini clicked",Toast.LENGTH_LONG).show();
                                stTravelType="Local";
                                tvTravelType.setText("Local");
                                alertDialog.dismiss();
                                getFareEstimates();
                                break;
                            case R.id.att_rb_out_station:
                                //Toast.makeText(getActivity(),"Micra clicked",Toast.LENGTH_LONG).show();
                                stTravelType="OutStation";
                                tvTravelType.setText("OutStation");
                                alertDialog.dismiss();
                                getFareEstimates();
                                break;
                        }
                        // alertDialog.dismiss();
                    }
                });

            }
        });
        */
        return  rootView;
    }

    public void getTariffRate()
    {
        Call<List<TariffRatePojo>> call=REST_CLIENT.getTariffRates(stCity,stCategory,companyId);
        call.enqueue(new Callback<List<TariffRatePojo>>() {
            @Override
            public void onResponse(Call<List<TariffRatePojo>> call, Response<List<TariffRatePojo>> response) {

                TariffRatePojo data;
                List<TariffRatePojo> dataList;

                if(myBottomSheet.isAdded())
                {
                    myBottomSheet.dismiss();
                }

                if(response.isSuccessful())
                {
                    dataList=response.body();

                    data=dataList.get(0);

                    tvBaseFare.setText(getString(R.string.Rs)+" "+data.getBaseFare());
                    tvBelow.setText(getString(R.string.Rs)+" "+data.getOTo15km()+" per km");
                    tvAbove.setText(getString(R.string.Rs)+" "+data.getAfter15km()+" per km");
                    tvMinFare.setText(getString(R.string.Rs)+" "+data.getMinimumfare());
                    // tvPeakTime.setText(data.getPeaktimecharges());
                    tvRideTime.setText(getString(R.string.Rs)+" "+data.getRidetimecharges()+" per min");
                }
                else
                {
                    Toast.makeText(getActivity(),"Error Showing Data !",Toast.LENGTH_SHORT).show();
                    tvBaseFare.setText(getString(R.string.Rs)+" "+"-");
                    tvBelow.setText(getString(R.string.Rs)+" "+"-"+" per km");
                    tvAbove.setText(getString(R.string.Rs)+" "+"-"+" per km");
                    tvMinFare.setText(getString(R.string.Rs)+" "+"-");
                    // tvPeakTime.setText(data.getPeaktimecharges());
                    tvRideTime.setText(getString(R.string.Rs)+" "+"-"+" per min");
                }
            }

            @Override
            public void onFailure(Call<List<TariffRatePojo>> call, Throwable t) {

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


    public void getFareEstimates()
    {
        JsonObject v=new JsonObject();
        v.addProperty("companyid",companyId);
        v.addProperty("location",stCity);
        v.addProperty("traveltype",stTravelType);
        v.addProperty("vehicle_type",stCategory);
        v.addProperty("approxkms",0);

        Call<List<OutStationPojo>> call1=REST_CLIENT.getFareEstimate(v);
        call1.enqueue(new Callback<List<OutStationPojo>>() {
            @Override
            public void onResponse(Call<List<OutStationPojo>> call, Response<List<OutStationPojo>> response) {

                List<OutStationPojo> dataList;
                OutStationPojo data;

                if(response.isSuccessful())
                {
                    dataList=response.body();

                    data=dataList.get(0);

                    tvBaseFare.setText(getString(R.string.Rs)+" "+data.getBasefare());
                    tvMinFare.setText(getString(R.string.Rs)+" "+data.getMinimumfare());

                    if(data.getSlabkmrate().equals(""))
                    {
                        tvBelow.setText("Not Applicable");
                    }
                    else {
                        tvBelow.setText(getString(R.string.Rs)+" "+data.getSlabkmrate()+" per km");
                    }

                    if(data.getAfterslabkm().equals("")) {

                        tvAbove.setText("Not Applicable");
                    }
                    else {
                        tvAbove.setText(getString(R.string.Rs)+" "+data.getAfterslabkm()+" per km");
                    }

                    if(data.getRidetimepermin().equals("")) {

                        tvRideTime.setText("Not Applicable");
                    }
                    else {
                        tvRideTime.setText(getString(R.string.Rs)+" "+data.getRidetimepermin()+" per min");
                    }

                    if(data.getOutsidekmsrate().equals(""))
                    {
                        tvPerKmRate.setText("Not Applicable");
                    }
                    else {
                        tvPerKmRate.setText(getString(R.string.Rs)+" "+data.getOutsidekmsrate());
                    }
                }
                else {

                    tvBaseFare.setText(getString(R.string.Rs)+" "+"-");
                    tvMinFare.setText(getString(R.string.Rs)+" "+"-");
                    tvBelow.setText("-");
                    tvAbove.setText("-");
                    tvRideTime.setText("-");
                    tvPerKmRate.setText("-");

                }
            }

            @Override
            public void onFailure(Call<List<OutStationPojo>> call, Throwable t) {

                tvBaseFare.setText(getString(R.string.Rs)+" "+"-");
                tvMinFare.setText(getString(R.string.Rs)+" "+"-");
                tvBelow.setText(getString(R.string.Rs)+" "+"-");
                tvAbove.setText(getString(R.string.Rs)+" "+"-");
                tvRideTime.setText(getString(R.string.Rs)+" "+"-");
                tvPerKmRate.setText(getString(R.string.Rs)+" "+"- ");

                Toast.makeText(getActivity(),"Please Check Internet Connectivity!",Toast.LENGTH_SHORT).show();

            }
        });

    }
}
