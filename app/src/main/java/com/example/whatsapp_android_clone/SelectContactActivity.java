package com.example.whatsapp_android_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.stream.Collectors;

public class SelectContactActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;

    private ActionBar bar;
    private DatabaseListener databaseListener;
    private RecyclerView contactRecyclerView;
    private ProgressBar progressBar;
    private SelectContactAdapter contactAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseListener = new ViewModelProvider(this).get(DatabaseListener.class);
        firebaseFirestore = FirebaseFirestore.getInstance();

        TextView debug = findViewById(R.id.debug_contact);
        progressBar = findViewById(R.id.progress_bar_select_contact);
        contactRecyclerView = findViewById(R.id.recycler_view_contact);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Select Contact");
            getSupportActionBar().setSubtitle("Getting contact...");
        }

        progressBar.setVisibility(View.GONE);


        //RECYCLER VIEW
        databaseListener.checkIsUserLoggedIn();
        databaseListener.getLoggedUser().observe(SelectContactActivity.this, currentUser -> {

            if(currentUser != null){

                databaseListener.getUserContacts(currentUser.getUid());
                databaseListener.getContactsList().observe(SelectContactActivity.this, contacts -> {

                    if(contacts != null && contacts.size() > 0){

                        //Change sub title to contacts size
                        String textContact;
                        if(contacts.size() <= 1){
                            textContact = contacts.size() + " contact";
                        } else {
                            textContact = contacts.size() + " contacts";
                        }

                        if(getSupportActionBar() != null){
                            getSupportActionBar().setSubtitle(textContact);
                        }

                        updateContactList(contacts);
                    } else {
                        //DISPLAY CONTACT NOT FOUND
                    }

                }); //End getContactsList

            } //End if

        }); //End getLoggedUser


    } // End onCreate



    //Option or Context menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_select_contact, menu);

        MenuItem search_menu =  menu.findItem(R.id.menu_select_contact_search);
        SearchView searchView = (SearchView) search_menu.getActionView();

        if(databaseListener.getKeyword().getValue() != null){
            searchView.setQuery(databaseListener.getKeyword().getValue(), false);
            searchView.setIconified(false);
        }

        updateContactListBySearch();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(SelectContactActivity.this, query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText != null){
                    databaseListener.setKeyword(newText);
                }

                updateContactListBySearch();

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_select_contact_refresh:
                return true;
            case R.id.menu_select_contact_help:
                return true;
            case R.id.menu_select_contact_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void updateContactList(List<SelectContactModel> modalContactList){

        contactAdapter = new SelectContactAdapter(SelectContactActivity.this, modalContactList);
        contactRecyclerView.setAdapter(contactAdapter);
        contactRecyclerView.setLayoutManager(new LinearLayoutManager(SelectContactActivity.this));
    }


    public void updateContactListBySearch(){
        databaseListener.getContactsList().observe(SelectContactActivity.this, contacts -> {
            if(contacts != null){
                databaseListener.getKeyword().observe(SelectContactActivity.this, keyword -> {
                    if(keyword != null){
                        updateContactList(contacts.stream()
                                .filter(data -> data.usernameProfileContact.toLowerCase().contains(keyword.toLowerCase()))
                                .map(data -> new SelectContactModel(data.type, data.idProfile, data.photoProfileContact, data.usernameProfileContact, data.desctiptionProfileContact))
                                .collect(Collectors.toList()));

                    }
                });
            }
        });
    }

}