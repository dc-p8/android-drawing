package com.example.dc.testdrawing;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Random;





public class MainActivity extends Activity {
    Colors colors;
    RelativeLayout rel;
    private View hscroll;
    Colorpicker picker;
    MyView mv;
    ToggleButton drag;
    ToggleButton fingers;
    ToggleButton pipette;
    SeekBar strokewidth;
    SeekBar zoom;
    public Runnable vibrate = null;
    Vibrator vb;
    View add;
    private SharedPreferences a_SP;

    public boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public void resetVisibility()
    {
        if(fingers.isChecked())//one finger
        {
            if(drag.isChecked()) //auto drag on
            {

                hscroll.setVisibility(View.GONE);
                strokewidth.setVisibility(View.GONE);
                fingers.setVisibility(View.GONE);
                pipette.setVisibility(View.GONE);
                drag.setVisibility(View.VISIBLE);
                zoom.setVisibility(View.VISIBLE);
            }
            else
            {
                hscroll.setVisibility(View.VISIBLE);
                strokewidth.setVisibility(View.VISIBLE);
                fingers.setVisibility(View.VISIBLE);
                pipette.setVisibility(View.VISIBLE);
                drag.setVisibility(View.VISIBLE);
                zoom.setVisibility(View.GONE);
            }
            if(pipette.isChecked()) //pipette selection on
            {
                drag.setVisibility(View.GONE);
                strokewidth.setVisibility(View.GONE);
                zoom.setVisibility(View.GONE);
            }
        }
        else
        {
            Log.e("test", "two");
            hscroll.setVisibility(View.VISIBLE);
            pipette.setVisibility(View.VISIBLE);
            fingers.setVisibility(View.VISIBLE);
            strokewidth.setVisibility(View.VISIBLE);
            drag.setVisibility(View.GONE);
            zoom.setVisibility(View.GONE);
            if(pipette.isChecked()) //pipette selection on
            {
                drag.setVisibility(View.GONE);
                strokewidth.setVisibility(View.GONE);
                zoom.setVisibility(View.GONE);
            }
        }
        Log.e("test", String.valueOf(drag.isChecked()));
    }

    boolean changezoom = true;

    public void startdraw()
    {
        fingers.setVisibility(View.GONE);
        drag.setVisibility(View.GONE);
        zoom.setVisibility(View.GONE);
        hscroll.setVisibility(View.GONE);
        strokewidth.setVisibility(View.GONE);
        pipette.setVisibility(View.GONE);
    }

    public void enddraw()
    {
        resetVisibility();
    }

    public void modifyColor(int r, int g, int b)
    {
        colors.modifyColor(r, g, b);
    }

    public void setColor(int r, int g, int b)
    {
        mv.setColor(r, g, b);
        hscroll.setBackgroundColor(Color.rgb(r, g, b));

    }

    public void Dragchange(boolean b)
    {
        if(b == true)
        {
            changezoom = false;
            double i = (mv.scaleFactor - mv.min_scale)/(mv.max_scale - mv.min_scale);
            i = Math.sqrt(i);
            zoom.setProgress((int)(i * 1000));
            mv.setAutoDrag();
        }
        else
            mv.setNoDrag();
        resetVisibility();
    }

    public void Fingerchange(boolean b)
    {
        if(b)//one fingers
        {
            mv.setOneFinger();
        }
        else
        {
            mv.setTwoFingers();
        }
        resetVisibility();
    }

    public void Pipettechange(Boolean b)
    {
        if(b)
        {
            mv.setPipette();
        }
        else
        {
            mv.setNoPipette();
        }
        resetVisibility();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.
    }

