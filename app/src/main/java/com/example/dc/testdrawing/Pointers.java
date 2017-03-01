package com.example.dc.testdrawing;

import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;

/**
 * Created by dc on 20/12/16.
 */

public class Pointers extends SparseArray<Pointer>
{
    int mode = 0;
    int drawing = -1;
    long drawing_start;
    /*

     */
    float middle_x, middle_y;
    public MyView parent;
    public android.os.Handler hd = new android.os.Handler();
    public long last_removed;
    public Runnable timerEnd = new Runnable() {
        @Override
        public void run() {
            ((MainActivity)parent.getContext()).vibrate.run();
        }
    };
    public Pointers(MyView parent)
    {
        super();
        this.parent = parent;
    }

    public void add(float x, float y, int id)
    {
        int size = this.size();
        hd.removeCallbacksAndMessages(null);

        com.example.dc.testdrawing.Pointer p_add = new com.example.dc.testdrawing.Pointer(x, y, System.currentTimeMillis());
        if(size == 0)
        {
            hd.postDelayed(timerEnd, 500);
            p_add.setFirst();
            last_removed = 0;
        }
        else
        {
            if(drawing != -1)
            {
                parent.touch_up();
                /*if(System.currentTimeMillis() - drawing_start < 200)
                    parent.cancel();*/
                drawing = -1;
            }
        }
        super.put(id, p_add);

    }

    public void remove(int id)
    {
        if(drawing == id) {
            parent.touch_up();
            drawing = -1;
        }
        if(size() == 1) {
            drawing = -1;
        }
        super.remove(id);
        last_removed = System.currentTimeMillis();
        hd.removeCallbacksAndMessages(null);
    }


    public void checkMoves(MotionEvent e)
    {
        for (int size = this.size(), i = 0; i < size; i++) {
            int id = e.getPointerId(i);
            //((MainActivity)parent.getContext()).tv2.setText(String.valueOf(id));
            // Log.e("test", "id " + String.valueOf(id) + " limit " + this.size());
            com.example.dc.testdrawing.Pointer point = this.get(id);
            if (point != null) {
                point.checkMove(e.getX(i), e.getY(i));
                if(point.moved)
                    hd.removeCallbacksAndMessages(null);

            }
        }
    }
    public void proceed(MotionEvent e)
    {
        checkMoves(e);
        long now = System.currentTimeMillis();
        com.example.dc.testdrawing.Pointer f;
        int id;
        if(parent.onefinger)
        {
            id = 0;
            f = get(0);
            if(size() == 1 && f.moved)
            {
                if(parent.auto_drag)
                    parent.translate(f.dx, f.dy);

                else if(now - last_removed > 200)
                {
                    if(parent.pipette)
                        parent.pipette(f.x - middle_x, f.y - middle_y);
                    else
                    {
                        if(drawing != id) {
                            drawing_start = now;
                            drawing = id;
                            parent.touch_start(f.x - middle_x, f.y - middle_y, now);
                        }
                        else{
                            float vx = f.x - middle_x;
                            float vy = f.y - middle_y;
                            parent.touch_move(vx, vy, now);
                        }
                    }

                }
            }

        }
        else
        {
            id = e.getPointerId(0);
            f = get(id);
            Log.e("test", String.valueOf(id));
            if(this.size() == 1 && f.moved) {
                if(now - last_removed > 200) {
                    if(parent.pipette)
                        parent.pipette(f.x - middle_x, f.y - middle_y);
                    else
                    {
                        if(drawing != id) {
                            drawing_start = now;
                            drawing = id;
                            parent.touch_start(f.x - middle_x, f.y - middle_y, now);
                        }
                        else{
                            drawing = id;
                            float vx = f.x - middle_x;
                            float vy = f.y - middle_y;
                            parent.touch_move(vx, vy, now);
                        }
                    }

                }
            }
            else
            {
                com.example.dc.testdrawing.Pointer f0 = get(e.getPointerId(0));
                com.example.dc.testdrawing.Pointer f1 = get(e.getPointerId(1));
                if(f0 != null && f1 != null)
                {
                    float dir0 = (float)Math.atan2(f0.dx, f0.dy);
                    float dir1 = (float)Math.atan2(f1.dx, f1.dy);
                    if(Math.abs(dir1 - dir0) < 2.3)
                    {
                        parent.translate(((f0.dx + f1.dx) / 2), ((f0.dy + f1.dy) / 2));
                    }
                    else
                    {
                        float dx = (f1.x - f0.x);
                        float dy = (f1.y - f0.y);
                        double new_gap = Math.sqrt((dx * dx) + (dy * dy));
                        dx = (f1.last.x - f0.last.x);
                        dy = (f1.last.y - f0.last.y);
                        double last_gap = Math.sqrt((dx * dx) + (dy * dy));
                        parent.scale(new_gap/last_gap);
                    }
                }
            }
        }
    }


}


