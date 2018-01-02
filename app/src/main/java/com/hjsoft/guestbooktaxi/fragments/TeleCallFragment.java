package com.hjsoft.guestbooktaxi.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.SessionManager;
import com.hjsoft.guestbooktaxi.activity.TeleCallActivity;
import com.hjsoft.guestbooktaxi.model.BookCabPojo;
import com.hjsoft.guestbooktaxi.model.MSeaterPojo;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 21/4/17.
 */
public class TeleCallFragment extends Fragment {

    View rootView;
    DatePicker dp;
    EditText etDesc;
    Button btSubmit;
    int hr,min,day,mnth,yr;
    String stDate;
    Spinner sp;
    API REST_CLIENT;
    String companyId="CMP00001";
    String guestProfileId,vehCategory,comments;
    String item="";
    SessionManager session;
    HashMap<String, String> user;
    TeleCallActivity a;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView= inflater.inflate(R.layout.fragment_telecall, container, false);
        dp=(DatePicker)rootView.findViewById(R.id.ft_datePicker);
        etDesc=(EditText)rootView.findViewById(R.id.ft_et_desc);
        btSubmit=(Button)rootView.findViewById(R.id.ft_bt_submit);
        sp=(Spinner)rootView.findViewById(R.id.ft_spinner);

        REST_CLIENT= RestClient.get();
        final List<String> categories = new ArrayList<String>();
        categories.add("--- select ---");

        Call<List<MSeaterPojo>> call=REST_CLIENT.getVehicles(companyId);
        call.enqueue(new Callback<List<MSeaterPojo>>() {
            @Override
            public void onResponse(Call<List<MSeaterPojo>> call, Response<List<MSeaterPojo>> response) {

                MSeaterPojo d;
                List<MSeaterPojo> dList;

                if(response.isSuccessful())
                {
                    dList=response.body();

                    for(int i=0;i<dList.size();i++)
                    {
                        d=dList.get(i);
                        categories.add(d.getVehCat()+"("+d.getVehName()+")");

                    }

                    //if(isAdded()) {

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(a, android.R.layout.simple_spinner_item, categories);

                        // Drop down layout style - list view with radio button
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // attaching data adapter to spinner
                        sp.setAdapter(dataAdapter);


                        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {

                                if (position != 0) {

                                    item = parent.getItemAtPosition(position).toString();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                   // }

                }
            }

            @Override
            public void onFailure(Call<List<MSeaterPojo>> call, Throwable t) {

            }
        });


//        categories.add("- select -");
//        categories.add("I need a copy of my invoice");
//        categories.add("Charged cancellation fee incorrectly");
//        categories.add("Driver did not come for pickup");
//        categories.add("Delay in pickup");
//        categories.add("Other driver related issue?");
//        categories.add("Driver was not contactable");
//        categories.add("Others");



        long now = System.currentTimeMillis() - 1000;

        dp.setMinDate(now);
        dp.setMaxDate(now+(1000*60*60*24*7));

        day = dp.getDayOfMonth();
        mnth = dp.getMonth()+1;
        yr = dp.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        dp.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {

                day=dayOfMonth;
                mnth=month+1;
                yr=year;

                //System.out.println("date is "+day+":::"+mnth+":::"+yr);
            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // stDate=day+"-"+mnth+"-"+yr;
                stDate=yr+"-"+mnth+"-"+day;


//                Date d = makeDateGMT(yr, mnth-1, day);
//                Date d1 = new Date();
//                System.out.println("*************** d & d1 " + d + "::" + d1);
//
//                if (d.equals(d1)) {
//
//                    Calendar datetime = Calendar.getInstance();
//                    Calendar c = Calendar.getInstance();
//                    datetime.set(Calendar.HOUR_OF_DAY, hr);
//                    datetime.set(Calendar.MINUTE, min);
//
//                    System.out.println(" *********** Time " + datetime.getTimeInMillis() + ":::" + c.getTimeInMillis());
//                    if (datetime.getTimeInMillis() > c.getTimeInMillis()) {
////            it's after current
//
//                        // stDate=day+"/"+mnth+"/"+yr;
//                        stDate =day+"-"+mnth+"-"+yr;
//                        //showPackageDetails();
//                        sendVehicleComments();
//
//                    } else {
////            it's before current'
//                        Toast.makeText(getActivity(), "Please Choose Time ahead of current time", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    // stDate=day+"/"+mnth+"/"+yr;
//                    stDate =day+"-"+mnth+"-"+yr;
//                    sendVehicleComments();

                //  showPackageDetails();
                //  }

                sendVehicleComments();
            }
        });





        return rootView;
    }


    private Date makeDateGMT(int year, int month, int day) {
        GregorianCalendar calendar = new GregorianCalendar();
        // calendar.setTimeZone(TimeZone.getTimeZone());
        calendar.set(year,month, day);
        return calendar.getTime();
    }

    public void sendVehicleComments()
    {

        if(!(item.equals(""))) {
            comments = etDesc.getText().toString().trim();

            session = new SessionManager(getActivity());

            user = session.getUserDetails();

            guestProfileId = user.get(SessionManager.KEY_PROFILE_ID);

            JsonObject v = new JsonObject();
            v.addProperty("guestprofileid", guestProfileId);
            v.addProperty("veh_cat", item);
            v.addProperty("booking_date", stDate);
            v.addProperty("comments", comments);
            v.addProperty("companyid", companyId);

            Call<BookCabPojo> call = REST_CLIENT.addVehComments(v);
            call.enqueue(new Callback<BookCabPojo>() {
                @Override
                public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                    if (response.isSuccessful()) {
                        Toast.makeText(getActivity(), "Data successfully sent!", Toast.LENGTH_LONG).show();
                        btSubmit.setVisibility(View.GONE);
                        etDesc.setEnabled(false);
                        //etDesc.setClickable(false);
                        //  sp.setClickable(false);
                        sp.setEnabled(false);
                        dp.setEnabled(false);
                        // dp.setClickable(false);
                    }
                }

                @Override
                public void onFailure(Call<BookCabPojo> call, Throwable t) {

                    Toast.makeText(getActivity(), "Connectivity Error! Please Check.", Toast.LENGTH_LONG).show();

                }
            });
        }
        else {

            Toast.makeText(getActivity(),"Please select the vehicle category!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof TeleCallActivity){
            a=(TeleCallActivity) context;
        }

    }
}
