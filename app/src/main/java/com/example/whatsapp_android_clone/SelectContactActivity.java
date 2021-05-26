package com.example.whatsapp_android_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectContactActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;

    private ActionBar bar;
    private DatabaseListener databaseListener;
    private CardView cardViewNewGroup, cardViewNewContact;
    private RecyclerView contactRecyclerView;
    private ProgressBar progressBar;

    private int index;

    private List<ModelProfileContact> modelContactList = new ArrayList<>();
    private List<List<String>> contactsList = new ArrayList<>();
    private ContactAdapter contactAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        index = 0;
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseListener = new DatabaseListener();
        firebaseFirestore = FirebaseFirestore.getInstance();

        TextView debug = findViewById(R.id.debug_contact);
        progressBar = findViewById(R.id.progress_bar_select_contact);
        contactRecyclerView = findViewById(R.id.recycler_view_contact);
        cardViewNewGroup = findViewById(R.id.card_view_new_group);
        cardViewNewContact = findViewById(R.id.card_view_new_contact);

        bar = getSupportActionBar();
        bar.setTitle("Select Contact");

        progressBar.setVisibility(View.GONE);

        cardViewNewContact.setOnClickListener( v -> {
            Intent intentToAddContact = new Intent(SelectContactActivity.this, AddContactActivity.class);
            startActivity(intentToAddContact);
        });


        //RECYCLER VIEW
        databaseListener.checkIsUserLoggedIn();
        databaseListener.getLoggedUser().observe(SelectContactActivity.this, currentUser -> {

            if(currentUser != null){
                databaseListener.getUserContacts(currentUser.getUid());
                databaseListener.getContacts().observe(SelectContactActivity.this, contacts -> {

                    progressBar.setVisibility(View.VISIBLE);

                    if(contacts != null && contacts.size() > 0){

                        index = 0;
                        List<String> contactsId = new ArrayList<>();

                        while(index < contacts.size()){
                            contactsId.add(contacts.get(index).get(0));
                            index++;
                        }

                        firebaseFirestore.collection("users")
                                .whereIn("uid", contactsId)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            int i = 0;
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                contacts.get(i).add(document.getData().get("photoProfile").toString());
                                                contacts.get(i).add(document.getData().get("description").toString());
                                                i++;
                                            }

                                            for(int a = 0; a < contacts.size(); a++ ){
                                                modelContactList.add(new ModelProfileContact(
                                                        contacts.get(a).get(0), //id
                                                        contacts.get(a).get(2), //photo
                                                        contacts.get(a).get(1), //username or contactName
                                                        contacts.get(a).get(3) //description
                                                ));
                                            }

                                            contactAdapter = new ContactAdapter(SelectContactActivity.this, modelContactList);
                                            contactRecyclerView.setAdapter(contactAdapter);
                                            contactRecyclerView.setLayoutManager(new LinearLayoutManager(SelectContactActivity.this));

                                            progressBar.setVisibility(View.GONE);

                                        } else {
//                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });

                    } else {
                        progressBar.setVisibility(View.GONE);
                        //DISPLAY CONTACT NOT FOUND
                    }

                }); //End getContacts
            } //End if
        }); //End getLoggedUser


    } // End onCreate


    //Option / Context menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_select_contact, menu);

        MenuItem search_menu =  menu.findItem(R.id.menu_select_contact_search);
        SearchView searchView = (SearchView) search_menu.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(SelectContactActivity.this, query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                Toast.makeText(SelectContactActivity.this, newText, Toast.LENGTH_SHORT).show();
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
}