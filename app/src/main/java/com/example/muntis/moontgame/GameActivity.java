package com.example.muntis.moontgame;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.IBinder;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;


public class GameActivity extends Activity {
    MyNetworkService mService;
    boolean myTurn = true;
    boolean firstTurn = true;
    boolean mBound = false;
    Handler messageHandler = new Handler();
    Intent myIntent;
    String myNickname;
    String otherNickname;
    Button[][] buttonBoard = new Button[8][8];

    // Defines callbacks for service binding, passed to bindService()
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MyNetworkService.LocalBinder binder = (MyNetworkService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mService.parentActiv = this;
        myIntent = getIntent();
        myNickname = myIntent.getStringExtra("nick");
        otherNickname = myIntent.getStringExtra("other_nick");
        TextView dd = (TextView) findViewById(R.id.debugTxt);
        dd.setText("");

        buttonBoard[0][0] = (Button) findViewById(R.id.a1);
        buttonBoard[0][1] = (Button) findViewById(R.id.a2);
        buttonBoard[0][2] = (Button) findViewById(R.id.a3);
        buttonBoard[0][3] = (Button) findViewById(R.id.a4);
        buttonBoard[0][4] = (Button) findViewById(R.id.a5);
        buttonBoard[0][5] = (Button) findViewById(R.id.a6);
        buttonBoard[0][6] = (Button) findViewById(R.id.a7);
        buttonBoard[0][7] = (Button) findViewById(R.id.a8);

        buttonBoard[1][0] = (Button) findViewById(R.id.b1);
        buttonBoard[1][1] = (Button) findViewById(R.id.b2);
        buttonBoard[1][2] = (Button) findViewById(R.id.b3);
        buttonBoard[1][3] = (Button) findViewById(R.id.b4);
        buttonBoard[1][4] = (Button) findViewById(R.id.b5);
        buttonBoard[1][5] = (Button) findViewById(R.id.b6);
        buttonBoard[1][6] = (Button) findViewById(R.id.b7);
        buttonBoard[1][7] = (Button) findViewById(R.id.b8);

        buttonBoard[2][0] = (Button) findViewById(R.id.c1);
        buttonBoard[2][1] = (Button) findViewById(R.id.c2);
        buttonBoard[2][2] = (Button) findViewById(R.id.c3);
        buttonBoard[2][3] = (Button) findViewById(R.id.c4);
        buttonBoard[2][4] = (Button) findViewById(R.id.c5);
        buttonBoard[2][5] = (Button) findViewById(R.id.c6);
        buttonBoard[2][6] = (Button) findViewById(R.id.c7);
        buttonBoard[2][7] = (Button) findViewById(R.id.c8);

        buttonBoard[3][0] = (Button) findViewById(R.id.d1);
        buttonBoard[3][1] = (Button) findViewById(R.id.d2);
        buttonBoard[3][2] = (Button) findViewById(R.id.d3);
        buttonBoard[3][3] = (Button) findViewById(R.id.d4);
        buttonBoard[3][4] = (Button) findViewById(R.id.d5);
        buttonBoard[3][5] = (Button) findViewById(R.id.d6);
        buttonBoard[3][6] = (Button) findViewById(R.id.d7);
        buttonBoard[3][7] = (Button) findViewById(R.id.d8);

        buttonBoard[4][0] = (Button) findViewById(R.id.e1);
        buttonBoard[4][1] = (Button) findViewById(R.id.e2);
        buttonBoard[4][2] = (Button) findViewById(R.id.e3);
        buttonBoard[4][3] = (Button) findViewById(R.id.e4);
        buttonBoard[4][4] = (Button) findViewById(R.id.e5);
        buttonBoard[4][5] = (Button) findViewById(R.id.e6);
        buttonBoard[4][6] = (Button) findViewById(R.id.e7);
        buttonBoard[4][7] = (Button) findViewById(R.id.e8);

        buttonBoard[5][0] = (Button) findViewById(R.id.f1);
        buttonBoard[5][1] = (Button) findViewById(R.id.f2);
        buttonBoard[5][2] = (Button) findViewById(R.id.f3);
        buttonBoard[5][3] = (Button) findViewById(R.id.f4);
        buttonBoard[5][4] = (Button) findViewById(R.id.f5);
        buttonBoard[5][5] = (Button) findViewById(R.id.f6);
        buttonBoard[5][6] = (Button) findViewById(R.id.f7);
        buttonBoard[5][7] = (Button) findViewById(R.id.f8);

        buttonBoard[6][0] = (Button) findViewById(R.id.g1);
        buttonBoard[6][1] = (Button) findViewById(R.id.g2);
        buttonBoard[6][2] = (Button) findViewById(R.id.g3);
        buttonBoard[6][3] = (Button) findViewById(R.id.g4);
        buttonBoard[6][4] = (Button) findViewById(R.id.g5);
        buttonBoard[6][5] = (Button) findViewById(R.id.g6);
        buttonBoard[6][6] = (Button) findViewById(R.id.g7);
        buttonBoard[6][7] = (Button) findViewById(R.id.g8);

        buttonBoard[7][0] = (Button) findViewById(R.id.h1);
        buttonBoard[7][1] = (Button) findViewById(R.id.h2);
        buttonBoard[7][2] = (Button) findViewById(R.id.h3);
        buttonBoard[7][3] = (Button) findViewById(R.id.h4);
        buttonBoard[7][4] = (Button) findViewById(R.id.h5);
        buttonBoard[7][5] = (Button) findViewById(R.id.h6);
        buttonBoard[7][6] = (Button) findViewById(R.id.h7);
        buttonBoard[7][7] = (Button) findViewById(R.id.h8);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //register to eventbus
        EventBus.getDefault().register(this);
        // Bind to LocalService
        Intent intent = new Intent(this, MyNetworkService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // this is called when gameButton is pressed
    public void onClick(View v) {

        //disable buttons till opponents turn has come

        final Button b = (Button) v;
        final String btnTxt = (String) b.getTag();
        if (myTurn) {
            mService.sendMessage("MOVE|" + myNickname + "|" + btnTxt);
            myTurn=false;
        }



    }

    // this is called when receives message from server
    public void onEvent(final ServerMessageReceivedBoard event) {

        Runnable doDisplayError = new Runnable() {
            public void run() {
                TextView dd = (TextView) findViewById(R.id.debugTxt);
                //dd.setText(event.board+event.myMove.toString());

                char[] b = event.board.toCharArray();
                char[] o = event.ownsFields.toCharArray();

                // setting board button values and colors
                for (int i=0; i<8; i++) {
                    for (int j=0;j<8; j++) {
                        //set field value
                        buttonBoard[i][j].setText(Character.toString(b[i * 8 + j]));

                        //set field color
                        if (o[i*8+j]=='1') { //for yourself @todo not 1 but player assigned nr
                            buttonBoard[i][j].getBackground().setColorFilter(Color.rgb(52, 152, 219), PorterDuff.Mode.MULTIPLY);
                        } else if (o[i*8+j]=='2') { // for enemy
                            buttonBoard[i][j].getBackground().setColorFilter(Color.rgb(46, 204, 113), PorterDuff.Mode.MULTIPLY);
                        }
                    }

                }
                // notify about first turn
                myTurn=event.myMove;
                TextView topTxt = (TextView) findViewById(R.id.topText);
                if (firstTurn && myTurn) {
                    Toast.makeText(getApplicationContext(), "You have first turn!", Toast.LENGTH_LONG).show();
                    topTxt.setText(Html.fromHtml("<font color=#3498DB>" + myNickname + "</font>  vs  <font color=#2ECC71>" + otherNickname + "</font>"));
                } else {
                    topTxt.setText(Html.fromHtml("<font color=#2ECC71>" + myNickname + "</font>  vs  <font color=#3498DB>" + otherNickname + "</font>"));
                }

                firstTurn=false;
            }

        };
        messageHandler.post(doDisplayError);
    }

    // this is called when receives message from server
    public void onEvent(final ServerMessageGameOver e) {

        Runnable doDisplayError = new Runnable() {
            public void run() {
                TextView dd = (TextView) findViewById(R.id.debugTxt);
                dd.setText("Game over! Final score: \n" + e.player1 + " : " + e.player1Score + "\n" + e.player2 + " : " + e.player2Score);
                dd.append("\nThe winner is " + e.winner + "!");
            }

        };
        messageHandler.post(doDisplayError);
    }
}
