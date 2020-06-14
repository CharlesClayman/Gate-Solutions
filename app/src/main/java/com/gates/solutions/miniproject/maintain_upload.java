package com.gates.solutions.miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;



public class maintain_upload extends AppCompatActivity implements View.OnClickListener {
    ImageView First_Image, Second_Image,Third_Image,Fourth_Image,Fifth_Image ;
    AppCompatEditText location_txt,amount_txt,tel_txt,description_txt;
    ViewFlipper viewFlipper;
    TextView next,previous;
    Button ApplyChanges_btn;
    Toolbar toolbar;
    public int imageCounter=0;
    private String itemID_Post="",uplaod_type;
    BottomNavigationView bottomNavigationView;
    ScrollView maintenance_page;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_upload);

        location_txt = findViewById(R.id.mypost_location_id);
        amount_txt = findViewById(R.id.mypost_amount_id);
        tel_txt = findViewById(R.id.mypost_tel_id);
        description_txt = findViewById(R.id.mypost_desc_id);
        ApplyChanges_btn = findViewById(R.id.Post_Btn_id);
        next = findViewById(R.id.rent_forward_id);
        previous = findViewById(R.id.rent_backward_id);
        viewFlipper = findViewById(R.id.rent_ImageFlipper_id);
        bottomNavigationView = findViewById(R.id.buttomNav_id);
        toolbar = findViewById(R.id.maintain_toolbar);
        maintenance_page = findViewById(R.id.maintenance_page);

        setSupportActionBar(toolbar);


        if(isNetworkAvailable() == false)
        {
            Snackbar.make(maintenance_page,"No internet connection",Snackbar.LENGTH_LONG).show();
        }

        next.setOnClickListener(this);
        previous.setOnClickListener(this);

        itemID_Post = getIntent().getStringExtra("post_pid");
        getProductDetails(itemID_Post);

        ApplyChanges_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable() == false)
                {
                    Snackbar.make(maintenance_page,"No internet connection",Snackbar.LENGTH_LONG).show();
                }else {

                if (location_txt.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No location specified", Toast.LENGTH_SHORT).show();
                } else if (amount_txt.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No amount specified", Toast.LENGTH_SHORT).show();
                } else if (tel_txt.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No telephone number specified", Toast.LENGTH_SHORT).show();
                } else if (description_txt.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No description specified", Toast.LENGTH_SHORT).show();
                } else {
                    //my post
                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("uploads").child(itemID_Post).child("Telephone").setValue(tel_txt.getText().toString());
                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("uploads").child(itemID_Post).child("Price").setValue(amount_txt.getText().toString());
                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("uploads").child(itemID_Post).child("Description").setValue(description_txt.getText().toString());
                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("uploads").child(itemID_Post).child("Location").setValue(location_txt.getText().toString());
                    switch (uplaod_type) {
                        case "Sales Upload":
                            FirebaseDatabase.getInstance().getReference().child("Sales Upload").child(itemID_Post).child("Telephone").setValue(tel_txt.getText().toString());
                            FirebaseDatabase.getInstance().getReference().child("Sales Upload").child(itemID_Post).child("Price").setValue(amount_txt.getText().toString());
                            FirebaseDatabase.getInstance().getReference().child("Sales Upload").child(itemID_Post).child("Description").setValue(description_txt.getText().toString());
                            FirebaseDatabase.getInstance().getReference().child("Sales Upload").child(itemID_Post).child("Location").setValue(location_txt.getText().toString());
                            break;
                        case "Rent Upload":
                            FirebaseDatabase.getInstance().getReference().child("Rent Upload").child(itemID_Post).child("Telephone").setValue(tel_txt.getText().toString());
                            FirebaseDatabase.getInstance().getReference().child("Rent Upload").child(itemID_Post).child("Price").setValue(amount_txt.getText().toString());
                            FirebaseDatabase.getInstance().getReference().child("Rent Upload").child(itemID_Post).child("Description").setValue(description_txt.getText().toString());
                            FirebaseDatabase.getInstance().getReference().child("Rent Upload").child(itemID_Post).child("Location").setValue(location_txt.getText().toString());
                            break;
                    }

                    Toast.makeText(getApplicationContext(), "Post updated successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(maintain_upload.this, HomeActivity.class);
                    intent.putExtra("update", "1");
                    startActivity(intent);

                }
            }
            }
        });



    }

    private void getProductDetails(String itemID)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("uploads");
        reference.child(itemID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isNetworkAvailable() == false)
                {
                    Snackbar.make(maintenance_page,"No internet connection",Snackbar.LENGTH_LONG).show();
                }
                if(dataSnapshot.exists())
                {
                    final Sales_Items sales_items = dataSnapshot.getValue(Sales_Items.class);
                    location_txt.setText(sales_items.getLocation());
                    tel_txt.setText(sales_items.getTelephone());
                    description_txt.setText(sales_items.getDescription());
                    amount_txt.setText(sales_items.getPrice());
                    uplaod_type = sales_items.getType();

                    if(sales_items.getFirst_Image_Url() != null)
                    {
                      First_Image = new ImageView(getApplicationContext());
                      Picasso.get().load(sales_items.getFirst_Image_Url()).into(First_Image);
                      viewFlipper.addView(First_Image);
                        imageCounter++;
                    }

                    if(sales_items.getSecond_Image_Url() != null)
                    {
                       Second_Image = new ImageView(getApplicationContext());
                       Picasso.get().load(sales_items.getSecond_Image_Url()).into(Second_Image);
                        viewFlipper.addView(Second_Image);

                        imageCounter++;
                    }

                    if(sales_items.getThird_Image_Url() != null)
                    {
                        Third_Image = new ImageView(getApplicationContext());
                        Picasso.get().load(sales_items.getSecond_Image_Url()).into(Third_Image);
                        viewFlipper.addView(Third_Image);
                        imageCounter++;
                    }

                    if(sales_items.getFourth_Image_Url() != null)
                    {
                        Fourth_Image = new ImageView(getApplicationContext());
                        Picasso.get().load(sales_items.getSecond_Image_Url()).into(Fourth_Image);
                        viewFlipper.addView(Fourth_Image);
                        imageCounter++;
                    }

                    if(sales_items.getFifth_Image_Url() != null)
                    {
                        Fifth_Image = new ImageView(getApplicationContext());
                        Picasso.get().load(sales_items.getSecond_Image_Url()).into(Fifth_Image);
                        viewFlipper.addView(Fifth_Image);
                        imageCounter++;
                    }

                   // Toast.makeText(getApplicationContext(),imageCounter,Toast.LENGTH_SHORT).show();
                    if (imageCounter <= 1)
                    {
                        previous.setVisibility(View.INVISIBLE);
                        next.setVisibility(View.INVISIBLE);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        if(v == next)
        {
            viewFlipper.showNext();
        }
        else if(v == previous)
        {
            viewFlipper.showPrevious();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
