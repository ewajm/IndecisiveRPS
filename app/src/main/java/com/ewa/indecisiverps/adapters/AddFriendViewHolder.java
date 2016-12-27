package com.ewa.indecisiverps.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

//TODO: add check for whether person is friend already
//TODO: transform image and remove click listener when invitation sent
//TODO: rescind invitation?

public class AddFriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private final View mItemView;
    @Bind(R.id.addFriendImageView) ImageView mAddFriendImageView;
    @Bind(R.id.friendIconImageView) ImageView mFriendIconImageView;
    @Bind(R.id.friendEmailTextView) TextView mFriendEmailTextView;
    @Bind(R.id.friendNameTextView) TextView mFriendNameTextView;
    User mUser;
    Context mContext;
    String mCurrentUserId;
    FirebaseAuth mAuth;
    boolean mIsFriend = false;

    public AddFriendViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mItemView = itemView;
        mContext = itemView.getContext();
        mAddFriendImageView.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
    }

    public void bindUser(User user) {
        mUser = user;
        if(user.getUserId().equals(mCurrentUserId)){
            mAddFriendImageView.setVisibility(View.GONE);
            mFriendNameTextView.setText("You");
        } else {
            mAddFriendImageView.setVisibility(View.VISIBLE);
            mFriendNameTextView.setText(mUser.getUsername());
        }
        DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_REF).child(mCurrentUserId).child("friends").child(mUser.getUserId());
        friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    mAddFriendImageView.setImageResource(R.drawable.ic_action_accept);
                    mIsFriend = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mFriendEmailTextView.setText(mUser.getEmail());
        Typeface headingFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/titan_one_regular.ttf");
        mFriendNameTextView.setTypeface(headingFont);
        String initial = mUser.getUsername().substring(0, 1);
        String url = "https://dummyimage.com/80x80/0096a7/ffffff.png&text=" + initial;
        Picasso.with(mContext).load(url).fit().into(mFriendIconImageView);
    }

    @Override
    public void onClick(View view) {
        if(mIsFriend){
            Toast.makeText(mContext, "You are already friends with this person!", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_REF).child(mUser.getUserId()).child("friends").child(mCurrentUserId).child("status");
            userRef.setValue(Constants.STATUS_PENDING);
            Toast.makeText(mContext, "Invitation sent!", Toast.LENGTH_SHORT).show();
            mAddFriendImageView.setImageResource(R.drawable.ic_action_accept);
            mIsFriend = true;
        }
    }
}