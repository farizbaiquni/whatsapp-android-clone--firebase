package com.example.whatsapp_android_clone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private TextView textViewUsername;
    private EditText editTextDescription;
    private CircleImageView circleImageViewPhotoProfile;

    private FirebaseUser currentUser;
    FirebaseFirestore firebaseFirestoreDatabase;
    private FirebaseStorage firebaseStorage;
    private StorageReference storagePhotoProfileReference;
    private DatabaseListener databaseListener;
    private final int IMAGE_CHOOSE_CODE = 10;
    private final int READ_EXTERNAL_STORAGE_CODE = 100;

    private Uri uriPhotoProfileUploaded;
    private String currentUsernameUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        uriPhotoProfileUploaded = null;
        databaseListener = new DatabaseListener();
        firebaseFirestoreDatabase = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storagePhotoProfileReference = firebaseStorage.getReference().child("photoProfile").child(currentUser.getUid());


        textViewUsername = findViewById(R.id.text_view_edit_profil_name);
        editTextDescription = findViewById(R.id.edit_text_edit_description);
        circleImageViewPhotoProfile = findViewById(R.id.image_profile_setting_menu);


        //View Model and Live Data to fetch user data from firestore sync / realtime
        databaseListener = new ViewModelProvider(this).get(DatabaseListener.class);
        //Check is user loggeed in or not, to avoid null exception
        databaseListener.checkIsUserLoggedIn();
        databaseListener.getLoggedUser().observe(EditProfileActivity.this, loggedUser -> {
            if(loggedUser != null){
                databaseListener.getUserInformation(loggedUser.getUid());
            }
        });

        //Updating profile layout based on user data in firestore and updating if there's a change
        databaseListener.getUsername().observe(EditProfileActivity.this, username -> {
            textViewUsername.setText(username);
            currentUsernameUser = username;
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

        textViewUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetEditUsername bottomSheetEditUsername = new BottomSheetEditUsername(currentUsernameUser, currentUser.getUid());
                bottomSheetEditUsername.show(getSupportFragmentManager(), "Show edit username");
            }
        });


        circleImageViewPhotoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CHECK PERMISSION TO READ EXTERNAL STORAGE
                if (Build.VERSION.SDK_INT >= 23) {
                    if(!checkExternalStoragePermission()){
                        ActivityCompat.requestPermissions(
                                EditProfileActivity.this,
                                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                READ_EXTERNAL_STORAGE_CODE);
                    } else {
                        pickAnImage();
                    }
                }

            }
        });

    } //End onCreate



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_CHOOSE_CODE && resultCode == RESULT_OK && data != null){
            Uri uriSelectedImage = data.getData();
            uploadPhotoProfile(uriSelectedImage, storagePhotoProfileReference);

            //circleImageViewPhotoProfile.setImageURI(selectedImage);
        }
    }

    private void pickAnImage(){
        Intent intentChooseImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooser = Intent.createChooser(intentChooseImage, "Pick one :");
        startActivityForResult(chooser, IMAGE_CHOOSE_CODE);
    }

    private boolean checkExternalStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(EditProfileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result;
    }

    public void uploadPhotoProfile(Uri uriImageSelected, StorageReference reference){

        UploadTask uploadTask = reference.putFile(uriImageSelected);

        //UPLOAD TO STORAGE IN FIREBASE
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

        //GET URL IMAGE FROM STORAGE
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    uriPhotoProfileUploaded = task.getResult();
                    updateUrlPhotoProfile(uriPhotoProfileUploaded.toString());
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }


    private void updateUrlPhotoProfile(String newUrl){

        DocumentReference userRef = firebaseFirestoreDatabase.collection("users").document(currentUser.getUid());

        // Set the "isCapital" field of the city 'DC'
        userRef
                .update("photoProfile", newUrl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error updating document", e);
                    }
                });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case READ_EXTERNAL_STORAGE_CODE :
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    pickAnImage();
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Toast.makeText(this, "Read External Storage is needed to change photo profile",
                            Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

} //End EditProfilActivity class