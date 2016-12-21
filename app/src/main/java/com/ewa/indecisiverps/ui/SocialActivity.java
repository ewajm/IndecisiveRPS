package com.ewa.indecisiverps.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.ewa.indecisiverps.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SocialActivity extends AppCompatActivity {
    @Bind(R.id.addFriendButton) Button mAddFriendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        ButterKnife.bind(this);

        mAddFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addFriendIntent = new Intent(SocialActivity.this, AddFriendActivity.class);
                startActivity(addFriendIntent);
            }
        });
    }

}
