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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyPost_Fragment extends Fragment {

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("uploads");
    public RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    TextView no_post;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.mypost_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.myPost_progress_id);
        progressBar.setVisibility(View.VISIBLE);
        no_post = view.findViewById(R.id.no_post_id);
        recyclerView = view.findViewById(R.id.recycler_id);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

    }
    public void SearchForItem_Location(String searchText)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("uploads");
        FirebaseRecyclerOptions<Sales_Items> options =
                new FirebaseRecyclerOptions.Builder<Sales_Items>().setQuery(reference.orderByChild("Location").startAt(searchText).endAt(searchText),Sales_Items.class).build();

        FirebaseRecyclerAdapter<Sales_Items,myPostViewHolder> adapter = new FirebaseRecyclerAdapter<Sales_Items, myPostViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull myPostViewHolder holder, int position, @NonNull final Sales_Items model) {
                String amount = currencyFormat(model.getPrice());
                holder.location_txt.setText(model.getLocation());
                holder.price_txt.setText("GHC "+amount);
                holder.type.setText(model.getType());
                //   holder.time.setText(model.getTime());
                Picasso.get().load(model.getFirst_Image_Url()).into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(),maintain_upload.class);
                        intent.putExtra("post_pid",model.getPid());
                        startActivity(intent);
                    }
                });

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

    public void SearchForItem_Price(String searchText)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("uploads");
        FirebaseRecyclerOptions<Sales_Items> options =
                new FirebaseRecyclerOptions.Builder<Sales_Items>().setQuery(reference.orderByChild("Price").startAt(searchText).endAt(searchText),Sales_Items.class).build();

        FirebaseRecyclerAdapter<Sales_Items,myPostViewHolder> adapter = new FirebaseRecyclerAdapter<Sales_Items, myPostViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull myPostViewHolder holder, int position, @NonNull final Sales_Items model) {
                String amount = currencyFormat(model.getPrice());
                holder.location_txt.setText(model.getLocation());
                holder.price_txt.setText("GHC "+amount);
                holder.type.setText(model.getType());
                //   holder.time.setText(model.getTime());
                Picasso.get().load(model.getFirst_Image_Url()).into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(),maintain_upload.class);
                        intent.putExtra("post_pid",model.getPid());
                        startActivity(intent);
                    }
                });

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
                no_post.setVisibility(View.INVISIBLE);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(),maintain_upload.class);
                        intent.putExtra("post_pid",model.getPid());
                        startActivity(intent);
                    }
                });

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
