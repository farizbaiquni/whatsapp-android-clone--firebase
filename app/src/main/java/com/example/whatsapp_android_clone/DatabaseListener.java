package com.example.whatsapp_android_clone;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class DatabaseListener extends ViewModel {

    public FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    public FirebaseFirestore firestoreDatabase = FirebaseFirestore.getInstance();
    public Boolean isUserInFirestoreExist = true;
    public Boolean isErrorOccuredDuringFetchUser = false;

    private MutableLiveData<String> username = new MutableLiveData<String>("No Connection....");
    public MutableLiveData<String> getUsername() {
        return username;
    }

    private MutableLiveData<String> description = new MutableLiveData<>("No Connection...");
    public MutableLiveData<String> getDescription(){
        return description;
    }

    private MutableLiveData<String> photoProfile = new MutableLiveData<>("");
    public MutableLiveData<String> getPhotoProfile(){
        return photoProfile;
    }

    private MutableLiveData<FirebaseUser> loggedUser = new MutableLiveData<>(null);
    public MutableLiveData<FirebaseUser> getLoggedUser(){
        return loggedUser;
    }

    public void checkIsUserLoggedIn(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            loggedUser.setValue(currentUser);
        }
    }

    //Get user information from firestore
    public void getUserInformation(String userId){
        final DocumentReference docRef = firestoreDatabase.collection("users").document(userId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                //LISTEN FAILED
                if (e != null) {
                    isErrorOccuredDuringFetchUser = true;
                    username.setValue("Error Fetch");
                    description.setValue("Error Fetch");
                    return;
                }

                //SUCCESS GET DATA
                if (snapshot != null && snapshot.exists()) {
                    isErrorOccuredDuringFetchUser = false;

                    if(snapshot.getData().get("username") == null && snapshot.getData().get("description") == null
                            && snapshot.getData().get("photoProfile") == null){
                        username.setValue("Not Set");
                        description.setValue("Not Set");
                    }

                    if(snapshot.getData().get("username") == null || snapshot.getData().get("username") == ""){
                        username.setValue("Not Set");
                    } else {
                        username.setValue(snapshot.getData().get("username").toString());
                    }

                    if(snapshot.getData().get("description") == null || snapshot.getData().get("description") == ""){
                        description.setValue("Not Set");
                    } else {
                        description.setValue(snapshot.getData().get("description").toString());
                    }

                    try {
                        photoProfile.setValue(snapshot.getData().get("photoProfile").toString());
                    } catch (Exception error){
                        photoProfile.setValue("");
                    }

                } else {
                    isUserInFirestoreExist = false;
                }
            }
        });
    }
}
