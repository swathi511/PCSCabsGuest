package com.hjsoft.guestbooktaxi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjsoft.guestbooktaxi.R;
import com.hjsoft.guestbooktaxi.model.WalletData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by hjsoft on 3/2/18.
 */
public class WalletRecyclerAdapter extends RecyclerView.Adapter<WalletRecyclerAdapter.MyViewHolder> {

    Context context;
    LayoutInflater inflater;
    Geocoder geocoder;
    RecyclerView rview;
    ArrayList<WalletData> mResultList;
    WalletData data;
    int pos;


    public WalletRecyclerAdapter(Context context, ArrayList<WalletData> mResultList, RecyclerView rview)
    {

        this.context=context;
        this.mResultList=mResultList;
        this.rview=rview;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        geocoder=new Geocoder(context, Locale.getDefault());

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_wallet, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        data=mResultList.get(position);
        holder.tvAmount.setText(context.getString(R.string.Rs)+" "+data.getAmount());
       // holder.tvDate.setText(data.getDate());
        String d[]=data.getDesc().split("-");



        if(d.length>1) {
            System.out.println("*** "+d[0]+":"+d[1]);
            holder.tvBid.setText(d[1]);
            holder.tvDesc.setText(d[0]);
        }
        else {

            holder.tvDesc.setText(d[0]);
            holder.tvDesc.setAllCaps(true);
        }

        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);
            //String date = dateFormat.format(data.getDate());
            String format = new SimpleDateFormat("MMM d, yy   HH:mm a", Locale.ENGLISH).format(dateFormat.parse(data.getDate()));
            holder.tvDate.setText(format);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        // holder.tvTripId.setText("TRIP ID"+data.getRequestId());

    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvAmount,tvDate,tvDesc,tvBid;

        public MyViewHolder(final View itemView) {
            super(itemView);

            tvAmount=(TextView)itemView.findViewById(R.id.rw_tv_amount);
            tvDate=(TextView)itemView.findViewById(R.id.rw_tv_date);
            tvDesc=(TextView)itemView.findViewById(R.id.rw_tv_desc);
            tvBid=(TextView)itemView.findViewById(R.id.rw_tv_bid);

        }


    }





}
