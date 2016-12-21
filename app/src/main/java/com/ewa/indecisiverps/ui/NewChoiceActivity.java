package com.ewa.indecisiverps.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.models.Choice;
import com.google.firebase.auth.FirebaseAuth;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewChoiceActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.choiceOneEditText) EditText mChoiceOneEditText;
    @Bind(R.id.choiceTwoEditText) EditText mChoiceTwoEditText;
    @Bind(R.id.soloButton) Button mSoloButton;
    @Bind(R.id.friendButton) Button mFriendButton;
    @Bind(R.id.headingTextView) TextView mHeadingTextView;
    String mUserName;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_choice);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth != null){
            mUserName = mAuth.getCurrentUser().getDisplayName();
        }
        Typeface headingFont = Typeface.createFromAsset(getAssets(), "fonts/titan_one_regular.ttf");
        mHeadingTextView.setTypeface(headingFont);
        mSoloButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String option1 = mChoiceOneEditText.getText().toString().trim();
        String option2 = mChoiceTwoEditText.getText().toString().trim();


        if(option1.length() > 0 && option2.length() > 0){
            Choice newChoice = new Choice(option1, option2);
            Random random = new Random();
            if(random.nextInt(2) == 0){
                if(mUserName != null){
                    newChoice.setPlayer1(mUserName);
                } else {
                    newChoice.setPlayer1("user");
                }
                newChoice.setPlayer2("computer");
            } else {
                if(mUserName != null){
                    newChoice.setPlayer2(mUserName);
                } else {
                    newChoice.setPlayer2("user");
                }
                newChoice.setPlayer1("computer");
            }
            switch(view.getId()){
                case R.id.soloButton:
                    newChoice.setMode(1);
                    Intent intent = new Intent(this, ResolveRoundActivity.class);
                    intent.putExtra("choice", Parcels.wrap(newChoice));
                    startActivity(intent);
                    break;
                case R.id.friendButton:
                    newChoice.setMode(2);
                    break;
            }
        } else {
            Toast.makeText(this, "Please enter two choices", Toast.LENGTH_SHORT).show();
        }

    }
}
