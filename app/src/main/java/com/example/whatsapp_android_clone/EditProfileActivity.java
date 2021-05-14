package com.example.whatsapp_android_clone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class EditProfileActivity extends AppCompatActivity {

    private String usernamePofile, descriptionProfile;
    EditText editTextUsername, editTextDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editTextUsername = findViewById(R.id.edit_text_edit_profil_name);
        editTextDescription = findViewById(R.id.edit_text_edit_description);

        usernamePofile = getIntent().getExtras().getString("username");
        descriptionProfile = getIntent().getExtras().getString(("description"));

        editTextUsername.setText(usernamePofile);
        editTextDescription.setText(descriptionProfile);


    }
}