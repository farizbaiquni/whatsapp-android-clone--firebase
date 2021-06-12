package com.example.whatsapp_android_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp_android_clone.adapter.SelectContactAdapter;
import com.example.whatsapp_android_clone.model.SelectContactModel;
import com.example.whatsapp_android_clone.viewModel.SelectContactViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SelectContactActivity extends AppCompatActivity {

    private RecyclerView contactRecyclerView;
    private ProgressBar progressBar;
    private SelectContactAdapter contactAdapter;
    private SelectContactViewModel selectContactViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        TextView debug = findViewById(R.id.debug_contact);
        progressBar = findViewById(R.id.progress_bar_select_contact);
        contactRecyclerView = findViewById(R.id.recycler_view_contact);

        selectContactViewModel = new ViewModelProvider(this).get(SelectContactViewModel.class);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Select Contact");
            getSupportActionBar().setSubtitle(null);
        }

        progressBar.setVisibility(View.GONE);

        selectContactViewModel.getNumberContact().observe(SelectContactActivity.this, numberContact -> {
            if(numberContact != null){
                if(numberContact > 1){
                    getSupportActionBar().setSubtitle(Integer.toString(numberContact) + " contacts");
                } else if(numberContact >= 0 && numberContact <= 1){
                    getSupportActionBar().setSubtitle(Integer.toString(numberContact) + " contact");
                } else if(numberContact == -1){
                    getSupportActionBar().setSubtitle("Getting contacts...");
                } else {
                    getSupportActionBar().setSubtitle("Failed getting contacts...");
                }
            }
        });

        updateContactList();

        selectContactViewModel.getContactList();
        selectContactViewModel.getSelectContactModel().observe(SelectContactActivity.this, contacts -> {
            contactAdapter.notifyDataSetChanged();

            if(contacts != null && !contacts.isEmpty()){
                selectContactViewModel.getKeyword().observe(SelectContactActivity.this, keyword -> {
                    if(keyword != null && !keyword.isEmpty()){
                        contactAdapter = new SelectContactAdapter(SelectContactActivity.this,
                                contacts.stream()
                                        .filter(data -> data.getUsernameProfileContact().toLowerCase()
                                                .contains(keyword.toLowerCase()) && data.getType() != 0)
                                        .collect(Collectors.toList()));
                        contactRecyclerView.setAdapter(contactAdapter);
                        contactRecyclerView.setLayoutManager(new LinearLayoutManager(SelectContactActivity.this));
                    } else {
                        updateContactList();
                    }
                });
            }

        });



    } // End onCreate



    //Option or Context menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_select_contact, menu);

        MenuItem search_menu =  menu.findItem(R.id.menu_select_contact_search);
        SearchView searchView = (SearchView) search_menu.getActionView();

        if(selectContactViewModel.getKeyword().getValue() != null){
            if(!selectContactViewModel.getKeyword().getValue().isEmpty()){
                searchView.setQuery(selectContactViewModel.getKeyword().getValue(), false);
                searchView.setIconified(false);
            }
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(SelectContactActivity.this, query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText != null){
                    selectContactViewModel.setKeyword(newText);

                }

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


    public void updateContactList(){
        contactAdapter = new SelectContactAdapter(SelectContactActivity.this,
                selectContactViewModel.getSelectContactModel().getValue());
        contactRecyclerView.setAdapter(contactAdapter);
        contactRecyclerView.setLayoutManager(new LinearLayoutManager(SelectContactActivity.this));
    }




}