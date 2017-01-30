package com.ewa.indecisiverps.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ewa.indecisiverps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    @Bind(R.id.loginButton) Button mPasswordLoginButton;
    @Bind(R.id.emailEditText) EditText mEmailEditText;
    @Bind(R.id.passwordEditText) EditText mPasswordEditText;
    @Bind(R.id.registerTextView) TextView mRegisterTextView;
    @Bind(R.id.firebaseLoginTextView) TextView mFirebaseLoginTextView;
    @Bind(R.id.forgotPasswordTextView) TextView mForgotPasswordTextView;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressDialog mAuthProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
        mRegisterTextView.setOnClickListener(this);
        mPasswordLoginButton.setOnClickListener(this);
        mForgotPasswordTextView.setOnClickListener(this);
        createAuthProgressDialog();
        Typeface headingFont = Typeface.createFromAsset(getAssets(), "fonts/titan_one_regular.ttf");
        mFirebaseLoginTextView.setTypeface(headingFont);

    }

    private void createAuthProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading...");
        mAuthProgressDialog.setMessage("Authenticating with Firebase...");
        mAuthProgressDialog.setCancelable(false);
    }

    private void createPasswordResetDialog(){
        LayoutInflater inflater = this.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.password_reset_dialog_layout, null);
        final EditText resetEmailEditText = (EditText) dialogView.findViewById(R.id.resetEmailEditText);
        final AlertDialog resetDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Request Reset", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create();
        resetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = resetDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String resetEmail = resetEmailEditText.getText().toString();
                        boolean isGoodEmail =
                                (resetEmail != null && android.util.Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches());
                        if(isGoodEmail){
                            mAuth.sendPasswordResetEmail(resetEmail)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "Reset instructions sent to email address", Toast.LENGTH_SHORT).show();
                                                resetDialog.dismiss();
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Something went wrong - please check your email address", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        } else {
                            resetEmailEditText.setError("Not a valid email");
                        }
                    }
                });
            }
        });
        resetDialog.show();
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.registerTextView:
                Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.loginButton:
                loginWithPassword();
                break;
            case R.id.forgotPasswordTextView:
                createPasswordResetDialog();
                break;
        }
    }

    private void loginWithPassword() {
        String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();
        if (email.equals("")) {
            mEmailEditText.setError("Please enter your email");
            return;
        }
        if (password.equals("")) {
            mPasswordEditText.setError("Password cannot be blank");
            return;
        }
        mAuthProgressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mAuthProgressDialog.dismiss();
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
