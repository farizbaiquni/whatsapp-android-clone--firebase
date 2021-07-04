package com.example.whatsapp_android_clone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.whatsapp_android_clone.databinding.ActivityLoginBinding;
import com.example.whatsapp_android_clone.databinding.ActivityPersonalChatBinding;

public class PersonalChatActivity extends AppCompatActivity {

    private ActivityPersonalChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonalChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbarChat);
        getSupportActionBar().setTitle("Halo");

        binding.imgeButtonBackArrow.setOnClickListener(v -> {
            Intent intent = new Intent(PersonalChatActivity.this, MainActivity.class);
            startActivity(intent);
        });

        String idUser = getIntent().getExtras().get("idRoom").toString();
        Toast.makeText(this, idUser, Toast.LENGTH_SHORT).show();


    }
}