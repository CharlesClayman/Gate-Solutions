package com.gates.solutions.miniproject;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

public class Registration  extends AppCompatActivity {
    Button register;
    static AppCompatEditText Telephone,Username,Email,Password,Confirm_password;
    RelativeLayout signUpPage;
    FirebaseAuth firebaseAuth;
    RelativeLayout registration_page;
    ProgressBar progressBar;
    private Uri resultUri;
    AlertDialog b;
    AlertDialog.Builder dialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        register = findViewById(R.id.NextBtn_id);
        signUpPage = findViewById(R.id.signUpPage_id);
        Username = findViewById(R.id.UsernameTxt_id);
        Email = findViewById(R.id.emailTxt_id);
        Password = findViewById(R.id.passwordTxt_id);
        Confirm_password = findViewById(R.id.confirmPassTxt_id);
        registration_page = findViewById(R.id.signUpPage_id);
        progressBar = findViewById(R.id.registration_Prog_id);
        progressBar.setVisibility(View.GONE);

        if(isNetworkAvailable() == false)
        {
            Snackbar.make(registration_page,"No internet connection",Snackbar.LENGTH_LONG).show();
        }

        firebaseAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable() == false)
                {
                    Snackbar.make(registration_page,"No internet connection",Snackbar.LENGTH_LONG).show();
                }else {


                if (Username.getText().toString().isEmpty() && Email.getText().toString().isEmpty() &&
                        Password.getText().toString().isEmpty() && Confirm_password.getText().toString().isEmpty()) {
                    Snackbar.make(signUpPage, "Empty fields", Snackbar.LENGTH_SHORT).show();
                } else if (Username.getText().toString().isEmpty()) {
                    Snackbar.make(signUpPage, "Username field is empty", Snackbar.LENGTH_SHORT).show();
                    Username.requestFocus();
                } else if (Email.getText().toString().isEmpty()) {
                    Snackbar.make(signUpPage, "Email field is empty", Snackbar.LENGTH_SHORT).show();
                    Email.requestFocus();
                } else if (Password.getText().toString().isEmpty()) {
                    Snackbar.make(signUpPage, "Password field is empty", Snackbar.LENGTH_SHORT).show();
                    Password.requestFocus();
                } else if (Confirm_password.getText().toString().isEmpty()) {
                    Snackbar.make(signUpPage, "Confirm Password field is empty", Snackbar.LENGTH_SHORT).show();
                    Password.requestFocus();
                } else if (!(Password.getText().toString().equals(Confirm_password.getText().toString())) && !Confirm_password.getText().toString().isEmpty()) {
                    Snackbar.make(signUpPage, "Password mismatch", Snackbar.LENGTH_SHORT).show();
                    Confirm_password.setText("");
                    Confirm_password.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(Email.getText().toString(), Password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Uri drawable_Uri = Uri.parse("android.resource://com.gates.solutions.miniproject/drawable/default_pic");
                                final Uri imageUri = drawable_Uri;
                                resultUri = imageUri;
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
                                                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(newImage);
                                                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username").setValue(Username.getText().toString());
                                                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue("offline");
                                                        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("id").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                        Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_LONG).show();
                                                        progressBar.setVisibility(View.GONE);
                                                        startActivity(new Intent(Registration.this, MainActivity.class));
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
                                }

                                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username").setValue(Username.getText().toString());
                                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("status").setValue("offline");
                                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("id").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(Registration.this, MainActivity.class));
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Snackbar.make(signUpPage, "Poor network...try again", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(signUpPage, "Registration failed", Snackbar.LENGTH_LONG).show();
                        }
                    });


                }
            }

            }
        });

    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

