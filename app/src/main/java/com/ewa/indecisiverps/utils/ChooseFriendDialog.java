package com.ewa.indecisiverps.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by ewa on 12/21/2016.
 */

public class ChooseFriendDialog extends DialogFragment {
    ArrayList<User> mFriends = new ArrayList<>();
    ArrayList<String> mFriendNames = new ArrayList<>();
    ListView mListView;
    private ArrayAdapter<String> mFriendsAdapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View v = inflater.inflate(R.layout.choose_friend_dialog_layout, null);
        builder.setView(v);
        createUserLists();
        mFriendsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mFriendNames);
        mListView = (ListView) v.findViewById(R.id.friendListView);
        TextView empty = (TextView) v.findViewById(android.R.id.empty);
        mListView.setEmptyView(empty);
        mListView.setAdapter(mFriendsAdapter);
            // Add action buttons
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ChooseFriendDialog.this.getDialog().cancel();
            }
        });
        return builder.create();
    }

    private void createUserLists() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query friendQuery = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_REF).child(userId).child("friends").orderByChild("status").equalTo(Constants.STATUS_RESOLVED);
        friendQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String thisUserId =  snapshot.getKey();
                        DatabaseReference thisUserRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_REF).child(thisUserId);
                        thisUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User thisUser = dataSnapshot.getValue(User.class);
                                mFriends.add(thisUser);
                                mFriendNames.add(thisUser.getUsername());
                                mFriendsAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}