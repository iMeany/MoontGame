package com.example.muntis.moontgame;

import android.app.Activity;

/**
 * Created by Muntis on 2015.06.26..
 * ServerMessageEvent is class that carries messages recieved from server
 */
public class ServerMessageGameOver {
    public final String player1;
    public final String player2;
    public final String winner;
    public final int player1Score;
    public final int player2Score;

    public ServerMessageGameOver(String p, String ps, String pp, String pps, String w) {
        this.player1 = p;
        this.player1Score = Integer.parseInt(ps);
        this.player2 = pp;
        this.player2Score = Integer.parseInt(pps);
        this.winner = w;
    }

}
