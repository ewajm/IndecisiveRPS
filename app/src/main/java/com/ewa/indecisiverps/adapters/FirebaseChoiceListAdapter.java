package com.ewa.indecisiverps.adapters;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewPropertyAnimator;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.models.Choice;
import com.ewa.indecisiverps.utils.OnStartDragListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by ewa on 12/20/2016.
 */

public class FirebaseChoiceListAdapter extends FirebaseRecyclerAdapter<Choice, ChoiceViewHolder> implements ItemTouchHelperAdapter{
    private Query mRef;
    private OnStartDragListener mOnStartDragListener;
    private Context mContext;
    private ChildEventListener mChildEventListener;
    private ArrayList<Choice> mChoices;
    private String mUserId;
    private int mOrientation;

    public FirebaseChoiceListAdapter(Class<Choice> modelClass, int modelLayout,
                                   Class<ChoiceViewHolder> viewHolderClass,
                                   Query ref, OnStartDragListener onStartDragListener, Context context) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mRef = ref;
        mOnStartDragListener = onStartDragListener;
        mContext = context;
        mChoices = new ArrayList<>();
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mChildEventListener = ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mChoices.add(dataSnapshot.getValue(Choice.class));
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
    protected void populateViewHolder(final ChoiceViewHolder viewHolder, Choice model, int position) {
        viewHolder.bindChoice(model);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        final Choice choice = mChoices.get(position);
        if(choice.getMode() == 2){
            String mOtherUserId = choice.getStartPlayerId().equals(mUserId) ? choice.getOpponentPlayerId() : choice.getStartPlayerId();
            FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHOICE_REF).child(mOtherUserId).child(choice.getPushId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_ROUND_REF).child(choice.getPushId()).removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_ROUND_REF).child(choice.getPushId()).removeValue();
        }
        mChoices.remove(position);
        notifyDataSetChanged();
        getRef(position).removeValue();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        mRef.removeEventListener(mChildEventListener);
    }

}
