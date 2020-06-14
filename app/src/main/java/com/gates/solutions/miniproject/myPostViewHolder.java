package com.gates.solutions.miniproject;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import Interface.itemClickListener;
import androidx.recyclerview.widget.RecyclerView;

public class myPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView location_txt,price_txt,type,delete,time;
    public ImageView imageView;
    RecyclerView recyclerView;
    itemClickListener Listner;

    public myPostViewHolder(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.SalesHouseImage_id);
        location_txt = itemView.findViewById(R.id.salesLocation_id);
        price_txt = itemView.findViewById(R.id.salesPrice_id);
        recyclerView = itemView.findViewById(R.id.recycler_id);
        type = itemView.findViewById(R.id.textview);
        delete = itemView.findViewById(R.id.myPostDelete_id);
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
