package com.ewa.indecisiverps.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.models.Choice;
import com.ewa.indecisiverps.models.User;
import com.ewa.indecisiverps.ui.NewChoiceActivity;
import com.ewa.indecisiverps.utils.DatabaseUtil;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ewa on 12/21/2016.
 */

public class FriendListAdapter  extends FirebaseRecyclerAdapter<User, FriendViewHolder> {
    private final Query mRef;
    private final ChildEventListener mChildEventListener;
    private ArrayList<User> mUsers = new ArrayList<>();
    private Context mContext;
    private FriendViewHolder mViewHolder;
    private String mUserId;
    private TextView mEmptyView;

    public FriendListAdapter(Class<User> modelClass, int modelLayout, Class<FriendViewHolder> viewHolderClass, Query ref, TextView emptyView, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mRef = ref;
        mEmptyView = emptyView;
        mContext = context;
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mChildEventListener = mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mUsers.add(dataSnapshot.getValue(User.class));
                mEmptyView.setVisibility(View.GONE);
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
    public void populateViewHolder(final FriendViewHolder holder, User model, int position) {
        holder.bindUser(model);
        holder.mDecideNowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickPosition = holder.getAdapterPosition();
                User user = mUsers.get(clickPosition);
                Intent intent = new Intent(mContext, NewChoiceActivity.class);
                intent.putExtra("opponent", Parcels.wrap(user));
                mContext.startActivity(intent);
            }
        });
        holder.mRemoveFriendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickPosition = holder.getAdapterPosition();
                final User user = mUsers.get(clickPosition);
                new AlertDialog.Builder(mContext)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Remove Friend")
                        .setMessage("Are you sure you want to DESTROY YOUR RELATIONSHIP with " + user.getUsername() + "?!?!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //update children hashmap
                                removeDatabaseAssociations(user.getUserId());
                                mUsers.remove(user);
                                if(mUsers.size() == 0){
                                   mEmptyView.setVisibility(View.VISIBLE);
                                }
                                notifyDataSetChanged();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void removeDatabaseAssociations(final String userId) {
        final DatabaseReference pendingRef = DatabaseUtil.getDatabase().getInstance().getReference(Constants.FIREBASE_CHOICE_REF).child(mUserId).child(Constants.STATUS_PENDING);
        final DatabaseReference opponentPendingRef = DatabaseUtil.getDatabase().getInstance().getReference(Constants.FIREBASE_CHOICE_REF).child(userId).child(Constants.STATUS_PENDING);
        final DatabaseReference readyRef = DatabaseUtil.getDatabase().getInstance().getReference(Constants.FIREBASE_CHOICE_REF).child(mUserId).child(Constants.STATUS_READY);
        final DatabaseReference opponentReadyRef = DatabaseUtil.getDatabase().getInstance().getReference(Constants.FIREBASE_CHOICE_REF).child(userId).child(Constants.STATUS_READY);
        pendingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Choice choice = snapshot.getValue(Choice.class);
                    if(choice.getStartPlayerId().equals(userId) || choice.getOpponentPlayerId().equals(userId)){
                        pendingRef.child(choice.getPushId()).removeValue();
                        opponentReadyRef.child(choice.getPushId()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        readyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Choice choice = snapshot.getValue(Choice.class);
                    if(choice.getStartPlayerId().equals(userId) || choice.getOpponentPlayerId().equals(userId)){
                        readyRef.child(choice.getPushId()).removeValue();
                        opponentPendingRef.child(choice.getPushId()).removeValue();
                    }
                }
                HashMap<String, Object> deleteFriendMap = new HashMap<>();
                String currentUserPath = "/" + Constants.FIREBASE_USER_FRIEND_REF + "/" + mUserId + "/";
                String friendUserPath = "/" + Constants.FIREBASE_USER_FRIEND_REF + "/" + userId + "/";
                deleteFriendMap.put(friendUserPath+ Constants.STATUS_RESOLVED + "/" +mUserId, null);
                deleteFriendMap.put(currentUserPath + Constants.STATUS_RESOLVED + "/" + userId, null);
                DatabaseUtil.getDatabase().getInstance().getReference().updateChildren(deleteFriendMap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void cleanup() {
        super.cleanup();
        mRef.removeEventListener(mChildEventListener);
    }

}
