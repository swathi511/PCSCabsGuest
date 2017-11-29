package com.hjsoft.guestbooktaxi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hjsoft.guestbooktaxi.R;

/**
 * Created by hjsoft on 17/12/16.
 */
public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {

    String mString;
    View v;

    public static MyBottomSheetDialogFragment newInstance(String string) {
        MyBottomSheetDialogFragment f = new MyBottomSheetDialogFragment();
        Bundle args = new Bundle();
        args.putString("string", string);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mString = getArguments().getString("string");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.layout_modal_bottom_sheet, container, false);
        TextView tv = (TextView) v.findViewById(R.id.text);

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}