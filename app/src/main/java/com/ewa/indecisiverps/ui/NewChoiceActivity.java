package com.ewa.indecisiverps.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.models.Choice;
import com.ewa.indecisiverps.models.User;
import com.ewa.indecisiverps.utils.DatabaseUtil;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewChoiceActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.choiceOneEditText) EditText mChoiceOneEditText;
    @Bind(R.id.choiceTwoEditText) EditText mChoiceTwoEditText;
    @Bind(R.id.soloButton) Button mSoloButton;
    @Bind(R.id.friendButton) Button mFriendButton;
    @Bind(R.id.headingTextView) TextView mHeadingTextView;
    @Bind(R.id.impartialityModeCheckbox) CheckBox mImpartialityModeCheckbox;
    String mUserName;
    String mUserId;
    private User mOpponent;
    ArrayList<User> mFriends = new ArrayList<>();
    ListView mListView;
    private Choice mNewChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_choice);
        ButterKnife.bind(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            mUserName = auth.getCurrentUser().getDisplayName();
            mUserId = auth.getCurrentUser().getUid();
        } else {
            mFriendButton.setEnabled(false);
        }
        Choice choice = Parcels.unwrap(getIntent().getParcelableExtra("choice"));
        if(choice != null){
            mChoiceOneEditText.setText(choice.getOption1());
            mChoiceTwoEditText.setText(choice.getOption2());
            mNewChoice = choice;
            mNewChoice.setStatus(Constants.STATUS_PENDING);
            mNewChoice.setWin(null);
        }

        mOpponent = Parcels.unwrap(getIntent().getParcelableExtra("opponent"));
        if(mOpponent != null){
            mSoloButton.setEnabled(false);
        }
        Typeface headingFont = Typeface.createFromAsset(getAssets(), "fonts/titan_one_regular.ttf");
        mHeadingTextView.setTypeface(headingFont);
        mSoloButton.setOnClickListener(this);
        mFriendButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String option1 = mChoiceOneEditText.getText().toString().trim();
        String option2 = mChoiceTwoEditText.getText().toString().trim();
        if(option1.length() > 0 && option2.length() > 0){
            if(mNewChoice == null){
                mNewChoice = new Choice(option1, option2);
            } else {
                mNewChoice.setOption1(option1);
                mNewChoice.setOption2(option2);
            }
            mNewChoice.setStartPlayerId(mUserId);
            mNewChoice.setImpartialityMode(mImpartialityModeCheckbox.isChecked());
            Random random = new Random();
            if(random.nextInt(2) == 0){
                if(mUserName != null){
                    mNewChoice.setPlayer1(mUserName);
                } else {
                    mNewChoice.setPlayer1("user");
                }
                mNewChoice.setPlayer2("opponent");
            } else {
                if(mUserName != null){
                    mNewChoice.setPlayer2(mUserName);
                } else {
                    mNewChoice.setPlayer2("user");
                }
                mNewChoice.setPlayer1("opponent");
            }
            switch(view.getId()){
                case R.id.soloButton:
                    mNewChoice.setMode(1);
                    Intent intent = new Intent(this, ResolveRoundActivity.class);
                    intent.putExtra("choice", Parcels.wrap(mNewChoice));
                    startActivity(intent);
                    break;
                case R.id.friendButton:
                    mNewChoice.setMode(2);
                    if(mOpponent == null){
                       createFriendDialog();
                    } else {
                        if(mNewChoice.getPlayer1().equals("opponent")){
                            mNewChoice.setPlayer1(mOpponent.getUsername());
                        } else {
                            mNewChoice.setPlayer2(mOpponent.getUsername());
                        }
                        mNewChoice.setOpponentPlayerId(mOpponent.getUserId());
                        Intent friendIntent = new Intent(NewChoiceActivity.this, ResolveRoundActivity.class);
                        friendIntent.putExtra("choice", Parcels.wrap(mNewChoice));
                        startActivity(friendIntent);
                    }
                    break;
            }
        } else {
            Toast.makeText(this, "Please enter two choices", Toast.LENGTH_SHORT).show();
        }

    }

    public void createFriendDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View v = inflater.inflate(R.layout.choose_friend_dialog_layout, null);
        builder.setView(v);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference friendQuery = DatabaseUtil.getDatabase().getInstance().getReference(Constants.FIREBASE_USER_FRIEND_REF).child(userId).child(Constants.STATUS_RESOLVED);
        FirebaseListAdapter friendsAdapter = new FirebaseListAdapter<User>(this, User.class, android.R.layout.simple_list_item_1, friendQuery) {
            @Override
            protected void populateView(View v, User model, int position) {
                ((TextView) v.findViewById(android.R.id.text1)).setText(model.getUsername());
                mFriends.add(model);
            }
        };
        mListView = (ListView) v.findViewById(R.id.friendListView);
        TextView empty = (TextView) v.findViewById(android.R.id.empty);
        mListView.setEmptyView(empty);
        mListView.setAdapter(friendsAdapter);
        // Add action buttons
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User opponent = mFriends.get(i);
                if(mNewChoice.getPlayer1().equals("opponent")){
                    mNewChoice.setPlayer1(opponent.getUsername());
                } else {
                    mNewChoice.setPlayer2(opponent.getUsername());
                }
                mNewChoice.setOpponentPlayerId(opponent.getUserId());
                dialog.dismiss();
                Intent intent = new Intent(NewChoiceActivity.this, ResolveRoundActivity.class);
                intent.putExtra("choice", Parcels.wrap(mNewChoice));
                startActivity(intent);
            }
        });
        dialog.show();
    }

}
