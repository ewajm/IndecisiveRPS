package com.ewa.indecisiverps.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.models.Choice;
import com.ewa.indecisiverps.models.Round;
import com.ewa.indecisiverps.ui.NewChoiceActivity;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Gets rounds for choice and displays in dialog
 */

public class ChoiceHistoryDialog extends DialogFragment implements View.OnClickListener {
    private Button mDecideAgainButton;
    private Choice mChoice;

    public ChoiceHistoryDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ChoiceHistoryDialog newInstance(Choice choice) {
        ChoiceHistoryDialog frag = new ChoiceHistoryDialog();
        Bundle args = new Bundle();
        args.putParcelable("choice", Parcels.wrap(choice));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choice_history_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mChoice = Parcels.unwrap(getArguments().getParcelable("choice"));
        ListView roundHistoryListView = (ListView) view.findViewById(R.id.roundHistoryListView);
        TextView optionsTextView = (TextView) view.findViewById(R.id.optionsTextView);
        TextView playersTextView = (TextView) view.findViewById(R.id.playersTextView);
        TextView winnerTextView = (TextView) view.findViewById(R.id.winnerTextView);
        Button closeButton = (Button) view.findViewById(R.id.closeDialogButton);
        mDecideAgainButton = (Button) view.findViewById(R.id.decideAgainButton);
        mDecideAgainButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        optionsTextView.setText(mChoice.getOption1() + " vs " + mChoice.getOption2());
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.colorPrimaryTranslucent);
        if (mChoice.getPlayer1().equals("opponent") || mChoice.getPlayer2().equals("opponent")) {
            playersTextView.setText(R.string.solo_mode);
        } else {
            playersTextView.setText("Players: " + mChoice.getPlayer1() + ", " + mChoice.getPlayer2());
        }
        winnerTextView.setText(mChoice.getWin());
        DatabaseReference roundRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_ROUND_REF).child(mChoice.getPushId());
        FirebaseListAdapter<Round> firebaseAdapter = new FirebaseListAdapter<Round>(getActivity(), Round.class, android.R.layout.two_line_list_item, roundRef) {
            @Override
            protected void populateView(View v, Round model, int position) {
                if(model.getTimestamp() == 0){
                    ((TextView) v.findViewById(android.R.id.text1)).setText(R.string.date_unknown);
                } else {
                    String dateString = new SimpleDateFormat("MM/dd/yyyy h:mm a", Locale.getDefault()).format(new Date(model.getTimestamp()));
                    ((TextView) v.findViewById(android.R.id.text1)).setText(dateString);
                }
                ((TextView) v.findViewById(android.R.id.text2)).setText(mChoice.getPlayer1() + ": " + model.getPlayer1Move() + " vs " + mChoice.getPlayer2() + ": " + model.getPlayer2Move());
            }
        };
        roundHistoryListView.setAdapter(firebaseAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view == mDecideAgainButton) {
            Intent playAgainIntent = new Intent(getActivity(), NewChoiceActivity.class);
            playAgainIntent.putExtra("choice", Parcels.wrap(mChoice));
            getActivity().startActivity(playAgainIntent);
        } else {
            getDialog().cancel();
        }
    }
}