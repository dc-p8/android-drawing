package com.example.dc.testdrawing;

import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by dc on 20/12/16.
 */

public class Manager {
    static Socket mSocket;
    private MyView myView;
    ByteArrayOutputStream stream;
    File img;
    //FileOutputStream stream;

    public void connect()
    {
        Log.e("test", "CONNECT M");
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on("endchunk", onEndchunk);
        mSocket.on("chunk", onChunk);
        mSocket.on("lines", onLines);
        mSocket.connect();
        requestBitmap();
    }
    public void disconect()
    {
        Log.e("test", "DISCONECT M");
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off("endchunk", onEndchunk);
        mSocket.off("chunk", onChunk);
        mSocket.off("lines", onLines);
        mSocket.disconnect();
    }

    private void requestBitmap()
    {
        Log.e("test", "requestbitmap");
        mSocket.emit("requestbitmap");
        //stream = new ByteArrayOutputStream();
        img = new File(myView.getContext().getCacheDir(), "img.png");
        if(img.exists())
            img.delete();
        try
        {
            stream = new ByteArrayOutputStream();
            //stream = new FileOutputStream(img);
            Log.e("test", "STREAM DATA : " + stream.toString());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("test", "connected");
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };
    private Emitter.Listener onChunk = new Emitter.Listener()
    {
        @Override
        public void call(Object... args){

            try
            {
                stream.write((byte[])args[0]);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e.getMessage());
            }
        }
    };
    private Emitter.Listener onEndchunk = new Emitter.Listener()
    {

        @Override
        public void call(Object... args){

            Log.e("test", "end");

            try
            {
                stream.flush();
                stream.close();


            }
            catch (Exception e)
            {
                throw new RuntimeException(e.getMessage());
            }

            byte[] data = stream.toByteArray();
            //Log.e("test", "DATA STREAM SIZE : " + data.length);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            myView.receive_image(BitmapFactory.decodeByteArray(data, 0, data.length, options));//(BitmapFactory.decodeFile(myView.getContext().getCacheDir() + "/img.png"));
            //myView.receive_image(BitmapFactory.decodeFile(myView.getContext().getCacheDir() + "/img.png"));

        }
    };

    private Emitter.Listener onLines = new Emitter.Listener()
    {

        @Override
        public void call(Object... args){

            JSONArray data = (JSONArray) args[0];
            Log.e("lines", String.valueOf(data.length()));
            int myJsonArraySize = data.length();
            int col;
            float stroke_witdh;
            float start_pos_x;
            float start_pos_y;
            float end_pos_x;
            float end_pos_y;
            for (int i = 0; i < myJsonArraySize; i++) {
                try
                {
                    JSONObject o = (JSONObject) data.get(i);
                    col = o.getInt("colint");
                    stroke_witdh = (float)o.getDouble("stroke");
                    start_pos_x = (float)o.getDouble("startx");
                    start_pos_y = (float)o.getDouble("starty");
                    end_pos_x = (float)o.getDouble("endx");
                    end_pos_y = (float)o.getDouble("endy");
                    myView.receive_move_line(col, stroke_witdh, start_pos_x, start_pos_y, end_pos_x, end_pos_y);
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e.getMessage());
                }
                // Do whatever you have to do to myJsonObject...
            }
        }
    };
    public void send(int col, float stroke_witdh, float start_pos_x, float start_pos_y, float end_pos_x, float end_pos_y)
    {
        if(!mSocket.connected())
            return;
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put("colstr", String.format("#%6x", col & 0x00FFFFFF));
            jsonObject.put("colint", col);
            jsonObject.put("stroke", stroke_witdh);
            jsonObject.put("startx", start_pos_x);
            jsonObject.put("starty", start_pos_y);
            jsonObject.put("endx", end_pos_x);
            jsonObject.put("endy", end_pos_y);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
        mSocket.emit("line", jsonObject);//, stroke_witdh, start_pos_x, start_pos_y, end_pos_x, end_pos_y);
    }
    public Manager(MyView mv)
    {
        myView = mv;
        mSocket = Connexion.getSocket();
        Log.e("test", "NEW MANAGER");
        //connect();
    }
}
