package com.gates.solutions.miniproject;

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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

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
    ProgressBar progressBar,profileUpdateProgressBar;
    private String mProfileImageUrl, SearchInput;
    private ImageView mProfileImage;
    private Uri resultUri,cameraResultUri;
    private ViewPager viewPager;
    private ViewPagerAdapter mViewPagerAdapter;



    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mUser = database.getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    FirebaseRecyclerOptions<Sales_Items> options;



    Menu optionsMenu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        optionsMenu = menu;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ((Sales_Fragment) getSupportFragmentManager().findFragmentById(R.id.salesFragment)).search(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((Sales_Fragment) getSupportFragmentManager().findFragmentById(R.id.salesFragment)).search(newText);

                return false;
            }
        });

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
                }
                options = new FirebaseRecyclerOptions.Builder<Sales_Items>().setQuery(SearchView_ref.orderByChild("Location"),Sales_Items.class).build();
                break;
            case R.id.price_id:
                if (optionsMenu.getItem(1).isChecked()) {
                    optionsMenu.getItem(1).setChecked(false);
                } else {
                    optionsMenu.getItem(1).setChecked(true);
                    searchView.setQueryHint("Enter price");
                    Toast.makeText(getApplicationContext(),"Search by price selected",Toast.LENGTH_SHORT).show();
                }
                options = new FirebaseRecyclerOptions.Builder<Sales_Items>().setQuery(SearchView_ref.orderByChild("Price"),Sales_Items.class).build();
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer);

        viewPager = findViewById(R.id.view_pager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mViewPagerAdapter);

        progressBar = findViewById(R.id.home_progress_id);
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
        progressBar.setVisibility(View.VISIBLE);
        profileUpdateProgressBar.setVisibility(View.INVISIBLE);
        navEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());


      /*  mUser.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String username = dataSnapshot.getValue(String.class);
                        navUsername.setText(username);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/

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
                    navUsername.setText(map.get("Username").toString());
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
                                FirebaseAuth.getInstance().signOut();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if(user == null){
                                    Intent intent = new Intent(HomeActivity.this,MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK));
                                    startActivity(intent);

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
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Tsatsaboli Gold Checker");
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
            case R.id.cameraLayout_id:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                    }
                }
                break;
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
                                    Toast toast = Toast.makeText(getApplicationContext(),"Profile photo removed",Toast.LENGTH_LONG);
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

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            profileUpdateProgressBar.setVisibility(View.VISIBLE);
            cameraResultUri = data.getData();
            mProfileImage.setImageURI(cameraResultUri);

                StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profile_images").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), cameraResultUri);

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
                        Toast toast = Toast.makeText(getApplicationContext(), "Network failure. Please try again later.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                });

        }else if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


 /*   @Overridew
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        FirebaseRecyclerAdapter<Sales_Items,myPostViewHolder> adapter = new FirebaseRecyclerAdapter<Sales_Items, myPostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull myPostViewHolder holder, int position, @NonNull Sales_Items model) {
                String amount = currencyFormat(model.getPrice());
                holder.location_txt.setText(model.getLocation());
                holder.price_txt.setText("GHC "+amount);
                //  holder.time.setText(model.getTime());
                Picasso.get().load(model.getFirst_Image_Url()).into(holder.imageView);

            }

            @NonNull
            @Override
            public myPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item,parent,false);
                myPostViewHolder holder = new myPostViewHolder(view);
                return holder;
            }
        };
        switch (bottom_nav_checker)
        {
            case 1:
                Sales_Fragment sf = new Sales_Fragment();
                sf.recyclerView.setAdapter(adapter);
                adapter.startListening();
                break;
            case 2:
                Rent_Fragment rf = new Rent_Fragment();
                rf.recyclerView.setAdapter(adapter);
                adapter.startListening();
                break;
            case 3:
                MyPost_Fragment mf = new MyPost_Fragment();
                mf.recyclerView.setAdapter(adapter);
                adapter.startListening();
                break;
        }


        return true;
    } */


    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

}
