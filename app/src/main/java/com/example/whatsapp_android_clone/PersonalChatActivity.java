package com.example.whatsapp_android_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.whatsapp_android_clone.adapter.PersonalChatAdapter;
import com.example.whatsapp_android_clone.databinding.ActivityPersonalChatBinding;
import com.example.whatsapp_android_clone.viewModel.PersonalChatViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class PersonalChatActivity extends AppCompatActivity {

    private ActivityPersonalChatBinding binding;
    private PersonalChatViewModel personalChatViewModel;
    private PersonalChatAdapter personalChatAdapter;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonalChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbarChat);

        personalChatViewModel = new ViewModelProvider(this).get(PersonalChatViewModel.class);

        binding.imgeButtonBackArrow.setOnClickListener(v -> {
            Intent intent = new Intent(PersonalChatActivity.this, MainActivity.class);
            startActivity(intent);
        });

        String idRoom = getIntent().getExtras().get("idRoom").toString();
        String opponentUsername = getIntent().getExtras().getString("opponentUsername");

        binding.textViewNameToolbar.setText(opponentUsername);
        if(binding.editTextSendMessage != null){
            if(binding.editTextSendMessage.length() > 0){
                binding.buttonSendMessage.setEnabled(true);
            }else{
                binding.buttonSendMessage.setEnabled(false);
            }
        } else {
            binding.buttonSendMessage.setEnabled(false);
        }


        binding.buttonSendMessage.setOnClickListener(v -> {

            LocalDateTime dateTime = LocalDateTime.now();
            Map<String, Object> message = new HashMap<>();
            message.put("message", binding.editTextSendMessage.getText().toString());
            message.put("userId", currentUser.getUid());
            message.put("timestamps", dateTime.toString());
            message.put("deleteStatus", false);


            firebaseFirestore.collection("rooms")
                    .document(idRoom)
                    .collection("messages")
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.w(TAG, "Error adding document", e);
                        }
                    });

        });


        binding.editTextSendMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0){
                    //Toast.makeText(PersonalChatActivity.this, "more", Toast.LENGTH_SHORT).show();
                    binding.buttonSendMessage.setEnabled(true);
                } else {
                    //Toast.makeText(PersonalChatActivity.this, "less", Toast.LENGTH_SHORT).show();
                    binding.buttonSendMessage.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(personalChatViewModel.getListenerRooms().getValue().equals(false)){
            personalChatViewModel.gettingPersonalChats(idRoom);
        }

//        personalChatViewModel.getPersonalChat().observe(PersonalChatActivity.this, chats -> {
//            if(chats != null && !chats.isEmpty()){
//                personalChatAdapter = new PersonalChatAdapter(PersonalChatActivity.this, chats);
//                binding.recyclerViewPersonalChats.setAdapter(personalChatAdapter);
//                binding.recyclerViewPersonalChats.setLayoutManager(new LinearLayoutManager(this));
//            }
//        });

        personalChatViewModel.getPersonalChatMap().observe(PersonalChatActivity.this, chats -> {
            if(chats != null && !chats.isEmpty()){
                personalChatAdapter = new PersonalChatAdapter(PersonalChatActivity.this, chats);
                binding.recyclerViewPersonalChats.setAdapter(personalChatAdapter);
                binding.recyclerViewPersonalChats.setLayoutManager(new LinearLayoutManager(this));
            }
        });
    }
}