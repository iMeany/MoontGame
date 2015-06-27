package com.example.muntis.moontgame;

import android.app.Activity;

/**
 * Created by Muntis on 2015.06.26..
 * ServerMessageEvent is class that carries messages recieved from server
 */
public class ServerMessageGameEvent {
    public final String message;
    public final Activity activ;

    public ServerMessageGameEvent(String message, Activity ma) {
        this.message = message;
        this.activ = ma;
    }
}
