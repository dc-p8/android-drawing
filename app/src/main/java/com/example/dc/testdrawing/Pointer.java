package com.example.dc.testdrawing;

import android.graphics.PointF;

/**
 * Created by dc on 20/12/16.
 */

public class Pointer extends PointF
{
    public boolean moved;
    private boolean isFirst = false;
    public PointF last;
    public float dx, dy;
    public long time;
    public boolean drawing = false;
    boolean firstmove;
    public int id;

    public void setFirst()
    {
        this.isFirst = true;
    }
    public long getStartTime()
    {
        return time;
    }

    public Pointer(float x, float y, long now)
    {
        super(x, y);
        time = now;
        moved = false;
        last = new PointF();
        last.x = x;
        last.y = y;
    }
    public void checkMove(float n_x, float n_y)
    {
        last.x = this.x;
        last.y = this.y;
        x = n_x;
        y = n_y;
        firstmove = false;
        if(last.x != x || last.y != y)
        {
            if(moved == false)
            {
                firstmove = true;
                // first move, start line
            }
            moved = true;
            dx = last.x - x;
            dy = last.y - y;
        }
        else
        {
            this.moved = false;
            this.dx = 0;
            this.dy = 0;
        }
    }
}