package com.ewa.indecisiverps.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.models.Choice;
import com.ewa.indecisiverps.ui.DecisionsActivity;
import com.ewa.indecisiverps.ui.ResolveRoundActivity;
import com.ewa.indecisiverps.utils.ChoiceHistoryDialog;
import com.google.firebase.auth.FirebaseAuth;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChoiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    @Bind(R.id.decisionOptionsTextView) TextView mDecisionOptionsTextView;
    TextView mOpponentView;
    Choice mChoice;
    Context mContext;

    public ChoiceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        if(itemView.findViewById(R.id.opponentTextView) != null){
            mOpponentView = (TextView) itemView.findViewById(R.id.opponentTextView);
        }
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindChoice(Choice choice) {
        mChoice = choice;
        mDecisionOptionsTextView.setText(mChoice.getOption1() + " VS " + mChoice.getOption2());
        if(mOpponentView != null){
            String opponent = FirebaseAuth.getInstance().getCurrentUser().getDisplayName().equals(mChoice.getPlayer1()) ? mChoice.getPlayer2() : mChoice.getPlayer1();
            mOpponentView.setText(String.format(mContext.getString(R.string.opponent_label), opponent));
        }
        Typeface headingFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/titan_one_regular.ttf");
        mDecisionOptionsTextView.setTypeface(headingFont);
    }

    @Override
    public void onClick(View view) {
        if(mChoice.getStatus().equals(Constants.STATUS_RESOLVED)){
            showChoiceHistoryDialog();
        } else if(mChoice.getStatus().equals(Constants.STATUS_READY)){
            Intent resolveIntent = new Intent(mContext, ResolveRoundActivity.class);
            resolveIntent.putExtra("choice", Parcels.wrap(mChoice));
            mContext.startActivity(resolveIntent);
        }
    }

    private void showChoiceHistoryDialog(){
        DecisionsActivity decisionsActivity = (DecisionsActivity) mContext;
        FragmentManager fm = decisionsActivity.getSupportFragmentManager();
        ChoiceHistoryDialog choiceHistoryDialog = ChoiceHistoryDialog.newInstance(mChoice);
        choiceHistoryDialog.show(fm, "fragment_edit_name");
    }
}
