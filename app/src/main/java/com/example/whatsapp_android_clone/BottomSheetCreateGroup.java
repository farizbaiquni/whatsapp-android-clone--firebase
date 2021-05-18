package com.example.whatsapp_android_clone;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.zip.Inflater;

public class BottomSheetCreateGroup extends BottomSheetDialogFragment {

    private EditText editTextGroupName;
    private TextView textViewCountText, cancelCreateGroup, saveCreateGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetCreateGroup.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_create_group, container, false);
        editTextGroupName = view.findViewById(R.id.edit_text_group_name);
        textViewCountText = view.findViewById(R.id.text_view_grouo_count_text);
        cancelCreateGroup = view.findViewById(R.id.text_view_group_cancel);
        editTextGroupName.requestFocus();
        editTextGroupName.setSelectAllOnFocus(true);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textViewCountText.setText(String.valueOf(25 - s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        editTextGroupName.addTextChangedListener(textWatcher);


        cancelCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

}
