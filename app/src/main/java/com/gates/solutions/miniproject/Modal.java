package com.gates.solutions.miniproject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Modal extends BottomSheetDialogFragment {
    private ButtomSheetListener mListener;

    TextView gallery_layout,removePic_layout;
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

        gallery_layout = view.findViewById(R.id.Upload_id);
        removePic_layout = view.findViewById(R.id.Remove_id);
        modal_layout = view.findViewById(R.id.modal_id);


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
