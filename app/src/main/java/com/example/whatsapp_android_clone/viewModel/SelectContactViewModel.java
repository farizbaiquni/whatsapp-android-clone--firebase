package com.example.whatsapp_android_clone.viewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.whatsapp_android_clone.adapter.SelectContactAdapter;
import com.example.whatsapp_android_clone.model.SelectContactModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectContactViewModel extends ViewModel {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser loggedUser = FirebaseAuth.getInstance().getCurrentUser();


    private MutableLiveData<Boolean> isConnectingDatabase = new MutableLiveData<>(false);
    public LiveData<Boolean> getIsConnectingDatabase(){
        return this.isConnectingDatabase;
    }

    private MutableLiveData<String> keyword = new MutableLiveData<>(null);
    public LiveData<String> getKeyword(){ return keyword; }
    public void setKeyword(String key){ this.keyword.setValue(key);}

    private MutableLiveData<Integer> numberContact = new MutableLiveData<Integer>(null);
    public LiveData<Integer> getNumberContact(){
        return this.numberContact;
    }

    private MutableLiveData<List<SelectContactModel>> selectContactModel = new MutableLiveData<>(
            Arrays.asList(
                    new SelectContactModel(
                            0, //type
                            "", //id
                            "", //photo
                            "New Group", //username or contactName
                            "" //description
                    ),

                    new SelectContactModel(
                            0, //type
                            "", //id
                            "", //photo
                            "New Contact", //username or contactName
                            "" //description
                    )
            )
    );

    public LiveData<List<SelectContactModel>> getSelectContactModel(){
        return this.selectContactModel;
    }

    public void setSelectContactModel(List<SelectContactModel> model){
        this.selectContactModel.setValue(model);
    }


    public void getContactList(){

        firebaseFirestore.collection("users")
                .document(loggedUser.getUid())
                .collection("contacts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            //Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        List<List<String>> tempContacts = new ArrayList<>();
                        List<String> tempContactsId = new ArrayList<>();

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


                        if(!tempContacts.isEmpty()) {

                            Log.d("IS TEMP CONTACTS EMPT", "NOOOOO");

                            numberContact.setValue(tempContacts.size());

                            int index = 0;
                            while (index < tempContacts.size()) {
                                tempContactsId.add(tempContacts.get(index).get(0));
                                index++;
                            }

                            Log.d("CONTACTS ID", tempContactsId.toString());

                            firebaseFirestore.collection("users")
                                    .whereIn("uid", tempContactsId)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {

                                                List<SelectContactModel> tempModel = new ArrayList<>();
                                                tempModel.add(new SelectContactModel(
                                                        0, //type
                                                        "", //id
                                                        "", //photo
                                                        "New Group", //username or contactName
                                                        "" //description
                                                ));
                                                tempModel.add(new SelectContactModel(
                                                        0, //type
                                                        "", //id
                                                        "", //photo
                                                        "New Contact", //username or contactName
                                                        "" //description
                                                ));

                                                int i = 0;
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    tempModel.add(new SelectContactModel(
                                                            1, //type
                                                            tempContacts.get(i).get(0), //id
                                                            document.getData().get("photoProfile").toString(), //photoProfile
                                                            tempContacts.get(i).get(1), //username or contactName
                                                            document.getData().get("description").toString()//description
                                                    ));
                                                    i++;
                                                }

//                                                selectContactModel.setValue(tempModel);

                                            } else {
                                                //Error getting documents
                                            }
                                        }
                                    });

                        //Contacts empty
                        } else {
                            numberContact.setValue(0);

                            List<SelectContactModel> tempModel = new ArrayList<>();

                            tempModel.add(new SelectContactModel(
                                    0, //type
                                    "", //id
                                    "", //photo
                                    "New Group", //username or contactName
                                    "" //description
                            ));

                            tempModel.add(new SelectContactModel(
                                    0, //type
                                    "", //id
                                    "", //photo
                                    "New Contact", //username or contactName
                                    "" //description
                            ));

                            selectContactModel.setValue(tempModel);


                        } // End if check is tempContacts empty


                    }
                });

    }// End getContactList








}
