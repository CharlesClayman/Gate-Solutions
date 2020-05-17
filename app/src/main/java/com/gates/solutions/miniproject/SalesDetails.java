package com.gates.solutions.miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class SalesDetails extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    TextView poster,location,tel,postTime,desc,Price,next,previous;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Sales Upload");
    LinearLayout call_layout;
    private String itemID_sales="",TelNum="";
    ImageView imageView1,imageView2,imageView3,imageView4,imageView5;
    public int imageCounter=0;
    ViewFlipper viewFlipper;

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
                if(dataSnapshot.exists())
                {
                    final Sales_Items sales_items = dataSnapshot.getValue(Sales_Items.class);
                    location.setText("Location: "+sales_items.getLocation());
                    tel.setText("Telephone: "+sales_items.getTelephone());
                    postTime.setText("Posted On: "+sales_items.getTime());
                    desc.setText(sales_items.getDescription());
                    tel.setText(sales_items.getTelephone());
                    Price.setText("GHC "+currencyFormat(sales_items.getPrice()));


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
}
