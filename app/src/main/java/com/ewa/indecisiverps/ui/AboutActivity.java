package com.ewa.indecisiverps.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.utils.PrivacyPolicyDialog;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {
    @Bind(R.id.privacyPolicyButton) Button mPrivacyPolicyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        mPrivacyPolicyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrivacyPolicyDialog privacyPolicyDialog = PrivacyPolicyDialog.newInstance();
                privacyPolicyDialog.show(getSupportFragmentManager(), "privacy_policy_dialog");
            }
        });
    }

}
