package com.ewa.indecisiverps.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ewa.indecisiverps.R;
import com.ewa.indecisiverps.models.Choice;

import org.parceler.Parcels;

public class ChooseMoveActivity extends AppCompatActivity {
    Choice mChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_move);

        Intent intent = getIntent();
        mChoice = Parcels.unwrap(intent.getParcelableExtra("choice"));
    }
}
