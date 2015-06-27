package com.example.muntis.moontgame;

import android.app.Activity;

/**
 * Created by Muntis on 2015.06.26..
 * ServerMessageEvent is class that carries messages recieved from server
 */
public class ServerMessageEvent {
    public final String message;
    public final Activity activ;

    public ServerMessageEvent(String message, Activity ma) {
        this.message = message;
        this.activ = ma;
    }
}
