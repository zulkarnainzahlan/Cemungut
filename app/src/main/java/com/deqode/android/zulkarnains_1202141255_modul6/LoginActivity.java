package com.deqode.android.zulkarnains_1202141255_modul6;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    EditText loginEmail, loginPassword;
    Button btnLogin;
    TextView linkSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // create new firebase instance
        auth = FirebaseAuth.getInstance();

        // check if user already logged in
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        loginEmail = findViewById(R.id.edEmail);
        loginPassword = findViewById(R.id.edPass);
        linkSignUp = findViewById(R.id.bDaftar);
        btnLogin = findViewById(R.id.bMasuk);

        // to sign up activity
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginFirebase();
            }
        });
    }

    private void loginFirebase() {
        String email = loginEmail.getText().toString();
        final String password = loginPassword.getText().toString();

        // checking fields
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Your email is still empty",
                    Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Your password is still empty",
                    Toast.LENGTH_SHORT).show();
        }

        // login user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // if login not success
                        if (!task.isSuccessful()) {
                            if (password.length() < 6) { // if password less than 6 chars
                                loginPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "Authentication Failed!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        // if login success
                        else {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }
}
