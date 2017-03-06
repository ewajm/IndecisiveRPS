package com.ewa.indecisiverps.utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.ewa.indecisiverps.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import static android.app.DialogFragment.STYLE_NO_TITLE;

/**
 * Created by Ewa on 3/6/2017.
 */

public class PrivacyPolicyDialog extends DialogFragment implements View.OnClickListener {
    private CheckBox mOptOutBox;

    public PrivacyPolicyDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static PrivacyPolicyDialog newInstance() {
        return new PrivacyPolicyDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.privacy_policy_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view

        Button closeButton = (Button) view.findViewById(R.id.closeDialogButton);
        mOptOutBox = (CheckBox) view.findViewById(R.id.analyticsCheckBox);
        mOptOutBox.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        getDialog().requestWindowFeature(STYLE_NO_TITLE);
    }

    @Override
    public void onClick(View view) {
        if (view == mOptOutBox) {
            FirebaseAnalytics.getInstance(getActivity()).setAnalyticsCollectionEnabled(!mOptOutBox.isChecked());
        } else {
            getDialog().cancel();
        }
    }
}
