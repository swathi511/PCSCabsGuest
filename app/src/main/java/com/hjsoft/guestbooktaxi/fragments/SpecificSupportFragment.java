package com.hjsoft.guestbooktaxi.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.adapter.DBAdapter;
import com.hjsoft.guestbooktaxi.model.BookCabPojo;
import com.hjsoft.guestbooktaxi.model.FormattedAllRidesData;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 24/4/17.
 */
public class SpecificSupportFragment extends Fragment {

    View rootView;
    DBAdapter dbAdapter;
    int position;
    ArrayList<FormattedAllRidesData> dataList=new ArrayList<>();
    FormattedAllRidesData data;
    TextView tvRid,tvBdate,tvTotalBill,tvPickupLoc,tvDrop;
    Button btSubmit;
    EditText etDesc;
    TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7;
    Spinner sp;
    API REST_CLIENT;
    String feedback="";
    String companyId="CMP00001";
    TextView tvRefId;
    boolean userSelect = false;
    LinearLayout llResponse;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_specific_support, container, false);
        tvRid=(TextView)rootView.findViewById(R.id.fss_tv_rid);
        tvBdate=(TextView)rootView.findViewById(R.id.fss_tv_b_date);
        tvTotalBill=(TextView)rootView.findViewById(R.id.fss_tv_total_bill);
        tvPickupLoc=(TextView)rootView.findViewById(R.id.fss_tv_pickup_loc);
        tvDrop=(TextView)rootView.findViewById(R.id.fss_tv_drop_loc);
        btSubmit=(Button) rootView.findViewById(R.id.fss_bt_submit);
        etDesc=(EditText)rootView.findViewById(R.id.fss_et_desc);

        sp=(Spinner)rootView.findViewById(R.id.spinner);
        tvRefId=(TextView)rootView.findViewById(R.id.fss_tv_refid);

        llResponse=(LinearLayout)rootView.findViewById(R.id.fss_ll_response);
        llResponse.setVisibility(View.GONE);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("- select -");
        categories.add("I need a copy of my invoice");
        categories.add("Charged cancellation fee incorrectly");
        categories.add("Driver did not come for pickup");
        categories.add("Delay in pickup");
        categories.add("Other driver related issue?");
        categories.add("Driver was not contactable");
        categories.add("Others");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        sp.setAdapter(dataAdapter);

        sp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                userSelect = true;
                return false;
            }
        });

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                if(position!=0&&userSelect) {

                    String item = parent.getItemAtPosition(position).toString();
                    feedback = item;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvRid.setText(data.getRequestId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);
        String format = new SimpleDateFormat("E, MMM d, yy   hh:mm a",Locale.ENGLISH).format(data.getRideDate());
        tvBdate.setText(format);
        String[] amount = data.getTotalAmount().split("-");

        if (data.getTotalAmount().equals("-")) {
            tvTotalBill.setText(getString(R.string.Rs)+" "+"0");
        } else {

            String fare = amount[0];
            String tax = amount[1];

            int totalBill = (int)(Double.parseDouble(fare)+Double.parseDouble(tax));
            tvTotalBill.setText(getString(R.string.Rs)+" "+ String.valueOf(totalBill));
        }

        tvPickupLoc.setText(data.getFromLocation());
        tvDrop.setText(data.getToLocation());

        etDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                feedback=feedback+etDesc.getText().toString().trim();

                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();

                JsonObject v=new JsonObject();
                v.addProperty("requestid",data.getRequestId());
                v.addProperty("companyid",companyId);
                v.addProperty("feedback",feedback);

                Call<BookCabPojo> call=REST_CLIENT.sendFeedback(v);
                call.enqueue(new Callback<BookCabPojo>() {
                    @Override
                    public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                        BookCabPojo data;

                        if(response.isSuccessful())
                        {
                            progressDialog.dismiss();
                            data=response.body();
                            btSubmit.setVisibility(View.GONE);
                            llResponse.setVisibility(View.VISIBLE);
                            tvRefId.setText("Reference Id ' "+data.getMessage()+" ' generated.");
                            etDesc.setEnabled(false);
                            etDesc.setClickable(false);
                            sp.setEnabled(false);
                            sp.setClickable(false);

                        }
                    }

                    @Override
                    public void onFailure(Call<BookCabPojo> call, Throwable t) {

                        progressDialog.dismiss();
                        Toast.makeText(getActivity(),"Check Internet Connection!",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position=getArguments().getInt("position");
        dataList=(ArrayList<FormattedAllRidesData>) getArguments().getSerializable("list");
        data=dataList.get(position);

        REST_CLIENT= RestClient.get();
    }
}
