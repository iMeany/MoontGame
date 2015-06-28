package com.example.muntis.moontgame;

import android.app.Activity;

/**
 * Created by Muntis on 2015.06.26..
 * ServerMessageEvent is class that carries messages recieved from server
 */
public class ServerMessageGameMoveEvent {
    public final String board;
    public final Activity activ;

    public ServerMessageGameMoveEvent(String message, Activity a) {
        this.board = message;
        this.activ = a;
    }
}