    @Override
    protected void onPause() {
        super.onPause();
        mv.manager.disconect();

        try
        {
            String pref = "";
            for( int s = colors.getChildCount() - 1; s >= 0; s--)
            {
                Colorbutton col = (Colorbutton) colors.getChildAt(s);
                pref += String.format("#%06X;", Color.rgb(col.r, col.g, col.b) & 0x00FFFFFF);
            }
            Log.e("PREF", pref);
            a_SP.edit().putString("colors", pref).putInt("colorselected", colors.selected).commit();
        }
        catch (Exception e){}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("test", "DESTROY");
        String pref = "";
        for( int s = colors.getChildCount() - 1; s >= 0; s--)
        {
            Colorbutton col = (Colorbutton) colors.getChildAt(s);
            pref += String.format("#%06X;", Color.rgb(col.r, col.g, col.b) & 0x00FFFFFF);
        }
        Log.e("PREF", pref);
        a_SP.edit().putString("colors", pref).putInt("colorselected", colors.selected).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mv.manager.connect();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrate = new Runnable() {
            @Override
            public void run() {
                vb.vibrate(30);
            }
        };
        mv = (MyView)findViewById(R.id.draw);
        rel = (RelativeLayout)findViewById(R.id.activity_main);
        if(isLandscape())
            hscroll = (ScrollView)findViewById(R.id.HSCROLL);
        else
            hscroll = (HorizontalScrollView)findViewById(R.id.HSCROLL);
        colors = (Colors)findViewById(R.id.COL);
        picker = new Colorpicker(this);
        drag = (ToggleButton)findViewById(R.id.Drag);
        fingers = (ToggleButton)findViewById(R.id.Fingers);
        pipette = (ToggleButton)findViewById(R.id.Pipette);
        zoom = (SeekBar)findViewById(R.id.zoom);
        strokewidth = (SeekBar)findViewById(R.id.strokewidth);
        add = findViewById(R.id.add);
        a_SP = getSharedPreferences("userinfo", MODE_PRIVATE);


        String[] colors_str = a_SP.getString("colors", "").split(";");
        Log.e("TEST", String.format("%d SIZE %s", colors_str.length, colors_str[0]));

        for (String col : colors_str)
        {
            Log.e("COLOR", "test " + col);
            int c = 0;
            try
            {
                c = Color.parseColor(col);
                colors.addColor(Color.red(c), Color.green(c), Color.blue(c));
            }
            catch (Exception e) {}
        }
        if(colors.getChildCount() < 1)
        {
            colors.addColor(0, 0, 0);
            colors.addColor(255, 255, 255);
        }

        int selected = a_SP.getInt("colorselected", 0);
        Log.e("SELECTED", String.format("test %d", selected));
        if(selected < colors.getChildCount())
        {
            colors.select(selected);
            Log.e("test", "now");
        }
        else
            colors.select(0);



        hscroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_MOVE)
                {
                    ((Colorbutton)(colors.getChildAt(colors.touched))).unselect();
                }
                return false;
            }
        });

        drag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Dragchange(b);
            }
        });

        fingers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Fingerchange(b);
            }
        });
        pipette.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Pipettechange(b);
            }
        });
        zoom.setMax(1000);
        zoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(!changezoom) {
                    changezoom = true;
                }
                else
                {
                    double newscale = ((double)i / 1000.0);
                    newscale = newscale * newscale;
                    mv.scaleFactor = mv.min_scale + (newscale  * (mv.max_scale - mv.min_scale));
                    //mv.scaleFactor = mv.min_scale + (((double) (Math.log((double)i) / Math.log(2.0)) / 9.960) * (mv.max_scale - mv.min_scale));
                    Log.e("scale", String.format("%d : newscale : %3.3f factor : %3.3f (min %f max %f)", i, newscale, mv.scaleFactor, mv.min_scale, mv.max_scale));
                    mv.invalidate();
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}@Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        strokewidth.setMax(1000);
        strokewidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mv.setStrokeWidth((float)i / 1000f);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        add.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    colors.modify(colors.selected, true);
                    pipette.setChecked(false);
                }
                return true;
            }

        });
        resetVisibility();
    }

}


