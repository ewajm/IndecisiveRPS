package com.ewa.indecisiverps.adapters;

        import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.models.User;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InvitationViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.acceptRequestImageView) ImageView mAcceptRequestImageView;
    @Bind(R.id.refuseRequestImageView) ImageView mRefuseRequestImageView;
    @Bind(R.id.friendIconImageView) ImageView mFriendIconImageView;
    @Bind(R.id.fromEmailTextView) TextView mFromEmailTextView;
    @Bind(R.id.fromNameTextView) TextView mFromNameTextView;
    User mUser;
    Context mContext;

    public InvitationViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
    }

    public void bindUser(User user) {
        mUser = user;
        mFromEmailTextView.setText(mUser.getEmail());
        mFromNameTextView.setText(mUser.getUsername());
        Typeface headingFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/titan_one_regular.ttf");
        mFromNameTextView.setTypeface(headingFont);
        String initial = mUser.getUsername().substring(0, 1);
        String url = "https://dummyimage.com/80x80/0096a7/ffffff.png&text=" + initial;
        Picasso.with(mContext).load(url).fit().into(mFriendIconImageView);
    }
}