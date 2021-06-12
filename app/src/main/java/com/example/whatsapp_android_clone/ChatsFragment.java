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
import com.example.whatsapp_android_clone.model.ChatsFragmentModel;
import com.example.whatsapp_android_clone.viewModel.ChatsFragmentViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
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

        chatsFragmentViewModel.gettingRoomsList();
        chatsFragmentViewModel.getRoomsList().observe(getActivity(), rooms -> {
            if(rooms != null && !rooms.isEmpty()){
                chatsFragmentViewModel.gettingChatsList(rooms);
            }
        });

        chatsFragmentViewModel.getChatsProfileList().observe(getActivity(), chats -> {
            if(chats != null && !chats.isEmpty()){
                chatsFragmentViewModel.getKeyword().observe(getActivity(), keyword -> {
                    if (keyword != null && !keyword.isEmpty()){
                        chatsFragmentAdapter = new ChatsFragmentAdapter(chats.stream()
                                .filter(data -> data.getUsername().toLowerCase()
                                .contains(keyword.toLowerCase()))
                                .collect(Collectors.toList()));
                        recyclerView.setAdapter(chatsFragmentAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                    } else {
                        chatsFragmentAdapter = new ChatsFragmentAdapter(chats);
                        recyclerView.setAdapter(chatsFragmentAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                });
            }
        });


        fab_select_contact.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SelectContactActivity.class);
            startActivity(intent);
        });

        return view;
    }

}