package com.example.whatsapp_android_clone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    private RecyclerView settingRecyclerView;
    private SettingMenuAdapter settingMenuAdapter;

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


        modelSettingMenus.add(new ModelSettingMenu(ModelSettingMenu.PROFILE_TYPE, R.drawable.friends,
                "Fariz Baiquni", "Coding is Fun", R.drawable.barcode));
        for(int i = 0; i <= settingMenuNames.length - 1; i++){
            modelSettingMenus.add(new ModelSettingMenu(ModelSettingMenu.MENU_TYPE, settingMenuImages[i],
                    settingMenuNames[i], settingMenuDesctiptions[i], 0));
        }
        modelSettingMenus.add(new ModelSettingMenu(ModelSettingMenu.INVITE_FRIEND_TYPE, R.drawable.friends,
                "Invite a friend", "", 0));

        settingMenuAdapter = new SettingMenuAdapter(SettingActivity.this, modelSettingMenus);
        settingRecyclerView.setAdapter(settingMenuAdapter);
        settingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}