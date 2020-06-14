package com.gates.solutions.miniproject;

import chat_section.MessageActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;


import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.DecimalFormat;


public class SalesDetails extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    TextView poster,location,tel,postTime,desc,Price,next,previous;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Sales Upload");
    LinearLayout call_layout;
    private String itemID_sales="",TelNum="",poster_id;
    ImageView imageView1,imageView2,imageView3,imageView4,imageView5;
    public int imageCounter=0;
    ViewFlipper viewFlipper;
    LinearLayout chat,call_chat_layout;
    RelativeLayout salesDetails_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_details);
        toolbar = findViewById(R.id.detail_Toolbar_id);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        poster = findViewById(R.id.detailsWhoPosted_id);
        location = findViewById(R.id.details_location_id);
        tel = findViewById(R.id.detail_Tel_id);
        postTime = findViewById(R.id.detailsTimePosted_id);
        desc = findViewById(R.id.detailsDescription_id);
        Price = findViewById(R.id.detailsPrice_id);
        next = findViewById(R.id.forward_id);
        previous = findViewById(R.id.backward_id);
        viewFlipper = findViewById(R.id.ImageFlipper_id);
        chat = findViewById(R.id.chat_id);
        call_chat_layout = findViewById(R.id.rent_call_id);
        salesDetails_page = findViewById(R.id.salesDetails_id);

        if(isNetworkAvailable() == false)
        {
            Snackbar.make(salesDetails_page,"No internet connection",Snackbar.LENGTH_LONG).show();
        }

        next.setOnClickListener(this);
        previous.setOnClickListener(this);

        itemID_sales = getIntent().getStringExtra("sales_pid");
        getProductDetails(itemID_sales);




    }


    private void getProductDetails(String itemID)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Sales Upload");
        reference.child(itemID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(isNetworkAvailable() == false)
                {
                    Snackbar.make(salesDetails_page,"No internet connection",Snackbar.LENGTH_LONG).show();
                }
                if(dataSnapshot.exists())
                {
                    final Sales_Items sales_items = dataSnapshot.getValue(Sales_Items.class);
                    location.setText("Location: "+sales_items.getLocation());
                    tel.setText("Telephone: "+sales_items.getTelephone());
                    postTime.setText("Posted On: "+sales_items.getTime());
                    desc.setText(sales_items.getDescription());
                    tel.setText(sales_items.getTelephone());
                    Price.setText("GHC "+currencyFormat(sales_items.getPrice()));
                    poster_id = sales_items.getPoster_id();
                    poster.setText("Posted by: "+sales_items.getPosted_by());


                    if(poster_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    {
                        call_chat_layout.setVisibility(View.INVISIBLE);
                    }else{
                        chat.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                                intent.putExtra("userid",poster_id);
                                startActivity(intent);
                            }
                        });
                    }

                                if(sales_items.getFirst_Image_Url() != null)
                             {
                                 imageView1 = new ImageView(SalesDetails.this);
                                 Picasso.get().load(sales_items.getFirst_Image_Url()).into(imageView1);
                                 viewFlipper.addView(imageView1);
                                 imageCounter++;
                             }
                                if(sales_items.getSecond_Image_Url() != null)
                                {
                                    imageView2 = new ImageView(SalesDetails.this);
                                    Picasso.get().load(sales_items.getSecond_Image_Url()).into(imageView2);
                                    viewFlipper.addView(imageView2);
                                    imageCounter++;
                                }

                                if(sales_items.getThird_Image_Url() != null)
                                {
                                    imageView3 = new ImageView(SalesDetails.this);
                                    Picasso.get().load(sales_items.getThird_Image_Url()).into(imageView3);
                                    viewFlipper.addView(imageView3);
                                    imageCounter++;
                                }

                                if(sales_items.getFourth_Image_Url() != null)
                                {
                                    imageView4 = new ImageView(SalesDetails.this);
                                    Picasso.get().load(sales_items.getFourth_Image_Url()).into(imageView4);
                                    viewFlipper.addView(imageView4);
                                    imageCounter++;
                                }

                                if(sales_items.getFifth_Image_Url() != null)
                                {
                                    imageView5 = new ImageView(SalesDetails.this);
                                    Picasso.get().load(sales_items.getFifth_Image_Url()).into(imageView5);
                                    viewFlipper.addView(imageView5);
                                    imageCounter++;
                                }

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

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
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
