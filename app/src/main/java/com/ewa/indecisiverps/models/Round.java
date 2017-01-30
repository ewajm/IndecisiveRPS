package com.ewa.indecisiverps.models;

import com.ewa.indecisiverps.Constants;

import org.parceler.Parcel;

import java.util.Arrays;
import java.util.List;

@Parcel
public class Round {
    String player1Move;
    String player2Move;
    String pushId;
    String decisionId;
    long timestamp;

    public Round() {
    }

    public Round(long timestamp){
        this.timestamp = timestamp;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(String decisionId) {
        this.decisionId = decisionId;
    }

    public int checkWin(){
        int win=0;
        List<String> baseArray = Arrays.asList(Constants.RPS_ROCK, Constants.RPS_PAPER, Constants.RPS_SCISSORS);
        List<String> beatsArray = Arrays.asList(Constants.RPS_SCISSORS, Constants.RPS_ROCK, Constants.RPS_PAPER);
        if(player1Move.equals(player2Move)){
            win = -1;
        } else if(baseArray.indexOf(player1Move) == beatsArray.indexOf(player2Move)){
            win = 0;
        } else {
            win = 1;
        }
        return win;
    }
}
