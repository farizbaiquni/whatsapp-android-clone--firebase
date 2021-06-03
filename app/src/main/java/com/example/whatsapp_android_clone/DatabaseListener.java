package com.example.whatsapp_android_clone;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
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
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


    public FirebaseFirestore firestoreDatabase = FirebaseFirestore.getInstance();
    public Boolean isUserInFirestoreExist = true;
    public Boolean isErrorOccuredDuringFetchUser = false;



    //================================== LOGGED USER INFORMATION / PROFILE ==================================
    private MutableLiveData<List<SettingMenuModel>> settingMenuModel = new MutableLiveData<>(null);
    public LiveData<List<SettingMenuModel>> getSettingMenuModel(){
        return settingMenuModel;
    }
    public void initSettingMenuModel(String settingMenuDesctiptions [], String settingMenuNames[]){

        List<SettingMenuModel> tempSettingMenuModel = new ArrayList<>();

        int settingMenuImages[] = {R.drawable.key, R.drawable.chat, R.drawable.notification,
                R.drawable.usage, R.drawable.help};


        //PROFILE
        tempSettingMenuModel.add(new SettingMenuModel(
                SettingMenuModel.PROFILE_TYPE,
                R.drawable.friends,
                "Getting Data...",
                "Getting Data...",
                R.drawable.barcode,
                ""
        ));


        //OPTION MENU
        for(int i = 0; i <= settingMenuNames.length - 1; i++){
            tempSettingMenuModel.add(new SettingMenuModel(SettingMenuModel.MENU_TYPE,
                    settingMenuImages[i],
                    settingMenuNames[i],
                    settingMenuDesctiptions[i],
                    0,
                    ""));
        }

        //INVITE A FRIEND
        tempSettingMenuModel.add(new SettingMenuModel(SettingMenuModel.INVITE_FRIEND_TYPE,
                R.drawable.friends,
                "Invite a friend",
                "",
                0,
                ""));

        settingMenuModel.setValue(null);
        settingMenuModel.setValue(tempSettingMenuModel);

    }


    private MutableLiveData<List<String>> userInformation = new MutableLiveData<>(null);
    public LiveData<List<String>> getUserInformation(){
        return userInformation;
    }
    public void updateUserInformation(String userId){
        userInformation.setValue(null);

        List<String> tempUserInformation = new ArrayList<>();
        List<SettingMenuModel> tempSettingMenuModel = new ArrayList<>();

        final DocumentReference docRef = firestoreDatabase.collection("users").document(userId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                //LISTEN FAILED
                if (e != null) {
                    isErrorOccuredDuringFetchUser = true;
                    tempUserInformation.add("");
                    tempUserInformation.add("Error getting data");
                    tempUserInformation.add("Error getting data");
                    userInformation.setValue(tempUserInformation);
                    return;
                }

                //SUCCESS GET DATA
                if (snapshot != null && snapshot.exists()) {
                    isErrorOccuredDuringFetchUser = false;

                    try {
                        tempUserInformation.add(snapshot.getData().get("photoProfile").toString());
                    } catch (Exception ee){
                        tempUserInformation.add("");
                    }

                    if(snapshot.getData().get("username") == null || snapshot.getData().get("username").toString().length() < 1){
                        tempUserInformation.add("Not Set");
                    } else {
                        tempUserInformation.add(snapshot.getData().get("username").toString());
                    }

                    if(snapshot.getData().get("description") == null || snapshot.getData().get("description").toString().length() < 1){
                        tempUserInformation.add("Not Set");
                    } else {
                        tempUserInformation.add(snapshot.getData().get("description").toString());
                    }

                    userInformation.setValue(tempUserInformation);


                } else {
                    isUserInFirestoreExist = false;
                }
            }
        });


    } // End getUserInformation






    //================================== SELECT CONTACT ==================================
    private MutableLiveData<List<SelectContactModel>> contactsList = new MutableLiveData<>(null);
    public LiveData<List<SelectContactModel>> getContactsList(){return contactsList; }

    private MutableLiveData<String> keyword = new MutableLiveData<>(null);
    public LiveData<String> getKeyword(){ return keyword; }
    public void setKeyword(String key){ this.keyword.setValue(key);}

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





    //================================== CHECK IS USER LOGGED IN ==================================
    private MutableLiveData<FirebaseUser> loggedUser = new MutableLiveData<>(null);
    public LiveData<FirebaseUser> getLoggedUser(){
        return loggedUser;
    }
    public void checkIsUserLoggedIn(){
        if(currentUser != null){
            loggedUser.setValue(currentUser);
        }
    }








}
