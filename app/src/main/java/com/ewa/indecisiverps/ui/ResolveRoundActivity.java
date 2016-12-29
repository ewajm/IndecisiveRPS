package com.ewa.indecisiverps.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.models.Choice;
import com.ewa.indecisiverps.models.Round;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
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
    @Bind(R.id.winningChoiceTextView) TextView mWinningChoiceTextView;
    private Choice mChoice;
    private Round mRound;
    private ArrayList<Round> mRoundList = new ArrayList<>();
    private String[] mMoveArray;
    private ImageView mPlayersImageView;
    private ObjectAnimator mSlidInAnimator;
    private ImageView mOpponentImageView;
    private String[] mOptions;
    private String mComputerMove;
    private int mPlayerNumber;
    private int mOpponentNumber;
    private DatabaseReference mChoiceRef;
    private DatabaseReference mRoundRef;
    String mUserId;
    String mUsername;
    private FirebaseAuth mAuth;
    private SoundPool mSoundPool;
    private int mEntranceSound;
    private int mPaperSound;
    private int mScissorsSound;
    private int mRockSound;
    private int mSoundToPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolve_round);
        ButterKnife.bind(this);

        mRound = new Round();
        mChoice = Parcels.unwrap(getIntent().getParcelableExtra("choice"));
        mAuth = FirebaseAuth.getInstance();
        
        setUpPlayersAndOptions();
        setUpSounds();
        
        mPlayingForTextView.setText(String.format(getString(R.string.playing_for), mOptions[mPlayerNumber]));
        mOption1TextView.setText(mOptions[0]);
        mOption2TextView.setText(mOptions[1]);
        Typeface headingFont = Typeface.createFromAsset(getAssets(), "fonts/titan_one_regular.ttf");
        mPlayingForTextView.setTypeface(headingFont);
        mOption1TextView.setTypeface(headingFont);
        mOption2TextView.setTypeface(headingFont);
        mWinnerTextView.setTypeface(headingFont);
        mWinningChoiceTextView.setTypeface(headingFont);
        mGameButton.setEnabled(false);

        mBottomSheetBehavior1 = BottomSheetBehavior.from(mBottomSheet);


        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right);
        mOption1TextView.startAnimation(slideIn);

        slideIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mOption1TextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSoundPool.play(mEntranceSound, 1.0F, 1.0F, 0, 0, 1.0F);
                Animation slideIn2 = AnimationUtils.loadAnimation(ResolveRoundActivity.this, R.anim.slide_in_from_left);
                mOption2TextView.startAnimation(slideIn2);

                slideIn2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mOption2TextView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mSoundPool.play(mEntranceSound, 1.0F, 1.0F, 0, 0, 1.0F);
                        if(mChoice.getWin() == null){
                            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                            mGameButton.setEnabled(true);
                        } else {
                            showFinishedRound();
                        }
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
                mGameButton.setText("Another Round");
                mPlayersImageView.setVisibility(View.INVISIBLE);
                switch(view.getId()){
                    case R.id.scissorsButton:
                        mMoveArray[mPlayerNumber] = Constants.RPS_SCISSORS;
                        break;
                    case R.id.rockButton:
                        mMoveArray[mPlayerNumber] = Constants.RPS_ROCK;
                        break;
                    case R.id.paperButton:
                        mMoveArray[mPlayerNumber] = Constants.RPS_PAPER;
                        break;
                }
                setMoveImageAndSound(mMoveArray[mPlayerNumber], mPlayersImageView);
                if(mChoice.getStatus() == null){
                    mChoice.setStatus(Constants.STATUS_PENDING);
                }
                if(mUserId != null && mChoice.getPushId() == null){
                    DatabaseReference pushRef = mChoiceRef.push();
                    mChoice.setPushId(pushRef.getKey());
                    pushRef.setValue(mChoice);
                }
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Animation slideInBottom = AnimationUtils.loadAnimation(ResolveRoundActivity.this, R.anim.slide_in_from_bottom);
                mPlayersImageView.startAnimation(slideInBottom);

                slideInBottom.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mPlayersImageView.setVisibility(View.VISIBLE);
                        mSoundPool.play(mSoundToPlay, 1.0F, 1.0F, 0, 0, 1.0F);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        if(mChoice.getMode() == 1){
                            gameMove();
                        } else if(mChoice.getStatus().equals(Constants.STATUS_PENDING)){
                            resolveSocialStartRound();
                        } else {
                            getOpponentMove();
                        }
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

    private void setUpSounds() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = (new SoundPool.Builder()).setMaxStreams(5).build();
        }else{
            mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 5);
        }
        mEntranceSound = mSoundPool.load(this, R.raw.entrance1, 1);
        mPaperSound = mSoundPool.load(this, R.raw.paper, 1);
        mScissorsSound = mSoundPool.load(this, R.raw.scissors, 1);
        mRockSound = mSoundPool.load(this, R.raw.rock, 1);

    }


    //sets up game variables
    private void setUpPlayersAndOptions() {
        if(mAuth.getCurrentUser() != null){
            mUserId = mAuth.getCurrentUser().getUid();
            mUsername = mAuth.getCurrentUser().getDisplayName();
            mChoiceRef = FirebaseDatabase.getInstance().getReference("choices").child(mUserId);
            mPlayerNumber = mChoice.getPlayer1().equals(mUsername) ? 0: 1;
        } else {
            mPlayerNumber = mChoice.getPlayer1().equals("user") ? 0: 1;
        }
        if(mChoice.isImpartialityMode()){
            mPlayingForTextView.setVisibility(View.GONE);
        }
        mOpponentNumber = mPlayerNumber == 0 ? 1:0;
        mPlayersImageView = mPlayerNumber == 0 ? mOption1ImageView: mOption2ImageView;
        mOpponentImageView = mPlayerNumber == 0 ? mOption2ImageView: mOption1ImageView;
        mOptions = new String[]{mChoice.getOption1(), mChoice.getOption2()};
    }

    private void showFinishedRound() {
        mRoundRef = FirebaseDatabase.getInstance().getReference("rounds").child(mChoice.getPushId());
        mRoundRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Round round = snapshot.getValue(Round.class);
                    mRoundList.add(round);
                }
                mPlayersImageView.setVisibility(View.INVISIBLE);
                mOpponentImageView.setVisibility(View.INVISIBLE);
                if(mChoice.getWin().equals(Constants.STATUS_TIE)){
                    mRound = mRoundList.get(mRoundList.size()-2);
                } else {
                    mRound = mRoundList.get(mRoundList.size()-1);
                }
                if(mPlayerNumber == 0){
                    setMoveImageAndSound(mRound.getPlayer1Move(), mOption1ImageView);
                } else {
                    setMoveImageAndSound(mRound.getPlayer2Move(), mOption2ImageView);
                }


                Animation slideInBottom = AnimationUtils.loadAnimation(ResolveRoundActivity.this, R.anim.slide_in_from_bottom);
                mPlayersImageView.startAnimation(slideInBottom);

                slideInBottom.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mPlayersImageView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mSoundPool.play(mSoundToPlay, 1.0F, 1.0F, 0, 0, 1.0F);
                        if(mOpponentNumber == 0){
                            setMoveImageAndSound(mRound.getPlayer1Move(), mOption1ImageView);
                        } else {
                            setMoveImageAndSound(mRound.getPlayer2Move(), mOption2ImageView);
                        }
                        Animation slideInBottom = AnimationUtils.loadAnimation(ResolveRoundActivity.this, R.anim.slide_in_from_bottom);
                        mOpponentImageView.startAnimation(slideInBottom);
                        slideInBottom.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                mOpponentImageView.setVisibility(View.VISIBLE);
                                mSoundPool.play(mSoundToPlay, 1.0F, 1.0F, 0, 0, 1.0F);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                mGameButton.setEnabled(true);
                                if(!mChoice.getWin().equals(Constants.STATUS_TIE)){
                                    mChoice.setStatus(Constants.STATUS_RESOLVED);
                                }
                                resolveSocialEndRound();
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setMoveImageAndSound(String move, ImageView targetView) {
        if(move.equals(Constants.RPS_PAPER)){
            targetView.setImageResource(R.drawable.paper2);
            mSoundToPlay = mPaperSound;
        } else if(move.equals(Constants.RPS_ROCK)){
            targetView.setImageResource(R.drawable.rock);
            mSoundToPlay = mRockSound;
        } else {
            targetView.setImageResource(R.drawable.scissors2);
            mSoundToPlay = mScissorsSound;
        }
    }

    private void getOpponentMove() {
        mRoundRef = FirebaseDatabase.getInstance().getReference("rounds").child(mChoice.getPushId());
        mRoundRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Round round = snapshot.getValue(Round.class);
                    mRoundList.add(round);
                }
                mRound = mRoundList.get(mRoundList.size()-1);
                mOpponentImageView.setVisibility(View.INVISIBLE);
                if(mRound.getPlayer1Move().length() == 0){
                    mRound.setPlayer1Move(mMoveArray[mPlayerNumber]);
                    mMoveArray[mOpponentNumber] = mRound.getPlayer2Move();
                } else {
                    mRound.setPlayer2Move(mMoveArray[mPlayerNumber]);
                    mMoveArray[mOpponentNumber] = mRound.getPlayer1Move();
                }
                setMoveImageAndSound(mMoveArray[mOpponentNumber], mOpponentImageView);
                Animation slideInBottom = AnimationUtils.loadAnimation(ResolveRoundActivity.this, R.anim.slide_in_from_bottom);
                mOpponentImageView.startAnimation(slideInBottom);

                slideInBottom.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mSoundPool.play(mSoundToPlay, 1.0F, 1.0F, 0, 0, 1.0F);
                        mOpponentImageView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        resolveSocialEndRound();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void resolveSocialEndRound() {
        if(mRound.getPushId() != null && !mChoice.getStatus().equals(Constants.STATUS_RESOLVED)){
            mRoundRef.child(mRound.getPushId()).setValue(mRound);
        }
        int winPosition = mRound.checkWin();
        if(winPosition == -1){
            mWinnerTextView.setText("It's a tie!");
            mGameButton.setText("Another Round");
            if(mChoice.getWin() == null){
                mChoice.setWin(Constants.STATUS_TIE);
                mChoice.setStatus(Constants.STATUS_PENDING);
                mChoiceRef.child(mChoice.getPushId()).setValue(mChoice);
            } else {
                mChoice.setWin(null);
            }
        } else {
            if(winPosition == mPlayerNumber){
                mWinnerTextView.setText("Nice job!");
            } else {
                if(mOpponentNumber == 0){
                    mWinnerTextView.setText("Newp, " + mChoice.getPlayer1() + " wins it!");
                } else {
                    mWinnerTextView.setText("Newp, " + mChoice.getPlayer2() + " wins it!");
                }
            }
            mChoice.setWin(mOptions[winPosition]);
            if(!mChoice.getStatus().equals(Constants.STATUS_RESOLVED)){
                String sendId = mChoice.getOpponentPlayerId().equals(mUserId) ? mChoice.getStartPlayerId() : mChoice.getOpponentPlayerId();
                DatabaseReference opponentRef = FirebaseDatabase.getInstance().getReference("choices").child(sendId).child(mChoice.getPushId());
                opponentRef.setValue(mChoice);
                mChoice.setStatus(Constants.STATUS_RESOLVED);
            }
            mChoiceRef.child(mChoice.getPushId()).setValue(mChoice);
            mWinningChoiceTextView.setText(mOptions[winPosition] + " wins!");
            mWinningChoiceTextView.setVisibility(View.VISIBLE);
            mGameButton.setText("New Decision");
            mCancelButton.setText("Great! Done Now.");
        }
        mWinnerTextView.setVisibility(View.VISIBLE);
        mGameButton.setVisibility(View.VISIBLE);
    }

    private void resolveSocialStartRound() {
        for(int i = 0; i < mMoveArray.length; i++){
            if(mMoveArray[i] == null){
                mMoveArray[i] = "";
            }
        }
        mRound.setPlayer1Move(mMoveArray[0]);
        mRound.setPlayer2Move(mMoveArray[1]);
        mRoundRef = FirebaseDatabase.getInstance().getReference("rounds").child(mChoice.getPushId());
        DatabaseReference pushRef = mRoundRef.push();
        if(!mChoice.getStatus().equals(Constants.STATUS_PENDING)){
            mChoice.setStatus(Constants.STATUS_PENDING);
        }
        mRound.setPushId(pushRef.getKey());
        pushRef.setValue(mRound);
        mChoice.setStatus(Constants.STATUS_READY);
        String sendId = mChoice.getOpponentPlayerId().equals(mUserId) ? mChoice.getStartPlayerId() : mChoice.getOpponentPlayerId();
        DatabaseReference opponentRef = FirebaseDatabase.getInstance().getReference("choices").child(sendId).child(mChoice.getPushId());
        opponentRef.setValue(mChoice);
        Toast.makeText(this, "Decision sent to opponent!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ResolveRoundActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void resolveSoloRound() {
        int winPosition=0;
        for(int i = 0; i < mMoveArray.length; i++){
            if(mMoveArray[i] == null){
                mMoveArray[i] = mComputerMove;
            }
        }
        mRound.setPlayer1Move(mMoveArray[0]);
        mRound.setPlayer2Move(mMoveArray[1]);
        if(mUserId != null){
            mRoundRef = FirebaseDatabase.getInstance().getReference("rounds").child(mChoice.getPushId());
            DatabaseReference pushRef = mRoundRef.push();
            mRound.setPushId(pushRef.getKey());
            pushRef.setValue(mRound);
        }
        winPosition = mRound.checkWin();
        if(winPosition == -1){
            mWinnerTextView.setText("It's a tie!");
            mGameButton.setText("Another Round");
        } else {
            if(winPosition == mPlayerNumber){
                mWinnerTextView.setText("Nice job!");
            } else {
                mWinnerTextView.setText("Lol nope!");
            }
            mChoice.setStatus(Constants.STATUS_RESOLVED);
            mChoice.setWin(mOptions[winPosition]);
            if(mUserId != null){
                mChoiceRef.child(mChoice.getPushId()).setValue(mChoice);
            }
            mWinningChoiceTextView.setText(mOptions[winPosition] + " wins!");
            mWinningChoiceTextView.setVisibility(View.VISIBLE);
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
                    mOption1ImageView.setVisibility(View.INVISIBLE);
                    mOption2ImageView.setVisibility(View.INVISIBLE);
                    mGameButton.setText("Choose Move");
                } else if (mGameButton.getText().toString().equals("New Decision")){
                    Intent newGameIntent = new Intent(this, NewChoiceActivity.class);
                    startActivity(newGameIntent);
                } else {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                break;
        }

    }

    public void gameMove(){
        String[] rpsArray = {Constants.RPS_PAPER, Constants.RPS_ROCK, Constants.RPS_SCISSORS};
        int[] rpsImageArray = {R.drawable.paper2, R.drawable.rock, R.drawable.scissors2};
        Random random = new Random();
        int compMove = random.nextInt(rpsArray.length);
        mComputerMove = rpsArray[compMove];
        mOpponentImageView.setVisibility(View.INVISIBLE);
        setMoveImageAndSound(mComputerMove, mOpponentImageView);
        Animation slideInBottom = AnimationUtils.loadAnimation(ResolveRoundActivity.this, R.anim.slide_in_from_bottom);
        mOpponentImageView.startAnimation(slideInBottom);


        slideInBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mOpponentImageView.setVisibility(View.VISIBLE);
                mSoundPool.play(mSoundToPlay, 1.0F, 1.0F, 0, 0, 1.0F);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                resolveSoloRound();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
