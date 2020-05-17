package com.gates.solutions.miniproject;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class MyPost_Fragment extends Fragment {

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("uploads");
    public RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mypost_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_id);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Sales_Items> options =
                new FirebaseRecyclerOptions.Builder<Sales_Items>()
                        .setQuery(reference,Sales_Items.class).build();

        FirebaseRecyclerAdapter<Sales_Items,myPostViewHolder> adapter = new FirebaseRecyclerAdapter<Sales_Items, myPostViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull myPostViewHolder holder, int position, @NonNull final Sales_Items model) {
                String amount = currencyFormat(model.getPrice());
                holder.location_txt.setText(model.getLocation());
                holder.price_txt.setText("GHC "+amount);
                holder.type.setText(model.getType());
             //   holder.time.setText(model.getTime());
                Picasso.get().load(model.getFirst_Image_Url()).into(holder.imageView);

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("uploads").child(model.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseDatabase.getInstance().getReference().child("Rent Upload").child(model.getPid()).removeValue();
                                FirebaseDatabase.getInstance().getReference().child("Sales Upload").child(model.getPid()).removeValue();
                                Toast.makeText(getContext(),"Post deleted successfully",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {

                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"Deletion failed",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }


            @NonNull
            @Override
            public myPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.item,parent,false);
                myPostViewHolder holder = new myPostViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }
}
