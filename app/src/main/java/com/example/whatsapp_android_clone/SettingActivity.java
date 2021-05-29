package com.example.whatsapp_android_clone;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    private DatabaseListener databaseListener;

    private RecyclerView settingRecyclerView;
    private SettingMenuAdapter settingMenuAdapter;
    private String username ="No Connection...", description = "No Connection...", photoProfile = "";

    private String settingMenuDesctiptions [], settingMenuNames[];
    private int settingMenuImages[] = {R.drawable.key, R.drawable.chat, R.drawable.notification,
            R.drawable.usage, R.drawable.help};

    List<SettingMenuModel> settingMenuModels = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");

        settingRecyclerView = findViewById(R.id.recycler_view_setting);

        //Setting RecyclerView
        settingMenuNames = getResources().getStringArray(R.array.setting_menu_name);
        settingMenuDesctiptions = getResources().getStringArray(R.array.setting_menu_description);


        //View Model and Live Data to fetch user data from firestore sync / realtime
        databaseListener = new ViewModelProvider(this).get(DatabaseListener.class);
        //Check is user loggeed in or not, to avoid null exception
        databaseListener.checkIsUserLoggedIn();
        databaseListener.getLoggedUser().observe(SettingActivity.this, loggedUser -> {
            if(loggedUser != null){
                databaseListener.getUserInformation(loggedUser.getUid());
            }
        });

        //Updating profile layout based on user data in firestore and updating if there's a change
        databaseListener.getUsername().observe(SettingActivity.this, data -> {
            username = data;
            callSetAdapterSettingMenu(username, description, photoProfile);
        });


        databaseListener.getDescription().observe(SettingActivity.this, data -> {
            description = data;
            callSetAdapterSettingMenu(username, description, photoProfile);
        });


        databaseListener.getPhotoProfile().observe(SettingActivity.this, data -> {
            photoProfile = data;
            callSetAdapterSettingMenu(username, description, photoProfile);
        });



    } //end onCreate


    private void callSetAdapterSettingMenu(String username, String description, String photoProfile){
        settingMenuModels.clear();

        //PROFILE
        settingMenuModels.add(new SettingMenuModel(SettingMenuModel.PROFILE_TYPE,
                R.drawable.friends,
                username,
                description,
                R.drawable.barcode,
                photoProfile
        ));

        //OPTION MENU
        for(int i = 0; i <= settingMenuNames.length - 1; i++){
            settingMenuModels.add(new SettingMenuModel(SettingMenuModel.MENU_TYPE,
                    settingMenuImages[i],
                    settingMenuNames[i],
                    settingMenuDesctiptions[i],
                    0,
                    ""));
        }

        //INVITE A FRIEND
        settingMenuModels.add(new SettingMenuModel(SettingMenuModel.INVITE_FRIEND_TYPE,
                R.drawable.friends,
                "Invite a friend",
                "",
                0,
                ""));

        settingMenuAdapter = new SettingMenuAdapter(SettingActivity.this, settingMenuModels);
        settingRecyclerView.setAdapter(settingMenuAdapter);
        settingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


} //end setting activity class