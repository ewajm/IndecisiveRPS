package com.ewa.indecisiverps.adapters;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.models.User;
import com.ewa.indecisiverps.utils.DatabaseUtil;
import com.ewa.indecisiverps.utils.NotificationHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class InviteListAdapter extends FirebaseRecyclerAdapter<User, InvitationViewHolder>{
    private ArrayList<User> mUsers = new ArrayList<>();
    private ChildEventListener mChildEventListener;
    private Context mContext;
    private Query mRef;
    private String mUserId;
    private LinearLayout mInviteLayout;
    private User mUser;

    public InviteListAdapter(Class<User> modelClass, int modelLayout, Class<InvitationViewHolder> viewHolderClass, Query ref, LinearLayout inviteLayout, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mRef = ref;
        mInviteLayout = inviteLayout;
        mContext = context;
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseUtil.getDatabase().getInstance().getReference(Constants.FIREBASE_USER_REF).child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChildEventListener = mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mUsers.add(dataSnapshot.getValue(User.class));
                mInviteLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void populateViewHolder(final InvitationViewHolder holder, User model, int position) {
        holder.bindUser(model);
        holder.mAcceptRequestImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickPosition = holder.getAdapterPosition();
                User user = mUsers.get(clickPosition);

                HashMap<String, Object> updateFriendsList = new HashMap<>();
                String currentUserPath = "/" + Constants.FIREBASE_USER_FRIEND_REF + "/" + mUserId + "/";
                String friendUserPath = "/" + Constants.FIREBASE_USER_FRIEND_REF + "/" + user.getUserId() + "/";
                Map<String, Object> friendMap = new ObjectMapper().convertValue(user, Map.class);
                Map<String, Object> userMap = new ObjectMapper().convertValue(mUser, Map.class);
                updateFriendsList.put(currentUserPath+ Constants.STATUS_RESOLVED + "/" + user.getUserId(), friendMap);
                updateFriendsList.put(friendUserPath+ Constants.STATUS_RESOLVED + "/" +mUserId, userMap);
                updateFriendsList.put(currentUserPath+ Constants.STATUS_PENDING + "/" + user.getUserId(), null);
                DatabaseUtil.getDatabase().getInstance().getReference().updateChildren(updateFriendsList);
                NotificationHelper helper = new NotificationHelper(mContext);
                helper.sendFriendNotification("You can now decide things with " + mUser.getUsername(), user);
                helper.sendFriendNotification("You can now decide things with " + mUser.getUsername(), user);
                Toast.makeText(mContext, "Your are now friends with " + user.getUsername(), Toast.LENGTH_SHORT).show();
                mUsers.remove(user);
                if(mUsers.size() == 0){
                    mInviteLayout.setVisibility(View.GONE);
                }
            }
        });
        holder.mRefuseRequestImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickPosition = holder.getAdapterPosition();
                User user = mUsers.get(clickPosition);
                DatabaseUtil.getDatabase().getInstance().getReference(Constants.FIREBASE_USER_FRIEND_REF).child(mUserId).child(Constants.STATUS_PENDING).child(user.getUserId()).removeValue();
                mUsers.remove(user);
                if(mUsers.size() == 0){
                    mInviteLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void cleanup() {
        super.cleanup();
        mRef.removeEventListener(mChildEventListener);
    }
}
