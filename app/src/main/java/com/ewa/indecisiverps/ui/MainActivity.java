package com.ewa.indecisiverps.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ewa.indecisiverps.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.headingTextView) TextView mHeadingTextView;
    @Bind(R.id.subheaadingTextView) TextView mSubheadingTextView;
    @Bind(R.id.decideNowButton) Button mDecideNowButton;
    @Bind(R.id.decisionsButton) Button mDecisionsButton;
    @Bind(R.id.socialButton) Button mSocialButton;
    @Bind(R.id.aboutButton) Button mAboutButton;
    @Bind(R.id.loginButton) Button mLoginButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String mUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Typeface headingFont = Typeface.createFromAsset(getAssets(), "fonts/titan_one_regular.ttf");
        mHeadingTextView.setTypeface(headingFont);
        mSubheadingTextView.setTypeface(headingFont);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mUserName =  user.getDisplayName();
                    mLoginButton.setText("Logout");
                    mDecisionsButton.setVisibility(View.VISIBLE);
                    mSocialButton.setVisibility(View.VISIBLE);
                } else {
                    mLoginButton.setText("Login");
                    mDecisionsButton.setVisibility(View.GONE);
                    mSocialButton.setVisibility(View.GONE);
                }
            }
        };

        mDecideNowButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mAboutButton.setOnClickListener(this);
        mDecisionsButton.setOnClickListener(this);
        mSocialButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.decideNowButton:
                Intent decideNowIntent = new Intent(this, NewChoiceActivity.class);
                startActivity(decideNowIntent);
                break;
            case R.id.aboutButton:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.loginButton:
                if(mLoginButton.getText().toString().equals("Login")){
                    Intent loginIntent = new Intent(this, LoginActivity.class);
                    startActivity(loginIntent);
                } else {
                    FirebaseAuth.getInstance().signOut();
                }
                break;
            case R.id.decisionsButton:
                Intent decisionsIntent = new Intent(this, DecisionsActivity.class);
                startActivity(decisionsIntent);
                break;
            case R.id.socialButton:
                Intent socialIntent = new Intent(this, SocialActivity.class);
                startActivity(socialIntent);
        }
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
