package com.ewa.indecisiverps.ui;

import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.adapters.AddFriendViewHolder;
import com.ewa.indecisiverps.models.User;
import com.ewa.indecisiverps.utils.DatabaseUtil;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddFriendActivity extends AppCompatActivity {
    @Bind(R.id.addFriendRecyclerView) RecyclerView mAddFriendRecyclerView;
    @Bind(R.id.addFriendHeadingTextView) TextView mAddFriendHeadingTextView;
    @Bind(R.id.emptyView) TextView mEmptyView;
    FirebaseRecyclerAdapter mFirebaseAdapter;
    Query mFirebaseQuery;
    boolean mSearchDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);

        Typeface headingFont = Typeface.createFromAsset(getAssets(), "fonts/titan_one_regular.ttf");
        mAddFriendHeadingTextView.setTypeface(headingFont);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        ButterKnife.bind(this);
        mEmptyView.setVisibility(View.INVISIBLE);
        mAddFriendRecyclerView.setVisibility(View.VISIBLE);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if(mFirebaseAdapter != null){
                    mFirebaseAdapter.cleanup();
                }
                mAddFriendRecyclerView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.INVISIBLE);
                mSearchDone = true;
                searchUsers(query.trim());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if(query.length() > 1){
                    if(mFirebaseAdapter != null){
                        mFirebaseAdapter.cleanup();
                    }
                    searchUsers(query.trim());
                }
                return false;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }

    private void searchUsers(String query) {
        if(isEmail(query)){
            mFirebaseQuery = DatabaseUtil.getDatabase().getInstance().getReference(Constants.FIREBASE_USER_REF).orderByChild("email").equalTo(query);
        } else {
            mFirebaseQuery = DatabaseUtil.getDatabase().getInstance().getReference(Constants.FIREBASE_USER_REF).orderByChild("username").startAt(query).endAt(query + "~");
        }
        mFirebaseAdapter = new FirebaseRecyclerAdapter<User, AddFriendViewHolder>(User.class, R.layout.add_friend_list_item, AddFriendViewHolder.class, mFirebaseQuery) {
            @Override
            protected void populateViewHolder(AddFriendViewHolder viewHolder, User model, int position) {
                viewHolder.bindUser(model);
            }
        };
        if(mSearchDone){
            mFirebaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChildren()){
                        mEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        mEmptyView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        mAddFriendRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAddFriendRecyclerView.setAdapter(mFirebaseAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private boolean isEmail(String email) {
        boolean isEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isEmail) {
            return false;
        }
        return isEmail;
    }
}
