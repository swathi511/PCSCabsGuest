package com.hjsoft.guestbooktaxi.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hjsoft.guestbooktaxi.R;

/**
 * Created by hjsoft on 16/12/16.
 */
public class ProfileFragment extends Fragment {

    TextView tvSave;
    ImageButton ibUsername,ibPassword,ibMobileNumber;
    Button btLogout;
    EditText tvUsername,tvPassword,tvMobileNumber;
    AlertDialog alertDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        tvUsername=(EditText)rootView.findViewById(R.id.fp_tv_username);
        tvPassword=(EditText) rootView.findViewById(R.id.fp_tv_password);
        tvMobileNumber=(EditText) rootView.findViewById(R.id.fp_tv_mobile_number);
        ibUsername=(ImageButton)rootView.findViewById(R.id.fp_ib_username);
        ibPassword=(ImageButton)rootView.findViewById(R.id.fp_ib_password);
        ibMobileNumber=(ImageButton)rootView.findViewById(R.id.fp_ib_mobile_number);
        tvSave=(TextView)rootView.findViewById(R.id.fp_tv_save);
        btLogout=(Button)rootView.findViewById(R.id.fp_bt_logout);

        tvUsername.setCursorVisible(false);
        tvPassword.setCursorVisible(false);
        tvMobileNumber.setCursorVisible(false);

        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // do nothingSimpleDateFormat
                tvUsername.setFocusable(false);
                tvUsername.setFocusableInTouchMode(false);
                InputMethodManager inputMethodManager =
                        (InputMethodManager)getActivity().getSystemService(
                                Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(
                        getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        });

        tvPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvPassword.setFocusable(false);
                tvPassword.setFocusableInTouchMode(false);
                InputMethodManager inputMethodManager =
                        (InputMethodManager)getActivity().getSystemService(
                                Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(
                        getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        });

        tvMobileNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvMobileNumber.setFocusable(false);
                tvMobileNumber.setFocusableInTouchMode(false);
                InputMethodManager inputMethodManager =
                        (InputMethodManager)getActivity().getSystemService(
                                Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(
                        getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        });

        ibUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvUsername.setText("");

                tvUsername.setCursorVisible(true);
                tvUsername.setFocusableInTouchMode(true);
              //  tvUsername.setInputType(InputType.TYPE_CLASS_TEXT);
                tvUsername.requestFocus();
             //   tvUsername.setFocusable(true);
                final InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(tvUsername, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        ibPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //tvPassword.setText("");
                /*
                tvPassword.setCursorVisible(true);
                tvPassword.setFocusableInTouchMode(true);
                //  tvUsername.setInputType(InputType.TYPE_CLASS_TEXT);
                tvPassword.requestFocus();
                tvPassword.setFocusable(true);
                final InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(tvPassword, InputMethodManager.SHOW_IMPLICIT);*/

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(view.getContext());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_change_password, null);
                dialogBuilder.setView(dialogView);

                alertDialog = dialogBuilder.create();
                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);

                final TextView tvNPwd=(TextView)dialogView.findViewById(R.id.ap_et_new_password);
                final TextView tvNConfirmPwd=(TextView)dialogView.findViewById(R.id.ap_et_new_c_password);

                Button save=(Button)dialogView.findViewById(R.id.ap_bt_save);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final String stNPwd=tvNPwd.getText().toString().trim();
                        final String stNCPwd=tvNConfirmPwd.getText().toString().trim();

                        if(stNPwd.equals(stNCPwd))
                        {
                            tvPassword.setText(stNPwd);
                            //tvPassword.setText("hello");
                            alertDialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(getActivity(),"New and Confirm Password should be same",Toast.LENGTH_LONG).show();
                            tvNPwd.setText("");
                            tvNConfirmPwd.setText("");
                        }
                    }
                });
            }
        });

        ibMobileNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvMobileNumber.setText("");

                tvMobileNumber.setCursorVisible(true);
                tvMobileNumber.setFocusableInTouchMode(true);
                //  tvUsername.setInputType(InputType.TYPE_CLASS_TEXT);
                tvMobileNumber.requestFocus();
                tvMobileNumber.setFocusable(true);
                final InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(tvMobileNumber, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvUsername.setCursorVisible(false);
                tvPassword.setCursorVisible(false);
                tvMobileNumber.setCursorVisible(false);
            }
        });

        return  rootView;
    }
}
