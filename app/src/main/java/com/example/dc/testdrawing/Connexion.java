package com.example.dc.testdrawing;

/**
 * Created by dc on 05/12/16.
 */


import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class Connexion {
    private final String URL = "http://cardinaux.hd.free.fr";
    public static Connexion INSTANCE = new Connexion();
    private static Socket a_Socket;

    private Connexion(){
        try{a_Socket = IO.socket(URL);}
        catch (URISyntaxException e) {throw new RuntimeException();}
        a_Socket.connect();
    };
    public static Socket getSocket()
    {
        return a_Socket;
    }
}
