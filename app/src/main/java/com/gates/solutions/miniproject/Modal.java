package com.gates.solutions.miniproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Dialog;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Modal extends BottomSheetDialogFragment {
    private ButtomSheetListener mListener;

    TextView camera_layout,gallery_layout,removePic_layout;
    LinearLayout modal_layout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.modal,container,false);
        return v;
    }

    public interface ButtomSheetListener {
        void onModalItemSelected(int ItemId);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (ButtomSheetListener ) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+" must implement ButtomSheetListener ");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        camera_layout = view.findViewById(R.id.cameraLayout_id);
        gallery_layout = view.findViewById(R.id.Upload_id);
        removePic_layout = view.findViewById(R.id.Remove_id);
        modal_layout = view.findViewById(R.id.modal_id);


        camera_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onModalItemSelected(R.id.cameraLayout_id);
                dismiss();
            }
        });

        gallery_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onModalItemSelected(R.id.Upload_id);
                dismiss();
            }
        });

        removePic_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onModalItemSelected(R.id.Remove_id);
                dismiss();
            }
        });
    }

}
