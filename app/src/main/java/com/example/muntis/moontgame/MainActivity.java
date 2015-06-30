package com.example.muntis.moontgame;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import com.example.muntis.moontgame.MyNetworkService;

public class MainActivity extends ActionBarActivity {
    MyNetworkService mService;
    boolean mBound = false;
    Handler messageHandler = new Handler();
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mService.parentActiv = this;

        TextView response = (TextView) findViewById(R.id.response);
        response.setText("");

        EditText startNick = (EditText) findViewById(R.id.nick);
        startNick.setText(android.os.Build.MODEL);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //register to eventbus
        EventBus.getDefault().register(this);
        // Bind to LocalService
        Intent intent = new Intent(this, MyNetworkService.class);
        //startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);



    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    // Defines callbacks for service binding, passed to bindService()
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    // when connect is pressed
    public void onConnectClick(View v) {
        if (mBound) {
            // try to establish connection with server
            EditText ip = (EditText) findViewById(R.id.address);
            EditText port = (EditText) findViewById(R.id.port);
            EditText nick = (EditText) findViewById(R.id.nick);

            mService.createConnection(ip.getText().toString(), Integer.parseInt(port.getText().toString()), nick.getText().toString(), this);

        }
    }


    // this is called when recieves message from server
    public void onEvent(final ServerMessageEvent event) {
        Runnable displayResponse = new Runnable() {
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
        messageHandler.post(displayResponse);
    }

    public void onEvent(final ActivityChangeEvent activEvent) {

        Intent intent = new Intent(this, GameActivity.class);
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        EditText nick = (EditText) findViewById(R.id.nick);
        intent.putExtra("nick", nick.getText().toString());
        intent.putExtra("other_nick", activEvent.otherNickname);

        Runnable doDisplayError = new Runnable() {
            public void run() {
                TextView response = (TextView) findViewById(R.id.response);
                response.setText("");
            }

        };
        messageHandler.post(doDisplayError);
        startActivity(intent);

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


