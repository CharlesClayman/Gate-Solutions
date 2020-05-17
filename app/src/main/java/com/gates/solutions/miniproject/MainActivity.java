package com.gates.solutions.miniproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Button signInBtn;
    TextView signUpTxt,forgotPasswordTxt;
    AppCompatEditText userEmail,userPassword;
    RelativeLayout signInPage;
    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener stateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signInBtn = findViewById(R.id.signInBtn_id);
        forgotPasswordTxt = findViewById(R.id.forgotPassword_id);
        signUpTxt = findViewById(R.id.signUptxt_id);
        userEmail = findViewById(R.id.emailTxt_id);
        userPassword = findViewById(R.id.passwordTxt_id);
        signInPage = findViewById(R.id.signInPage_id);
        progressBar = findViewById(R.id.signIn_Prog_id);
        progressBar.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        stateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null)
                {
                    startActivity(new Intent(MainActivity.this,HomeActivity.class));
                }
            }
        };

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if(userEmail.getText().toString().isEmpty())
                {
                    progressBar.setVisibility(View.GONE);
                    final Snackbar snackbar= Snackbar.make(signInPage,"Email field is empty",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    snackbar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    userEmail.requestFocus();
                }
                else if(userPassword.getText().toString().isEmpty())
                {
                    progressBar.setVisibility(View.GONE);
                    final Snackbar snackbar = Snackbar.make(signInPage,"Password field is empty",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    snackbar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            snackbar.dismiss();
                        }
                    });
                    userPassword.requestFocus();
                }
                else if(userPassword.getText().toString().isEmpty() && userPassword.getText().toString().isEmpty())
                {
                    progressBar.setVisibility(View.GONE);
                    final Snackbar snackbar = Snackbar.make(signInPage,"Empty fields",Snackbar.LENGTH_SHORT);
                    snackbar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    userEmail.requestFocus();
                }else {
                    firebaseAuth.signInWithEmailAndPassword(userEmail.getText().toString(), userPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                    } else {
                                        Snackbar.make(signInPage, "Network failure...try again", Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(signInPage, "Email or password is incorrect", Snackbar.LENGTH_LONG).show();
                                }
                            });
                }

                forgotPasswordTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(userEmail.getText().toString().isEmpty()){
                            Snackbar.make(signInPage,"Enter Email",Snackbar.LENGTH_LONG).show();
                            userEmail.requestFocus();
                        }else{
                            firebaseAuth.sendPasswordResetEmail(userEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Snackbar.make(signInPage,"Check email for password reset",Snackbar.LENGTH_LONG).show();
                                    }
                                    else{
                                        Snackbar.make(signInPage,"Password reset failed",Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(signInPage,"Password reset failed",Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }

                    }
                });
            }
        });

        signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,Registration.class));
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(stateListener);
    }

}
