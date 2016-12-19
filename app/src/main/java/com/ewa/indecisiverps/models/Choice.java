package com.ewa.indecisiverps.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ewa on 12/19/2016.
 */
@Parcel
public class Choice {
    List<String> players = new ArrayList<>();
    List<String> options = new ArrayList<>();
    List<Integer> playersToOptions = new ArrayList<>();
    List<String> playerMoves = new ArrayList<>();
    String pushId;
    int mode;

    public Choice() {
    }

    public Choice(List<String> options) {
        this.options = options;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public List<Integer> getPlayersToOptions() {
        return playersToOptions;
    }

    public void setPlayersToOptions(List<Integer> playersToOptions) {
        this.playersToOptions = playersToOptions;
    }

    public List<String> getPlayerMoves() {
        return playerMoves;
    }

    public void setPlayerMoves(List<String> playerMoves) {
        this.playerMoves = playerMoves;
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
}
