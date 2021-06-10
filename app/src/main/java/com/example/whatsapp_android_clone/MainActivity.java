package com.example.whatsapp_android_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.whatsapp_android_clone.viewModel.ChatsFragmentViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser currentUser;

    private Toolbar toolBarTop;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private TabsAdapter tabsAdapter;
    private DatabaseListener databaseListener;
    private ChatsFragmentViewModel chatsFragmentViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolBarTop = findViewById(R.id.toolbar_top);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.viewPager2);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        chatsFragmentViewModel = new ViewModelProvider(this).get(ChatsFragmentViewModel.class);
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
        } else {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://whatsapp-clone-android-a9652-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference statusLastOnline = database.getReference("users").child(currentUser.getUid()).child("lastOnline");
            DatabaseReference statusOnlineRef = database.getReference("users").child(currentUser.getUid()).child("statusOnline");

            statusOnlineRef.setValue("online");

            statusOnlineRef.onDisconnect().setValue("offline");
            statusLastOnline.onDisconnect().setValue(LocalDateTime.now());

        }
    }




    //Context menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);

        MenuItem searchMenu = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenu.getActionView();
        searchView.setQueryHint("Search...");

        if(chatsFragmentViewModel.getKeyword().getValue() != null &&
                !chatsFragmentViewModel.getKeyword().getValue().isEmpty()){
            searchView.setQuery(chatsFragmentViewModel.getKeyword().getValue(), false);
            searchView.setIconified(false);
        }

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                chatsFragmentViewModel.setKeyword(newText);
//                Toast.makeText(MainActivity.this, chatsFragmentViewModel.getKeyword().getValue(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

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