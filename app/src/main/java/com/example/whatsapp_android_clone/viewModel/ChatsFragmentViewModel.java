package com.example.whatsapp_android_clone.viewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.whatsapp_android_clone.model.ChatsFragmentModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatsFragmentViewModel extends ViewModel {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    //STATUS LISTENER (CALLED OR NOT)
    private MutableLiveData<Boolean> listenerRoomsCalled = new MutableLiveData<>(false);
    public LiveData<Boolean> getListenerRoomsCalled(){
        return this.listenerRoomsCalled;
    }

    private MutableLiveData<Boolean> listenerPersonalCalled = new MutableLiveData<>(false);
    public LiveData<Boolean> getListenerPersonalCalled(){
        return this.listenerPersonalCalled;
    }

    private MutableLiveData<Boolean> listenerContactNamelCalled = new MutableLiveData<>(false);
    public LiveData<Boolean> getListenerContactNameCalled(){
        return this.listenerContactNamelCalled;
    }

    private MutableLiveData<Boolean> listenerUpdateChatProfileList = new MutableLiveData<>(false);
    public LiveData<Boolean> getListenerUpdateChatProfileList(){
        return this.listenerUpdateChatProfileList;
    }

    private MutableLiveData<Boolean> allUsernamePhotoIsEmpty = new MutableLiveData<>(false);
    public LiveData<Boolean> getAllUsernamePhotoIsEmpty(){
        return this.allUsernamePhotoIsEmpty;
    }

    //ISINYA ADALAH {typeRoom : idRoom}
    private MutableLiveData<Map<String, String>> roomsMap =  new MutableLiveData<>(null);
    public LiveData<Map<String, String>> getRoomsMap(){
        return this.roomsMap;
    }

    //ISINYA ADALAH {idUser: [username, photoUrl]}
    private MutableLiveData<Map<String, List<String>>> allUsernamePhoto = new MutableLiveData<>(null);
    public LiveData<Map<String, List<String>>> getAllUsernamePhoto(){
        return this.allUsernamePhoto;
    }
    //ISINYA ADALAH {idUser: [username, photoUrl]}
    private MutableLiveData<Map<String, List<String>>> personalMap = new MutableLiveData<>();
    public LiveData<Map<String, List<String>>> getPersonalMap(){
        return this.personalMap;
    }

    //ISINYA ADALAH {idUser: contactName}
    private MutableLiveData<Map<String, String>> contactNameMap = new MutableLiveData<>();
    public LiveData<Map<String, String>> getContactNameMap(){
        return this.contactNameMap;
    }

    //ISINYA ADALAH [ [photo, username, message, Messagedate] ]
    private MutableLiveData<List<ChatsFragmentModel>> chatsProfileList = new MutableLiveData<>();
    public LiveData<List<ChatsFragmentModel>> getChatsProfileList(){
        return this.chatsProfileList;
    }


    private MutableLiveData<String> keyword = new MutableLiveData<>(null);
    public LiveData<String> getKeyword(){
        return this.keyword;
    }
    public void setKeyword(String key){
        this.keyword.setValue(key);
    }


    public void gettingListenerRooms(){

        firebaseFirestore.collection("users").document(currentUser.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    //Log.d("ROOM MAP SNAPSHOT ", snapshot.getData().get("roomsMap").toString());
                    if(snapshot.getData().get("roomsMap") != null){
                        listenerRoomsCalled.setValue(true);
                        roomsMap.setValue((Map)snapshot.getData().get("roomsMap"));
                        //Log.d("ROOOM MAP ", snapshot.getData().get("roomsMap").toString());
                        //Log.d("ROOOM MAP ", roomsMap.getValue().toString());
                    }
                } else {
                    //Log.d(TAG, "Current data: null");
                }

            }
        });

    }

    public void gettingListenerPersonal(){
        if(getRoomsMap().getValue() != null && !getRoomsMap().getValue().isEmpty()){

            List<String> tempRoomsPersonalList = new ArrayList<>();

            int a = 0;
            for (Map.Entry<String, String> entry : getRoomsMap().getValue().entrySet()) {
                //FILTER ROOM -> ONLY PERSONAL ROOM
                if(entry.getValue().equals("personal")){
                    tempRoomsPersonalList.add(entry.getKey());
                }

                if( (a == (getRoomsMap().getValue().size() - 1)) &&
                        (tempRoomsPersonalList != null && !tempRoomsPersonalList.isEmpty())) {
                    //Log.d("ROOMS PERSONAL USER ", tempRoomsPersonalList.toString());
                    firebaseFirestore.collection("users")
                            .whereArrayContainsAny("rooms", tempRoomsPersonalList)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value,
                                                    @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        //Log.w(TAG, "Listen failed.", e);
                                        return;
                                    }

                                    Map<String, List<String>> tempPersonalMap = new HashMap<>();

                                    int i = 0;
                                    for (QueryDocumentSnapshot doc : value) {
                                        allUsernamePhotoIsEmpty.setValue(false);
                                        if(!doc.getData().get("uid").equals(currentUser.getUid())){
                                            List<String> tempValuePersonalList = new ArrayList<>();
                                            tempValuePersonalList.add(doc.getData().get("username").toString());
                                            tempValuePersonalList.add(doc.getData().get("photoProfile").toString());
                                            tempPersonalMap.put(doc.getData().get("uid").toString(), tempValuePersonalList);
                                            //Log.d("DOC USER ", doc.getData().toString());
                                        }
                                        if(i == (value.size() - 1)){
                                            personalMap.setValue(tempPersonalMap);
                                            listenerPersonalCalled.setValue(true);
                                            //Log.d("PERSONAL MAP ", personalMap.getValue().toString());
                                        }
                                        i++;
                                    }
                                }
                            });
                }
                a++;

            }

            //Log.d("hhhhh ",  Integer.toString(getRoomsMap().getValue().size()));

        }
    }

    public void gettingListenerContactName(){

        Map<String, String> tempContactNameMap = new HashMap<>();

        firebaseFirestore.collection("users")
                .document(currentUser.getUid())
                .collection("contacts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            //Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        //Log.d("LENGTH ", Integer.toString(value.size()));
                        //Log.d("LENGTH ", personalMap.getValue().toString());

                        if(value.size() <= 0){
                            if(personalMap.getValue() == null){
                                allUsernamePhotoIsEmpty.setValue(true);
                                if(listenerContactNamelCalled.getValue().equals(false)){
                                    listenerContactNamelCalled.setValue(true);
                                }
                            }else if(personalMap.getValue().isEmpty()) {
                                allUsernamePhotoIsEmpty.setValue(true);
                                if(listenerContactNamelCalled.getValue().equals(false)){
                                    listenerContactNamelCalled.setValue(true);
                                }
                            }else{
                                allUsernamePhotoIsEmpty.setValue(false);
                                if(listenerContactNamelCalled.getValue().equals(false)){
                                    listenerContactNamelCalled.setValue(true);
                                    updateAllUsernamePhoto();
                                }
                            }
                        }

                        int i = 0;
                        for (QueryDocumentSnapshot doc : value) {
                            allUsernamePhotoIsEmpty.setValue(false);
                            //Log.d("GGGGGGGGGGGGGGGGG ", "personalMap.getValue().toString()");
                            tempContactNameMap.put(doc.getData().get("id").toString(),
                                    doc.getData().get("contactName").toString());
                            if( i == (value.size() - 1)){
                                //Log.d("CONTACT NAME MAP  ", tempContactNameMap.toString());
                                listenerContactNamelCalled.setValue(true);
                                contactNameMap.setValue(tempContactNameMap);
                            }
                            i++;
                        }
                    }
                });
    }

    public void updateAllUsernamePhoto(){

        if(listenerRoomsCalled.getValue().equals(true) && listenerPersonalCalled.getValue().equals(true)
                && listenerContactNamelCalled.getValue().equals(true)){

            if(personalMap.getValue() != null && contactNameMap.getValue() != null){
                Log.d("OPSI PERTAMA ", "DIPANGGIL");

                Map<String, List<String>> tempAllUsernamePhoto = new HashMap<>(personalMap.getValue());

                for (Map.Entry<String, String> entry : contactNameMap.getValue().entrySet()) {
                    if(tempAllUsernamePhoto.containsKey(entry.getKey())){
                        tempAllUsernamePhoto.replace(entry.getKey(), Arrays.asList(entry.getValue(),
                                tempAllUsernamePhoto.get(entry.getKey()).get(1)));
                    } else {
                        tempAllUsernamePhoto.put(entry.getKey(), Arrays.asList(entry.getValue(), ""));
                    }
                }

                allUsernamePhoto.setValue(tempAllUsernamePhoto);
                tempAllUsernamePhoto.clear();

            } else if(personalMap.getValue() == null && contactNameMap.getValue() != null){

                Log.d("OPSI KEDUA ", "DIPANGGIL");
                Map <String, List<String>>tempAllUsernamePhoto = new HashMap<>();

                for (Map.Entry<String, String> entry : contactNameMap.getValue().entrySet()) {
                    tempAllUsernamePhoto.put(entry.getKey(), Arrays.asList(entry.getValue(), ""));
                }

                allUsernamePhoto.setValue(tempAllUsernamePhoto);
                tempAllUsernamePhoto.clear();

            } else if(personalMap.getValue() != null && contactNameMap.getValue() == null){
                Log.d("OPSI KETIGA ", "DIPANGGIL");
                allUsernamePhoto.setValue(personalMap.getValue());
                //Log.d("OPSI KETIGA ", allUsernamePhoto.getValue().toString());

            }

        }

    }

    public void updateChatsProfileList(){

        //Log.d("ALL USERNAME PHOTO ", allUsernamePhotoParam.toString());

        if(listenerUpdateChatProfileList.getValue().equals(false)){
            if(roomsMap.getValue() != null && !roomsMap.getValue().isEmpty()) {
                Map<String, List<String>> allUsernamePhotoParam = new HashMap<>(allUsernamePhoto.getValue());
                List<ChatsFragmentModel> tempAllModelList = new ArrayList<>();
                List<String> tempAllRoomsList = new ArrayList<>(roomsMap.getValue().keySet());

                if(allUsernamePhotoParam != null && !allUsernamePhotoParam.isEmpty()){
                    firebaseFirestore.collection("rooms")
                            .whereIn("idRoom", tempAllRoomsList)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value,
                                                    @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        //Log.w(TAG, "Listen failed.", e);
                                        return;
                                    }

                                    tempAllModelList.clear();

                                    int i = 0;
                                    for (QueryDocumentSnapshot doc : value) {
                                        // TYPE ROOM PERSONAL
                                        if(doc.getData().get("typeRoom").toString().equals("personal")){
                                            List<List<String>> tempMember = new ArrayList<>();
                                            tempMember.add((List) doc.getData().get("member"));
                                            tempMember.get(0).remove(currentUser.getUid());
                                            String id = tempMember.get(0).get(0);
                                            //Log.d("ID USER ", allUsernamePhotoParam.toString());

                                            tempAllModelList.add(new ChatsFragmentModel(
                                                    doc.getId(),
                                                    allUsernamePhotoParam.get(id).get(1).toString(), //Photo
                                                    allUsernamePhotoParam.get(id).get(0).toString(), //Username
                                                    ((Map)doc.getData().get("lastMessage")).get("message").toString(),//lastMessage
                                                    ((Map)doc.getData().get("lastMessage")).get("date").toString(), //lastMessageDate
                                                    "0"
                                            ));
                                        }

                                        i++;
                                        if(i == (value.size() - 1)){
                                            listenerUpdateChatProfileList.setValue(true);
                                            chatsProfileList.setValue(tempAllModelList);
                                        }

                                    }
                                }
                            });
                }
            }
        }

    } // End Void


}
