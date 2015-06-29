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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

public class MyNetworkService extends Service {

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    public static String sendMsg;

    public static BufferedReader inStream;
    public static PrintWriter outStream;
    public static Socket myClientSocket;
    public static ServerSocket myServerSocket;

    Runnable connectWrite=null;
    public String serverAdr;
    public static String nickname;
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

                // just to not re-initialize them
                boolean nickRetry = false;
                String line = "";
                String board = "";
                String owns = "";
                String f = "";

                boolean gameRunning = true;

                while(gameRunning) {
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

                    // when move from other player is received
                    } else if (line.startsWith("BOARD|")) {
                        Pattern p = Pattern.compile("BOARD|\\|(.*?)\\|(.*?)\\|(.*?)$");
                        Matcher mm = p.matcher(line);

                        while(mm.find()) {
                            board = mm.group(1);
                            owns = mm.group(2);
                            f =  mm.group(3);
                        }
                        boolean myMove=false;
                        if (f.equals(nickname)) {
                            myMove = true;
                        }
                        EventBus.getDefault().post((new ServerMessageReceivedBoard(board, owns, parentActiv, myMove)));

                    // game on server started, go to game
                    } else if (line.startsWith("GAMESTARTED|")) {

                        //get other nickname
                        Pattern p = Pattern.compile("GAMESTARTED\\|(.*?)\\|(.*?)$");
                        Matcher mm = p.matcher(line);
                        String otherNick = "other_nick";
                        while(mm.find()) {
                            if (mm.group(1).equals(nickname)) {
                                otherNick=mm.group(2);
                            } else {
                                otherNick=mm.group(1);
                            }
                        }
                        //toast about other players nickname
                        // EventBus.getDefault().post((new ServerMessageEvent("OTHER: " + otherNick, parentActiv)));

                        // go to game activity
                        EventBus.getDefault().post((new ActivityChangeEvent(otherNick)));


                    // text message received from other player
                    } else if (line.startsWith("GAMEOVER|")) {
                        //get points and victor
                        Pattern p = Pattern.compile("GAMEOVER\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)\\|(.*?)$");
                        Matcher mm = p.matcher(line);
                        while(mm.find()) {
                            EventBus.getDefault().post((new ServerMessageGameOver(mm.group(1), //p1 nick
                                                                                mm.group(2),  // p1 score
                                                                                mm.group(3),  // p2 nick
                                                                                mm.group(4),  // p2 score
                                                                                mm.group(5)   // winner nick
                                                                        )));
                        }
                        gameRunning = false;

                    } else if (line.startsWith("MESSAGE")) {
                        // @todo implement game chat here if needed

                    // random stuff from server for debug
                    } else {
                        //EventBus.getDefault().post((new ServerMessageEvent(line, parentActiv)));
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    // sends message using set connection
    public Void sendMessage(String msg) {

        // start parallel network stream writing thread
        Runnable connectWrite = new connectSocketWrite();
        sendMsg = msg; // @todo ??????
        outStream.println(sendMsg);
        //new Thread(connectWrite).start();
        return null;
    }

    class connectSocketWrite implements Runnable {
        @Override
        public void run() {
            try {
                outStream.println(sendMsg);
            }catch (UnknownError e) {
                e.printStackTrace();
            }
        }
    }

}
