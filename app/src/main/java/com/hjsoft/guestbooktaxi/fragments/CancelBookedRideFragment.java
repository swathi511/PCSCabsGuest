package com.hjsoft.guestbooktaxi.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.activity.MyRidesActivity;
import com.hjsoft.guestbooktaxi.activity.RideCancelActivity;
import com.hjsoft.guestbooktaxi.adapter.DBAdapter;
import com.hjsoft.guestbooktaxi.model.BookCabPojo;
import com.hjsoft.guestbooktaxi.model.CabData;
import com.hjsoft.guestbooktaxi.model.CancelPojo;
import com.hjsoft.guestbooktaxi.model.FormattedAllRidesData;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 1/3/18.
 */
public class CancelBookedRideFragment extends Fragment {

    TextView tvRid,tvBDate,tvTravelType,tvPickupLoc,tvDropLoc;
    ArrayList<FormattedAllRidesData> dataList=new ArrayList<>();
    FormattedAllRidesData data;
    int position;
    Button btCancelRide;
    boolean checked=false;
    String cancelOption="";
    DBAdapter dbAdapter;
    API REST_CLIENT;
    String companyId="CMP00001",requestId;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SharedPref";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cancel_booked_ride, container, false);

        tvRid=(TextView)rootView.findViewById(R.id.fcbr_tv_trip_id);
        tvBDate=(TextView)rootView.findViewById(R.id.fcbr_tv_date);
        tvTravelType=(TextView)rootView.findViewById(R.id.fcbr_tv_travel_type);
        tvPickupLoc=(TextView)rootView.findViewById(R.id.fcbr_tv_from);
        tvDropLoc=(TextView)rootView.findViewById(R.id.fcbr_tv_to);
        btCancelRide=(Button)rootView.findViewById(R.id.fcbr_bt_cancel_ride);

        tvRid.setText(data.getRequestId());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);
        String date = dateFormat.format(data.getRideDate());
        String format = new SimpleDateFormat("E, MMM d, yy   hh:mm a",Locale.ENGLISH).format(data.getRideDate());

        tvBDate.setText(format);
        tvTravelType.setText(data.getTravelType()+" - "+data.getTravelPackage());
        tvPickupLoc.setText(data.getFromLocation());
        tvDropLoc.setText(data.getToLocation());

        btCancelRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertCancelRide();
            }
        });

        return  rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbAdapter=new DBAdapter(getContext());
        dbAdapter=dbAdapter.open();

        REST_CLIENT= RestClient.get();

        position=getArguments().getInt("position");
        dataList=(ArrayList<FormattedAllRidesData>) getArguments().getSerializable("list");
        // Toast.makeText(getActivity(),"value "+position,Toast.LENGTH_LONG).show();

        data=dataList.get(position);
        requestId=data.getRequestId();

        pref = getActivity().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }


    public void alertCancelRide()
    {
        /*AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_cancel_ride, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);

        TextView ok=(TextView) dialogView.findViewById(R.id.acr_bt_yes);*/
        checked=false;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_cancel_options, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        final RadioGroup rg=(RadioGroup)dialogView.findViewById(R.id.aco_rb);
        TextView ok=(TextView)dialogView.findViewById(R.id.aco_bt_ok);

        ArrayList<String> cancelData=dbAdapter.getCancelOptions();

        for (int i = 0; i < cancelData.size(); i++) {
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setText(cancelData.get(i));
            radioButton.setId(i);
            rg.addView(radioButton);
        }

        //set listener to radio button group
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                System.out.println(rg.getCheckedRadioButtonId()+"::"+checkedId);
                int checkedRadioButtonId =rg.getCheckedRadioButtonId();
                RadioButton radioBtn = (RadioButton)dialogView.findViewById(checkedId);
                //System.out.println("++++"+radioBtn.getText());
                //System.out.println("++++"+radioBtn.getText().toString());

                cancelOption=radioBtn.getText().toString();
                checked=true;
                //radioBtn.getText()
                //Toast.makeText(ConfigurationActivity.this, radioBtn.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checked)
                {


                    alertDialog.dismiss();
                    //checkForArrivalNotification();

                    String id = dbAdapter.getCancelId(cancelOption);

                    // System.out.println("@@@@@@ id iss "+id);

                    final JsonObject v = new JsonObject();
                    v.addProperty("requestid", requestId);
                    v.addProperty("companyid", companyId);
                    v.addProperty("reasonid", id);

                    Call<BookCabPojo> call1 = REST_CLIENT.doCancel(v);
                    call1.enqueue(new Callback<BookCabPojo>() {
                        @Override
                        public void onResponse(Call<BookCabPojo> call1, Response<BookCabPojo> response) {

                            if (response.isSuccessful()) {

                                Call<List<CancelPojo>> call = REST_CLIENT.sendCancelStatus(v);
                                call.enqueue(new Callback<List<CancelPojo>>() {
                                    @Override
                                    public void onResponse(Call<List<CancelPojo>> call, Response<List<CancelPojo>> response) {

                                        List<CancelPojo> msgList;
                                        CancelPojo msg;
                                        //CabData data;

                                        if (response.isSuccessful()) {
                                            msgList = response.body();

                                            for (int j = 0; j < msgList.size(); j++) {
                                                msg = msgList.get(0);

                                                String cancelDetails = msg.getCancelmessage();
                                                String[] cancelFeeDetails = cancelDetails.split("-");
                                                String cancelFee = cancelFeeDetails[0];
                                                String paymentMode = "none";

                                                if (cancelFee.equals("0")) {

                                                } else {
                                                    if (msg.getPaycash().equals("0")) {
                                                        paymentMode = "wallet";
                                                        String walletAmount = msg.getWalletamount();
                                                        String timeUpdated = java.text.DateFormat.getTimeInstance().format(new Date());

                                                        if (dbAdapter.isWalletPresent() > 0) {
                                                            dbAdapter.updateWalletAmount(walletAmount, timeUpdated);

                                                        } else {

                                                            dbAdapter.insertWalletAmount(walletAmount, timeUpdated);
                                                        }
                                                    } else {

                                                        paymentMode = "cash";
                                                    }
                                                }

                                                ArrayList<CabData> cabAcceptedData = new ArrayList<CabData>();
                                                cabAcceptedData.add(new CabData("", "",
                                                        data.getDriverMobile(), data.getVehicleCategory(), "", "", data.getDriverName(), data.getDriverPic(), ""));
                                                // dbAdapter.updateUserRideEntry(requestId,"cancelled");

                                                editor.putString("pickup_location",data.getFromLocation());
                                                editor.putString("drop_location",data.getToLocation());
                                                editor.commit();

                                                Intent i = new Intent(getActivity(), RideCancelActivity.class);
                                                i.putExtra("list", cabAcceptedData);
                                                i.putExtra("cancelFee", cancelFee);
                                                i.putExtra("requestId", requestId);
                                                i.putExtra("paymentMode", paymentMode);
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
                else {
                    Toast.makeText(getActivity(),"Please select a reason!",Toast.LENGTH_SHORT).show();

                }
            }
        });

       /* TextView no=(TextView) dialogView.findViewById(R.id.acr_bt_no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });*/

    }
}
