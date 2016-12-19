package com.ewa.indecisiverps.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ewa.indecisiverps.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewChoiceActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.choiceOneEditText) EditText mChoiceOneEditText;
    @Bind(R.id.choiceTwoEditText) EditText mChoiceTwoEditText;
    @Bind(R.id.soloButton) Button mSoloButton;
    @Bind(R.id.friendButton) Button mFriendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_choice);
        ButterKnife.bind(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.soloButton:
                break;
            case R.id.friendButton:
                break;
        }
    }
}
