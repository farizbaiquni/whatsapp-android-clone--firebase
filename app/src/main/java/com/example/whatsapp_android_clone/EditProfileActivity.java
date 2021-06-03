package com.example.whatsapp_android_clone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private TextView textViewUsername;
    private EditText editTextDescription;
    private CircleImageView circleImageViewPhotoProfile;

    private FirebaseUser currentUser;
    private DatabaseListener databaseListener;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storagePhotoProfileReference;

    private final int CHOOSE_IMAGE_GALERRY_CODE = 10;
    private final int CHOOSE_IMAGE_CAMERA_CODE = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle("Profile");
        }
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
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
                databaseListener.updateUserInformation(loggedUser.getUid());
            }
        });


        //Updating profile layout based on user data in firestore and updating if there's a change
        databaseListener.getUserInformation().observe(EditProfileActivity.this, userData -> {
            if(userData != null){

                try {
                    Picasso.get().load(userData.get(0)).into(circleImageViewPhotoProfile);
                }catch (Exception e){
                    circleImageViewPhotoProfile.setImageResource(R.drawable.friends);
                }

                textViewUsername.setText(userData.get(1));
                editTextDescription.setText(userData.get(2));

            }
        });


        textViewUsername.setOnClickListener(v -> {
            BottomSheetEditUsername bottomSheetEditUsername =
                    new BottomSheetEditUsername(databaseListener.getUserInformation().getValue().get(1), currentUser.getUid());
            bottomSheetEditUsername.show(getSupportFragmentManager(), "Show edit username");
        });


        circleImageViewPhotoProfile.setOnClickListener(v -> {
            BottomSheetOptionChangePhotoProfile bottomSheetOptionChangePhotoProfile = new
                    BottomSheetOptionChangePhotoProfile(currentUser.getUid() ,storagePhotoProfileReference);
            bottomSheetOptionChangePhotoProfile.show(getSupportFragmentManager(), "SHOW OPTION");
        });

    } //End onCreate



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE_CAMERA_CODE && resultCode == RESULT_OK && data != null){
            Bundle extras = data.getExtras();
            Bitmap bitmapImageCaptured = (Bitmap) extras.get("data");
            uploadPhotoProfileFromCamera(bitmapImageCaptured, storagePhotoProfileReference);

        } else if (requestCode == CHOOSE_IMAGE_GALERRY_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageFromGallery = data.getData();
            uploadPhotoProfileFromGallery(selectedImageFromGallery, storagePhotoProfileReference);

        }
    }


    public void uploadPhotoProfileFromGallery(Uri uriImageSelected, StorageReference reference){

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

                //GET URL IMAGE FROM STORAGE
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                            Uri downloadUri = task.getResult();
                            updateUrlPhotoProfile(downloadUri.toString());
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });
            }
        });
    }


    public void uploadPhotoProfileFromCamera(Bitmap bitmapPhotoCaptured, StorageReference reference){
        //UPLOAD IMAGE TO STORAGE FIREBASE
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapPhotoCaptured.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = reference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(EditProfileActivity.this, "Failed Upload Foto", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //GET URL IMAGE FROM STORAGE FIREBASE
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                            Uri downloadUri = task.getResult();
                            updateUrlPhotoProfile(downloadUri.toString());
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });
            }
        });
    }

    //UPDATE URL PHOTO PROFILE IN FIRESTORE
    private void updateUrlPhotoProfile(String newUrl){
        DocumentReference userRef = firebaseFirestore.collection("users").document(currentUser.getUid());
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



} //End EditProfilActivity class