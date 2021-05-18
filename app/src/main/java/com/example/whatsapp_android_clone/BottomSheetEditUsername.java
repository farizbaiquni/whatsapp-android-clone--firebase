package com.example.whatsapp_android_clone;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BottomSheetEditUsername extends BottomSheetDialogFragment {

    private String username, userId;
    private FirebaseFirestore firebaseFirestore;

    BottomSheetEditUsername(String username, String userId){
        this.username = username;
        this.userId = userId;
    }

    private EditText editTextInputUsername;
    private TextView textViewCountText, cancelEditUsername, saveEditUsername;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetCreateGroup.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_edit_username, container, false);
        editTextInputUsername = view.findViewById(R.id.edit_text_edit_username_name);
        textViewCountText = view.findViewById(R.id.text_view_edit_username_count_text);
        cancelEditUsername = view.findViewById(R.id.text_view_edit_username_cancel);
        saveEditUsername = view.findViewById(R.id.text_view_edit_username_save);
        editTextInputUsername.setText(username);
        editTextInputUsername.requestFocus();

        firebaseFirestore = FirebaseFirestore.getInstance();

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

        editTextInputUsername.addTextChangedListener(textWatcher);


        cancelEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        saveEditUsername.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {

                if(editTextInputUsername.length() <= 0){
                    Toast.makeText(getActivity(), "Name can't empty", Toast.LENGTH_SHORT).show();
                } else {
                    DocumentReference washingtonRef = firebaseFirestore.collection("users").document(userId);
                    washingtonRef
                            .update("username", editTextInputUsername.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dismiss();
                                }
                            });
                }
            }
        });

        return view;
    }

}
