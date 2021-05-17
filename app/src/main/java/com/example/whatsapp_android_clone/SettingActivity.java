package com.example.whatsapp_android_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    private DatabaseListener databaseListener;

    private RecyclerView settingRecyclerView;
    private SettingMenuAdapter settingMenuAdapter;
    private String username ="No Connection...", description = "No Connection...", photoProfile = "";
    private String prevUsername, prevDescription, prevPhotoProfile;

    private String settingMenuDesctiptions [], settingMenuNames[];
    private int settingMenuImages[] = {R.drawable.key, R.drawable.chat, R.drawable.notification,
            R.drawable.usage, R.drawable.help};
    List<ModelSettingMenu> modelSettingMenus = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        settingRecyclerView = findViewById(R.id.recycler_view_setting);

        //Setting RecyclerView
        settingMenuNames = getResources().getStringArray(R.array.setting_menu_name);
        settingMenuDesctiptions = getResources().getStringArray(R.array.setting_menu_description);


        //View Model and Live Data to fetch user data from firestore sync / realtime
        databaseListener = new ViewModelProvider(this).get(DatabaseListener.class);
        databaseListener.getUserInformation();

        //Updating profile layout based on user data in firestore and updating if there's a change
        databaseListener.getUsername().observe(SettingActivity.this, data -> {
            prevUsername = username;
            username = data;
            if(prevUsername != username){
                modelSettingMenus.clear();
            }
            callSetAdapterSettingMenu(username, description, photoProfile);
        });


        databaseListener.getDescription().observe(SettingActivity.this, data -> {
            prevDescription = description;
            description = data;
            if(prevDescription != description){
                modelSettingMenus.clear();
            }
            callSetAdapterSettingMenu(username, description, photoProfile);
        });


        databaseListener.getPhotoProfile().observe(SettingActivity.this, data -> {
            prevPhotoProfile = photoProfile;
            photoProfile = data;
            if(photoProfile != prevPhotoProfile){
                modelSettingMenus.clear();
            }
            callSetAdapterSettingMenu(username, description, photoProfile);
        });



    } //end onCreate


    private void callSetAdapterSettingMenu(String username, String description, String photoProfile){

        //PROFILE
        modelSettingMenus.add(new ModelSettingMenu(ModelSettingMenu.PROFILE_TYPE,
                R.drawable.friends,
                username,
                description,
                R.drawable.barcode,
                photoProfile
        ));

        //OPTION MENU
        for(int i = 0; i <= settingMenuNames.length - 1; i++){
            modelSettingMenus.add(new ModelSettingMenu(ModelSettingMenu.MENU_TYPE,
                    settingMenuImages[i],
                    settingMenuNames[i],
                    settingMenuDesctiptions[i],
                    0,
                    ""));
        }

        //INVITE A FRIEND
        modelSettingMenus.add(new ModelSettingMenu(ModelSettingMenu.INVITE_FRIEND_TYPE,
                R.drawable.friends,
                "Invite a friend",
                "",
                0,
                ""));

        settingMenuAdapter = new SettingMenuAdapter(SettingActivity.this, modelSettingMenus);
        settingRecyclerView.setAdapter(settingMenuAdapter);
        settingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


} //end setting activity class