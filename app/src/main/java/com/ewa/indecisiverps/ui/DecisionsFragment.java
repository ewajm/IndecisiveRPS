package com.ewa.indecisiverps.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.adapters.ChoiceViewHolder;
import com.ewa.indecisiverps.adapters.FirebaseChoiceListAdapter;
import com.ewa.indecisiverps.models.Choice;
import com.ewa.indecisiverps.utils.DatabaseUtil;
import com.ewa.indecisiverps.utils.OnStartDragListener;
import com.ewa.indecisiverps.utils.SimpleItemTouchHelperCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class DecisionsFragment extends Fragment implements OnStartDragListener {
    @Bind(R.id.decisionsRecyclerView) RecyclerView mDecisionRecyclerView;
    @Bind(R.id.emptyView) TextView mEmptyView;
    private String mUserId;
    private FirebaseChoiceListAdapter mFirebaseAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private String mStatus;

    public DecisionsFragment() {
        // Required empty public constructor
    }
    public static DecisionsFragment newInstance(int sectionNumber) {
        DecisionsFragment fragment = new DecisionsFragment();
        Bundle args = new Bundle();
        args.putInt("status", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] statusArray = {Constants.STATUS_READY, Constants.STATUS_PENDING, Constants.STATUS_RESOLVED};
        mStatus = statusArray[getArguments().getInt("status", 0)];
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decisions, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpFirebaseAdapter();
    }

    private void setUpFirebaseAdapter() {
        Query choiceRef = DatabaseUtil.getDatabase().getInstance().getReference(Constants.FIREBASE_CHOICE_REF).child(mUserId).child(mStatus);
        mFirebaseAdapter = new FirebaseChoiceListAdapter(Choice.class, R.layout.decision_list_item_layout, ChoiceViewHolder.class, choiceRef, this, getActivity());
        choiceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChildren()){
                    if(mDecisionRecyclerView.getVisibility() == View.VISIBLE){
                        mDecisionRecyclerView.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                    }
                } else {
                    if(mDecisionRecyclerView.getVisibility() == View.GONE){
                        mDecisionRecyclerView.setVisibility(View.VISIBLE);
                        mEmptyView.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDecisionRecyclerView.setHasFixedSize(true);
        mDecisionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDecisionRecyclerView.setAdapter(mFirebaseAdapter);

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                mFirebaseAdapter.notifyDataSetChanged();
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mFirebaseAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mDecisionRecyclerView);
    }


    @Override
    public void onStop() {
        super.onStop();
        if(mFirebaseAdapter != null){
            mFirebaseAdapter.cleanup();
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }


}
