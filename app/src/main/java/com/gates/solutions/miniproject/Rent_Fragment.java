package com.gates.solutions.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class Rent_Fragment extends Fragment {
    public DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Rent Upload");
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public Toast toast;
    TextView no_rent;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rent_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.rent_progress_id);
        progressBar.setVisibility(View.VISIBLE);
        no_rent = view.findViewById(R.id.no_rent_id);
        recyclerView = view.findViewById(R.id.recycler_id);
        recyclerView.setHasFixedSize(true);
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

    }

    public void SearchForItem_Location(String searchText)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Rent Upload");
        FirebaseRecyclerOptions<Sales_Items> options = new FirebaseRecyclerOptions.Builder<Sales_Items>().setQuery(reference.orderByChild("Location").startAt(searchText).endAt(searchText),Sales_Items.class
        ).build();

        FirebaseRecyclerAdapter<Sales_Items, RentViewHolder> adapter = new
                FirebaseRecyclerAdapter<Sales_Items, RentViewHolder>(options) {
                    @NonNull
                    @Override
                    public RentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_item, parent, false);
                        RentViewHolder holder = new RentViewHolder(view);
                        return holder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull RentViewHolder holder, int position, @NonNull final Sales_Items model) {
                        String amount = currencyFormat(model.getPrice());
                        holder.location_txt.setText(model.getLocation());
                        holder.price_txt.setText("GHC " + amount);
                        holder.time.setText(model.getTime());
                        Picasso.get().load(model.getFirst_Image_Url()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), RentDetails.class);
                                intent.putExtra("rent_pid", model.getPid());
                                startActivity(intent);
                            }
                        });

                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    public void SearchForItem_Price(String searchText)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Rent Upload");
        FirebaseRecyclerOptions<Sales_Items> options = new FirebaseRecyclerOptions.Builder<Sales_Items>().setQuery(reference.orderByChild("Price").startAt(searchText).endAt(searchText),Sales_Items.class
        ).build();

        FirebaseRecyclerAdapter<Sales_Items, RentViewHolder> adapter = new
                FirebaseRecyclerAdapter<Sales_Items, RentViewHolder>(options) {
                    @NonNull
                    @Override
                    public RentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_item, parent, false);
                        RentViewHolder holder = new RentViewHolder(view);
                        return holder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull RentViewHolder holder, int position, @NonNull final Sales_Items model) {
                        String amount = currencyFormat(model.getPrice());
                        holder.location_txt.setText(model.getLocation());
                        holder.price_txt.setText("GHC " + amount);
                        holder.time.setText(model.getTime());
                        Picasso.get().load(model.getFirst_Image_Url()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), RentDetails.class);
                                intent.putExtra("rent_pid", model.getPid());
                                startActivity(intent);
                            }
                        });

                    }

                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Sales_Items> options = new FirebaseRecyclerOptions.Builder<Sales_Items>()
                .setQuery(reference, Sales_Items.class).build();

        FirebaseRecyclerAdapter<Sales_Items,RentViewHolder> adapter = new FirebaseRecyclerAdapter<Sales_Items, RentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RentViewHolder holder, int position, @NonNull final Sales_Items model) {
                String amount = currencyFormat(model.getPrice());
                holder.location_txt.setText(model.getLocation());
                holder.price_txt.setText("GHC "+amount);
                holder.time.setText(model.getTime());
                Picasso.get().load(model.getFirst_Image_Url()).into(holder.imageView);

                no_rent.setVisibility(View.INVISIBLE);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(),RentDetails.class);
                        intent.putExtra("rent_pid",model.getPid());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public RentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.sales_item,parent,false);
                RentViewHolder holder = new RentViewHolder(view);
                return holder;
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
    }
    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

}
