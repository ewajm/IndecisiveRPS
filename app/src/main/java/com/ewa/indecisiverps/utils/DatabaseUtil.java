package com.ewa.indecisiverps.utils;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Database wrapper to prevent errors from setPersistenceEnabled being called when a database already exists
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
