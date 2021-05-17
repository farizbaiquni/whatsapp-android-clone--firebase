package com.example.whatsapp_android_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SignUpEmailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestoreDatabase;
    private FirebaseDatabase realtimeDatabase;
    private DatabaseReference realtimeReference;

    private Button buttonCreateAccount;
    private TextView textViewAlreadyHaveAccount;
    private EditText editTextEmailSignUp, editTextPasswordSignUp, editTextUsernameSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_email);

        textViewAlreadyHaveAccount = findViewById(R.id.text_view_login);
        editTextEmailSignUp = findViewById(R.id.edit_text_email_signUp);
        editTextPasswordSignUp = findViewById(R.id.edit_text_password_signUp);
        buttonCreateAccount = findViewById(R.id.button_create_account_email);
        editTextUsernameSignUp = findViewById(R.id.edit_text_username_signUp);

        mAuth = FirebaseAuth.getInstance();
        firestoreDatabase = FirebaseFirestore.getInstance();
        realtimeDatabase = FirebaseDatabase.getInstance();
        realtimeReference = realtimeDatabase.getReference("users");



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

                String email = editTextEmailSignUp.getText().toString();
                String password = editTextPasswordSignUp.getText().toString();
                final String username = editTextUsernameSignUp.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
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
                                            .setDisplayName(username)
                                            .build();


                                    currentUser.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    Map<String, Object> user = new HashMap<>();
                                                    user.put("uid",currentUser.getUid());
                                                    user.put("username", currentUser.getDisplayName());
                                                    user.put("description", "");
                                                    user.put("email", currentUser.getEmail());
                                                    user.put("phone", "" );
                                                    user.put("groups", Arrays.asList());
                                                    user.put("contacts", Arrays.asList(1, 2, 3));
                                                    user.put("create_at", new Timestamp(new Date()));;

                                                    Map<String, Object> userr = new HashMap<>();
                                                    userr.put("uid",currentUser.getUid());
                                                    userr.put("username", username );
                                                    userr.put("email", email );
                                                    userr.put("phone", "" );

                                                    //REALTIME DATABASE
                                                    realtimeReference.setValue(userr);

                                                    //FIRESTORE DATABASE
                                                    firestoreDatabase.collection("users")
                                                            .document(currentUser.getUid())
                                                            .set(user)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(SignUpEmailActivity.this, "Firestore Sukses", Toast.LENGTH_SHORT).show();
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
            };
        });


    }
}