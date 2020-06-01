package com.gates.solutions.miniproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Add_House extends AppCompatActivity {
    RadioGroup radioGroup;
    RadioButton rentRadioButton,salesRadioButton;
    Button AddPhoto_Btn,Post_Btn;
    EditText location_EditTxt,RPM_EditTxt,Price_EditTxt,Telephone_EditTxt,Description_EditTxt;
    RelativeLayout AddPostPage;
    LinearLayout selectedImagesLayout;
    String imageEncoded,SaveCurrentDate,SaveCurrentTime,productRandomKey;
    List<String> imagesEncodedList;
    Uri  restoreImage1,restoreImage2,restoreImage3,restoreImage4,restoreImage5;
    ImageView First_Image, Second_Image,Third_Image,Fourth_Image,Fifth_Image ;
    int imageCounter=0;
    AlertDialog alertDialog;
    AlertDialog.Builder builder;
    ProgressDialog progressDialog;
    StorageReference postImagesRef;
    DatabaseReference sales_reference,rent_reference,myPost_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__house);


        radioGroup = findViewById(R.id.radiogroup_id);
        rentRadioButton = findViewById(R.id.rentRadioButton);
        salesRadioButton = findViewById(R.id.salesRadioButton);
        AddPhoto_Btn = findViewById(R.id.add_Photo_Btn_id);
        Post_Btn = findViewById(R.id.Post_Btn_id);
        location_EditTxt = findViewById(R.id.location_EditTxt_id);
        RPM_EditTxt = findViewById(R.id.rpm_EditTxt_id);
        Price_EditTxt = findViewById(R.id.price_EditTxt_id);
        Telephone_EditTxt = findViewById(R.id.telephone_EditTxt_id);
        Description_EditTxt = findViewById(R.id.Description_EditTxt_id);
        AddPostPage = findViewById(R.id.add_PostPage_id);
        selectedImagesLayout = findViewById(R.id.selectedImagesLayout_id);
        progressDialog = new ProgressDialog(this);


        Calendar calendar = Calendar.getInstance();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM, yyyy");
            SaveCurrentDate = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:MM:SS a");
            SaveCurrentTime = currentTime.format(calendar.getTime());
        }
        //ends

        //Generating random keys to identity each upload
        productRandomKey = SaveCurrentDate +" "+ SaveCurrentTime;
         sales_reference = FirebaseDatabase.getInstance().getReference().child("Sales Upload").child(productRandomKey);
         rent_reference = FirebaseDatabase.getInstance().getReference().child("Rent Upload").child(productRandomKey);
         myPost_reference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("uploads").child(productRandomKey);
          postImagesRef = FirebaseStorage.getInstance().getReference().child("Posted_Images").child(productRandomKey);


        AddPhoto_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
            };
        });

        Price_EditTxt.setEnabled(false);
        RPM_EditTxt.setEnabled(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.rentRadioButton:
                        if(!Price_EditTxt.toString().isEmpty())
                        {
                            Price_EditTxt.setText("");
                        }
                        Price_EditTxt.setEnabled(false);
                        RPM_EditTxt.setEnabled(true);
                        break;
                    case R.id.salesRadioButton:
                        if(!RPM_EditTxt.toString().isEmpty())
                        {
                            RPM_EditTxt.setText("");
                        }
                        RPM_EditTxt.setEnabled(false);
                        Price_EditTxt.setEnabled(true);
                        break;
                }
            }
        });


        Post_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  validateAddPostDate();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            // When an Image is picked
            if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                imagesEncodedList = new ArrayList<String>();
                if(data.getData()!=null){
                    Uri mImageUri=data.getData();

                    switch (imageCounter)
                    {
                        case 0:
                            First_Image = new ImageView(Add_House.this);
                            First_Image.setImageURI(mImageUri);
                             restoreImage1= mImageUri;
                            addSelectedImage(First_Image, 100, 100);
                            First_Image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    builder = new AlertDialog.Builder(Add_House.this);
                                    builder.setMessage("Do you want to delete ?");
                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (First_Image != null) {
                                                ViewGroup parent = (ViewGroup) First_Image.getParent();
                                                if (parent != null) {
                                                    parent.removeView(First_Image);
                                                    restoreImage1 = null;
                                                }
                                            }
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    alertDialog = builder.create();
                                    alertDialog.show();
                                    alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                    alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                }
                            });
                            imageCounter++;
                            break;
                        case 1:
                            Second_Image = new ImageView(Add_House.this);
                            Second_Image.setImageURI(mImageUri);
                            restoreImage2= mImageUri;
                            addSelectedImage(Second_Image, 100, 100);
                            Second_Image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    builder = new AlertDialog.Builder(Add_House.this);
                                    builder.setMessage("Do you want to delete ?");
                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Second_Image != null) {
                                                ViewGroup parent = (ViewGroup) Second_Image.getParent();
                                                if (parent != null) {
                                                    parent.removeView(Second_Image);
                                                    restoreImage2 = null;
                                                }
                                            }
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    alertDialog = builder.create();
                                    alertDialog.show();
                                    alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                    alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                }
                            });
                            imageCounter++;
                            break;
                        case 2:
                            Third_Image = new ImageView(Add_House.this);
                            Third_Image.setImageURI(mImageUri);
                            restoreImage3= mImageUri;
                            addSelectedImage(Third_Image, 100, 100);
                            Third_Image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    builder = new AlertDialog.Builder(Add_House.this);
                                    builder.setMessage("Do you want to delete ?");
                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Third_Image != null) {
                                                ViewGroup parent = (ViewGroup) Third_Image.getParent();
                                                if (parent != null) {
                                                    parent.removeView(Third_Image);
                                                    restoreImage3 = null;
                                                }
                                            }
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    alertDialog = builder.create();
                                    alertDialog.show();
                                    alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                    alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                }
                            });
                            imageCounter++;

                            break;
                        case 3:
                            Fourth_Image = new ImageView(Add_House.this);
                            Fourth_Image.setImageURI(mImageUri);
                            restoreImage4= mImageUri;
                            addSelectedImage(Fourth_Image, 100, 100);
                            Fourth_Image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    builder = new AlertDialog.Builder(Add_House.this);
                                    builder.setMessage("Do you want to delete ?");
                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Fourth_Image != null) {
                                                ViewGroup parent = (ViewGroup) Fourth_Image.getParent();
                                                if (parent != null) {
                                                    parent.removeView(Fourth_Image);
                                                    restoreImage4 = null;
                                                }
                                            }
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    alertDialog = builder.create();
                                    alertDialog.show();
                                    alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                    alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                    imageCounter++;
                                }
                            });
                            imageCounter++;
                            break;
                        case 4:
                            Fifth_Image = new ImageView(Add_House.this);
                            Fifth_Image.setImageURI(mImageUri);
                            restoreImage5= mImageUri;
                            addSelectedImage(Fifth_Image, 100, 100);
                            Fifth_Image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    builder = new AlertDialog.Builder(Add_House.this);
                                    builder.setMessage("Do you want to delete ?");
                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Fifth_Image != null) {
                                                ViewGroup parent = (ViewGroup) Fifth_Image.getParent();
                                                if (parent != null) {
                                                    parent.removeView(Fifth_Image);
                                                    restoreImage5 = null;
                                                }
                                            }
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    alertDialog = builder.create();
                                    alertDialog.show();
                                    alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                    alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                }
                            });
                            imageCounter++;
                            break;
                        default:
                            Toast.makeText(getApplicationContext(),"Maximum of 5 pictures",Toast.LENGTH_LONG).show();
                    }

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(mImageUri,
                            filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded  = cursor.getString(columnIndex);
                    cursor.close();

                } else {
                    if (data.getClipData() != null) {

                        //checking to see if number of images selected exceeds the limit
                        ClipData clipData = data.getClipData();
                        if (clipData.getItemCount() > 5) {
                            Toast.makeText(getApplicationContext(), "Maximum of 5 pictures", Toast.LENGTH_LONG).show();
                            return;
                        }else{


                        ClipData mClipData = data.getClipData();
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();

                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(imageEncoded);
                            cursor.close();
                        }



                            for (int j = 0; j < mArrayUri.size(); j++) {
                                  switch (j)
                                  {
                                      case 0:
                                          First_Image = new ImageView(Add_House.this);
                                          First_Image.setImageURI(mArrayUri.get(j));
                                          restoreImage1 = mArrayUri.get(j);
                                          addSelectedImage(First_Image, 100, 100);
                                          First_Image.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  builder = new AlertDialog.Builder(Add_House.this);
                                                  builder.setMessage("Do you want to delete ?");
                                                  builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                      @Override
                                                      public void onClick(DialogInterface dialogInterface, int i) {
                                                          dialogInterface.dismiss();
                                                      }
                                                  });

                                                  builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                      @Override
                                                      public void onClick(DialogInterface dialogInterface, int i) {
                                                          if (First_Image != null) {
                                                              ViewGroup parent = (ViewGroup) First_Image.getParent();
                                                              if (parent != null) {
                                                                  parent.removeView(First_Image);
                                                                  restoreImage1 = null;
                                                              }
                                                          }
                                                          dialogInterface.dismiss();
                                                      }
                                                  });
                                                  alertDialog = builder.create();
                                                  alertDialog.show();
                                                  alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                                  alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                              }
                                          });
                                          break;
                                      case 1:
                                          Second_Image = new ImageView(Add_House.this);
                                          Second_Image.setImageURI(mArrayUri.get(j));
                                           restoreImage2 = mArrayUri.get(j);
                                          addSelectedImage(Second_Image, 100, 100);
                                          Second_Image.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  builder = new AlertDialog.Builder(Add_House.this);
                                                  builder.setMessage("Do you want to delete ?");
                                                  builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                      @Override
                                                      public void onClick(DialogInterface dialogInterface, int i) {
                                                          dialogInterface.dismiss();
                                                      }
                                                  });

                                                  builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                      @Override
                                                      public void onClick(DialogInterface dialogInterface, int i) {
                                                          if (Second_Image != null) {
                                                              ViewGroup parent = (ViewGroup) Second_Image.getParent();
                                                              if (parent != null) {
                                                                  parent.removeView(Second_Image);
                                                                  restoreImage2 = null;
                                                              }
                                                          }
                                                          dialogInterface.dismiss();
                                                      }
                                                  });
                                                  alertDialog = builder.create();
                                                  alertDialog.show();
                                                  alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                                  alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                              }
                                          });
                                          break;
                                      case 2:
                                          Third_Image = new ImageView(Add_House.this);
                                          Third_Image.setImageURI(mArrayUri.get(j));
                                          restoreImage3 = mArrayUri.get(j);
                                          addSelectedImage(Third_Image, 100, 100);
                                          Third_Image.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  builder = new AlertDialog.Builder(Add_House.this);
                                                  builder.setMessage("Do you want to delete ?");
                                                  builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                      @Override
                                                      public void onClick(DialogInterface dialogInterface, int i) {
                                                          dialogInterface.dismiss();
                                                      }
                                                  });

                                                  builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                      @Override
                                                      public void onClick(DialogInterface dialogInterface, int i) {
                                                          if (Third_Image != null) {
                                                              ViewGroup parent = (ViewGroup) Third_Image.getParent();
                                                              if (parent != null) {
                                                                  parent.removeView(Third_Image);
                                                                  restoreImage3 = null;
                                                              }
                                                          }
                                                          dialogInterface.dismiss();
                                                      }
                                                  });
                                                  alertDialog = builder.create();
                                                  alertDialog.show();
                                                  alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                                  alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                              }
                                          });
                                          break;
                                      case 3:
                                          Fourth_Image = new ImageView(Add_House.this);
                                          Fourth_Image.setImageURI(mArrayUri.get(j));
                                           restoreImage4 = mArrayUri.get(j);
                                          addSelectedImage(Fourth_Image, 100, 100);
                                          Fourth_Image.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  builder = new AlertDialog.Builder(Add_House.this);
                                                  builder.setMessage("Do you want to delete ?");
                                                  builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                      @Override
                                                      public void onClick(DialogInterface dialogInterface, int i) {
                                                          dialogInterface.dismiss();
                                                      }
                                                  });

                                                  builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                      @Override
                                                      public void onClick(DialogInterface dialogInterface, int i) {
                                                          if (Fourth_Image != null) {
                                                              ViewGroup parent = (ViewGroup) Fourth_Image.getParent();
                                                              if (parent != null) {
                                                                  parent.removeView(Fourth_Image);
                                                                  restoreImage4 = null;
                                                              }
                                                          }
                                                          dialogInterface.dismiss();
                                                      }
                                                  });
                                                  alertDialog = builder.create();
                                                  alertDialog.show();
                                                  alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                                  alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                              }
                                          });
                                          break;
                                      case 4:
                                          Fifth_Image = new ImageView(Add_House.this);
                                          Fifth_Image.setImageURI(mArrayUri.get(j));
                                          restoreImage5 = mArrayUri.get(j);
                                          addSelectedImage(Fifth_Image, 100, 100);
                                          Fifth_Image.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  builder = new AlertDialog.Builder(Add_House.this);
                                                  builder.setMessage("Do you want to delete ?");
                                                  builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                      @Override
                                                      public void onClick(DialogInterface dialogInterface, int i) {
                                                          dialogInterface.dismiss();
                                                      }
                                                  });

                                                  builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                      @Override
                                                      public void onClick(DialogInterface dialogInterface, int i) {
                                                          if (Fifth_Image != null) {
                                                              ViewGroup parent = (ViewGroup) Fifth_Image.getParent();
                                                              if (parent != null) {
                                                                  parent.removeView(Fifth_Image);
                                                                  restoreImage5 = null;
                                                              }
                                                          }
                                                          dialogInterface.dismiss();
                                                      }
                                                  });
                                                  alertDialog = builder.create();
                                                  alertDialog.show();
                                                  alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                                  alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                                              }
                                          });
                                          break;

                                  }
                            }
                        }

                    }

                }

            } else {
                Toast.makeText(this, "No selected image",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this,"Ops...Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }


    }

    public void addSelectedImage(ImageView imageView,int width,int height)
    {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width,height);
        layoutParams.setMarginEnd(3);
        imageView.setLayoutParams(layoutParams);
        selectedImagesLayout.addView(imageView);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(restoreImage1 != null)
        {
            outState.putParcelable("imageKey1",restoreImage1);
        }
        if(restoreImage2 != null)
        {
            outState.putParcelable("imageKey2",restoreImage2);
        }
        if(restoreImage3 != null)
        {
            outState.putParcelable("imageKey3",restoreImage3);
        }
        if(restoreImage4 != null)
        {
            outState.putParcelable("imageKey4",restoreImage4);
        }
        if(restoreImage5 != null)
        {
            outState.putParcelable("imageKey5",restoreImage5);
        }






    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getParcelable("imageKey1") != null)
        {
            First_Image = new ImageView(Add_House.this);
            Uri uri = savedInstanceState.getParcelable("imageKey1");
            First_Image.setImageURI(uri);
            addSelectedImage(First_Image, 100, 100);
            First_Image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder = new AlertDialog.Builder(Add_House.this);
                    builder.setMessage("Do you want to delete ?");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (First_Image != null) {
                                ViewGroup parent = (ViewGroup) First_Image.getParent();
                                if (parent != null) {
                                    parent.removeView(First_Image);
                                    restoreImage1 = null;
                                }
                            }
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                }
            });
        }
        if(savedInstanceState.getParcelable("imageKey2") != null)
        {
            Second_Image = new ImageView(Add_House.this);
            Uri uri = savedInstanceState.getParcelable("imageKey2");
            Second_Image.setImageURI(uri);
            addSelectedImage(Second_Image, 100, 100);
            Second_Image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder = new AlertDialog.Builder(Add_House.this);
                    builder.setMessage("Do you want to delete ?");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (Second_Image != null) {
                                ViewGroup parent = (ViewGroup) Second_Image.getParent();
                                if (parent != null) {
                                    parent.removeView(Second_Image);
                                    restoreImage2 = null;
                                }
                            }
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                }
            });
        }
        if(savedInstanceState.getParcelable("imageKey3") != null)
        {
            Third_Image = new ImageView(Add_House.this);
            Uri uri = savedInstanceState.getParcelable("imageKey3");
            Third_Image.setImageURI(uri);
            addSelectedImage(Third_Image, 100, 100);
            Third_Image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder = new AlertDialog.Builder(Add_House.this);
                    builder.setMessage("Do you want to delete ?");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (Third_Image != null) {
                                ViewGroup parent = (ViewGroup) Third_Image.getParent();
                                if (parent != null) {
                                    parent.removeView(Third_Image);
                                    restoreImage3 = null;
                                }
                            }
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                }
            });
        }
        if(savedInstanceState.getParcelable("imageKey4") != null)
        {
            Fourth_Image = new ImageView(Add_House.this);
            Uri uri = savedInstanceState.getParcelable("imageKey4");
            Fourth_Image.setImageURI(uri);
            addSelectedImage(Fourth_Image, 100, 100);
            Fourth_Image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder = new AlertDialog.Builder(Add_House.this);
                    builder.setMessage("Do you want to delete ?");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (Fourth_Image != null) {
                                ViewGroup parent = (ViewGroup) Fourth_Image.getParent();
                                if (parent != null) {
                                    parent.removeView(Fourth_Image);
                                    restoreImage4 = null;
                                }
                            }
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                }
            });
        }
        if(savedInstanceState.getParcelable("imageKey5") != null)
        {
            Fifth_Image = new ImageView(Add_House.this);
            Uri uri = savedInstanceState.getParcelable("imageKey5");
            Fifth_Image.setImageURI(uri);
            addSelectedImage(Fifth_Image, 100, 100);
            Fifth_Image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder = new AlertDialog.Builder(Add_House.this);
                    builder.setMessage("Do you want to delete ?");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (Fifth_Image != null) {
                                ViewGroup parent = (ViewGroup) Fifth_Image.getParent();
                                if (parent != null) {
                                    parent.removeView(Fifth_Image);
                                    restoreImage5 = null;
                                }
                            }
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                    alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorBlue));
                }
            });
        }else{
            return;
        }
    }



    private void validateAddPostDate()
    {
           storeHouseInformation();
    }

    private void storeHouseInformation( )
    {
        progressDialog.setTitle("Adding New Post");
        progressDialog.setMessage("Please wait ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(salesRadioButton.isChecked())
        {

        for(int i=0;i<6;i++)
        {
            switch (i)
            {
                case 0:
                        if(restoreImage1 != null) {
                            StorageReference filePath = postImagesRef.child(restoreImage1.getLastPathSegment());
                            final UploadTask uploadtask = filePath.putFile(restoreImage1);
                            uploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Map Image = new HashMap();
                                            Image.put("First_Image_Url", uri.toString());
                                            sales_reference.updateChildren(Image);
                                            myPost_reference.updateChildren(Image);
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.v("msg","............................."+ e.getMessage());
                                }
                            });
                        }else {
                            Log.v("msg",".................................restore image1 is empty");
                            continue;
                        }
                    break;
                case 1:
                    if(restoreImage2 != null)
                    {
                        StorageReference filePath = postImagesRef.child(restoreImage2.getLastPathSegment());
                        final UploadTask uploadtask = filePath.putFile(restoreImage2);
                        uploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Map Image = new HashMap();
                                        Image.put("Second_Image_Url", uri.toString());
                                        sales_reference.updateChildren(Image);
                                        myPost_reference.updateChildren(Image);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Error:"+e.getMessage(),Toast.LENGTH_LONG).show();
                                Log.v("msg","............................."+ e.getMessage());
                            }
                        });
                    }else{
                        Log.v("msg",".................................restore image2 is empty");
                        continue;
                    }
                    break;
                case 2:
                    if(restoreImage3 != null)
                    {
                        StorageReference filePath = postImagesRef.child(restoreImage3.getLastPathSegment());
                        final UploadTask uploadtask = filePath.putFile(restoreImage3);
                        uploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Map Image = new HashMap();
                                        Image.put("Third_Image_Url", uri.toString());
                                        sales_reference.updateChildren(Image);
                                        myPost_reference.updateChildren(Image);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Error:"+e.getMessage(),Toast.LENGTH_LONG).show();
                                Log.v("msg","............................."+ e.getMessage());
                            }
                        });
                    }else{
                        Log.v("msg",".................................restore image3 is empty");
                        continue;
                    }
                    break;
                case 3:
                    if(restoreImage4!= null)
                    {
                        StorageReference filePath = postImagesRef.child(restoreImage4.getLastPathSegment());
                        final UploadTask uploadtask = filePath.putFile(restoreImage4);
                        uploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Map Image = new HashMap();
                                        Image.put("Fourth_Image_Url", uri.toString());
                                        sales_reference.updateChildren(Image);
                                        myPost_reference.updateChildren(Image);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Error:"+e.getMessage(),Toast.LENGTH_LONG).show();
                                Log.v("msg","............................."+ e.getMessage());
                            }
                        });
                    }else{
                        Log.v("msg",".................................restore image4 is empty");
                        continue;
                    }
                    break;
                case 4:
                    if(restoreImage5 != null)
                    {
                        StorageReference filePath = postImagesRef.child(restoreImage5.getLastPathSegment());
                        final UploadTask uploadtask = filePath.putFile(restoreImage5);
                        uploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Map Image = new HashMap();
                                        Image.put("Fifth_Image_Url", uri.toString());
                                        sales_reference.updateChildren(Image);
                                        myPost_reference.updateChildren(Image);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Error:"+e.getMessage(),Toast.LENGTH_LONG).show();
                                Log.v("msg","............................."+ e.getMessage());
                            }
                        });
                    }else{

                            Log.v("msg",".................................restore image5 is empty");
                        continue;
                    }
                    break;
                case 5:
                    break;
                default:
                    break;
            }


        }
        Sales_saveUploadDetails();
        progressDialog.dismiss();
      /*  Toast toast = Toast.makeText(getApplicationContext(), "Upload complete", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();*/
        }else if(rentRadioButton.isChecked())
        {
            for(int i=0;i<6;i++)
            {
                switch (i)
                {
                    case 0:
                        if(restoreImage1 != null) {
                            StorageReference filePath = postImagesRef.child(restoreImage1.getLastPathSegment());
                            final UploadTask uploadtask = filePath.putFile(restoreImage1);
                            uploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Map Image = new HashMap();
                                            Image.put("First_Image_Url", uri.toString());
                                            rent_reference.updateChildren(Image);
                                            myPost_reference.updateChildren(Image);

                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.v("msg","............................."+ e.getMessage());
                                }
                            });
                        }else {
                            Log.v("msg",".................................restore image1 is empty");
                            continue;
                        }
                        break;
                    case 1:
                        if(restoreImage2 != null)
                        {
                            StorageReference filePath = postImagesRef.child(restoreImage2.getLastPathSegment());
                            final UploadTask uploadtask = filePath.putFile(restoreImage2);
                            uploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Map Image = new HashMap();
                                            Image.put("Second_Image_Url", uri.toString());
                                            rent_reference.updateChildren(Image);
                                            myPost_reference.updateChildren(Image);
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Error:"+e.getMessage(),Toast.LENGTH_LONG).show();
                                    Log.v("msg","............................."+ e.getMessage());
                                }
                            });
                        }else{
                            Log.v("msg",".................................restore image2 is empty");
                            continue;
                        }
                        break;
                    case 2:
                        if(restoreImage3 != null)
                        {
                            StorageReference filePath = postImagesRef.child(restoreImage3.getLastPathSegment());
                            final UploadTask uploadtask = filePath.putFile(restoreImage3);
                            uploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Map Image = new HashMap();
                                            Image.put("Third_Image_Url", uri.toString());
                                            rent_reference.updateChildren(Image);
                                            myPost_reference.updateChildren(Image);

                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Error:"+e.getMessage(),Toast.LENGTH_LONG).show();
                                    Log.v("msg","............................."+ e.getMessage());
                                }
                            });
                        }else{
                            Log.v("msg",".................................restore image3 is empty");
                            continue;
                        }
                        break;
                    case 3:
                        if(restoreImage4!= null)
                        {
                            StorageReference filePath = postImagesRef.child(restoreImage4.getLastPathSegment());
                            final UploadTask uploadtask = filePath.putFile(restoreImage4);
                            uploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Map Image = new HashMap();
                                            Image.put("Fourth_Image_Url", uri.toString());
                                            rent_reference.updateChildren(Image);
                                            myPost_reference.updateChildren(Image);
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Error:"+e.getMessage(),Toast.LENGTH_LONG).show();
                                    Log.v("msg","............................."+ e.getMessage());
                                }
                            });
                        }else{
                            Log.v("msg",".................................restore image4 is empty");
                            continue;
                        }
                        break;
                    case 4:
                        if(restoreImage5 != null)
                        {
                            StorageReference filePath = postImagesRef.child(restoreImage5.getLastPathSegment());
                            final UploadTask uploadtask = filePath.putFile(restoreImage5);
                            uploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Map Image = new HashMap();
                                            Image.put("Fifth_Image_Url", uri.toString());
                                            rent_reference.updateChildren(Image);
                                            myPost_reference.updateChildren(Image);
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),"Error:"+e.getMessage(),Toast.LENGTH_LONG).show();
                                    Log.v("msg","............................."+ e.getMessage());
                                }
                            });
                        }else{

                            Log.v("msg",".................................restore image5 is empty");
                            continue;
                        }
                        break;
                    case 5:
                        break;
                    default:
                        break;
                }


            }
            Rent_saveUploadDetails();
            progressDialog.dismiss();
      /*  Toast toast = Toast.makeText(getApplicationContext(), "Upload complete", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();*/
        }
    }

    private void Sales_saveUploadDetails()
    {
        Map details = new HashMap();
        details.put("Type","Sales Upload");
        details.put("Location",location_EditTxt.getText().toString());
        details.put("Price",Price_EditTxt.getText().toString());
        details.put("Telephone",Telephone_EditTxt.getText().toString());
        details.put("Description",Description_EditTxt.getText().toString());
        details.put("Time",SaveCurrentDate);
        details.put("Pid",productRandomKey);
        details.put("Poster_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
        sales_reference.updateChildren(details).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {
                    startActivity(new Intent(Add_House.this,HomeActivity.class));
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Details uploaded successfully...",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Error:"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        myPost_reference.updateChildren(details).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {
                    startActivity(new Intent(Add_House.this,HomeActivity.class));
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Details uploaded successfully...",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Error:"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Rent_saveUploadDetails()
    {
        Map details = new HashMap();
        details.put("Type","Rent Upload");
        details.put("Location",location_EditTxt.getText().toString());
        details.put("Price",RPM_EditTxt.getText().toString());
        details.put("Telephone",Telephone_EditTxt.getText().toString());
        details.put("Description",Description_EditTxt.getText().toString());
        details.put("Time",SaveCurrentDate);
        details.put("Pid",productRandomKey);
        details.put("Poster id",FirebaseAuth.getInstance().getCurrentUser().getUid());
        rent_reference.updateChildren(details).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {
                    startActivity(new Intent(Add_House.this,HomeActivity.class));
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Details uploaded successfully...",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Error:"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        myPost_reference.updateChildren(details).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful())
                {
                    startActivity(new Intent(Add_House.this,HomeActivity.class));
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Details uploaded successfully...",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Error:"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

}

