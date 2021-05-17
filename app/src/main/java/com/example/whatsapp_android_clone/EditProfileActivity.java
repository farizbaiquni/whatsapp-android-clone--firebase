package com.example.whatsapp_android_clone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    EditText editTextUsername, editTextDescription;
    CircleImageView circleImageViewPhotoProfile;

    DatabaseListener databaseListener = new DatabaseListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        databaseListener.getUserInformation();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editTextUsername = findViewById(R.id.edit_text_edit_profil_name);
        editTextDescription = findViewById(R.id.edit_text_edit_description);
        circleImageViewPhotoProfile = findViewById(R.id.image_profile_setting_menu);

        //View Model and Live Data to fetch user data from firestore sync / realtime
        databaseListener = new ViewModelProvider(this).get(DatabaseListener.class);
        databaseListener.getUserInformation();

        //Updating profile layout based on user data in firestore and updating if there's a change
        databaseListener.getUsername().observe(EditProfileActivity.this, username -> {
            editTextUsername.setText(username);
        });


        databaseListener.getDescription().observe(EditProfileActivity.this, description -> {
            editTextDescription.setText(description);
        });


        databaseListener.getPhotoProfile().observe(EditProfileActivity.this, photoProfile -> {
            try{
                Picasso.get().load(photoProfile).into(circleImageViewPhotoProfile);
            } catch (Exception e){
                circleImageViewPhotoProfile.setImageResource(R.drawable.friends);
            }
        });



    }
}