package com.example.muntis.moontgame;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;


public class GameActivity extends ActionBarActivity {
    MyNetworkService mService;
    boolean mBound = false;
    Handler messageHandler = new Handler();
    Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mService.parentActiv = this;
        myIntent = getIntent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //register to eventbus
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
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
        Button b = (Button) v;

        //String nickn = myIntent.getStringExtra("nick");
        //mService.sendMessage("MOVE"+ "|" + nickn + "|" + b.getText().toString());

        //@todo probably disable buttons till opponents turn has come
    }

    // this is called when recieves message from server
    public void onEvent(final ServerMessageGameEvent event) {
        Runnable doDisplayError = new Runnable() {
            public void run() {
                TextView serverResponse = (TextView) event.activ.findViewById(R.id.response);
                if (event.message.equals("CHANGE_NICK")) {
                    serverResponse.setText("Try a different nickname");
                } else if (event.message.equals("ACCEPTED_WAIT")) {
                    serverResponse.setText("Nick accepted! Waiting for a game.");
                } else {
                    Toast.makeText(getApplicationContext(), event.message, Toast.LENGTH_LONG).show();
                }
            }
        };
        messageHandler.post(doDisplayError);
    }

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
}
