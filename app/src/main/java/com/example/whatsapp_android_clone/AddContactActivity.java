package com.example.whatsapp_android_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp_android_clone.model.AddContactModel;
import com.example.whatsapp_android_clone.viewModel.AddContactViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddContactActivity extends AppCompatActivity {

    private TextInputLayout textInputLayoutContactName;
    private TextView textViewError, textViewUsernameUserFound, textViewDescriptionFound;
    private EditText editTextNameContact;
    private Button buttonSubmit;
    private CardView cardViewUserFound;
    private CircleImageView circleImageViewImageUserFound;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser currentUser;
    private boolean isUserFound;
    private String idSearchedUser,
            photoSearchedUser,
            usernameSearchedUser,
            descriptionSearchedUser,
            emailSearchedUser;

    private AddContactModel addContactModel;
    private AddContactViewModel addContactViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Add Contact");
        }

        addContactViewModel = new ViewModelProvider(this).get(AddContactViewModel.class);

        textInputLayoutContactName = findViewById(R.id.et_name_contact_add_contact_layout);
        textViewError = findViewById(R.id.user_contact_not_found);
        cardViewUserFound = findViewById(R.id.user_contact_found);
        circleImageViewImageUserFound = findViewById(R.id.user_contact_found_image);
        textViewUsernameUserFound = findViewById(R.id.user_contact_found_username);
        textViewDescriptionFound = findViewById(R.id.user_contact_found_description);
        editTextNameContact = findViewById(R.id.et_name_contact_add_contact);
        buttonSubmit = findViewById(R.id.btn_submit_add_contact);

        clearLayout();

        idSearchedUser = "";
        photoSearchedUser = "";
        usernameSearchedUser = "";
        descriptionSearchedUser = "";
        emailSearchedUser = "";

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if(addContactViewModel.getAddContactModel().getValue() != null){
            cardViewUserFound.setVisibility(View.VISIBLE);
            textInputLayoutContactName.setVisibility(View.VISIBLE);
            buttonSubmit.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(addContactViewModel.getAddContactModel().getValue().getPhotoProfile()).into(circleImageViewImageUserFound);
            } catch (Exception e){
                circleImageViewImageUserFound.setImageResource(R.drawable.friends);
            }

            textViewUsernameUserFound.setText(addContactViewModel.getAddContactModel().getValue().getContactName());
            textViewDescriptionFound.setText(addContactViewModel.getAddContactModel().getValue().getDescription());
        }


        //USER CLICK ADD CONTACT
        buttonSubmit.setOnClickListener(v -> {

            if(TextUtils.isEmpty(editTextNameContact.getText().toString())){
                textInputLayoutContactName.setError("Contact name can't be empty");
            } else {

                Map<String, Object> data = new HashMap<>();
                data.put("id", addContactViewModel.getAddContactModel().getValue().getId());
                data.put("contactName", addContactViewModel.getAddContactModel().getValue().getContactName());

                firebaseFirestore.collection("users")
                        .document(currentUser.getUid())
                        .collection("contacts")
                        .document(emailSearchedUser.trim())
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(AddContactActivity.this, SelectContactActivity.class);
                                clearLayout();
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddContactActivity.this, "Failed add contact", Toast.LENGTH_SHORT).show();
                            }
                        });
            }


        }); // End button submit onClickListener

    } // End onCreate


    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser == null){
            Intent intent = new Intent(AddContactActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }//End onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);

        MenuItem searchMenu = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchMenu.getActionView();
        searchView.setQueryHint("Id");

        if(addContactViewModel.getKeyword() != null){
            searchView.setQuery(addContactViewModel.getKeyword().getValue(), false);
            searchView.setIconified(false);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                clearLayout();
                checkIsUserExist(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                addContactViewModel.setKeyword(newText);
                textViewError.setVisibility(View.GONE);
                return false;
            }
        });

        return true;
    }

    private void checkIsUserExist(String querySearch){

        isUserFound = false;
        idSearchedUser = "";
        photoSearchedUser = "";
        usernameSearchedUser = "";
        descriptionSearchedUser = "";
        emailSearchedUser = "";

       //Check user by ID
        firebaseFirestore.collection("users")
                .whereEqualTo("id", querySearch)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                //CHECK IS USER NOT EQUAL LOGGED USER
                                if(!document.getId().equals(currentUser.getUid())){
                                    isUserFound = true;

                                    //CHECK IS USER ALREADY EXIST IN CONTACT
                                    firebaseFirestore.collection("users")
                                            .document(currentUser.getUid())
                                            .collection("contacts")
                                            .document(emailSearchedUser)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    textViewError.setVisibility(View.VISIBLE);
                                                    textViewError.setText("This account is already exist in your contact");

                                                } else {

                                                    idSearchedUser = document.getId().toString();
                                                    photoSearchedUser = Objects.requireNonNull(document.getData().get("photoProfile")).toString();
                                                    emailSearchedUser = Objects.requireNonNull(document.getData().get("email")).toString();
                                                    usernameSearchedUser = Objects.requireNonNull(document.getData().get("username")).toString();
                                                    descriptionSearchedUser = Objects.requireNonNull(document.getData().get("description")).toString();


                                                    addContactViewModel.setAddContactModel(
                                                            new AddContactModel(
                                                                    idSearchedUser,
                                                                    photoSearchedUser,
                                                                    usernameSearchedUser,
                                                                    descriptionSearchedUser
                                                            )
                                                    );
                                                    isUserFound = true;
                                                    cardViewUserFound.setVisibility(View.VISIBLE);
                                                    textInputLayoutContactName.setVisibility(View.VISIBLE);
                                                    buttonSubmit.setVisibility(View.VISIBLE);
                                                    try {
                                                        Picasso.get()
                                                                .load(addContactViewModel.getAddContactModel().getValue().getPhotoProfile())
                                                                .into(circleImageViewImageUserFound);
                                                    } catch (Exception e){
                                                        circleImageViewImageUserFound.setImageResource(R.drawable.friends);
                                                    }
                                                    textViewUsernameUserFound.setText(addContactViewModel.getAddContactModel().getValue().getContactName());
                                                    textViewDescriptionFound.setText(addContactViewModel.getAddContactModel().getValue().getDescription());

                                                }
                                            } else {
                                                Toast.makeText(AddContactActivity.this, "Someting went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }); //End check is user is already in contact
                                }

                            }
                        } // End task.isSuccessful()

                        if(task.isComplete()){
                            if(!isUserFound){
                                textViewError.setVisibility(View.VISIBLE);
                                textViewError.setText("User not found");
                            }
                        }
                    }
                }); // End search user exist

    }//End checkIsUserExist

    private void clearLayout(){
        textInputLayoutContactName.setError(null);
        textViewError.setVisibility(View.GONE);
        cardViewUserFound.setVisibility(View.GONE);
        textInputLayoutContactName.setVisibility(View.GONE);
        buttonSubmit.setVisibility(View.GONE);
    }



}



