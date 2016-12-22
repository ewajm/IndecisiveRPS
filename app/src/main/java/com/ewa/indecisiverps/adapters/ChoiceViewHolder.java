package com.ewa.indecisiverps.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.models.Choice;
import com.ewa.indecisiverps.ui.NewChoiceActivity;
import com.ewa.indecisiverps.ui.ResolveRoundActivity;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.R.id.message;
import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by ewa on 12/20/2016.
 */

public class ChoiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    @Bind(R.id.decisionOptionsTextView) TextView mDecisionOptionsTextView;
    Choice mChoice;
    Context mContext;

    public ChoiceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindChoice(Choice choice) {
        mChoice = choice;
        mDecisionOptionsTextView.setText(mChoice.getOption1() + " VS " + mChoice.getOption2());
        Typeface headingFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/titan_one_regular.ttf");
        mDecisionOptionsTextView.setTypeface(headingFont);
    }

    @Override
    public void onClick(View view) {
        Log.i("viewholder", "onClick: ");
        if(mChoice.getStatus().equals(Constants.STATUS_RESOLVED)){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            String message = "Play this decision again?";
            builder.setMessage(message);
            builder.setPositiveButton("Yeppers", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent playAgainIntent = new Intent(mContext, NewChoiceActivity.class);
                    playAgainIntent.putExtra("choice", Parcels.wrap(mChoice));
                    mContext.startActivity(playAgainIntent);
                }
            });
            builder.setNegativeButton("Nah", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.create().show();
        } else if(mChoice.getStatus().equals(Constants.STATUS_READY)){
            Intent resolveIntent = new Intent(mContext, ResolveRoundActivity.class);
            resolveIntent.putExtra("choice", Parcels.wrap(mChoice));
            mContext.startActivity(resolveIntent);
        }
    }
}