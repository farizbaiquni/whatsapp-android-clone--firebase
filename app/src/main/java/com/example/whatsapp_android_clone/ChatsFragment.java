package com.example.whatsapp_android_clone;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.whatsapp_android_clone.adapter.ChatsFragmentAdapter;
import com.example.whatsapp_android_clone.viewModel.ChatsFragmentViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.stream.Collectors;

public class ChatsFragment extends Fragment {

    private FloatingActionButton fab_select_contact;
    private ChatsFragmentViewModel chatsFragmentViewModel;
    private RecyclerView recyclerView;
    private ChatsFragmentAdapter chatsFragmentAdapter;


    public ChatsFragment() {
        // Required empty public constructor
    }

    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        fab_select_contact = view.findViewById(R.id.fab_select_contact);
        recyclerView = view.findViewById(R.id.recycler_view_chats);

        chatsFragmentViewModel = new ViewModelProvider(getActivity()).get(ChatsFragmentViewModel.class);


        if(chatsFragmentViewModel.getListenerRoomsCalled().getValue().equals(false)){
            chatsFragmentViewModel.gettingListenerRooms();
        }

        chatsFragmentViewModel.getRoomsMap().observe(getActivity(), rooms -> {
            if(rooms != null && !rooms.isEmpty()){
                //updateChatsProfileListLog.d("ROOOM MAP ", rooms.toString());
                if(chatsFragmentViewModel.getListenerPersonalCalled().getValue().equals(false)){
                    chatsFragmentViewModel.gettingListenerPersonal();
                }

            }
        });

        chatsFragmentViewModel.getListenerPersonalCalled().observe(getActivity(), listenerPersonalCalled -> {
            //LISTENER CONTACT-NAME CALL AFTER PERSONAL FETCH FINISH
            if(listenerPersonalCalled.equals(true)){
                //Log.d("PERSONAL MAP ", chatsFragmentViewModel.getPersonalMap().getValue().toString());
                if (chatsFragmentViewModel.getListenerContactNameCalled().getValue().equals(false)) {
                    chatsFragmentViewModel.gettingListenerContactName();
                }
            }
        });

        chatsFragmentViewModel.getPersonalMap().observe(getActivity(), personal -> {
            if(personal != null && !personal.isEmpty()){
                chatsFragmentViewModel.updateAllUsernamePhoto();
                //Log.d("PERSONAL MAP ", chatsFragmentViewModel.getPersonalMap().getValue().toString());
                //Log.d("PERSONAL MAP ", personal.toString());
            }
        });

        chatsFragmentViewModel.getContactNameMap().observe(getActivity(), contactName -> {
            if(contactName != null && !contactName.isEmpty()){
                chatsFragmentViewModel.updateAllUsernamePhoto();
                //Log.d("CONTACT NAME MAP ", contactName.toString());
            }
        });

        chatsFragmentViewModel.getAllUsernamePhoto().observe(getActivity(), allUsernamePhoto -> {
            if(allUsernamePhoto != null && chatsFragmentViewModel.getAllUsernamePhotoIsEmpty().getValue().equals(false)){
                //Log.d("ALLLLLLLLLL ", allUsernamePhoto.toString());
                chatsFragmentViewModel.updateChatsProfileList();
            }

        });


        //CONDITION IF LOGGED USER DON'T HAVE ALL-USERNAME-PHOTO EVEN THOUGH ALL LISTENER HAVE BEEN CALLED
        chatsFragmentViewModel.getAllUsernamePhotoIsEmpty().observe(getActivity(), isEmpty -> {
            if(isEmpty.equals(true) && chatsFragmentViewModel.getListenerRoomsCalled().getValue().equals(true)
            && chatsFragmentViewModel.getListenerPersonalCalled().getValue().equals(true) &&
            chatsFragmentViewModel.getListenerContactNameCalled().getValue().equals(true)){
                chatsFragmentViewModel.updateChatsProfileList();
            }
        });


        chatsFragmentViewModel.getChatsProfileList().observe(getActivity(), chatsProfile -> {
            if(chatsProfile != null && !chatsProfile.isEmpty()){
                //Log.d("OBSERVE CHATS PROFILE ", chatsProfile.toString());
                if(chatsFragmentViewModel.getListenerRoomsCalled().getValue().equals(true) &&
                        chatsFragmentViewModel.getListenerPersonalCalled().getValue().equals(true) &&
                        chatsFragmentViewModel.getListenerContactNameCalled().getValue().equals(true)){

                    chatsFragmentViewModel.getKeyword().observe(getActivity(), keyword -> {
                       if(keyword != null && !keyword.isEmpty()){
                           chatsFragmentAdapter = new ChatsFragmentAdapter(
                                   getActivity(),
                                   chatsProfile.stream()
                                           .filter(data -> data.getUsername().toLowerCase().contains(keyword.toLowerCase()))
                                           .collect(Collectors.toList())
                           );
                           recyclerView.setAdapter(chatsFragmentAdapter);
                           recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                       } else {
                           chatsFragmentAdapter = new ChatsFragmentAdapter(getActivity(), chatsProfile);
                           recyclerView.setAdapter(chatsFragmentAdapter);
                           recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                       }
                    });
                }
            }
        });

        fab_select_contact.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SelectContactActivity.class);
            startActivity(intent);
            //Toast.makeText(getActivity(), Boolean.toString(chatsFragmentViewModel.getListenerContactNameCalled().getValue()), Toast.LENGTH_SHORT).show();
        });

        return view;
    }

}