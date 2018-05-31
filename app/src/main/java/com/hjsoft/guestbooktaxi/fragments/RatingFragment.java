package com.hjsoft.guestbooktaxi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.SessionManager;
import com.hjsoft.guestbooktaxi.model.BookCabPojo;
import com.hjsoft.guestbooktaxi.webservices.API;
import com.hjsoft.guestbooktaxi.webservices.RestClient;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 12/2/18.
 */
public class RatingFragment extends Fragment {

    View v;
    double otherCharges=0;
    RatingBar rbStars;
    String guestProfileId;
    HashMap<String, String> user;
    SessionManager session;
    int rating=0;
    TextView tvRatingText,tvSubmit;
    EditText etNote;
    String requestId;
    String companyId="CMP00001";
    API REST_CLIENT;
    Bundle b;
    boolean r=false;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       v=inflater.inflate(R.layout.activity_rating, container, false);
        tvSubmit=(TextView)v.findViewById(R.id.ar_tv_ok);
        rbStars=(RatingBar)v.findViewById(R.id.ar_rb_stars);
        etNote=(EditText)v.findViewById(R.id.ar_et_note);
        tvRatingText=(TextView)v.findViewById(R.id.ar_tv_rating_text);

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(r) {

                    String note = etNote.getText().toString().trim();
                    System.out.println("@@@@@@@@@@@@" + note);

                    JsonObject v1 = new JsonObject();
                    v1.addProperty("profileid", guestProfileId);
                    v1.addProperty("reqid", requestId);
                    v1.addProperty("rating", rating);
                    v1.addProperty("companyid", companyId);
                    v1.addProperty("remarks", etNote.getText().toString().trim());

                    Call<BookCabPojo> call = REST_CLIENT.sendRating(v1);
                    call.enqueue(new Callback<BookCabPojo>() {
                        @Override
                        public void onResponse(Call<BookCabPojo> call, Response<BookCabPojo> response) {

                            if (response.isSuccessful()) {
                                rbStars.setClickable(false);
                                rbStars.setEnabled(false);
                                etNote.setClickable(false);
                                etNote.setEnabled(false);
                                Toast.makeText(getActivity(), "Thanks for the rating!", Toast.LENGTH_SHORT).show();
                                tvSubmit.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(Call<BookCabPojo> call, Throwable t) {

                        }
                    });
                }
                else {
                    Toast.makeText(getActivity(),"Select your rating!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        rbStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                r=true;

                rating=(int)v;

                tvRatingText.setVisibility(View.VISIBLE);

                if(v==1)
                {
                    tvRatingText.setText("Terrible");
                }
                else if(v==2)
                {
                    tvRatingText.setText("Bad");
                }
                else if(v==3)
                {
                    tvRatingText.setText("Ok");
                }
                else if(v==4)
                {
                    tvRatingText.setText("Good");
                }
                else {
                    tvRatingText.setText("Excellent");
                }
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

        b=getActivity().getIntent().getExtras();

        requestId=b.getString("requestId");


    }
}
