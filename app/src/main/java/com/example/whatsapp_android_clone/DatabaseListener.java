package com.example.whatsapp_android_clone;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DatabaseListener extends ViewModel {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public FirebaseFirestore firestoreDatabase = FirebaseFirestore.getInstance();
    public Boolean isUserInFirestoreExist = true;
    public Boolean isErrorOccuredDuringFetchUser = false;

    private MutableLiveData<List<SelectContactModel>> contactsList = new MutableLiveData<>(null);
    public MutableLiveData<List<SelectContactModel>> getContactsList(){return contactsList; }

    private MutableLiveData<String> keyword = new MutableLiveData<>(null);
    public MutableLiveData<String> getKeyword(){ return keyword; }
    public void setKeyword(String key){ this.keyword.setValue(key);}

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

    } // End getUserInformation


    public void getUserContacts(String userId){
        firestoreDatabase.collection("users")
                .document(userId)
                .collection("contacts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            //Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        List<List<String>> tempContacts = new ArrayList<>();

                        for (QueryDocumentSnapshot doc : value) {

                            List<String> contact = new ArrayList<>();

                            if (doc.getData().get("id") != null) {
                                contact.add(doc.getData().get("id").toString());
                            }

                            if (doc.getData().get("contactName") != null) {
                                contact.add(doc.getData().get("contactName").toString());
                            }

                            tempContacts.add(contact);
                        }

                        if(tempContacts != null){

                            List<String> contactsId = new ArrayList<>();

                            int index = 0;
                            while(index < tempContacts.size()){
                                contactsId.add(tempContacts.get(index).get(0));
                                index++;
                            }

                            firebaseFirestore.collection("users")
                                    .whereIn("uid", contactsId)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                int i = 0;
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    tempContacts.get(i).add(document.getData().get("photoProfile").toString());
                                                    tempContacts.get(i).add(document.getData().get("description").toString());
                                                    i++;
                                                }

                                                List<SelectContactModel> selectContactModelList = new ArrayList<>();

                                                selectContactModelList.add(new SelectContactModel(
                                                        0, //type
                                                        "", //id
                                                        "", //photo
                                                        "New Group", //username or contactName
                                                        "" //description
                                                ));

                                                selectContactModelList.add(new SelectContactModel(
                                                        0, //type
                                                        "", //id
                                                        "", //photo
                                                        "New Contact", //username or contactName
                                                        "" //description
                                                ));

                                                for(int a = 0; a < tempContacts.size(); a++ ){
                                                    selectContactModelList.add(new SelectContactModel(
                                                            1, //type
                                                            tempContacts.get(a).get(0), //id
                                                            tempContacts.get(a).get(2), //photo
                                                            tempContacts.get(a).get(1), //username or contactName
                                                            tempContacts.get(a).get(3) //description
                                                    ));
                                                }

                                                contactsList.setValue(selectContactModelList);

                                            } else {
                                                //Error getting documents
                                            }
                                        }
                                    });

                        }

//                        contacts.setValue(tempContacts);
                    }
                });

    }// End getUserContacts


}
