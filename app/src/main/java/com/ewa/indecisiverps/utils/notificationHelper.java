package com.ewa.indecisiverps.utils;

import android.content.Context;
import android.util.Log;

import com.ewa.indecisiverps.Constants;
import com.ewa.indecisiverps.models.Choice;
import com.ewa.indecisiverps.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class NotificationHelper {
    Context mContext;
    String mUserId;

    public NotificationHelper(Context context){
        mContext = context;
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void sendNotificationToOpponent(String message, Choice choice) {
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_NOTIFICATIONS_REF);
        Map<String, String> notification = new HashMap<>();
        String opponentId = mUserId.equals(choice.getStartPlayerId()) ? choice.getOpponentPlayerId(): choice.getStartPlayerId();
        notification.put("user", opponentId);
        notification.put("message", message);

        notificationsRef.push().setValue(notification);
    }

    public void sendFriendNotification(String message, User user){
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_NOTIFICATIONS_REF);
        Map<String, String> notification = new HashMap<>();
        notification.put("user", user.getUserId());
        notification.put("message", message);

        notificationsRef.push().setValue(notification);
    }
}
