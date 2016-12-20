package com.ewa.indecisiverps.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
    public static final String TAG = ResolveRoundActivity.class.getSimpleName();
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
    @Bind(R.id.winnerTextView) TextView mWinnerTextView;
    @Bind(R.id.gameButton) Button mGameButton;
    @Bind(R.id.cancelButton) Button mCancelButton;
    private Choice mChoice;
    private Round mRound;
    private String[] mMoveArray;
    private ImageView mPlayersImageView;
    private ObjectAnimator mSlidInAnimator;
    private ImageView mOpponentImageView;
    private String[] mOptions;
    private String mComputerMove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolve_round);
        ButterKnife.bind(this);

        mRound = new Round();
        mChoice = Parcels.unwrap(getIntent().getParcelableExtra("choice"));
        final int playerNumber = mChoice.getPlayer1().equals("user") ? 0: 1;
        mPlayersImageView = playerNumber == 0 ? mOption1ImageView: mOption2ImageView;
        mOpponentImageView = playerNumber == 0 ? mOption2ImageView: mOption1ImageView;
        mOptions = new String[]{mChoice.getOption1(), mChoice.getOption2()};
        mPlayingForTextView.setText(String.format(getString(R.string.playing_for), mOptions[playerNumber]));
        mOption1TextView.setText(mOptions[0]);
        mOption2TextView.setText(mOptions[1]);
        Typeface headingFont = Typeface.createFromAsset(getAssets(), "fonts/titan_one_regular.ttf");
        mPlayingForTextView.setTypeface(headingFont);
        mOption1TextView.setTypeface(headingFont);
        mOption2TextView.setTypeface(headingFont);
        mWinnerTextView.setTypeface(headingFont);

        mBottomSheetBehavior1 = BottomSheetBehavior.from(mBottomSheet);

        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right);
        mOption1TextView.startAnimation(slideIn);
        mOption1TextView.setVisibility(View.VISIBLE);
        slideIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation slideIn2 = AnimationUtils.loadAnimation(ResolveRoundActivity.this, R.anim.slide_in_from_left);
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
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

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
                slideInBottom.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        gameMove();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        };
        mScissorsButton.setOnClickListener(moveClickListener);
        mPaperButton.setOnClickListener(moveClickListener);
        mRockButton.setOnClickListener(moveClickListener);
        mGameButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
    }

    private void resolveRound() {
        int winPosition=0;
        for(int i = 0; i < mMoveArray.length; i++){
            if(mMoveArray[i] == null){
                mMoveArray[i] = mComputerMove;
            }
        }
        Log.i(TAG, "resolveRound: " + mMoveArray.toString());
        if(mMoveArray[0].equals(mMoveArray[1])){
            mWinnerTextView.setText("It's a tie!");
            mGameButton.setText("Another Round");
        }else {
            switch(mMoveArray[0]){
                case Constants.RPS_PAPER:
                    if(mMoveArray[1].equals(Constants.RPS_ROCK)){
                        winPosition = 0;
                    } else {
                        winPosition = 1;
                    }
                    break;
                case Constants.RPS_ROCK:
                    if(mMoveArray[1].equals(Constants.RPS_SCISSORS)){
                        winPosition = 0;
                    } else {
                        winPosition = 1;
                    }
                    break;
                case Constants.RPS_SCISSORS:
                    if(mMoveArray[1].equals(Constants.RPS_PAPER)){
                        winPosition = 0;
                    } else {
                        winPosition = 1;
                    }
                    break;
            }
            mWinnerTextView.setText(mOptions[winPosition] + " wins!");
            mGameButton.setText("New Decision");
            mCancelButton.setText("Great! Done Now.");
        }
        mWinnerTextView.setVisibility(View.VISIBLE);
        mGameButton.setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.cancelButton:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.gameButton:
                if(mGameButton.getText().toString().equals("Another Round")){
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                    mWinnerTextView.setVisibility(View.GONE);
                    mGameButton.setVisibility(View.GONE);
                    mOption1ImageView.setVisibility(View.INVISIBLE);
                    mOption2ImageView.setVisibility(View.INVISIBLE);
                } else {
                    Intent newGameIntent = new Intent(this, NewChoiceActivity.class);
                    startActivity(newGameIntent);
                }
                break;
        }

    }

    public void gameMove(){
        String[] rpsArray = {Constants.RPS_PAPER, Constants.RPS_ROCK, Constants.RPS_SCISSORS};
        int[] rpsImageArray = {R.drawable.paper, R.drawable.rock, R.drawable.scissors};
        Random random = new Random();
        int compMove = random.nextInt(rpsArray.length);
        mOpponentImageView.setImageResource(rpsImageArray[compMove]);
        mOpponentImageView.setVisibility(View.INVISIBLE);
        Animation slideInBottom = AnimationUtils.loadAnimation(ResolveRoundActivity.this, R.anim.slide_in_from_bottom);
        mOpponentImageView.startAnimation(slideInBottom);
        mOpponentImageView.setVisibility(View.VISIBLE);
        mComputerMove = rpsArray[compMove];
        slideInBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                resolveRound();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
