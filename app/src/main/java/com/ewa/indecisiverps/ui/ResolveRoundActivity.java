package com.ewa.indecisiverps.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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
import com.ewa.indecisiverps.utils.DatabaseUtil;
import com.ewa.indecisiverps.utils.NotificationHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
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
    @Bind(R.id.winnerTextView) TextView mWinnerTextView;
    @Bind(R.id.gameButton) Button mGameButton;
    @Bind(R.id.cancelButton) Button mCancelButton;
    @Bind(R.id.soundButton) ImageButton mSoundButton;
    @Bind(R.id.winningChoiceTextView) TextView mWinningChoiceTextView;
    private Choice mChoice;
    private Round mRound;
    private ArrayList<Round> mRoundList = new ArrayList<>();
    private String[] mMoveArray;
    private ImageView mPlayersImageView;
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
    private boolean mSFX;
    private SharedPreferences.Editor mEditor;
    private NotificationHelper mNotificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolve_round);
        ButterKnife.bind(this);

        mRound = new Round(Calendar.getInstance().getTimeInMillis());

        //choice is always coming from new choice activity or from ready section of decisions activity
        mChoice = Parcels.unwrap(getIntent().getParcelableExtra("choice"));
        mAuth = FirebaseAuth.getInstance();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = sharedPreferences.edit();
        mSFX = sharedPreferences.getBoolean(Constants.PREFS_SFX_KEY, true);
        if(!mSFX){
            mSoundButton.setImageResource(R.drawable.ic_action_mute);
        }

        //figures out whether user is player 1 or 2 and sets up variables accordingly
        setUpPlayersAndOptions();
        setUpSounds();

        //populates ui
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
                playSound(mEntranceSound);
                Animation slideIn2 = AnimationUtils.loadAnimation(ResolveRoundActivity.this, R.anim.slide_in_from_left);
                mOption2TextView.startAnimation(slideIn2);

                slideIn2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mOption2TextView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        playSound(mEntranceSound);
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
                    DatabaseReference pushRef = mChoiceRef.child(mChoice.getStatus()).push();
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
                        playSound(mSoundToPlay);
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
        mSoundButton.setOnClickListener(this);
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
            mChoiceRef = DatabaseUtil.getDatabase().getInstance().getReference(Constants.FIREBASE_CHOICE_REF).child(mUserId);
            mPlayerNumber = mChoice.getPlayer1().equals(mUsername) ? 0: 1;
            mNotificationHelper = new NotificationHelper(this);
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

    //plays most recent round and triggers resolveSocialEndRound (note: this is where status is set to resolved if most recent round not a tie)
    private void showFinishedRound() {
        mRoundRef = DatabaseUtil.getDatabase().getInstance().getReference(Constants.FIREBASE_ROUND_REF).child(mChoice.getPushId());
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
                        playSound(mSoundToPlay);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
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
                                playSound(mSoundToPlay);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                mGameButton.setEnabled(true);
                                if(!mChoice.getWin().equals(Constants.STATUS_TIE)){
                                    mChoiceRef.child(mChoice.getStatus()).child(mChoice.getPushId()).removeValue();
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




    private void resolveSocialEndRound() {
        //update round in database if coming from getOpponentMove
        if(mRound.getPushId() != null && !mChoice.getStatus().equals(Constants.STATUS_RESOLVED)){
            mRoundRef.child(mRound.getPushId()).setValue(mRound);
        }
        int winPosition = mRound.checkWin();

        if(winPosition == -1){
            //Round Tied
            mWinnerTextView.setText("It's a tie!");
            mGameButton.setText("Another Round");
            //if not coming from showFinishedRound, sets up choice variables to send to opponent so they can see finished round
            if(mChoice.getWin() == null){
                mChoice.setWin(Constants.STATUS_TIE);
                mChoiceRef.child(mChoice.getStatus()).child(mChoice.getPushId()).removeValue();
                mChoice.setStatus(Constants.STATUS_PENDING);
                mChoiceRef.child(mChoice.getStatus()).child(mChoice.getPushId()).setValue(mChoice);
            } else {
                //if coming from showFinishedRound, sets win to null in preparation for new round
                mChoice.setWin(null);
            }
        } else {
            //Round not tied
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
            //if not coming from show finished round, sets up choice to be sent to opponent to show finished round
            //Currently not going to make this into an update hashmap despite the multiple operations because 1. it will only ever be at most two things at a time, 2. saving mChoice in its exact state at the time of the operation is vital to app's operation; in order to create an update hashmap that would have the same effect, mChoice would need to be converted into a hashmap of values instead of passed in directly
            if(!mChoice.getStatus().equals(Constants.STATUS_RESOLVED)){
                String sendId = mChoice.getOpponentPlayerId().equals(mUserId) ? mChoice.getStartPlayerId() : mChoice.getOpponentPlayerId();
                DatabaseReference opponentRef = DatabaseUtil.getDatabase().getInstance().getReference(Constants.FIREBASE_CHOICE_REF).child(sendId);
                opponentRef.child(Constants.STATUS_PENDING).child(mChoice.getPushId()).removeValue();
                opponentRef.child(mChoice.getStatus()).child(mChoice.getPushId()).setValue(mChoice);
                mNotificationHelper.sendNotificationToOpponent(mChoice.getOption1() + " vs " + mChoice.getOption2() + " has been resolved!", mChoice);
                //set status to resolved at the end in order to save into current user's database
                mChoiceRef.child(mChoice.getStatus()).child(mChoice.getPushId()).removeValue();
                mChoice.setStatus(Constants.STATUS_RESOLVED);
            }
            mChoiceRef.child(mChoice.getStatus()).child(mChoice.getPushId()).setValue(mChoice);
            mWinningChoiceTextView.setText(mOptions[winPosition] + " wins!");
            mWinningChoiceTextView.setVisibility(View.VISIBLE);
            mGameButton.setText("New Decision");
            mCancelButton.setText("Great! Done Now.");
        }
        mWinnerTextView.setVisibility(View.VISIBLE);
        mGameButton.setVisibility(View.VISIBLE);
    }

    //only called for new round (coming from choose move drawer and opponent has not gone yet)
    //all this ever does is update a round object with the current move list (mRound is initialized in on create), save it to a database, and then send the choice to opponent
    private void resolveSocialStartRound() {
        for(int i = 0; i < mMoveArray.length; i++){
            if(mMoveArray[i] == null){
                mMoveArray[i] = "";
            }
        }
        mRound.setPlayer1Move(mMoveArray[0]);
        mRound.setPlayer2Move(mMoveArray[1]);
        mRoundRef = DatabaseUtil.getDatabase().getInstance().getReference(Constants.FIREBASE_ROUND_REF).child(mChoice.getPushId());
        DatabaseReference pushRef = mRoundRef.push();
        if(!mChoice.getStatus().equals(Constants.STATUS_PENDING)){
            mChoice.setStatus(Constants.STATUS_PENDING);
        }
        mRound.setPushId(pushRef.getKey());
        pushRef.setValue(mRound);
        String sendId = mChoice.getOpponentPlayerId().equals(mUserId) ? mChoice.getStartPlayerId() : mChoice.getOpponentPlayerId();
        DatabaseReference opponentRef = DatabaseUtil.getDatabase().getInstance().getReference(Constants.FIREBASE_CHOICE_REF).child(sendId);
        if(mChoice.getWin() != null){
            opponentRef.child(mChoice.getStatus()).child(mChoice.getPushId()).removeValue();
        }
        mChoice.setStatus(Constants.STATUS_READY);
        opponentRef.child(mChoice.getStatus()).child(mChoice.getPushId()).setValue(mChoice);
        Toast.makeText(this, "Decision sent to opponent!", Toast.LENGTH_SHORT).show();
        mNotificationHelper.sendNotificationToOpponent(mUsername + " has started a round with you!", mChoice);
        Intent intent = new Intent(ResolveRoundActivity.this, MainActivity.class);
        startActivity(intent);
    }

    //solo rounds are always resolved immediately and have two database transaction (one for round, one for choice)
    private void resolveSoloRound() {
        int winPosition;
        for(int i = 0; i < mMoveArray.length; i++){
            if(mMoveArray[i] == null){
                mMoveArray[i] = mComputerMove;
            }
        }
        mRound.setPlayer1Move(mMoveArray[0]);
        mRound.setPlayer2Move(mMoveArray[1]);
        if(mUserId != null){
            mRoundRef = DatabaseUtil.getDatabase().getInstance().getReference(Constants.FIREBASE_ROUND_REF).child(mChoice.getPushId());
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
                mChoiceRef.child(Constants.STATUS_PENDING).child(mChoice.getPushId()).removeValue();
                mChoiceRef.child(mChoice.getStatus()).child(mChoice.getPushId()).setValue(mChoice);
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
            case R.id.soundButton:
                mSFX = !mSFX;
                if(mSFX){
                    mSoundButton.setImageResource(R.drawable.ic_action_sound);
                } else {
                    mSoundButton.setImageResource(R.drawable.ic_action_mute);
                }
                mEditor.putBoolean(Constants.PREFS_SFX_KEY, mSFX).apply();
                break;
        }

    }


    private void setMoveImageAndSound(String move, ImageView targetView) {
        switch (move) {
            case Constants.RPS_PAPER:
                targetView.setImageResource(R.drawable.paper2);
                mSoundToPlay = mPaperSound;
                break;
            case Constants.RPS_ROCK:
                targetView.setImageResource(R.drawable.rock);
                mSoundToPlay = mRockSound;
                break;
            default:
                targetView.setImageResource(R.drawable.scissors2);
                mSoundToPlay = mScissorsSound;
                break;
        }
    }

    private void playSound(final int soundToPlay){
        if(mSFX){
            if(soundToPlay == mEntranceSound){
                mSoundPool.play(soundToPlay, 1.0F, 1.0F, 0, 0, 1.0F);
            } else {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mSoundPool.play(soundToPlay, 1.0F, 1.0F, 0, 0, 1.0F);
                    }
                }, 100);
            }
        }
    }

    private void getOpponentMove() {
        mRoundRef = DatabaseUtil.getDatabase().getInstance().getReference(Constants.FIREBASE_ROUND_REF).child(mChoice.getPushId());
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
                        playSound(mSoundToPlay);
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

    private void gameMove(){
        String[] rpsArray = {Constants.RPS_PAPER, Constants.RPS_ROCK, Constants.RPS_SCISSORS};
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
                playSound(mSoundToPlay);
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

    @Override
    protected void onStop() {
        super.onStop();
        if(mChoice.getMode()==1 && mChoice.getPushId() != null && mChoice.getStatus().equals(Constants.STATUS_PENDING)){
            mChoiceRef.child(mChoice.getStatus()).child(mChoice.getPushId()).removeValue();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSoundPool.release();
        mSoundPool = null;
    }
}
