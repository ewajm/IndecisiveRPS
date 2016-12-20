package com.ewa.indecisiverps.models;

import org.parceler.Parcel;

/**
 * Created by ewa on 12/20/2016.
 */
@Parcel
public class Round {
    String player1Move;
    String player2Move;
    String pushId;
    String decisionId;

    public Round() {
    }

    public String getPlayer1Move() {
        return player1Move;
    }

    public void setPlayer1Move(String player1Move) {
        this.player1Move = player1Move;
    }

    public String getPlayer2Move() {
        return player2Move;
    }

    public void setPlayer2Move(String player2Move) {
        this.player2Move = player2Move;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(String decisionId) {
        this.decisionId = decisionId;
    }
}
