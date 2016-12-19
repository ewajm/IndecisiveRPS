package com.ewa.indecisiverps.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ewa.indecisiverps.R;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Typeface headingFont = Typeface.createFromAsset(getAssets(), "fonts/titan_one_regular.ttf");
        mHeadingTextView.setTypeface(headingFont);
        mSubheadingTextView.setTypeface(headingFont);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.decideNowButton:
                Intent intent = new Intent(this, NewChoiceActivity.class);
                startActivity(intent);
                break;
            case R.id.aboutButton:
                break;
            case R.id.loginButton:
                break;
        }
    }
}
