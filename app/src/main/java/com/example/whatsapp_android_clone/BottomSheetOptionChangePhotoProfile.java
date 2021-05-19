package com.example.whatsapp_android_clone;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;


public class BottomSheetOptionChangePhotoProfile extends BottomSheetDialogFragment {

    String userId;
    StorageReference storagePhotoProfileReference;

    private FirebaseFirestore firebaseFirestore;

    private LinearLayout removePhoto, pickFromGallery, pickFromCamera;

    private final int CHOOSE_IMAGE_GALERRY_CODE = 10;
    private final int CHOOSE_IMAGE_CAMERA_CODE = 20;
    private final int PERMISSION_READ_EXTERNAL_STORAGE_CODE = 100;
    private final int PERMISSION_CAMERA_CODE = 200;


    BottomSheetOptionChangePhotoProfile(String userId ,StorageReference storageReference){
        this.userId = userId;
        this.storagePhotoProfileReference = storageReference;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetCreateGroup.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_option_pick_photo_profile, container, false);
        removePhoto = view.findViewById(R.id.remove_photo_option_change_photo_profile);
        pickFromGallery = view.findViewById(R.id.gallery_option_change_photo_profile);
        pickFromCamera = view.findViewById(R.id.camera_option_change_photo_profile);
        firebaseFirestore = FirebaseFirestore.getInstance();

        removePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePhotoProfile();
            }
        });

        pickFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CHECK CAMERA PERMISSION
                if (Build.VERSION.SDK_INT >= 23) {
                    if(!checkCameraPermission()){
                        ActivityCompat.requestPermissions(
                                getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                PERMISSION_CAMERA_CODE);
                    } else {
                        pickAnImageFromCamera();
                        dismiss();
                    }
                }
            }
        });

        pickFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CHECK READ EXTERNAL STORAGE PERMISSION
                if (Build.VERSION.SDK_INT >= 23) {
                    if(!checkReadExternalStoragePermission()){
                        ActivityCompat.requestPermissions(
                                getActivity(),
                                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                PERMISSION_READ_EXTERNAL_STORAGE_CODE);
                    } else {
                        pickAnImageFromGallery();
                        dismiss();
                    }
                }

            }
        });

        return view;

    } // End onCreateView


    private void pickAnImageFromGallery(){
        Intent intentChooseImageGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooser = Intent.createChooser(intentChooseImageGallery, "Pick one :");
        try {
            getActivity().startActivityForResult(chooser, CHOOSE_IMAGE_GALERRY_CODE);
        } catch (Exception e){
            Toast.makeText(getActivity(), "Failed open gallery", Toast.LENGTH_SHORT).show();
        }
    }

    private void pickAnImageFromCamera(){
        Intent intentTakePictureFromCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            getActivity().startActivityForResult(intentTakePictureFromCamera, CHOOSE_IMAGE_CAMERA_CODE );
        } catch (Exception e){
            Toast.makeText(getActivity(), "Failed open camera", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkReadExternalStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result;
    }

    private boolean checkCameraPermission(){
        boolean cameraPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
        return cameraPermission;
    }

    private void removePhotoProfile(){
        // Delete the file
        storagePhotoProfileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DocumentReference washingtonRef = firebaseFirestore.collection("users").document(userId);
                washingtonRef
                        .update("photoProfile", "")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "Error remove photo profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_READ_EXTERNAL_STORAGE_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    pickAnImageFromGallery();
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    Toast.makeText(getActivity(), "Read External Storage is needed to change photo profile",
                            Toast.LENGTH_SHORT).show();
                }
                return;

            case PERMISSION_CAMERA_CODE:
                if(grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        pickAnImageFromCamera();
                    }
                } else {
                    Toast.makeText(getActivity(), "Camera permission is needed to take a picture from camera", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }


}
