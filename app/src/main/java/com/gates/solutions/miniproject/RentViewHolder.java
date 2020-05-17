package com.gates.solutions.miniproject;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import Interface.itemClickListener;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView location_txt,price_txt,time;
    public ImageView imageView;
    RecyclerView recyclerView;

    itemClickListener Listner;

    public RentViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.SalesHouseImage_id);
        location_txt = itemView.findViewById(R.id.salesLocation_id);
        price_txt = itemView.findViewById(R.id.salesPrice_id);
        time = itemView.findViewById(R.id.salesTime_id);
        recyclerView = itemView.findViewById(R.id.recycler_id);
    }

    public void setItemOnClicklistener(itemClickListener Listener)
    {
        this.Listner = Listener;
    }

    @Override
    public void onClick(View v) {
        Listner.onClick(v,getAdapterPosition(),false);
    }
}
