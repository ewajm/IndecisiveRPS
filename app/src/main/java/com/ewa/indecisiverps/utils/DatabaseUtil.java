package com.ewa.indecisiverps.utils;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ewa on 1/28/2017.
 */

public class DatabaseUtil {

    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }

        return mDatabase;
    }
}
