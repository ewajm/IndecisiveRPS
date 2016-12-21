package com.ewa.indecisiverps.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.models.User;
import com.ewa.indecisiverps.ui.ResolveRoundActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

//TODO: add check for whether person is friend already
//TODO: transform image and remove click listener when invitation sent
//TODO: rescind invitation?

public class AddFriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    @Bind(R.id.addFriendImageView) ImageView mAddFriendImageView;
    @Bind(R.id.friendIconImageView) ImageView mFriendIconImageView;
    @Bind(R.id.friendEmailTextView) TextView mFriendEmailTextView;
    @Bind(R.id.friendNameTextView) TextView mFriendNameTextView;
    User mUser;
    Context mContext;
    String mCurrentUserId;
    FirebaseAuth mAuth;

    public AddFriendViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mAddFriendImageView.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
    }

    public void bindUser(User user) {
        mUser = user;
        mFriendEmailTextView.setText(mUser.getEmail());
        mFriendNameTextView.setText(mUser.getUsername());
        Typeface headingFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/titan_one_regular.ttf");
        mFriendNameTextView.setTypeface(headingFont);
        String initial = mUser.getUsername().substring(0, 1);

    }

    @Override
    public void onClick(View view) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_REF).child(mUser.getUserId()).child("friends").child(mCurrentUserId);
        userRef.setValue(Constants.STATUS_PENDING);
        Toast.makeText(mContext, "Invitation sent!", Toast.LENGTH_SHORT).show();
    }
}