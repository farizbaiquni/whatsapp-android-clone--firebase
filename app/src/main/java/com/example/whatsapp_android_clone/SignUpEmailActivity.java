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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpEmailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button buttonCreateAccount;
    private TextView textViewAlreadyHaveAccount;
    private EditText editTextEmailSignUp, editTextPasswordSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_email);

        textViewAlreadyHaveAccount = findViewById(R.id.text_view_login);
        editTextEmailSignUp = findViewById(R.id.edit_text_email_signUp);
        editTextPasswordSignUp = findViewById(R.id.edit_text_password_signUp);

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
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpEmailActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Intent intent = new Intent(SignUpEmailActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignUpEmailActivity.this, "Failed craate account", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            };
        });

//        @Override
//        public void onComplete(@NonNull Task<AuthResult> task) {
//            if (task.isSuccessful()) {
//                // Sign in success, update UI with the signed-in user's information
////                                    Intent intent = new Intent(SignUpEmailActivity.this, MainActivity.class);
////                                    startActivity(intent);
//            } else {
//                // If sign in fails, display a message to the user.
////                                    Toast.makeText(SignUpEmailActivity.this, "Failed craate account", Toast.LENGTH_SHORT).show();
//            }
//        }


    } //OnCreate
}