package com.example.dc.testdrawing;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by dc on 23/12/16.
 */

class Colorpicker extends Dialog
{
    int red, green, blue;
    public Dialog d;
    public Context c;
    SeekBar redSeek, greenSeek, blueSeek;
    TextView redText, greenText, blueText;
    Button ok;
    Button back;
    Button remove;

    int exit = 0;

    View base, newcolor;
    public Colorpicker(Context a) {
        super(a);
        this.c = a;
    }

    private void updatecolor(int r, int g, int b)
    {
        if(0 <= r && r <=255)
            this.red = r;
        else
            this.red = 0;

        if(0 <= r && r <=255)
            this.green = g;
        else
            this.green = 0;

        if(0 <= r && r <=255)
            this.blue = b;
        else
            this.blue = 0;

        redText.setText(String.valueOf(this.red));
        greenText.setText(String.valueOf(this.green));
        blueText.setText(String.valueOf(this.blue));

        redSeek.setProgress(red);
        greenSeek.setProgress(green);
        blueSeek.setProgress(blue);

        newcolor.setBackgroundColor(Color.rgb(red, green, blue));
    }

    public void show(int r, int g, int b, boolean add)
    {
        super.show();
        updatecolor(r, g, b);
        base.setBackgroundColor(Color.rgb(red, green, blue));
        if(add)
            remove.setVisibility(View.GONE);
        else
            remove.setVisibility(View.VISIBLE);
        exit = 0;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.colorpicker_layout);

        ok = (Button)findViewById(R.id.ok);
        remove = (Button)findViewById(R.id.remove);
        back = (Button)findViewById(R.id.back);

        base = (View)findViewById(R.id.colorbase);
        newcolor = (View) findViewById(R.id.newcolor);

        redSeek = (SeekBar)findViewById(R.id.redseek);
        greenSeek = (SeekBar)findViewById(R.id.greenseek);
        blueSeek = (SeekBar)findViewById(R.id.blueseek);

        redSeek.setMax(255);
        greenSeek.setMax(255);
        blueSeek.setMax(255);

        redText = (TextView) findViewById(R.id.redtext);
        greenText = (TextView)findViewById(R.id.greentext);
        blueText = (TextView) findViewById(R.id.bluetext);

        redText.setTextColor(Color.RED);
        greenText.setTextColor(Color.GREEN);
        blueText.setTextColor(Color.BLUE);

        redSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updatecolor(i, green, blue);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        greenSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updatecolor(red, i, blue);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        blueSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updatecolor(red, green, i);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ok.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit = 2;
                dismiss();
            }
        });
        remove.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit = 1;
                dismiss();
            }
        });
        back.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit = 0;
                dismiss();
            }
        });

    }
}