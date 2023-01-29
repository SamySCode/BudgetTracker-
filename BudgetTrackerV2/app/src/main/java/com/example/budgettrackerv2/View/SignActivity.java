package com.example.budgettrackerv2.View;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.example.budgettrackerv2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;

    private EditText passwordInput;
    private Button signupButton;
    private EditText emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        mAuth = FirebaseAuth.getInstance();

        mDialog = new ProgressDialog(this);

        EditText emailInput = findViewById(R.id.input_email);
        EditText passwordInput = findViewById(R.id.input_password);
        Button signupButton = findViewById(R.id.button_signup);

        signupButton.setOnClickListener(new View.OnClickListener() {
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
                // Perform sign up using FirebaseAuth
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign up success, move to next activity
                                    mDialog.dismiss();
                                    Intent intent = new Intent(SignActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(), "Registration Successful.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // If sign up fails, display a message to the user
                                    mDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Registration failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}