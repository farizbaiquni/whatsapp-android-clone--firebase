package com.example.whatsapp_android_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class SelectContactActivity extends AppCompatActivity {

    private ActionBar bar;
    private List<ModelProfileContact> modelContactList;
    private DatabaseListener databaseListener;
    private CardView cardViewNewGroup, cardViewNewContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        cardViewNewGroup = findViewById(R.id.card_view_new_group);
        cardViewNewContact = findViewById(R.id.card_view_new_contact);

        bar = getSupportActionBar();
        bar.setTitle("Select Contact");

        cardViewNewContact.setOnClickListener( v -> {
            Intent intentToAddContact = new Intent(SelectContactActivity.this, AddContactActivity.class);
            startActivity(intentToAddContact);
        });

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
                Toast.makeText(SelectContactActivity.this, query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(SelectContactActivity.this, newText, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "CLICK", Toast.LENGTH_SHORT).show();
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}