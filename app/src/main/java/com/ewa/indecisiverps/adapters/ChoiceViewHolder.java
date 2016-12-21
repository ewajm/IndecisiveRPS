package com.ewa.indecisiverps.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.models.Choice;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ewa on 12/20/2016.
 */

public class ChoiceViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.decisionOptionsTextView) TextView mDecisionOptionsTextView;
    Choice mChoice;
    Context mContext;

    public ChoiceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
    }

    public void bindChoice(Choice choice) {
        mChoice = choice;
        mDecisionOptionsTextView.setText(mChoice.getOption1() + " VS " + mChoice.getOption2());
        Typeface headingFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/titan_one_regular.ttf");
        mDecisionOptionsTextView.setTypeface(headingFont);
    }
}
