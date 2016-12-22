package com.ewa.indecisiverps.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.models.User;
import com.ewa.indecisiverps.ui.NewChoiceActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by ewa on 12/21/2016.
 */

public class FriendListAdapter  extends RecyclerView.Adapter<FriendViewHolder> implements View.OnClickListener{
    private ArrayList<User> mUsers = new ArrayList<>();
    private Context mContext;
    private FriendViewHolder mViewHolder;
    private String mUserId;

    public FriendListAdapter(ArrayList<User> users, Context context) {
        mUsers = users;
        mContext = context;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_item, parent, false);
        mViewHolder = new FriendViewHolder(view);
        mViewHolder.mDecideTextView.setOnClickListener(this);
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        holder.bindUser(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    @Override
    public void onClick(View view) {
        int clickPosition = mViewHolder.getAdapterPosition();
        User user = mUsers.get(clickPosition);
        if(view.getId() == R.id.decideTextView){
            Intent intent = new Intent(mContext, NewChoiceActivity.class);
            intent.putExtra("opponent", Parcels.wrap(user));
            mContext.startActivity(intent);
        }
//        else {
//            DatabaseReference currentUserFriendRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USER_REF).child(mUserId).child("friends").child(user.getUserId());
//            currentUserFriendRef.removeValue();
//        }
//        mUsers.remove(clickPosition);
//        notifyItemRemoved(clickPosition);
    }
}
