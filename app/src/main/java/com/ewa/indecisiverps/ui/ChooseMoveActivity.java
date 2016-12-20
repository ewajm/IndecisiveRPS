package com.ewa.indecisiverps.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.models.Choice;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChooseMoveActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.headingTextView) TextView mHeadingTextView;
    @Bind(R.id.playingForTextView) TextView mPlayingForTextView;
    @Bind(R.id.paperButton) ImageButton mPaperButton;
    @Bind(R.id.scissorsButton) ImageButton mScissorsButton;
    @Bind(R.id.rockButton) ImageButton mRockButton;
    Choice mChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_move);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mChoice = Parcels.unwrap(intent.getParcelableExtra("choice"));

        //eventually add logic for determining which player is which
        //int playerposition = position of player in the array: playerlist.indexof(userid)

        String yourChoice = mChoice.getPlayer1().equals("user") ? mChoice.getOption1(): mChoice.getOption2();
        mPlayingForTextView.setText(String.format(getString(R.string.playing_for), yourChoice));

        Typeface headingFont = Typeface.createFromAsset(getAssets(), "fonts/titan_one_regular.ttf");
        mHeadingTextView.setTypeface(headingFont);
        mPlayingForTextView.setTypeface(headingFont);

        mPaperButton.setOnClickListener(this);
        mScissorsButton.setOnClickListener(this);
        mRockButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
//        switch(view.getId()){
//            //change these to .add(playerposition, MOVE_CONSTANT)
//            case R.id.scissorsButton:
//                mChoice.getPlayerMoves().add(Constants.RPS_SCISSORS);
//                break;
//            case R.id.rockButton:
//                mChoice.getPlayerMoves().add(Constants.RPS_ROCK);
//                break;
//            case R.id.paperButton:
//                mChoice.getPlayerMoves().add(Constants.RPS_PAPER);
//                break;
//        }
        Intent intent = new Intent(this, ResolveRoundActivity.class);
        intent.putExtra("choice", Parcels.wrap(mChoice));
        startActivity(intent);
    }
}
