package com.example.whatsapp_android_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class AddContactActivity extends AppCompatActivity {

    private TextInputLayout textInputLayoutContactName, TextInputLayoutContactEmail;
    private TextView textViewUserNotFound, textViewUsernameUserFound, textViewDescriptionFound;
    private EditText editTextEmail, editTextNameContact;
    private Button buttonCheckIsUserExist, buttonSubmit;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        idSearchedUser = "";
        photoSearchedUser = "";
        usernameSearchedUser = "";
        descriptionSearchedUser = "";
        emailSearchedUser = "";

        textInputLayoutContactName = findViewById(R.id.et_name_contact_add_contact_layout);
        TextInputLayoutContactEmail = findViewById(R.id.et_email_add_contact_layout);
        textViewUserNotFound = findViewById(R.id.user_contact_not_found);
        cardViewUserFound = findViewById(R.id.user_contact_found);
        circleImageViewImageUserFound = findViewById(R.id.user_contact_found_image);
        textViewUsernameUserFound = findViewById(R.id.user_contact_found_username);
        textViewDescriptionFound = findViewById(R.id.user_contact_found_description);

        editTextEmail = findViewById(R.id.et_email_add_contact);
        editTextNameContact = findViewById(R.id.et_name_contact_add_contact);

        buttonCheckIsUserExist = findViewById(R.id.btn_check_is_user_exist);
        buttonSubmit = findViewById(R.id.btn_submit_add_contact);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();


        buttonCheckIsUserExist.setOnClickListener(v -> {

            isUserFound = false;
            idSearchedUser = "";
            photoSearchedUser = "";
            usernameSearchedUser = "";
            descriptionSearchedUser = "";
            emailSearchedUser = "";

            //Clear edit text error message
            TextInputLayoutContactEmail.setError(null);
            textInputLayoutContactName.setError(null);

            textViewUserNotFound.setVisibility(View.GONE);
            cardViewUserFound.setVisibility(View.GONE);
            textInputLayoutContactName.setVisibility(View.GONE);
            buttonSubmit.setVisibility(View.GONE);

            if(TextUtils.isEmpty(editTextEmail.getText().toString())){
                TextInputLayoutContactEmail.setError("Email can't be empty");
                textInputLayoutContactName.setError(null);
                textViewUserNotFound.setVisibility(View.GONE);
                cardViewUserFound.setVisibility(View.GONE);
                textInputLayoutContactName.setVisibility(View.GONE);
                buttonSubmit.setVisibility(View.GONE);
            } else if (!Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString().trim()).matches()) {
                TextInputLayoutContactEmail.setError("Invalid email format");
                textInputLayoutContactName.setError(null);
                textViewUserNotFound.setVisibility(View.GONE);
                cardViewUserFound.setVisibility(View.GONE);
                textInputLayoutContactName.setVisibility(View.GONE);
                buttonSubmit.setVisibility(View.GONE);
            } else {
                firebaseFirestore.collection("users")
                        .whereEqualTo("email", editTextEmail.getText().toString().trim())
                        .whereNotEqualTo("email", currentUser.getEmail())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        idSearchedUser = document.getId().toString();
                                        photoSearchedUser = document.getData().get("photoProfile").toString();
                                        emailSearchedUser = document.getData().get("email").toString();
                                        usernameSearchedUser = document.getData().get("username").toString();
                                        descriptionSearchedUser = document.getData().get("description").toString();

                                        firebaseFirestore.collection("users")
                                                .document(currentUser.getUid())
                                                .collection("contacts")
                                                .document(editTextEmail.getText().toString().trim())
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        TextInputLayoutContactEmail.setError("This account is already exist in your contact");
                                                        textInputLayoutContactName.setError(null);
                                                        textViewUserNotFound.setVisibility(View.GONE);
                                                        cardViewUserFound.setVisibility(View.GONE);
                                                        textInputLayoutContactName.setVisibility(View.GONE);
                                                        buttonSubmit.setVisibility(View.GONE);

                                                    } else {

                                                        TextInputLayoutContactEmail.setError(null);
                                                        textInputLayoutContactName.setError(null);
                                                        textViewUserNotFound.setVisibility(View.GONE);
                                                        cardViewUserFound.setVisibility(View.VISIBLE);
                                                        textInputLayoutContactName.setVisibility(View.VISIBLE);
                                                        buttonSubmit.setVisibility(View.VISIBLE);
                                                        try {
                                                            Picasso.get()
                                                                    .load(photoSearchedUser)
                                                                    .into(circleImageViewImageUserFound);
                                                        } catch (Exception e){
                                                            circleImageViewImageUserFound.setImageResource(R.drawable.friends);
                                                        }
                                                        textViewUsernameUserFound.setText(usernameSearchedUser);
                                                        textViewDescriptionFound.setText(descriptionSearchedUser);

                                                    }
                                                } else {
                                                    Toast.makeText(AddContactActivity.this, "Someting went wrong", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(AddContactActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                if(!isUserFound){
                    TextInputLayoutContactEmail.setError(null);
                    textInputLayoutContactName.setError(null);
                    textViewUserNotFound.setVisibility(View.VISIBLE);
                    cardViewUserFound.setVisibility(View.GONE);
                    textInputLayoutContactName.setVisibility(View.GONE);
                    buttonSubmit.setVisibility(View.GONE);
                }

            }


        }); // End button checkIsUserExist


        //USER CLICK ADD CONTACT
        buttonSubmit.setOnClickListener(v -> {
            //Clear edit text error message
            TextInputLayoutContactEmail.setError(null);
            textInputLayoutContactName.setError(null);

            if(TextUtils.isEmpty(editTextNameContact.getText().toString().trim())){
                TextInputLayoutContactEmail.setError(null);
                textInputLayoutContactName.setError("Contact name can't be empty");
                textViewUserNotFound.setVisibility(View.GONE);
                cardViewUserFound.setVisibility(View.GONE);
                textInputLayoutContactName.setVisibility(View.GONE);
                buttonSubmit.setVisibility(View.GONE);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("id", idSearchedUser);
            data.put("contactName", editTextNameContact.getText().toString());

            firebaseFirestore.collection("users")
                    .document(currentUser.getUid())
                    .collection("contacts")
                    .document(editTextEmail.getText().toString().trim())
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent = new Intent(AddContactActivity.this, SelectContactActivity.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddContactActivity.this, "Failed add contact", Toast.LENGTH_SHORT).show();
                        }
                    });

        }); // End button submit onClickListener

    } // End onCreate


    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser == null){
            Intent intent = new Intent(AddContactActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }


}



