package com.ewa.indecisiverps.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.models.Choice;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChooseMoveActivity extends AppCompatActivity {
    @Bind(R.id.headingTextView) TextView mHeadingTextView;
    @Bind(R.id.playingForTextView) TextView mPlayingForTextView;
    @Bind(R.id.paperButton) Button mPaperButton;
    @Bind(R.id.scissorsButton) Button mScissorsButton;
    @Bind(R.id.rockButton) Button mRockButton;
    Choice mChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_move);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mChoice = Parcels.unwrap(intent.getParcelableExtra("choice"));

        //eventually add logic for determining which player is which
        String yourChoice = mChoice.getOptions().get(mChoice.getPlayersToOptions().get(0));

        Typeface headingFont = Typeface.createFromAsset(getAssets(), "fonts/titan_one_regular.ttf");
        mHeadingTextView.setTypeface(headingFont);
        mPlayingForTextView.setTypeface(headingFont);


    }
}
