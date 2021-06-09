package com.example.whatsapp_android_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp_android_clone.model.SignUpEmailActivityModel;
import com.example.whatsapp_android_clone.viewModel.SignUpEmailActivityViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SignUpEmailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestoreDatabase;

    private SignUpEmailActivityViewModel signUpViewModel;

    private Boolean isError;

    private Button buttonCreateAccount;
    private TextView textViewAlreadyHaveAccount;
    private EditText editTextIdSignUp, editTextEmailSignUp, editTextPasswordSignUp, editTextUsernameSignUp;
    private TextInputLayout textInputLayoutId, textInputLayoutEmail, textInputLayoutPassword, textInputLayoutUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_email);


        textViewAlreadyHaveAccount = findViewById(R.id.text_view_login);
        buttonCreateAccount = findViewById(R.id.button_create_account_email);
        editTextIdSignUp = findViewById(R.id.edit_text_id_signUp);
        editTextEmailSignUp = findViewById(R.id.edit_text_email_signUp);
        editTextPasswordSignUp = findViewById(R.id.edit_text_password_signUp);
        editTextUsernameSignUp = findViewById(R.id.edit_text_username_signUp);

        textInputLayoutId = findViewById(R.id.edit_text_id_signUp_layout);
        textInputLayoutEmail = findViewById(R.id.edit_text_email_signUp_layout);
        textInputLayoutPassword = findViewById(R.id.edit_text_password_signUp_layout);
        textInputLayoutUsername = findViewById(R.id.edit_text_username_signUp_layout);

        mAuth = FirebaseAuth.getInstance();
        firestoreDatabase = FirebaseFirestore.getInstance();

        signUpViewModel = new ViewModelProvider(this).get(SignUpEmailActivityViewModel.class);

        editTextUsernameSignUp.setText(signUpViewModel.getSignUpModel().getValue().getUsername());
        editTextIdSignUp.setText(signUpViewModel.getSignUpModel().getValue().getId());
        editTextEmailSignUp.setText(signUpViewModel.getSignUpModel().getValue().getEmail());
        editTextPasswordSignUp.setText(signUpViewModel.getSignUpModel().getValue().getPassword());


        textViewAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpEmailActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isError = false;
                clearErrorMessage();

                if(TextUtils.isEmpty(editTextUsernameSignUp.getText().toString())){
                    textInputLayoutUsername.setError("Username can't be empty");
                    isError = true;
                }

                if(TextUtils.isEmpty(editTextIdSignUp.getText().toString())){
                    textInputLayoutId.setError("Id can't be empty");
                    isError = true;
                }

                if(TextUtils.isEmpty(editTextEmailSignUp.getText().toString())){
                    textInputLayoutEmail.setError("Email can't be empty");
                    isError = true;
                } else if(!Patterns.EMAIL_ADDRESS.matcher(editTextEmailSignUp.getText().toString().trim()).matches()){
                    textInputLayoutEmail.setError("Invalid email addrress format");
                    isError = true;
                }

                if(TextUtils.isEmpty(editTextPasswordSignUp.getText().toString())){
                    textInputLayoutPassword.setError("Password can't be empty");
                    isError = true;
                } else if(editTextPasswordSignUp.getText().toString().length() < 8){
                    textInputLayoutPassword.setError("Password must be at least 8 characters");
                    isError = true;
                }

                if(!isError){
                    String username = editTextUsernameSignUp.getText().toString();
                    String id = editTextIdSignUp.getText().toString();
                    String email = editTextEmailSignUp.getText().toString();
                    String password = editTextPasswordSignUp.getText().toString();

                    SignUpEmailActivityModel model = new SignUpEmailActivityModel(username, id, email, password);

                    signUpViewModel.setSignUpModel(model);

                    //Check is id has been used by other user or not
                    firestoreDatabase.collection("users")
                            .whereEqualTo("id", signUpViewModel.getSignUpModel().getValue().getId())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            textInputLayoutId.setError("Id has been used by other user");
                                            isError = true;
                                        }
                                    }

                                    if(task.isComplete()){
                                        if(!isError){
                                            createUserAccount();
                                        }
                                    }
                                }
                            });
                } //End else if

            };

        }); //End button create account listener

    } // End onCreate


    private void createUserAccount(){
        mAuth.createUserWithEmailAndPassword(signUpViewModel.getSignUpModel().getValue().getEmail()
                ,signUpViewModel.getSignUpModel().getValue().getPassword())
                .addOnCompleteListener(SignUpEmailActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            editTextUsernameSignUp.setText(null);
                            editTextEmailSignUp.setText(null);
                            editTextPasswordSignUp.setText(null);

                            currentUser = FirebaseAuth.getInstance().getCurrentUser();

                            //UPDATE DISPLAY NAME CURRENT USER
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(signUpViewModel.getSignUpModel().getValue().getUsername())
                                    .build();


                            currentUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            Map<String, Object> user = new HashMap<>();
                                            user.put("uid",currentUser.getUid());
                                            user.put("id", signUpViewModel.getSignUpModel().getValue().getEmail());
                                            user.put("username", currentUser.getDisplayName());
                                            user.put("description", "Hey there! Iam using WhatsApp");
                                            user.put("email", currentUser.getEmail());
                                            user.put("phone", "" );
                                            user.put("photoProfile", "");
                                            user.put("rooms", Arrays.asList());
                                            user.put("dateCreated", new Timestamp(new Date()));;

                                            //FIRESTORE DATABASE
                                            firestoreDatabase.collection("users")
                                                    .document(currentUser.getUid())
                                                    .set(user)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Intent intent = new Intent(SignUpEmailActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(SignUpEmailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                    });

                            //ELSE CREATE ACCOUNT EMAIL AND PASSWORD
                        } else {
                            Toast.makeText(SignUpEmailActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    } //End method createUserAccount()

    private void clearErrorMessage(){
        textInputLayoutUsername.setError(null);
        textInputLayoutEmail.setError(null);
        textInputLayoutId.setError(null);
        textInputLayoutPassword.setError(null);
    }

}