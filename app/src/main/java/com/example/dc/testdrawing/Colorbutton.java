package com.example.dc.testdrawing;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.RunnableFuture;

/**
 * Created by dc on 23/12/16.
 */

class Colors extends LinearLayout
{
    int selected;
    int touched;
    Context c;

    public Colors(Context c, AttributeSet atrs)
    {
        super(c, atrs);
        this.c = c;
    }
    public void addColor(int r, int g, int b)
    {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (30 * scale + 0.5f);
        Colorbutton add = new Colorbutton(r, g, b, 0, scale, this);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(pixels, pixels);
        int margin = (int)(5 * scale + 0.5f);
        if(((MainActivity)c).isLandscape())
        {
            param.topMargin = margin;
            param.bottomMargin = margin;
        }
        else
        {
            param.leftMargin = margin;
            param.rightMargin = margin;
        }

        add.setLayoutParams(param);
        selected++;


        addView(add, 0);
        resetids();
    }
    public void resetids()
    {
        for(int i = 0; i < getChildCount(); i++)
        {
            ((Colorbutton)getChildAt(i)).id = i;
        }
    }
    public void select(int id)
    {
        selected = id;
        Colorbutton v = (Colorbutton)getChildAt(id);
        ((MainActivity)c).setColor(v.r, v.g, v.b);
    }
    public void modifyColor(int r, int g, int b)
    {
        Colorbutton v = (Colorbutton)getChildAt(selected);
        v.r = r;
        v.g = g;
        v.b = b;
        v.setBackgroundColor(Color.rgb(v.r, v.g, v.b));
        select(selected);
    }
    public void removepipette()
    {
        ((MainActivity)c).pipette.setChecked(false);
    }
    public void remove(int id_removed)
    {
        if(getChildCount() > 1)
        {
            removeViewAt(id_removed);
            resetids();
            if(id_removed == selected)
            {
                if(selected > 0)
                {
                    select(id_removed - 1);
                }
                else
                    select(0);
            }

        }

    }
    public void modify(final int id_modified, final boolean add)
    {
        boolean hideremovebutton = add;
        if(getChildCount() == 1)
            hideremovebutton = true;
        final Colorbutton v = (Colorbutton)getChildAt(id_modified);
        ((MainActivity)c).vibrate.run();
        ((MainActivity)c).picker.show(v.r, v.g, v.b, hideremovebutton);
        ((MainActivity)c).picker.getWindow().setLayout((int)(getContext().getResources().getDisplayMetrics().widthPixels), (int)(getContext().getResources().getDisplayMetrics().heightPixels * 0.7));
        ((MainActivity)c).picker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                switch (((MainActivity)c).picker.exit)
                {
                    case 0: return;
                    case 1: remove(id_modified);break;
                    case 2 :
                    {
                        if(add)
                        {
                            addColor(((MainActivity)c).picker.red, ((MainActivity)c).picker.green, ((MainActivity)c).picker.blue);
                        }
                        else
                        {
                            v.r = ((MainActivity)c).picker.red;
                            v.g = ((MainActivity)c).picker.green;
                            v.b = ((MainActivity)c).picker.blue;
                            v.setBackgroundColor(Color.rgb(v.r, v.g, v.b));
                            if(id_modified == selected)
                            {
                                select(id_modified);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_MOVE)
        {
            ((Colorbutton)getChildAt(touched)).unselect();
        }
        return false;
    }
}

public class Colorbutton extends View
{
    Paint p;
    Colors allOthers;
    Bitmap image;
    public int id;
    int r, g, b;
    public android.os.Handler hd = new android.os.Handler();
    long startpress;
    boolean startselection = false;
    public Runnable timerEnd = new Runnable() {
        @Override
        public void run() {
            allOthers.modify(id, false);
        }
    };

    public Colorbutton(int r, int g, int b, int id, float scale, Colors allOthers)
    {
        super(allOthers.c);
        this.allOthers = allOthers;
        this.id = id;
        this.r = r;
        this.g = g;
        this.b = b;
        int pixels = (int) (30 * scale + 0.5f);
        setBackgroundColor(Color.rgb(r, g, b));
    }

    public void unselect()
    {
        startselection = false;
        hd.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                allOthers.removepipette();
                allOthers.touched = id;
                startselection = true;
                startpress = System.currentTimeMillis();
                hd.postDelayed(timerEnd, 500);


                break;
            }
            case MotionEvent.ACTION_UP:
            {
                Log.e("test", "up");
                if(startselection && System.currentTimeMillis() - startpress < 300)
                {
                    allOthers.select(id);
                }
                unselect();
                break;
            }
        }

        return true;
    }
}