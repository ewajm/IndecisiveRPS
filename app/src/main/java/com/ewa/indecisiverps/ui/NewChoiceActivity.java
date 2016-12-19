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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_choice);
        ButterKnife.bind(this);

        Typeface headingFont = Typeface.createFromAsset(getAssets(), "fonts/titan_one_regular.ttf");
        mHeadingTextView.setTypeface(headingFont);
        mSoloButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String option1 = mChoiceOneEditText.getText().toString().trim();
        String option2 = mChoiceTwoEditText.getText().toString().trim();


        if(option1.length() > 0 && option2.length() > 0){
            List<String> optionList = new ArrayList<>();
            optionList.add(option1);
            optionList.add(option2);
            Choice choice = new Choice(optionList);
            Random random = new Random();
            choice.getPlayersToOptions().add(random.nextInt(optionList.size()));
            switch(view.getId()){
                case R.id.soloButton:
                    choice.setMode(1);
                    Intent intent = new Intent(this, ChooseMoveActivity.class);
                    intent.putExtra("choice", Parcels.wrap(choice));
                    startActivity(intent);
                    break;
                case R.id.friendButton:
                    choice.setMode(2);
                    break;
            }
        } else {
            Toast.makeText(this, "Please enter two choices", Toast.LENGTH_SHORT).show();
        }

    }
}
