package com.gates.solutions.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class Chat_Fragment extends Fragment {
    public RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    DatabaseReference chat_ref = FirebaseDatabase.getInstance().getReference().child("Chats");
   private DatabaseReference chat_list_ref;

   List<String> mUsers;
   FirebaseUser fuser;
   DatabaseReference reference;
   List<String> userslist;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.chat_recycler_id);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager( getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        userslist = new ArrayList<>();

       /* reference = FirebaseDatabase.getInstance().getReference().child("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userslist.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Chat chat = snapshot.getValue(Chat.class);

                    if(chat.getSender().equals(fuser.getUid()))
                    {
                        userslist.add(chat.getReceiver());
                    }

                    if(chat.getReceiver().equals(fuser.getUid()))
                    {
                        userslist.add(chat.getSender());
                    }

                    readChats();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }
   /*
    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Sales_Items> options =
                new FirebaseRecyclerOptions.Builder<Sales_Items>()
                        .setQuery(chat_list_ref,Sales_Items.class).build();

        FirebaseRecyclerAdapter<Sales_Items,chat_ViewHolder> adapter = new FirebaseRecyclerAdapter<Sales_Items,chat_ViewHolder>(options){

            @NonNull
            @Override
            public chat_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.chat_item,parent,false);
                chat_ViewHolder holder = new chat_ViewHolder(view);
                return holder;
            }

            @Override
            protected void onBindViewHolder(@NonNull chat_ViewHolder holder, int position, @NonNull final Sales_Items model) {

                holder.Username.setText(model.getUsername());
                Picasso.get().load(model.getProfile_Image_Url()).into(holder.chat_icon);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(),messageActivity.class);
                        intent.putExtra("chat_pid",model.getPid());
                        startActivity(intent);
                    }
                });
            }
        };


        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    */
    private void readChats()
    {
        mUsers = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              /*  for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    User user = snapshot.getValue(User.class);
                    for(String id : userslist)
                    {
                        if(user.getId().equals(id))
                        {
                            if(mUsers.size() != 0)
                            {
                                for(User user1 : mUsers)
                                {
                                    if(!user.getId().equals(user1.getId()))
                                    {
                                        mUsers.add(user);
                                    }
                                }
                            }else {
                                mUsers.add(user);
                            }
                        }
                    }
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
