package com.hjsoft.guestbooktaxi.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.model.LocalPackagesPojo;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 25/4/17.
 */
public class PackagesFragment extends Fragment {

    View rootView;
    Button btCancel;

    TextView tvPkg1,tvPkg2,tvPkg3,tvPkg4,tvPkg5,tvPkg6,tvPkg7,tvPkg8,tvPkg9,tvPkg10;
    TextView tvRate1,tvRate2,tvRate3,tvRate4,tvRate5,tvRate6,tvRate7,tvRate8,tvRate9,tvRate10;
    TextView tvCat1,tvCat2,tvCat3,tvCat4,tvCat5,tvCat6,tvCat7,tvCat8,tvCat9,tvCat10;
    LinearLayout ll1,ll2,ll3,ll4,ll5,ll6,ll7,ll8,ll9,ll10;
    View vw1,vw2,vw3,vw4,vw5,vw6,vw7,vw8,vw9,vw10;
    API REST_CLIENT;
    String city="Hyderabad";
    String companyID="CMP00001";
    Bundle b;
    String stCategorySelected;
    int j=0;
    String stFare;
    TextView tvNoPkg;
    boolean flag=true;
    String stCity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.alert_local_packages, container, false);
        btCancel=(Button)rootView.findViewById(R.id.alp_bt_cancel);

        tvPkg1=(TextView)rootView.findViewById(R.id.alp_tv_pkg1);
        tvPkg2=(TextView)rootView.findViewById(R.id.alp_tv_pkg2);
        tvPkg3=(TextView)rootView.findViewById(R.id.alp_tv_pkg3);
        tvPkg4=(TextView)rootView.findViewById(R.id.alp_tv_pkg4);
        tvPkg5=(TextView)rootView.findViewById(R.id.alp_tv_pkg5);
        tvPkg6=(TextView)rootView.findViewById(R.id.alp_tv_pkg6);
        tvPkg7=(TextView)rootView.findViewById(R.id.alp_tv_pkg7);
        tvPkg8=(TextView)rootView.findViewById(R.id.alp_tv_pkg8);
        tvPkg9=(TextView)rootView.findViewById(R.id.alp_tv_pkg9);
        tvPkg10=(TextView)rootView.findViewById(R.id.alp_tv_pkg10);

        tvRate1=(TextView)rootView.findViewById(R.id.alp_tv_rate1);
        tvRate2=(TextView)rootView.findViewById(R.id.alp_tv_rate2);
        tvRate3=(TextView)rootView.findViewById(R.id.alp_tv_rate3);
        tvRate4=(TextView)rootView.findViewById(R.id.alp_tv_rate4);
        tvRate5=(TextView)rootView.findViewById(R.id.alp_tv_rate5);
        tvRate6=(TextView)rootView.findViewById(R.id.alp_tv_rate6);
        tvRate7=(TextView)rootView.findViewById(R.id.alp_tv_rate7);
        tvRate8=(TextView)rootView.findViewById(R.id.alp_tv_rate8);
        tvRate9=(TextView)rootView.findViewById(R.id.alp_tv_rate9);
        tvRate10=(TextView)rootView.findViewById(R.id.alp_tv_rate10);

        tvCat1=(TextView)rootView.findViewById(R.id.alp_tv_cab1);
        tvCat2=(TextView)rootView.findViewById(R.id.alp_tv_cab2);
        tvCat3=(TextView)rootView.findViewById(R.id.alp_tv_cab3);
        tvCat4=(TextView)rootView.findViewById(R.id.alp_tv_cab4);
        tvCat5=(TextView)rootView.findViewById(R.id.alp_tv_cab5);
        tvCat6=(TextView)rootView.findViewById(R.id.alp_tv_cab6);
        tvCat7=(TextView)rootView.findViewById(R.id.alp_tv_cab7);
        tvCat8=(TextView)rootView.findViewById(R.id.alp_tv_cab8);
        tvCat9=(TextView)rootView.findViewById(R.id.alp_tv_cab9);
        tvCat10=(TextView)rootView.findViewById(R.id.alp_tv_cab10);

        ll1=(LinearLayout)rootView.findViewById(R.id.alp_ll1);
        ll2=(LinearLayout)rootView.findViewById(R.id.alp_ll2);
        ll3=(LinearLayout)rootView.findViewById(R.id.alp_ll3);
        ll4=(LinearLayout)rootView.findViewById(R.id.alp_ll4);
        ll5=(LinearLayout)rootView.findViewById(R.id.alp_ll5);
        ll6=(LinearLayout)rootView.findViewById(R.id.alp_ll6);
        ll7=(LinearLayout)rootView.findViewById(R.id.alp_ll7);
        ll8=(LinearLayout)rootView.findViewById(R.id.alp_ll8);
        ll9=(LinearLayout)rootView.findViewById(R.id.alp_ll9);
        ll10=(LinearLayout)rootView.findViewById(R.id.alp_ll10);

        tvNoPkg=(TextView)rootView.findViewById(R.id.alp_tv_no_pkg);

        vw2=(View)rootView.findViewById(R.id.alpl_vw2);
        vw3=(View)rootView.findViewById(R.id.alpl_vw3);
        vw4=(View)rootView.findViewById(R.id.alpl_vw4);
        vw5=(View)rootView.findViewById(R.id.alpl_vw5);
        vw6=(View)rootView.findViewById(R.id.alpl_vw6);
        vw7=(View)rootView.findViewById(R.id.alpl_vw7);
        vw8=(View)rootView.findViewById(R.id.alpl_vw8);
        vw9=(View)rootView.findViewById(R.id.alpl_vw9);
        vw10=(View)rootView.findViewById(R.id.alpl_vw10);

        b=getActivity().getIntent().getExtras();

        stCategorySelected=b.getString("cabCat");
        stCity=b.getString("city");

        REST_CLIENT= RestClient.get();

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        Call<List<LocalPackagesPojo>> call=REST_CLIENT.getLocalPackages(stCity,companyID);
        call.enqueue(new Callback<List<LocalPackagesPojo>>() {
            @Override
            public void onResponse(Call<List<LocalPackagesPojo>> call, Response<List<LocalPackagesPojo>> response) {

                LocalPackagesPojo localData;
                List<LocalPackagesPojo> localDataList;

                if(response.isSuccessful())
                {
                    progressDialog.dismiss();
                    localDataList=response.body();

                    for(int i=0;i<localDataList.size();i++) {

                        localData = localDataList.get(i);

                        if (stCategorySelected.equals(localData.getVehcategory())) {

                            flag=false;
                            tvNoPkg.setText("Packages For "+stCategorySelected);
                            switch (j) {
                                case 0:
                                    ll1.setVisibility(View.VISIBLE);
                                    tvPkg1.setText(localData.getSlabhours() + " hr" + " / " + localData.getSlabkms() + " km");
                                    tvRate1.setText(getString(R.string.Rs) + " " + localData.getSlabrate());
                                    stFare=localData.getSlabrate();
                                    tvCat1.setText(localData.getVehcategory());
                                    j++;
                                    break;
                                case 1:
                                    ll2.setVisibility(View.VISIBLE);
                                    vw2.setVisibility(View.VISIBLE);
                                    tvPkg2.setText(localData.getSlabhours() + " hr" + " / " + localData.getSlabkms() + " km");
                                    tvRate2.setText(getString(R.string.Rs) + " " + localData.getSlabrate());
                                    stFare=localData.getSlabrate();

                                    tvCat2.setText(localData.getVehcategory());
                                    j++;

                                    break;
                                case 2:
                                    ll3.setVisibility(View.VISIBLE);
                                    vw3.setVisibility(View.VISIBLE);
                                    tvPkg3.setText(localData.getSlabhours() + " hr" + " / " + localData.getSlabkms() + " km");
                                    tvRate3.setText(getString(R.string.Rs) + " " + localData.getSlabrate());
                                    stFare=localData.getSlabrate();

                                    tvCat3.setText(localData.getVehcategory());
                                    j++;

                                    break;
                                case 3:
                                    ll4.setVisibility(View.VISIBLE);
                                    vw4.setVisibility(View.VISIBLE);
                                    tvPkg4.setText(localData.getSlabhours() + " hr" + " / " + localData.getSlabkms() + " km");
                                    tvRate4.setText(getString(R.string.Rs) + " " + localData.getSlabrate());
                                    stFare=localData.getSlabrate();

                                    tvCat4.setText(localData.getVehcategory());
                                    j++;

                                    break;
                                case 4:
                                    ll5.setVisibility(View.VISIBLE);
                                    vw5.setVisibility(View.VISIBLE);
                                    tvPkg5.setText(localData.getSlabhours() + " hr" + " / " + localData.getSlabkms() + " km");
                                    tvRate5.setText(getString(R.string.Rs) + " " + localData.getSlabrate());
                                    stFare=localData.getSlabrate();

                                    tvCat5.setText(localData.getVehcategory());
                                    j++;

                                    break;
                                case 5:
                                    ll6.setVisibility(View.VISIBLE);
                                    vw6.setVisibility(View.VISIBLE);
                                    tvPkg6.setText(localData.getSlabhours() + " hr" + " / " + localData.getSlabkms() + " km");
                                    tvRate6.setText(getString(R.string.Rs) + " " + localData.getSlabrate());
                                    tvCat6.setText(localData.getVehcategory());
                                    j++;

                                    break;
                                case 6:
                                    ll7.setVisibility(View.VISIBLE);
                                    vw7.setVisibility(View.VISIBLE);
                                    tvPkg7.setText(localData.getSlabhours() + " hr" + " / " + localData.getSlabkms() + " km");
                                    tvRate7.setText(getString(R.string.Rs) + " " + localData.getSlabrate());
                                    stFare=localData.getSlabrate();

                                    tvCat7.setText(localData.getVehcategory());
                                    j++;

                                    break;
                                case 7:
                                    ll8.setVisibility(View.VISIBLE);
                                    vw8.setVisibility(View.VISIBLE);
                                    tvPkg8.setText(localData.getSlabhours() + " hr" + " / " + localData.getSlabkms() + " km");
                                    tvRate8.setText(getString(R.string.Rs) + " " + localData.getSlabrate());
                                    stFare=localData.getSlabrate();

                                    tvCat8.setText(localData.getVehcategory());
                                    j++;

                                    break;
                                case 8:
                                    ll9.setVisibility(View.VISIBLE);
                                    vw9.setVisibility(View.VISIBLE);
                                    tvPkg9.setText(localData.getSlabhours() + " hr" + " / " + localData.getSlabkms() + " km");
                                    tvRate9.setText(getString(R.string.Rs) + " " + localData.getSlabrate());
                                    stFare=localData.getSlabrate();

                                    tvCat9.setText(localData.getVehcategory());
                                    j++;

                                    break;
                                case 9:
                                    ll10.setVisibility(View.VISIBLE);
                                    vw10.setVisibility(View.VISIBLE);
                                    tvPkg10.setText(localData.getSlabhours() + " hr" + " / " + localData.getSlabkms() + " km");
                                    tvRate10.setText(getString(R.string.Rs) + " " + localData.getSlabrate());
                                    stFare=localData.getSlabrate();

                                    tvCat10.setText(localData.getVehcategory());
                                    j++;

                                    break;
                            }
                        }
                    }

                    if(flag)
                    {
                        tvNoPkg.setText("No Packages Available for "+stCategorySelected);
                        btCancel.setVisibility(View.GONE);
                    }

                }
                else {

                    progressDialog.dismiss();
                    tvNoPkg.setText("No Packages Available for "+stCategorySelected);
                    btCancel.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Call<List<LocalPackagesPojo>> call, Throwable t) {

                progressDialog.dismiss();

                Toast.makeText(getActivity(),"Please check Internet Connection!",Toast.LENGTH_LONG).show();

            }
        });

        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.putExtra("localpkg",tvPkg1.getText().toString().trim());
                i.putExtra("fare",tvRate1.getText().toString());
                getActivity().setResult(3, i);
                getActivity().finish();
            }
        });

        ll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.putExtra("localpkg",tvPkg2.getText().toString().trim());
                i.putExtra("fare",tvRate2.getText().toString());
                getActivity().setResult(3, i);
                getActivity().finish();
            }
        });

        ll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.putExtra("localpkg",tvPkg3.getText().toString().trim());
                i.putExtra("fare",tvRate3.getText().toString());
                getActivity().setResult(3, i);
                getActivity().finish();
            }
        });

        ll4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.putExtra("localpkg",tvPkg4.getText().toString().trim());
                i.putExtra("fare",tvRate4.getText().toString());
                getActivity().setResult(3, i);
                getActivity().finish();
            }
        });

        ll5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.putExtra("localpkg",tvPkg5.getText().toString().trim());
                i.putExtra("fare",tvRate5.getText().toString());
                getActivity().setResult(3, i);
                getActivity().finish();
            }
        });

        ll6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.putExtra("localpkg",tvPkg6.getText().toString().trim());
                i.putExtra("fare",tvRate6.getText().toString());
                getActivity().setResult(3, i);
                getActivity().finish();
            }
        });

        ll7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.putExtra("localpkg",tvPkg7.getText().toString().trim());
                i.putExtra("fare",tvRate7.getText().toString());
                getActivity().setResult(3, i);
                getActivity().finish();
            }
        });

        ll8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.putExtra("localpkg",tvPkg8.getText().toString().trim());
                i.putExtra("fare",tvRate8.getText().toString());
                getActivity().setResult(3, i);
                getActivity().finish();
            }
        });

        ll9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.putExtra("localpkg",tvPkg9.getText().toString().trim());
                i.putExtra("fare",tvRate9.getText().toString());
                getActivity().setResult(3, i);
                getActivity().finish();
            }
        });

        ll10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.putExtra("localpkg",tvPkg10.getText().toString().trim());
                i.putExtra("fare",tvRate10.getText().toString());
                getActivity().setResult(3, i);
                getActivity().finish();
            }
        });


        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.putExtra("localpkg","");
                getActivity().setResult(3, i);
                getActivity().finish();
            }
        });

        return rootView;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
