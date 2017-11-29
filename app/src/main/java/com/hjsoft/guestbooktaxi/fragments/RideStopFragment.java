package com.hjsoft.guestbooktaxi.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.adapter.DBAdapter;
import com.hjsoft.guestbooktaxi.model.RideStopPojo;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 7/1/17.
 */
public class RideStopFragment extends Fragment {

    View rootView;
    TextView tvVehCat,tvVehType,tvRideStartTime,tvRideStopTime,tvPickupLoc,tvDropLoc,tvFare,tvDistance,tvTime,tvTotalFare,tvTaxes,tvTotalBill,tvOutstationBatta;
    LinearLayout tvOsBattaTitle;
    TextView tvDriverName,tvDriverMobile,tvTripId;
    API REST_CLIENT;
    String requestId,driverName,driverMobile;
    BottomSheetDialogFragment myBottomSheet;
    long diff=0;
    Button btPay;
    String companyId="CMP00001";
    TextView tvWalletAmnt,tvCash,tvOtherCharges;
    LinearLayout llWallet,llCash,llCashInfo,llWalletInfo,llOtherCharges;
    DBAdapter dbAdapter;
    LinearLayout llMain;
    String osbatta;
    double totalBill;
    double otherCharges=0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        REST_CLIENT= RestClient.get();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_ride_stop, container, false);
        tvVehCat=(TextView)rootView.findViewById(R.id.frs_tv_veh_cat);
        tvVehType=(TextView)rootView.findViewById(R.id.frs_tv_veh_type);
        tvRideStartTime=(TextView)rootView.findViewById(R.id.frs_tv_ride_start_time);
        tvRideStopTime=(TextView)rootView.findViewById(R.id.frs_tv_ride_stop_time);
        tvPickupLoc=(TextView)rootView.findViewById(R.id.frs_tv_pickup_loc);
        tvDropLoc=(TextView)rootView.findViewById(R.id.frs_tv_drop_loc);
        tvFare=(TextView)rootView.findViewById(R.id.frs_tv_total_fare);
        tvDistance=(TextView)rootView.findViewById(R.id.frs_tv_distance);
        tvTime=(TextView)rootView.findViewById(R.id.frs_tv_time);
        tvTotalFare=(TextView)rootView.findViewById(R.id.fsr_tv_total_fare);
        myBottomSheet = MyBottomSheetDialogFragment.newInstance("Modal Bottom Sheet");
        tvTaxes=(TextView)rootView.findViewById(R.id.frs_tv_taxes);
        tvTotalBill=(TextView)rootView.findViewById(R.id.frs_tv_total_bill);
        tvDriverName=(TextView)rootView.findViewById(R.id.frs_tv_driver_name);
        tvDriverMobile=(TextView)rootView.findViewById(R.id.frs_tv_driver_number);
        tvTripId=(TextView)rootView.findViewById(R.id.frs_tv_trip_id);
        btPay=(Button)rootView.findViewById(R.id.frs_bt_pay);
        tvWalletAmnt=(TextView)rootView.findViewById(R.id.frs_tv_wallet_amnt);
        tvCash=(TextView)rootView.findViewById(R.id.frs_tv_cash);
        llWallet=(LinearLayout)rootView.findViewById(R.id.frs_ll_wallet);
        llCash=(LinearLayout)rootView.findViewById(R.id.frs_ll_cash);
        llMain=(LinearLayout)rootView.findViewById(R.id.frs_ll_main);
        tvOutstationBatta=(TextView)rootView.findViewById(R.id.fsr_tv_os_batta);
        tvOsBattaTitle=(LinearLayout) rootView.findViewById(R.id.fsr_tv_os_batta_title);
        tvOsBattaTitle.setVisibility(View.GONE);
        tvOutstationBatta.setVisibility(View.GONE);

        llCashInfo=(LinearLayout)rootView.findViewById(R.id.fsr_ll_cash_info);
        llWalletInfo=(LinearLayout)rootView.findViewById(R.id.fsr_ll_wallet_info);
        tvOtherCharges=(TextView)rootView.findViewById(R.id.frs_tv_charges);
        llOtherCharges=(LinearLayout)rootView.findViewById(R.id.frs_ll_other_charges);
        llOtherCharges.setVisibility(View.GONE);

        requestId=getActivity().getIntent().getStringExtra("requestId");
        driverName=getActivity().getIntent().getStringExtra("driverName");
        driverMobile=getActivity().getIntent().getStringExtra("driverNumber");

        tvTripId.setText(requestId);
        tvDriverName.setText(driverName);
        tvDriverMobile.setText(driverMobile);

        dbAdapter=new DBAdapter(getContext());
        dbAdapter=dbAdapter.open();

        btPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait! Getting Invoice...");
        progressDialog.show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                 handler.removeCallbacks(this);


                Call<List<RideStopPojo>> call=REST_CLIENT.getRideStopData(requestId,companyId,"guest");
                call.enqueue(new Callback<List<RideStopPojo>>() {
                    @Override
                    public void onResponse(Call<List<RideStopPojo>> call, Response<List<RideStopPojo>> response) {

                        List<RideStopPojo> dataList;
                        RideStopPojo data;

                        if(response.isSuccessful())
                        {
                            llMain.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                            dataList=response.body();

                            data=dataList.get(0);

                            tvVehCat.setText(data.getVehicleCategory());
                            tvVehType.setText(data.getVehicleType());
                            tvRideStartTime.setText(data.getRidestarttime());
                            tvRideStopTime.setText(data.getRidestoptime());
                            tvPickupLoc.setText(data.getFromlocation());
                            tvDropLoc.setText(data.getTolocation());
                   /* tvFare.setText("Rs. "+data.getTotalfare());
                    tvTotalFare.setText("Rs. "+data.getTotalfare());
                    tvTotalBill.setText("Rs. "+data.getTotalfare());
                    tvTaxes.setText("Rs. 0");*/

                            if(data.getOtherCharges()==null||data.getOtherCharges().equals("0"))
                            {

                            }
                            else {
                                llOtherCharges.setVisibility(View.VISIBLE);
                                tvOtherCharges.setText(getString(R.string.Rs)+" "+data.getOtherCharges());
                                otherCharges=Double.parseDouble(data.getOtherCharges());
                            }

                            if(data.getTravelType().equals("outstation"))
                            {
                                tvOutstationBatta.setVisibility(View.VISIBLE);
                                tvOsBattaTitle.setVisibility(View.VISIBLE);
                                tvOutstationBatta.setText(getString(R.string.Rs)+" "+data.getDriverBattaAmt());
                                osbatta=data.getDriverBattaAmt();
                            }

                            if(data.getTotalfare().equals("")) {

                                tvFare.setText(getString(R.string.Rs)+" "+"-");
                                tvTotalFare.setText(getString(R.string.Rs)+" "+"-");
                                tvTotalBill.setText(getString(R.string.Rs)+" "+"-");
                                tvTaxes.setText(getString(R.string.Rs)+" "+"0");
                            }
                            else
                            {
                                String[] amount = data.getTotalfare().split("-");

                                if (data.getTotalfare().equals("-")) {
                                    tvFare.setText(getString(R.string.Rs)+" "+"0");
                                    tvTotalFare.setText(getString(R.string.Rs)+" "+"0");
                                    tvTotalBill.setText(getString(R.string.Rs)+" "+"0");
                                } else {

                                    String fare = amount[0];
                                    String tax = amount[1];

                                    if(data.getTravelType().equals("outstation"))
                                    {
                                        Double finalFare=Double.parseDouble(fare)-Double.parseDouble(osbatta)-otherCharges;
                                        tvFare.setText(getString(R.string.Rs)+" "+ String.valueOf(finalFare));
                                        tvTotalFare.setText(getString(R.string.Rs)+" "+ String.valueOf(finalFare));
                                    }
                                    else {
                                        Double finalFare=Double.parseDouble(fare)-otherCharges;

                                        tvFare.setText(getString(R.string.Rs)+" "+ finalFare);
                                        tvTotalFare.setText(getString(R.string.Rs)+" "+ finalFare);
                                    }

//                                    tvFare.setText(getString(R.string.Rs)+" "+ fare);
//                                    tvTotalFare.setText(getString(R.string.Rs)+" "+ fare);
                                    tvTaxes.setText(getString(R.string.Rs)+" "+ tax);
                                    totalBill = Double.parseDouble(fare)+Double.parseDouble(tax);
                                    tvTotalBill.setText(getString(R.string.Rs)+" "+ String.valueOf(totalBill));
                                }
                        /*
            tvFare.setText("Rs. "+data.getTotalAmount());
            tvTotalFare.setText("Rs. "+data.getTotalAmount());
            tvTotalBill.setText("Rs. "+data.getTotalAmount());*/
                            }

                            // double dis=Double.parseDouble(data.getDistancetravelled());
                            // dis=dis/1000;

                            tvDistance.setText(data.getDistancetravelled());


                            if((data.getRidestarttime().substring(data.getRidestarttime().length() - 4, data.getRidestarttime().length()).equals("a.m.")
                                    ||data.getRidestarttime().substring(data.getRidestarttime().length() - 4, data.getRidestarttime().length()).equals("p.m.")))
                            {

                                String time =data.getRidestarttime().substring(0, data.getRidestarttime().length() - 4);
                                //System.out.println(time);
                                String timePick = data.getRidestarttime().substring(data.getRidestarttime().length() - 4, data.getRidestarttime().length());
                               // System.out.println(timePick);
                                if (timePick.equals("p.m.")) {
                                    time += "PM";
                                } else if (timePick.equals("a.m.")) {
                                    time += "AM";
                                }

                                data.setRidestarttime(time);
                            }
                            else {

                            }

                            if((data.getRidestoptime().substring(data.getRidestoptime().length() - 4, data.getRidestoptime().length()).equals("a.m.")
                                    ||data.getRidestoptime().substring(data.getRidestoptime().length() - 4, data.getRidestoptime().length()).equals("p.m.")))
                            {

                                String time =data.getRidestoptime().substring(0, data.getRidestoptime().length() - 4);
                                //System.out.println(time);
                                String timePick = data.getRidestoptime().substring(data.getRidestoptime().length() - 4, data.getRidestoptime().length());
                                //System.out.println(timePick);
                                if (timePick.equals("p.m.")) {
                                    time += "PM";
                                } else if (timePick.equals("a.m.")) {
                                    time += "AM";
                                }

                                data.setRidestoptime(time);
                            }
                            else {

                            }

                            if(data.getTravelType().equals("local")||data.getTravelType().equals("Packages")) {

                                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH);
                                timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                                try {
                                    Date date1 = timeFormat.parse(data.getRidestarttime());
                                    Date date2 = timeFormat.parse(data.getRidestoptime());

                                    diff = (date2.getTime() - date1.getTime());

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                int Hours = (int) (diff / (1000 * 60 * 60));
                                int Mins = (int) (diff / (1000 * 60)) % 60;
                                long Secs = (int) (diff / 1000) % 60;

                                DecimalFormat formatter = new DecimalFormat("00");
                                String hFormatted = formatter.format(Hours);
                                String mFormatted = formatter.format(Mins);
                                String sFormatted = formatter.format(Secs);
                                String time = hFormatted + ":" + mFormatted + ":" + sFormatted;

                                tvTime.setText(time);
                            }
                            else {

                                SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.ENGLISH);
                                timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                                try {
                                    Date date1 = timeFormat.parse(data.getRidestarttime());
                                    Date date2 = timeFormat.parse(data.getRidestoptime());

                                    diff = (date2.getTime() - date1.getTime());

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                int Hours = (int) (diff / (1000 * 60 * 60));
                                int Mins = (int) (diff / (1000 * 60)) % 60;
                                long Secs = (int) (diff / 1000) % 60;

                                DecimalFormat formatter = new DecimalFormat("00");
                                String hFormatted = formatter.format(Hours);
                                String mFormatted = formatter.format(Mins);
                                String sFormatted = formatter.format(Secs);
                                String time = hFormatted + ":" + mFormatted + ":" + sFormatted;

                                tvTime.setText(time);

                            }

                            if(data.getPaymentMode().equals("cash"))
                            {
                                llWallet.setVisibility(View.GONE);
                                tvCash.setText(getString(R.string.Rs)+" "+data.getPaymentCash());
                                String timeUpdated=java.text.DateFormat.getTimeInstance().format(new Date());
                                if(dbAdapter.isWalletPresent()>0)
                                {
                                    dbAdapter.insertWalletAmount(data.getWalletBalance(),timeUpdated);
                                }
                                else {
                                    dbAdapter.updateWalletAmount(data.getWalletBalance(),timeUpdated);
                                }

                                llCashInfo.setVisibility(View.VISIBLE);

                            }
                            else if(data.getPaymentMode().equals("wallet"))
                            {
                                // llCash.setVisibility(View.GONE);
                                tvWalletAmnt.setText(getString(R.string.Rs)+" "+data.getPaymentWallet());
                                if(data.getPaymentCash().equals("0"))
                                {
                                    llCash.setVisibility(View.GONE);
                                }
                                else {
                                    tvCash.setText(getString(R.string.Rs)+" "+data.getPaymentCash());
                                }

                                String timeUpdated=java.text.DateFormat.getTimeInstance().format(new Date());
                                if(dbAdapter.isWalletPresent()>0)
                                {
                                    dbAdapter.insertWalletAmount(data.getWalletBalance(),timeUpdated);
                                }
                                else {
                                    dbAdapter.updateWalletAmount(data.getWalletBalance(),timeUpdated);
                                }

                                llWalletInfo.setVisibility(View.VISIBLE);
                            }
                            else {

                                llWallet.setVisibility(View.GONE);
                                tvCash.setText(getString(R.string.Rs)+" "+totalBill);

                                llCashInfo.setVisibility(View.VISIBLE);
                            }


                        }
                        else {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RideStopPojo>> call, Throwable t) {

                        progressDialog.dismiss();

                        if(myBottomSheet.isAdded())
                        {
                            //return;
                        }
                        else
                        {
                            myBottomSheet.show(getChildFragmentManager(), myBottomSheet.getTag());
                        }

                    }
                });
            }
        }, 8000);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
