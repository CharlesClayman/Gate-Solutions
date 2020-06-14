package com.gates.solutions.miniproject;

import android.view.View;
import android.widget.TextView;

import Interface.itemClickListener;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class chat_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    CircleImageView chat_icon;
    TextView Username;

    itemClickListener Listner;

    public chat_ViewHolder(@NonNull View itemView) {
        super(itemView);
        chat_icon = itemView.findViewById(R.id.chat_item_pic);
        Username = itemView.findViewById(R.id.chat_item_name);
    }

    public void setItemOnClicklistener(itemClickListener Listener) {
        this.Listner = Listener;
    }

    @Override
    public void onClick(View v) {
        Listner.onClick(v, getAdapterPosition(), false);
    }
}
