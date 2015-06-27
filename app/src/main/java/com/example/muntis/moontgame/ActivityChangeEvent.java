package com.example.muntis.moontgame;

/**
 * Created by Muntis on 2015.06.27..
 * ServerMessageEvent is class that gets called when changing activities with EventBus events
 */
public class ActivityChangeEvent {
    public final String otherNickname;

    public ActivityChangeEvent(String on) {
        this.otherNickname = on;

    }
}
