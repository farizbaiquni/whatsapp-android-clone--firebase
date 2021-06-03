package com.example.whatsapp_android_clone;

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

    List<SettingMenuModel> settingMenuModels = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Settings");
        }

        settingRecyclerView = findViewById(R.id.recycler_view_setting);

        //Setting RecyclerView
        settingMenuNames = getResources().getStringArray(R.array.setting_menu_name);
        settingMenuDesctiptions = getResources().getStringArray(R.array.setting_menu_description);


        //View Model and Live Data to fetch user data from firestore sync / realtime
        databaseListener = new ViewModelProvider(this).get(DatabaseListener.class);


        initRecyclerView();


        databaseListener.checkIsUserLoggedIn();
        databaseListener.getLoggedUser().observe(SettingActivity.this, loggedUser -> {
            if(loggedUser != null){
                databaseListener.updateUserInformation(loggedUser.getUid());
            }
        });



        databaseListener.getSettingMenuModel().observe(SettingActivity.this, data -> {
            if(data != null){

                databaseListener.getUserInformation().observe(SettingActivity.this, info -> {
                    if(info != null){

                        SettingMenuModel tempModel = new SettingMenuModel(
                                SettingMenuModel.PROFILE_TYPE,
                                R.drawable.friends,
                                info.get(1),
                                info.get(2),
                                R.drawable.barcode,
                                info.get(0)
                        );

                        data.set(0, tempModel);

                        settingMenuAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

    } //end onCreate


    private void initRecyclerView(){
        databaseListener.initSettingMenuModel(settingMenuNames, settingMenuDesctiptions);
        settingMenuAdapter = new SettingMenuAdapter(SettingActivity.this, databaseListener.getSettingMenuModel().getValue());
        settingRecyclerView.setAdapter(settingMenuAdapter);
        settingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


} //end setting activity class