package com.example.whatsapp_android_clone.viewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.whatsapp_android_clone.model.ChatsFragmentModel;
import com.example.whatsapp_android_clone.model.SelectContactModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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
import java.util.stream.Collectors;

public class ChatsFragmentViewModel extends ViewModel {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


    private MutableLiveData<List<ChatsFragmentModel>> searchChatsProfileList = new MutableLiveData<>();
    public LiveData<List<ChatsFragmentModel>> getSearchChatsProfileList(){
        return this.searchChatsProfileList;
    }
    public void setSearchChatsProfileList(List<ChatsFragmentModel> model){
        this.searchChatsProfileList.setValue(model);
    }


    private MutableLiveData<List<List<ChatsFragmentModel>>> chatsProfileList = new MutableLiveData<>();
    public LiveData<List<List<ChatsFragmentModel>>> getChatsProfileList(){
        return this.chatsProfileList;
    }


    private MutableLiveData<Map> roomsList =  new MutableLiveData<>();
    public LiveData<Map> getRoomsList(){
        return this.roomsList;
    }


    private MutableLiveData<String> keyword = new MutableLiveData<>();
    public LiveData<String> getKeyword(){
        return this.keyword;
    }

    public void setKeyword(String key){
        this.keyword.setValue(key);
    }


    //Get id_room array ---> Listener
    //Get room based on id_room array ---> Listener
    //Loop each fetch and assign to tempChatsList

    public void gettingRoomsList(){
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
                    roomsList.setValue((Map)snapshot.getData().get("roomsMap"));
                    //Log.d("ROOMS MAP ", roomsList.getValue().toString());

                } else {
                    //Log.d(TAG, "Current data: null");
                }

            }
        });

    }


    public void gettingChatsList(Map<String, String> rooms){

        //Log.d("LIST ", rooms.toString());

        List allRoomsPersonalList = new ArrayList();
        List allRoomsList = new ArrayList();


        for (Map.Entry<String, String> entry : rooms.entrySet()) {
            allRoomsList.add(entry.getKey());

            //FILTER ROOM -> ONLY PERSONAL ROOM
            if(entry.getValue().equals("personal")){
                allRoomsPersonalList.add(entry.getKey());
            }

        }

        //Log.d("PERSONAL ", allRoomsPersonalList.toString());

        Map<String, String> allUserNameChatsMap = new HashMap<>();
        Map<String, String> allUserPhotoProfileChatsMap = new HashMap<>();

        firebaseFirestore.collection("users")
                .whereArrayContainsAny("rooms", (List)allRoomsPersonalList)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d("ROOM ", document.getData().toString());
                                //Filter except currentUser
                                if(!document.getData().get("uid").toString().contains(currentUser.getUid())){
//                                    Log.d("DOCUMENT ", document.getData().toString());
                                    allUserNameChatsMap.put(document.getData().get("uid").toString(), document.getData().get("username").toString());
                                    allUserPhotoProfileChatsMap.put(document.getData().get("uid").toString(),
                                            document.getData().get("photoProfile").toString());
                                }
                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        if(task.isComplete()){
                            //Log.d("USER-USERNAME ", userUsernameList.toString());
                            firebaseFirestore.collection("users")
                                    .document(currentUser.getUid())
                                    .collection("contacts")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if(allUserNameChatsMap.containsKey(document.getData().get("id").toString())){
                                                        allUserNameChatsMap.replace(document.getData().get("id").toString(),
                                                                document.getData().get("contactName").toString());
                                                    } else {
                                                        allUserNameChatsMap.put(document.getData().get("id").toString(),
                                                                document.getData().get("contactName").toString());
                                                    }
                                                }
                                            } else {
                                                //Log.d(TAG, "Error getting documents: ", task.getException());
                                            }

                                            if(task.isComplete()){
                                                //Log.d("FINAL ALL USERNAME ", allUserNameChatsMap.toString());
                                                gettingRoomsById(allRoomsList, allUserNameChatsMap, allUserPhotoProfileChatsMap);

                                            }
                                        }
                                    });
                        }
                    }
                });

    }


    private void gettingRoomsById(List rooms, Map allUserNameChats, Map allUserPhotoProfileChats){

        Log.d("USERNAME ", allUserNameChats.toString());

        List<ChatsFragmentModel> tempSingleModel = new ArrayList<>();
        List<List<ChatsFragmentModel>> tempAllModel = new ArrayList<>();

        firebaseFirestore.collection("rooms")
                .whereIn("idRoom", (List) rooms)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                // TYPE ROOM PERSONAL
                                if(document.getData().get("typeRoom").toString().equals("personal")){
                                    List<List> tempMember = new ArrayList<>();
                                    tempMember.add((List)document.getData().get("member"));
                                    tempMember.get(0).remove(currentUser.getUid());
//                                    Log.d("MEMBER ", tempMember.get(0).toString());

                                    String id = tempMember.get(0).get(0).toString();
//                                    String photoProfile = allUserPhotoProfileChats.get(id).toString();
//                                    String username = allUserNameChats.get(id).toString();
//                                    Log.d("ID ", id);
//                                    Log.d("PHOTO PROFILE ", Boolean.toString(photoProfile.isEmpty()));
//                                    Log.d("USERNAME ", username);
//                                    Log.d("MESSAGE ", ((Map)document.getData().get("lastMessage")).get("message").toString());
//                                    Log.d("DATE ", ((Map)document.getData().get("lastMessage")).get("date").toString());


                                    tempSingleModel.add(new ChatsFragmentModel(
                                            allUserPhotoProfileChats.get(id).toString(), //Photo
                                            allUserNameChats.get(id).toString(), //Username
                                            ((Map)document.getData().get("lastMessage")).get("message").toString(),//lastMessage
                                            ((Map)document.getData().get("lastMessage")).get("date").toString(), //lastMessageDate
                                            "0"
                                    ));

                                }

                            }

                            tempAllModel.add(tempSingleModel);
                            chatsProfileList.setValue(tempAllModel);

                        } else {
                            //Error getting documents
                        }

                    }
                });
    }




}
