package com.example.muntis.moontgame;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import de.greenrobot.event.EventBus;

public class MyNetworkService extends Service {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();


    public static BufferedReader inStream;
    public static PrintWriter outStream;
    public static Socket myClientSocket;
    public static ServerSocket myServerSocket;

    public String serverAdr;
    public String nickname;
    public Integer serverPrt;
    public static Activity parentActiv;

    public static void setParentActiv(MainActivity parentActiv) {
        MyNetworkService.parentActiv = parentActiv;
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        MyNetworkService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MyNetworkService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myClientSocket = new Socket();
    }

    /**
     * methods for clients
     */
    //returns random number
    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }

    // establishes socket connection to ip:port
    public Boolean createConnection(String ip, int port, String n, MainActivity ma) {
        serverAdr = ip;
        serverPrt = port;
        nickname = n;
        parentActiv = ma;

        // start parallel network stream reading thread
        Runnable connect = new connectSocket();
        new Thread(connect).start();

        return true;
    }

    class connectSocket implements Runnable {

        @Override
        public void run() {

            try {
                myClientSocket = new Socket(serverAdr, serverPrt);
                inStream = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream()));
                outStream = new PrintWriter(myClientSocket.getOutputStream(), true);

                boolean nickRetry = false;
                String line = "";

                while(true) {
                    // process messages received from server
                    line = inStream.readLine();

                    // server asks for name
                    if (line.startsWith("SUBMITNAME")) {
                        // if nickname already taken
                        if (nickRetry) {
                            EventBus.getDefault().post(new ServerMessageEvent("CHANGE_NICK", parentActiv));
                        } else {
                            outStream.println(nickname);
                        }
                        nickRetry = true;

                    // name accepted, waiting for other player
                    } else if (line.startsWith("NAMEACCEPTED|"+nickname)) {
                        EventBus.getDefault().post(new ServerMessageEvent("ACCEPTED_WAIT", parentActiv));

                    // game on server started, go to game
                    } else if (line.matches("GAMESTARTED|.*?|"+nickname) || line.matches("GAMESTARTED|"+nickname+"|.*?") ) {

                        // get other nickname
                        String otherNick = "";
                        if (line.matches("GAMESTARTED|.*?|" + nickname)) {
                            // its in first part
                            otherNick = line.substring(12, line.length()-nickname.length()-1);
                            EventBus.getDefault().post((new ServerMessageEvent(otherNick, parentActiv)));
                        } else {
                            // its in second part
                            otherNick = line.substring(12+nickname.length()+1);
                            EventBus.getDefault().post((new ServerMessageEvent("OTHER: " + otherNick, parentActiv)));
                        }

                        // go to game activity
                        EventBus.getDefault().post((new ActivityChangeEvent(otherNick)));

                    // text message recieved from other player
                    } else if (line.startsWith("MESSAGE")) {
                        // @todo implement game chat here if needed

                    // random stuff from server for debug
                    } else {
                        EventBus.getDefault().post((new ServerMessageEvent(line, parentActiv)));
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    // sends message using set connection
    public Void sendMessage(String msg) {
        outStream.println(msg);
        return null;
    }
}
