package com.example.whatsapp_android_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser currentUser;

    private Toolbar toolBarTop;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private TabsAdapter tabsAdapter;
    private DatabaseListener databaseListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolBarTop = findViewById(R.id.toolbar_top);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.viewPager2);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseListener = new ViewModelProvider(this).get(DatabaseListener.class);
        //Check is user loggeed in or not, to avoid null exception
        databaseListener.checkIsUserLoggedIn();
        databaseListener.getLoggedUser().observe(MainActivity.this, loggedUser -> {
            if(loggedUser != null){
                databaseListener.updateUserInformation(loggedUser.getUid());
            }
        });


        //Toolbar
        setSupportActionBar(toolBarTop);

        //Bar and Viewpager2
        FragmentManager fragmentManager = getSupportFragmentManager();
        tabsAdapter = new TabsAdapter(fragmentManager, getLifecycle());
        viewPager2.setAdapter(tabsAdapter);
        tabLayout.addTab(tabLayout.newTab().setText("Chats"));
        tabLayout.addTab(tabLayout.newTab().setText("Status"));
        tabLayout.addTab(tabLayout.newTab().setText("Calls"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Kemungkinan untuk merubah posisi tab yang di selected
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    } //End of onCreate




    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }




    //Context menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_new_group:
                BottomSheetCreateGroup bottomSheetCreateGroup = new BottomSheetCreateGroup();
                bottomSheetCreateGroup.show(getSupportFragmentManager(), "SHOW");
                return true;
            case R.id.menu_setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                if(!databaseListener.isUserInFirestoreExist){
                    Toast.makeText(this, "User in firebase not found", Toast.LENGTH_SHORT).show();
                } else if(databaseListener.isErrorOccuredDuringFetchUser){
                    Toast.makeText(this, "Error during getting data from firestore", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(intent);
                }
                return true;
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intentLogOut = new Intent(this, LoginActivity.class);
                startActivity(intentLogOut);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



} //End of MainActivity class