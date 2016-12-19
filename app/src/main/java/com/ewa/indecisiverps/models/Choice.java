package com.ewa.indecisiverps.models;

/**
 * Created by ewa on 12/19/2016.
 */

public class Choice {
    String playerId;
    String choiceOne;
    String choiceTwo;
    int choicePlayerOne;
    int choicePlayerTwo;
    String throwPlayerOne;
    String throwPlayerTwo;
    String pushId;
    String mode;

    public Choice() {
    }

    public Choice(String choiceOne, String choiceTwo, String mode) {
        this.choiceOne = choiceOne;
        this.choiceTwo = choiceTwo;
        this.mode = mode;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getChoiceOne() {
        return choiceOne;
    }

    public void setChoiceOne(String choiceOne) {
        this.choiceOne = choiceOne;
    }

    public String getChoiceTwo() {
        return choiceTwo;
    }

    public void setChoiceTwo(String choiceTwo) {
        this.choiceTwo = choiceTwo;
    }

    public int getChoicePlayerOne() {
        return choicePlayerOne;
    }

    public void setChoicePlayerOne(int choicePlayerOne) {
        this.choicePlayerOne = choicePlayerOne;
    }

    public int getChoicePlayerTwo() {
        return choicePlayerTwo;
    }

    public void setChoicePlayerTwo(int choicePlayerTwo) {
        this.choicePlayerTwo = choicePlayerTwo;
    }

    public String getThrowPlayerOne() {
        return throwPlayerOne;
    }

    public void setThrowPlayerOne(String throwPlayerOne) {
        this.throwPlayerOne = throwPlayerOne;
    }

    public String getThrowPlayerTwo() {
        return throwPlayerTwo;
    }

    public void setThrowPlayerTwo(String throwPlayerTwo) {
        this.throwPlayerTwo = throwPlayerTwo;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
