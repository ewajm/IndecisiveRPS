package com.ewa.indecisiverps.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ewa on 12/19/2016.
 */
@Parcel
public class Choice {
    String option1;
    String option2;
    String player1;
    String player2;
    String startPlayerId;
    String opponentPlayerId;
    String pushId;
    int mode;

    String win;
    String status;
    boolean impartialityMode=false;

    public Choice() {
    }

    public Choice(String option1, String option2) {
        this.option1 = option1;
        this.option2 = option2;
    }

    public String getStartPlayerId() {
        return startPlayerId;
    }

    public void setStartPlayerId(String startPlayerId) {
        this.startPlayerId = startPlayerId;
    }

    public String getOpponentPlayerId() {
        return opponentPlayerId;
    }

    public void setOpponentPlayerId(String opponentPlayerId) {
        this.opponentPlayerId = opponentPlayerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getWin() {
        return win;
    }

    public void setWin(String win) {
        this.win = win;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean isImpartialityMode() {
        return impartialityMode;
    }

    public void setImpartialityMode(boolean impartialityMode) {
        this.impartialityMode = impartialityMode;
    }
}
