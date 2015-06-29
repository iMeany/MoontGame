package com.example.muntis.moontgame;

import android.app.Activity;

/**
 * Created by Muntis on 2015.06.26..
 * ServerMessageEvent is class that carries messages recieved from server
 */
public class ServerMessageGameMoveEvent {
    public final String board;
    public final String ownsFields;
    public final Boolean myMove;
    public final Activity activ;

    public ServerMessageGameMoveEvent(String b, String o, Activity a, Boolean f) {
        this.board = b;
        this.ownsFields = o;
        this.activ = a;
        this.myMove = f;
    }
}
