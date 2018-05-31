package com.hjsoft.guestbooktaxi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.SessionManager;
import com.hjsoft.guestbooktaxi.adapter.RecyclerAdapter;
import com.hjsoft.guestbooktaxi.adapter.WalletRecyclerAdapter;
import com.hjsoft.guestbooktaxi.model.WalletData;
import com.hjsoft.guestbooktaxi.model.WalletDataPojo;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 3/2/18.
 */
public class WalletHistoryFragment extends Fragment{

    View v;
    RecyclerView rView;
    WalletRecyclerAdapter mAdapter;
    SessionManager session;
    HashMap<String, String> user;
    String guestProfileId;
    ImageView ivRetry;
    API REST_CLIENT;
    String companyId="CMP00001";
    ArrayList<WalletData> walletDataList=new ArrayList<>();
    TextView tvNoWalletHistory;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v=inflater.inflate(R.layout.activity_wallet_history, container, false);
        rView=(RecyclerView) v.findViewById(R.id.awh_rview);
        ivRetry=(ImageView)v.findViewById(R.id.awh_iv_retry);
        tvNoWalletHistory=(TextView)v.findViewById(R.id.awh_tv_no_wh);
        tvNoWalletHistory.setVisibility(View.GONE);

        getWalletHistory();

        ivRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getWalletHistory();
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session=new SessionManager(getActivity());

        user=session.getUserDetails();

        guestProfileId=user.get(SessionManager.KEY_PROFILE_ID);
        REST_CLIENT= RestClient.get();
    }

    public void getWalletHistory()
    {

        tvNoWalletHistory.setVisibility(View.GONE);
        ivRetry.setVisibility(View.GONE);

        System.out.println();

        Call<ArrayList<WalletDataPojo>> call=REST_CLIENT.getWalletHistory(guestProfileId,companyId);
        call.enqueue(new Callback<ArrayList<WalletDataPojo>>() {
            @Override
            public void onResponse(Call<ArrayList<WalletDataPojo>> call, Response<ArrayList<WalletDataPojo>> response) {

                ArrayList<WalletDataPojo> dataList;
                WalletDataPojo data;

                if(response.isSuccessful())
                {
                    dataList=response.body();

                    for(int i=0;i<dataList.size();i++)
                    {
                        data=dataList.get(i);

                        walletDataList.add(new WalletData(data.getAmount(),data.getCreationdatetime(),data.getDescription()));
                    }

                   // walletDataList.add(new WalletData("200","2/3/2018 5:26:51 PM","Deducted money from wallet for the booking"));


                }


                if(walletDataList.size()!=0)
                {
                    mAdapter = new WalletRecyclerAdapter(getActivity(),walletDataList, rView);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    rView.setLayoutManager(mLayoutManager);
                    rView.setItemAnimator(new DefaultItemAnimator());
                    rView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                else
                {
                    tvNoWalletHistory.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<ArrayList<WalletDataPojo>> call, Throwable t) {

                Toast.makeText(getActivity(),"Check Internet connection!",Toast.LENGTH_SHORT).show();
                ivRetry.setVisibility(View.VISIBLE);

            }
        });

    }
}
