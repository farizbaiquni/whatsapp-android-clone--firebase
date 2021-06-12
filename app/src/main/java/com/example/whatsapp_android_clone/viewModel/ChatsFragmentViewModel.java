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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatsFragmentViewModel extends ViewModel {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


    private MutableLiveData<List<ChatsFragmentModel>> chatsProfileList = new MutableLiveData<>();
    public LiveData<List<ChatsFragmentModel>> getChatsProfileList(){
        return this.chatsProfileList;
    }


    private MutableLiveData<Map<String, String>> roomsList =  new MutableLiveData<>();
    public LiveData<Map<String, String>> getRoomsList(){
        return this.roomsList;
    }


    private MutableLiveData<String> keyword = new MutableLiveData<>(null);
    public LiveData<String> getKeyword(){
        return this.keyword;
    }

    public void setKeyword(String key){
        this.keyword.setValue(key);
    }

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
                    if(snapshot.getData().get("roomsMap") != null){
                        roomsList.setValue((Map)snapshot.getData().get("roomsMap"));
                    }

                } else {
                    //Log.d(TAG, "Current data: null");
                }

            }
        });

    }


    public void gettingChatsList(Map<String, String> rooms){

        List allRoomsPersonalList = new ArrayList();
        List allRoomsList = new ArrayList();


        for (Map.Entry<String, String> entry : rooms.entrySet()) {
            allRoomsList.add(entry.getKey());

            //FILTER ROOM -> ONLY PERSONAL ROOM
            if(entry.getValue().equals("personal")){
                allRoomsPersonalList.add(entry.getKey());
            }

        }

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
                                if(!document.getData().get("uid").toString().contains(currentUser.getUid())){
                                    allUserNameChatsMap.put(document.getData().get("uid").toString(), document.getData().get("username").toString());
                                    allUserPhotoProfileChatsMap.put(document.getData().get("uid").toString(),
                                            document.getData().get("photoProfile").toString());
                                }
                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        if(task.isComplete()){
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
                                                gettingAllDataRooms(allRoomsList, allUserNameChatsMap, allUserPhotoProfileChatsMap);

                                            }
                                        }
                                    });
                        }
                    }
                });

    }


    private void gettingAllDataRooms(List<String> rooms, Map<String, String> allUserNameChats, Map<String, String> allUserPhotoProfileChats){

        List<ChatsFragmentModel> tempAllModel = new ArrayList<>();

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

                                    String id = tempMember.get(0).get(0).toString();

                                    tempAllModel.add(new ChatsFragmentModel(
                                            allUserPhotoProfileChats.get(id).toString(), //Photo
                                            allUserNameChats.get(id).toString(), //Username
                                            ((Map)document.getData().get("lastMessage")).get("message").toString(),//lastMessage
                                            ((Map)document.getData().get("lastMessage")).get("date").toString(), //lastMessageDate
                                            "0"
                                    ));
                                }

                            }

                            chatsProfileList.setValue(tempAllModel);

                        } else {
                            //Error getting documents
                        }

                    }
                });
    }




}
