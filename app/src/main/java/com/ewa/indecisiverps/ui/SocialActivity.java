package com.ewa.indecisiverps.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.adapters.FriendListAdapter;
import com.ewa.indecisiverps.adapters.FriendViewHolder;
import com.ewa.indecisiverps.adapters.InvitationViewHolder;
import com.ewa.indecisiverps.adapters.InviteListAdapter;
import com.ewa.indecisiverps.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SocialActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.addFriendButton) Button mAddFriendButton;
    @Bind(R.id.invitationsRecyclerView) RecyclerView mInvitationRecyclerView;
    @Bind(R.id.friendsRecyclerView) RecyclerView mFriendsRecyclerView;
    @Bind(R.id.userIconImageView) ImageView muUserIconImageView;
    @Bind(R.id.usernameTextView) TextView mUsernameTextView;
    @Bind(R.id.invitationsLinearLayout) LinearLayout mInvitationsLinearLayout;
    @Bind(R.id.friendsLinearLayout) LinearLayout mFriendsLinearLayout;
    @Bind(R.id.emptyView) TextView mEmptyView;
    @Bind(R.id.goBackButton) Button mBackButton;
    String mCurrentUserId;
    ArrayList<User> mInvites = new ArrayList<>();
    ArrayList<User> mFriends = new ArrayList<>();
    InviteListAdapter mInviteAdapter;
    FriendListAdapter mFriendsAdapter;
    private String mCurrentUserName;
    private ValueEventListener mFriendsEventListener;
    private Query mFriendsQuery;
    private DatabaseReference mPendingFriendQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        ButterKnife.bind(this);
        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mCurrentUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        mUsernameTextView.setText(mCurrentUserName);
        Typeface headingFont = Typeface.createFromAsset(getAssets(), "fonts/titan_one_regular.ttf");
        mUsernameTextView.setTypeface(headingFont);
        String initial =mCurrentUserName.substring(0, 1);
        String url = "https://dummyimage.com/80x80/0096a7/ffffff.png&text=" + initial;
        Picasso.with(this).load(url).fit().into(muUserIconImageView);
        mAddFriendButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);
        createUserLists();
    }

    private void createUserLists() {
        mFriendsQuery = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_FRIEND_REF).child(mCurrentUserId).child(Constants.STATUS_RESOLVED);
        mPendingFriendQuery = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_FRIEND_REF).child(mCurrentUserId).child(Constants.STATUS_PENDING);
        mInviteAdapter = new InviteListAdapter(User.class, R.layout.invitation_list_item, InvitationViewHolder.class, mPendingFriendQuery, mInvitationsLinearLayout, this);
        mInvitationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mInvitationRecyclerView.setAdapter(mInviteAdapter);
        mFriendsAdapter = new FriendListAdapter(User.class, R.layout.friend_list_item, FriendViewHolder.class, mFriendsQuery, mEmptyView, this);
        mFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFriendsRecyclerView.setAdapter(mFriendsAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mFriendsAdapter != null){
            mFriendsAdapter.cleanup();
            mInviteAdapter.cleanup();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.addFriendButton:
                Intent addFriendIntent = new Intent(SocialActivity.this, AddFriendActivity.class);
                startActivity(addFriendIntent);
                break;
            case R.id.goBackButton:
                Intent goBackIntent = new Intent(SocialActivity.this, MainActivity.class);
                startActivity(goBackIntent);
                break;
        }
    }
}
