package com.ewa.indecisiverps.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.adapters.FirebaseChoiceListAdapter;
import com.ewa.indecisiverps.models.Choice;
import com.ewa.indecisiverps.utils.OnStartDragListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class DecisionsFragment extends Fragment implements OnStartDragListener {
    @Bind(R.id.decisionsRecyclerView) RecyclerView mDecisionRecyclerView;
    //@Bind(R.id.emptyView) TextView mEmptyView;
    private ArrayList<Choice> mChoiceArray = new ArrayList<>();
    private String mUserId;
    private FirebaseChoiceListAdapter mFirebaseAdapter;
    private Query mChoiceRef;
    private ItemTouchHelper mItemTouchHelper;

    public DecisionsFragment() {
        // Required empty public constructor
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
//        mTodoRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_TODOS_REFERENCE).child(mUserId).orderByChild("repoId").equalTo(mRepo.getPushId());
//        mFirebaseAdapter = new FirebaseTodoListAdapter(Todo.class, R.layout.saved_todo_list_item, SavedTodoViewHolder.class, mTodoRef, this, getActivity(), mRepo);
//        mTodoListView.setHasFixedSize(true);
//        mTodoListView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mTodoListView.setAdapter(mFirebaseAdapter);
//
//        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//                mFirebaseAdapter.notifyDataSetChanged();
//            }
//        });
//
//        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mFirebaseAdapter);
//        mItemTouchHelper = new ItemTouchHelper(callback);
//        mItemTouchHelper.attachToRecyclerView(mTodoListView);
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
