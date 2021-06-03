package com.example.whatsapp_android_clone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp_android_clone.databinding.ActivityLoginBinding;
import com.example.whatsapp_android_clone.databinding.ActivityMainBinding;
import com.example.whatsapp_android_clone.model.LoginModel;
import com.example.whatsapp_android_clone.viewModel.LoginViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Button buttonLogin, buttonPhone;
    private TextView forgetPassword, createAccoutEmail;
    private TextInputEditText emailLogin, passwordLogin;
    private TextInputLayout emailLoginLayout, passwordLoginLayout;
    private ActivityLoginBinding binding;

    private LoginModel loginModel;
    private LoginViewModel loginViewModel;
    private Boolean isInvalidInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        buttonLogin = findViewById(R.id.button_login);
        buttonPhone = findViewById(R.id.button_phone);
        createAccoutEmail = findViewById(R.id.text_view_signup_email);
        emailLogin = findViewById(R.id.edit_text_email_login);
        passwordLogin = findViewById(R.id.edit_text_password_login);

        mAuth = FirebaseAuth.getInstance();

        hideProgressBar();

        createAccoutEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpEmailActivity.class);
                startActivity(i);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isInvalidInput = false;

                String email = emailLogin.getText().toString();
                String password = passwordLogin.getText().toString();

                validateInput();

                if(!isInvalidInput){

                    showPrgressBar();

                    loginModel = new LoginModel(binding.editTextEmailLogin.getText().toString(),
                            binding.editTextPasswordLogin.getText().toString());

                    loginViewModel.setLoginModel(loginModel);

                    mAuth.signInWithEmailAndPassword(loginViewModel.getLoginModel().getValue().getEmail(),
                            loginViewModel.getLoginModel().getValue().getPassword())
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        emailLogin.setText(null);
                                        passwordLogin.setText(null);
                                        hideProgressBar();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    } // End onStart


    private void validateInput(){
        if(binding.editTextEmailLogin.getText().toString().length() <= 0){
            binding.editTextEmailLoginLayout.setError("Email can't be empty");
            isInvalidInput = true;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(binding.editTextEmailLogin.getText().toString().trim()).matches()){
            binding.editTextEmailLoginLayout.setError("Invalid email format");
            isInvalidInput = true;
        }

        if(binding.editTextPasswordLogin.getText().toString().length() <= 0){
            binding.editTextPasswordLoginLayput.setError("Password can't be empty");
            isInvalidInput = true;
        }
    } // End validateInput


    private void showPrgressBar(){
        binding.buttonUnclickable.setVisibility(View.VISIBLE);
        binding.progressBarLogin.setVisibility(View.VISIBLE);
    }


    private void hideProgressBar(){
        binding.buttonUnclickable.setVisibility(View.GONE);
        binding.progressBarLogin.setVisibility(View.GONE);
    }

}