package com.gates.solutions.miniproject;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

public class Registration  extends AppCompatActivity {
    Button register;
    static AppCompatEditText Telephone,Username,Email,Password,Confirm_password;
    RelativeLayout signUpPage;
    FirebaseAuth firebaseAuth;

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

        firebaseAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Username.getText().toString().isEmpty() && Telephone.getText().toString().isEmpty() && Email.getText().toString().isEmpty() &&
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
                }
                else if(!(Password.getText().toString().equals(Confirm_password.getText().toString())) && !Confirm_password.getText().toString().isEmpty())
                {
                    Snackbar.make(signUpPage, "Password mismatch", Snackbar.LENGTH_SHORT).show();
                    Confirm_password.setText("");
                    Confirm_password.requestFocus();
                }
                else{

                    firebaseAuth.createUserWithEmailAndPassword(Email.getText().toString(),Password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("message");
                                myRef.setValue(Username.getText().toString());
                                Toast.makeText(getApplicationContext(),"Successfully registered",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(Registration.this,MainActivity.class));
                            }else{
                                Snackbar.make(signUpPage,"Poor network...try again",Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(signUpPage,"Registration failed",Snackbar.LENGTH_LONG).show();
                        }
                    });


                }

            }
        });

    }


}
