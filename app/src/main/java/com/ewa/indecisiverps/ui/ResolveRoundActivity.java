package com.ewa.indecisiverps.ui;

import android.animation.ObjectAnimator;
import android.graphics.Typeface;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.models.Choice;
import com.ewa.indecisiverps.models.Round;

import org.parceler.Parcels;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResolveRoundActivity extends AppCompatActivity implements View.OnClickListener{
    private BottomSheetBehavior mBottomSheetBehavior1;
    @Bind(R.id.rockButton) ImageButton mRockButton;
    @Bind(R.id.scissorsButton) ImageButton mScissorsButton;
    @Bind(R.id.paperButton) ImageButton mPaperButton;
    @Bind(R.id.option1TextView) TextView mOption1TextView;
    @Bind(R.id.option2TextView) TextView mOption2TextView;
    @Bind(R.id.playingForTextView)  TextView mPlayingForTextView;
    @Bind(R.id.option1MoveImageView) ImageView mOption1ImageView;
    @Bind(R.id.option2MoveImageView) ImageView mOption2ImageView;
    @Bind(R.id.bottom_sheet1) View mBottomSheet;
    private Choice mChoice;
    private Round mRound;
    private String[] mMoveArray;
    private ImageView mPlayersImageView;
    private ObjectAnimator mSlidInAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolve_round);
        ButterKnife.bind(this);

        mRound = new Round();
        mChoice = Parcels.unwrap(getIntent().getParcelableExtra("choice"));
        final int playerNumber = mChoice.getPlayer1().equals("user") ? 0: 1;
        mPlayersImageView = playerNumber == 0 ? mOption1ImageView: mOption2ImageView;
        String[] options = {mChoice.getOption1(), mChoice.getOption2()};
        mPlayingForTextView.setText(String.format(getString(R.string.playing_for), options[playerNumber]));
        mOption1TextView.setText(options[0]);
        mOption2TextView.setText(options[1]);

        mBottomSheetBehavior1 = BottomSheetBehavior.from(mBottomSheet);

        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right);
        mOption1TextView.startAnimation(slideIn);
        mOption1TextView.setVisibility(View.VISIBLE);
        Animation slideIn2 = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_left);
        mOption2TextView.startAnimation(slideIn2);
        mOption2TextView.setVisibility(View.VISIBLE);
        slideIn2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        Typeface headingFont = Typeface.createFromAsset(getAssets(), "fonts/titan_one_regular.ttf");
        mPlayingForTextView.setTypeface(headingFont);

        View.OnClickListener moveClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMoveArray = new String[2];
                switch(view.getId()){
                    case R.id.scissorsButton:
                        mMoveArray[playerNumber] = Constants.RPS_SCISSORS;
                        mPlayersImageView.setImageResource(R.drawable.scissors);
                        break;
                    case R.id.rockButton:
                        mMoveArray[playerNumber] = Constants.RPS_ROCK;
                        mPlayersImageView.setImageResource(R.drawable.rock);
                        break;
                    case R.id.paperButton:
                        mMoveArray[playerNumber] = Constants.RPS_PAPER;
                        mPlayersImageView.setImageResource(R.drawable.paper);
                        break;
                }
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mPlayersImageView.setVisibility(View.INVISIBLE);
                Animation slideInBottom = AnimationUtils.loadAnimation(ResolveRoundActivity.this, R.anim.slide_in_from_bottom);
                mPlayersImageView.startAnimation(slideInBottom);
                mPlayersImageView.setVisibility(View.VISIBLE);

            }
        };
        mScissorsButton.setOnClickListener(moveClickListener);
        mPaperButton.setOnClickListener(moveClickListener);
        mRockButton.setOnClickListener(moveClickListener);
    }

    @Override
    public void onClick(View view) {

    }

    public String gameMove(){
        String[] rpsArray = {Constants.RPS_PAPER, Constants.RPS_ROCK, Constants.RPS_SCISSORS};
        Random random = new Random();
        return rpsArray[random.nextInt(rpsArray.length)];
    }
}
