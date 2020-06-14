package com.gates.solutions.miniproject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements Modal.ButtomSheetListener{
    int bottom_nav_checker;
    DatabaseReference SearchView_ref;
    Dialog dialog;
    RelativeLayout HomePage;
    Toolbar toolbar;
    SearchView searchView;
    CircleImageView toolbarPic,drawerPicture;
    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    TextView navEmail,navUsername;
    ProgressBar profileUpdateProgressBar;
    private String mProfileImageUrl;
    private final String status_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ImageView mProfileImage;
    private Uri resultUri;
    private ViewPager viewPager;
    private ViewPagerAdapter mViewPagerAdapter;
    Boolean location_truthValue=true;

    FirebaseUser firebaseUser;
    DatabaseReference reference;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mUser = database.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


    Menu optionsMenu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        optionsMenu = menu;
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.location_id:supportInvalidateOptionsMenu();
                if (optionsMenu.getItem(0).isChecked()) {
                    optionsMenu.getItem(0).setChecked(false);
                } else {
                    optionsMenu.getItem(0).setChecked(true);
                    searchView.setQueryHint("Enter location");
                    Toast.makeText(getApplicationContext(),"Search by location selected",Toast.LENGTH_SHORT).show();
                    location_truthValue = true;
                }
                break;
            case R.id.price_id:
                if (optionsMenu.getItem(1).isChecked()) {
                    optionsMenu.getItem(1).setChecked(false);

                } else {
                    optionsMenu.getItem(1).setChecked(true);
                    searchView.setQueryHint("Enter price");
                    Toast.makeText(getApplicationContext(),"Search by price selected",Toast.LENGTH_SHORT).show();
                    location_truthValue = false;
                }

                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer);

        if(isNetworkAvailable()== false)
        {
            Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_LONG).show();
        }

        viewPager = findViewById(R.id.view_pager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mViewPagerAdapter);


        profileUpdateProgressBar = findViewById(R.id.NavDrawer_progressBar);
        mProfileImage = findViewById(R.id.toolbarIcon_id);
        searchView = findViewById(R.id.searchSpace_id);
        HomePage = findViewById(R.id.homePage_id);
        toolbarPic = findViewById(R.id.toolbarIcon_id);
        bottomNavigationView = findViewById(R.id.buttomNav_id);
        drawerLayout = findViewById(R.id.drawerlayout_id);
        navigationView = findViewById(R.id.navView_id);

        View header= navigationView.getHeaderView(0);
        navEmail = header.findViewById(R.id.UserEmail_id);
        navUsername = header.findViewById(R.id.Username_id);
        drawerPicture = header.findViewById(R.id.headerImage_id);
        dialog = new Dialog(this);
        searchView = findViewById(R.id.searchSpace_id);
        profileUpdateProgressBar.setVisibility(View.INVISIBLE);
        navEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        Rent_Fragment fragment = (Rent_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                        fragment.onStart();
                        break;
                    case 1:
                        Sales_Fragment frag = (Sales_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                        frag.onStart();
                        break;
                    case 2:
                        MyPost_Fragment fr = (MyPost_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                        fr.onStart();
                        break;

                }
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            Rent_Fragment rent_fragment;
            Sales_Fragment sales_fragment;
            MyPost_Fragment myPost_fragment;

            @Override
            public boolean onQueryTextSubmit(String query) {

                switch (viewPager.getCurrentItem()) {
                    case 0:
                        if (location_truthValue) {
                            rent_fragment = (Rent_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            rent_fragment.SearchForItem_Location(query);
                        } else {

                            rent_fragment = (Rent_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            rent_fragment.SearchForItem_Price(query);
                        }
                        break;
                    case 1:
                        if (location_truthValue) {
                            sales_fragment = (Sales_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            sales_fragment.SearchForItem_Location(query);
                        } else {
                            sales_fragment = (Sales_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            sales_fragment.SearchForItem_Price(query);
                        }
                        break;
                    case 2:
                        if (location_truthValue) {
                            myPost_fragment = (MyPost_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            myPost_fragment.SearchForItem_Location(query);
                        } else {
                            myPost_fragment = (MyPost_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            myPost_fragment.SearchForItem_Price(query);
                        }

                        break;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        if (location_truthValue) {
                            rent_fragment = (Rent_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            //  rent_fragment.SearchItem_Location(newText);
                        } else {

                            rent_fragment = (Rent_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            rent_fragment.SearchForItem_Price(newText);
                        }
                        break;
                    case 1:
                        if (location_truthValue) {
                            sales_fragment = (Sales_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            sales_fragment.SearchForItem_Location(newText);
                        } else {
                            sales_fragment = (Sales_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            sales_fragment.SearchForItem_Price(newText);
                        }
                        break;
                    case 2:
                        if (location_truthValue) {
                            myPost_fragment = (MyPost_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            myPost_fragment.SearchForItem_Location(newText);
                        } else {
                            myPost_fragment = (MyPost_Fragment) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                            myPost_fragment.SearchForItem_Price(newText);
                        }

                        break;
                }
                return false;
            }
        });




        Intent i = getIntent();
        String data = i.getStringExtra("update");

        if (data != null && data.contentEquals("1")) {
            bottomNavigationView.setSelectedItemId(R.id.nav_post_id);
            bottomNavigationView.getMenu().findItem(R.id.nav_post_id).setChecked(true);
            viewPager.setCurrentItem(2);
        }

        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();
                if (map.get("profileImageUrl") != null){
                    mProfileImageUrl = map.get("profileImageUrl").toString();
                    Glide.with(getApplication()).load(mProfileImageUrl).into(toolbarPic);
                    Glide.with(getApplication()).load(mProfileImageUrl).into(drawerPicture);
                    profileUpdateProgressBar.setVisibility(View.GONE);
                }
                if(dataSnapshot.exists()){
                    navUsername.setText(map.get("username").toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id)
                {
                    case R.id.profile_id:
                        Modal buttomSheetDialog = new Modal();
                        buttomSheetDialog.show(getSupportFragmentManager(),"ButtomSheetDialog");
                        break;
                    case R.id.NavAddPost_id:
                        startActivity(new Intent(HomeActivity.this,Add_House.class));
                        break;
                    case R.id.logout_id:
                        builder = new AlertDialog.Builder(HomeActivity.this);
                        builder.setMessage("Do you want to logout ?");
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    status("offline");
                                    FirebaseAuth.getInstance().signOut();
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if(user == null ){
                                        Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK));
                                        startActivity(intent);

                                    }
                                }catch (Exception e){
                                    Log.i("Error ",e.getMessage());
                                }


                                dialogInterface.dismiss();
                            }

                        });

                        alertDialog = builder.create();
                        alertDialog.show();
                        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                        alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                        break;
                    case R.id.nav_share_id:
                        try{
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Gate Solutions");
                            String shareMessage ="let me recommend this app to you";
                            shareMessage = shareMessage + "http://play.google.com/store/apps/details?id="+ BuildConfig.APPLICATION_ID + "012323";
                            shareIntent.putExtra(Intent.EXTRA_TEXT,shareMessage);
                            startActivity(Intent.createChooser(shareIntent,"Choose One"));
                        }catch (Exception e){
                            Snackbar.make(navigationView,e.getMessage(),Snackbar.LENGTH_SHORT).show();

                        }
                        break;

                }
                return false;
            }
        });



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.OpenDrawer, R.string.CloseDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        toolbar = findViewById(R.id.toolbar_id);
        setSupportActionBar(toolbar);


        toolbarPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
                return;
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.nav_rent_id).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.nav_sales_id).setChecked(true);

                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.nav_post_id).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.nav_chat_id).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.nav_rent_id:
                        viewPager.setCurrentItem(0);
                        //fragment = new Sales_Fragment();
                        SearchView_ref = FirebaseDatabase.getInstance().getReference().child("Sales Upload");
                        bottom_nav_checker = 1;
                        break;
                    case R.id.nav_sales_id:
                        viewPager.setCurrentItem(1);
                        //fragment = new Rent_Fragment();
                        SearchView_ref = FirebaseDatabase.getInstance().getReference().child("Rent Upload");
                        bottom_nav_checker = 2;
                        break;
                    case R.id.nav_post_id:
                        viewPager.setCurrentItem(2);
                        // fragment = new MyPost_Fragment();
                        SearchView_ref = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("uploads");
                        bottom_nav_checker = 3;
                        break;
                    case R.id.nav_chat_id:
                        viewPager.setCurrentItem(3);
                        //   fragment = new Chat_Fragment();
                        //   SearchView_ref = FirebaseDatabase.getInstance().getReference().child("Sales Upload");
                        bottom_nav_checker = 4;
                        break;

                }

                return true;
            }
        });
    }





    @Override
    public void onModalItemSelected(int ItemId) {
        switch (ItemId) {

            case R.id.Upload_id:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 2);
                break;
            case R.id.Remove_id:
                profileUpdateProgressBar.setVisibility(View.VISIBLE);
                Uri drawable_Uri = Uri.parse("android.resource://com.gates.solutions.miniproject/drawable/default_pic");
                final Uri imageUri = drawable_Uri;
                resultUri = imageUri;
                mProfileImage.setImageURI(resultUri);
                if (resultUri != null) {

                    StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profile_images").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    //    Bitmap bitmap = null;
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), drawable_Uri);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                        byte[] image_data = baos.toByteArray();
                        UploadTask uploadTask = filepath.putBytes(image_data);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Map newImage = new HashMap();
                                        newImage.put("profileImageUrl", uri.toString());
                                        mUser.updateChildren(newImage);
                                        Toast toast = Toast.makeText(getApplicationContext(), "Profile photo removed", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                });
                            }
                        });
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            profileUpdateProgressBar.setVisibility(View.VISIBLE);
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);

            if (resultUri != null) {
                StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profile_images").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] image_data = baos.toByteArray();
                UploadTask uploadTask = filepath.putBytes(image_data);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Map newImage = new HashMap();
                                newImage.put("profileImageUrl", uri.toString());
                                mUser.updateChildren(newImage);
                                Toast toast = Toast.makeText(getApplicationContext(), "Profile photo updated", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        });
                    }
                });
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        profileUpdateProgressBar.setVisibility(View.GONE);
                        Toast toast = Toast.makeText(getApplicationContext(), "Network failure. Please try again later.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });
            }
        }
    }


    private void status(String status)
    {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            status("online");
        }catch (Exception e){
            Log.i("Error ",e.getMessage());
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        try{
            status("offline");
        }catch (Exception e){
            Log.i("Error ",e.getMessage());
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
