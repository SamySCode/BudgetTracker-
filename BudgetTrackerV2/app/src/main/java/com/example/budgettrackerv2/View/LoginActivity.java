package com.example.budgettrackerv2.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.budgettrackerv2.Controller.HomeActivity;
import com.example.budgettrackerv2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button signupButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        EditText emailInput = findViewById(R.id.input_email);
        EditText passwordInput = findViewById(R.id.input_password);
        Button loginButton = findViewById(R.id.button_login);
        Button signupButton = findViewById(R.id.button_signup);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (email.isEmpty()) {
                    // Display message to the user
                    Toast.makeText(getApplicationContext(), "Please enter your email.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty()) {
                    // Display message to the user
                    Toast.makeText(getApplicationContext(), "Please enter your password.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                mDialog.setMessage("Processing...");
                mDialog.show();
                // Perform login using FirebaseAuth
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Login success, move to next activity
                                    mDialog.dismiss();
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(), "Login Successful.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // If login fails, display a message to the user
                                    mDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Login failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the sign up activity when the sign up button is clicked
                Intent signUpIntent = new Intent(getApplicationContext(), SignActivity.class);
                startActivity(signUpIntent);
            }
        });
    }
}