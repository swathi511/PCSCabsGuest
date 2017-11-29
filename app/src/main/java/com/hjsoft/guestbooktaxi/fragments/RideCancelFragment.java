package com.hjsoft.guestbooktaxi.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.adapter.DBAdapter;
import com.hjsoft.guestbooktaxi.model.CabData;

import java.util.ArrayList;

/**
 * Created by hjsoft on 30/1/17.
 */
public class RideCancelFragment extends Fragment{

    TextView tvVehCat,tvFrom,tvTo,tvTripId,tvDriverName,tvCancelCharge,tvCancelText;
    ArrayList<CabData> list;
    CabData data;
    String stCancelFee,stRequestId;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";
    String pickupLoc,dropLoc;
    String stPaymentMode;
    LinearLayout llWallet;
    TextView tvWalletBalance;
    DBAdapter dbAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ride_cancel, container, false);

        tvVehCat=(TextView)rootView.findViewById(R.id.frc_tv_veh_cat);
        tvDriverName=(TextView)rootView.findViewById(R.id.frc_tv_driver_name);
        tvFrom=(TextView)rootView.findViewById(R.id.frc_tv_from);
        tvTo=(TextView)rootView.findViewById(R.id.frc_tv_to);
        tvTripId=(TextView)rootView.findViewById(R.id.frc_tv_trip_id);
        tvCancelCharge=(TextView)rootView.findViewById(R.id.frc_tv_cancel_charge);
        tvCancelText=(TextView)rootView.findViewById(R.id.frc_tv_cancel_text);
        tvWalletBalance=(TextView)rootView.findViewById(R.id.frc_tv_wallet_balance);
        llWallet=(LinearLayout) rootView.findViewById(R.id.frc_ll_wallet);
        llWallet.setVisibility(View.GONE);

        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

        pickupLoc=pref.getString("pickup_location",null);
        dropLoc=pref.getString("drop_location",null);

        dbAdapter=new DBAdapter(getContext());
        dbAdapter=dbAdapter.open();

        list=(ArrayList<CabData>) getActivity().getIntent().getSerializableExtra("list");
        Bundle d=getActivity().getIntent().getExtras();
        if(d!=null) {
            stCancelFee= d.getString("cancelFee");
            stRequestId=d.getString("requestId");
            stPaymentMode=d.getString("paymentMode");
        }

        if(list!=null) {

            data=list.get(0);

            tvTripId.setText(stRequestId);
            tvDriverName.setText(data.getDriverName());
            tvVehCat.setText(data.getCabCat());
            tvFrom.setText(pickupLoc);
            tvTo.setText(dropLoc);

            if(stCancelFee.equals("0 "))
            {
                tvCancelCharge.setText("Booking Cancelled !");
                tvCancelCharge.setTextColor(Color.parseColor("#0067de"));
                tvCancelText.setVisibility(View.GONE);
            }
            else {
                if(stPaymentMode.equals("cash")) {

                    tvCancelCharge.setText(getString(R.string.Rs) + " " + stCancelFee);
                }
                else {

                    tvCancelCharge.setText(getString(R.string.Rs) + " " + stCancelFee);
                    tvCancelText.setText("Cancellation Charges deducted from wallet");
                    llWallet.setVisibility(View.VISIBLE);
                    tvWalletBalance.setText(getString(R.string.Rs) + " " +dbAdapter.getWalletAmount());

                }
            }
        }
        return rootView;
    }
}
