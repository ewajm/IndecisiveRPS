package com.ewa.indecisiverps.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by ewa on 12/21/2016.
 */

public class InviteListAdapter extends RecyclerView.Adapter<InvitationViewHolder>{
    private ArrayList<User> mUsers = new ArrayList<>();
    private Context mContext;
    private String mUserId;

    public InviteListAdapter(ArrayList<User> users, Context context) {
        mUsers = users;
        mContext = context;
    }

    @Override
    public InvitationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invitation_list_item, parent, false);
        final InvitationViewHolder viewHolder = new InvitationViewHolder(view);
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        viewHolder.mAcceptRequestImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickPosition = viewHolder.getAdapterPosition();
                User user = mUsers.get(clickPosition);
                DatabaseReference currentUserFriendRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_REF).child(mUserId).child("friends").child(user.getUserId()).child("status");
                DatabaseReference newFriendUserRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_REF).child(user.getUserId()).child("friends").child(mUserId).child("status");
                currentUserFriendRef.setValue(Constants.STATUS_RESOLVED);
                newFriendUserRef.setValue(Constants.STATUS_RESOLVED);
                Toast.makeText(mContext, "Your are now friends with " + user.getUsername(), Toast.LENGTH_SHORT).show();
                mUsers.remove(user);
                notifyDataSetChanged();
            }
        });
        viewHolder.mRefuseRequestImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickPosition = viewHolder.getAdapterPosition();
                User user = mUsers.get(clickPosition);
                DatabaseReference currentUserFriendRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_REF).child(mUserId).child("friends").child(user.getUserId());
                currentUserFriendRef.removeValue();
                mUsers.remove(user);
                notifyDataSetChanged();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(InvitationViewHolder holder, int position) {
        holder.bindUser(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
