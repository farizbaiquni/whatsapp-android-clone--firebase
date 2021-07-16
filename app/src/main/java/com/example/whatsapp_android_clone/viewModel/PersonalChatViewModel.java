package com.example.whatsapp_android_clone.viewModel;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.whatsapp_android_clone.model.PersonalChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalChatViewModel extends ViewModel {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    private MutableLiveData<Map<Number, PersonalChatModel>> peronalChatMap = new MutableLiveData<>();
    public LiveData<Map<Number, PersonalChatModel>> getPersonalChatMap(){ return this.peronalChatMap; }

    private MutableLiveData<List<PersonalChatModel>> personalChat = new MutableLiveData<>();
    public LiveData<List<PersonalChatModel>> getPersonalChat(){
        return this.personalChat;
    }

    private MutableLiveData<Boolean> listenerRooms = new MutableLiveData<>(false);
    public LiveData<Boolean> getListenerRooms(){
        return this.listenerRooms;
    }

    public void gettingPersonalChats(String idRoom){
        //Log.d("ID ROOM", idRoom);
        List<PersonalChatModel> tempMessages = new ArrayList();
        Map<Number, PersonalChatModel> tempMessagesMap = new HashMap<>();

        firebaseFirestore.collection("rooms")
                .document(idRoom)
                .collection("messages")
                .orderBy("timestamps")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            //Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        tempMessages.clear();
                        tempMessagesMap.clear();

                        int i = 0;
                        for (QueryDocumentSnapshot doc : value) {

                            Log.d("VALUE USER ID", doc.get("userId").toString()) ;
                            Log.d("VALUE MESSAGE", doc.get("message").toString()) ;
                            Log.d("VALUE TIMESTAMPS", doc.get("timestamps").toString());
                            if(doc.get("timestampsEpoch") == null) {
                                Log.d("VALUE EPOCH", "NULL");
                            } else {
                                Log.d("VALUE EPOCH", doc.get("timestampsEpoch").toString()) ;
                            }
//                            Log.d("VALUE DELETE STATUS", doc.get("deleteStatus").toString());
//                            Log.d("VALUE DELETE STATUS", doc.get("deleteStatus").toString()) ;
//                            Log.d("VALUE MESSAGE STATUS", doc.get("messageStatus").toString()) ;

                            if(doc.get("userId") != null && doc.get("message") != null && doc.get("timestamps") != null
                                    && doc.get("timestampsEpoch") != null && doc.get("deleteStatus") != null && doc.get("messageStatus") != null){

                                tempMessages.add( new PersonalChatModel (
                                        doc.get("userId").toString(),
                                        doc.get("message").toString(),
                                        doc.get("timestamps").toString(),
                                        (Long) doc.get("timestampsEpoch"),
                                        (boolean) doc.get("deleteStatus"),
                                        (Long) doc.get("messageStatus"),
                                        doc.get("userId").toString().equals(currentUser.getUid()) ? 1 : 2
                                ));

                                tempMessagesMap.put((Number) doc.get("messageStatus"), new PersonalChatModel(
                                        doc.get("userId").toString(),
                                        doc.get("message").toString(),
                                        doc.get("timestamps").toString(),
                                        (Long) doc.get("timestampsEpoch"),
                                        (boolean) doc.get("deleteStatus"),
                                        (Long) doc.get("messageStatus"),
                                        doc.get("userId").toString().equals(currentUser.getUid()) ? 1 : 2
                                ));


                                if(i == (value.size() - 1)){
                                    personalChat.setValue(tempMessages);
                                    peronalChatMap.setValue(tempMessagesMap);
                                    if(listenerRooms.getValue().equals(false)){
                                        listenerRooms.setValue(true);
                                    }
                                }

                                i++;

                            }
                        }
                    }
                });
    }

}
