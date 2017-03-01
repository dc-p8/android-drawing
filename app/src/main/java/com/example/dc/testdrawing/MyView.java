package com.example.dc.testdrawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;


public class MyView extends View {
    Manager manager;
    Context c;
    boolean auto_drag = false;
    boolean onefinger = false;
    boolean pipette = false;
    float c_x, c_y;
    float zoom_x, zoom_y;
    private Pointers pointers;
    public Bitmap image;
    public double scaleFactor = 0;
    public Paint p;
    public Paint other_p;
    public Paint overlay_p;
    Canvas pathCanvas;
    double min_scale;
    double max_scale;
    float mX, mY;
    float lastmidx, lastmidy;
    float velocity, width, lastSpeed, lastVelocity, lastWidth;
    Point last;
    float initialVelocity = 1;
    float initialStrokeWidth = 15;
    float maxStrokeWidth = 100;
    float minStrokeWidth = 1;
    float VELOCITY_FILTER_WEIGHT = 0.5f;
    int n = 0;

    public MyView(Context context, AttributeSet atrs) {
        super(context, atrs);
        this.c = context;
        init(context);
    }
    public MyView(Context context) {
        super(context);
        this.c = context;
        init(context);
    }
    public void setStrokeWidth(float normal_stroke)
    {
        initialStrokeWidth = (minStrokeWidth + (normal_stroke * (maxStrokeWidth - minStrokeWidth)));
        Log.e("test", String.valueOf(initialStrokeWidth) + " " + String.valueOf(normal_stroke));
        invalidate();
    }
    public void setColor(int r, int g, int b)
    {
        int color = Color.rgb(r, g, b);
        p.setColor(color);
    }
    public void setAutoDrag()
    {
        auto_drag = true;
    }
    public void setNoDrag()
    {
        auto_drag = false;
    }
    public void setTwoFingers(){onefinger = false;}
    public void setOneFinger() {onefinger = true;}
    public void setPipette(){pipette = true;}
    public void setNoPipette(){pipette = false;}

    void receive_image(Bitmap i)
    {
        image = i;//.copy(Bitmap.Config.ARGB_8888, true);
        scaleFactor = (float)this.getWidth() / (float)(image.getWidth());
        min_scale = scaleFactor * 0.1;
        max_scale = scaleFactor * 100.0;
        c_x = (float)(image.getWidth()) / 2f;
        c_y = (float)(image.getHeight()) / 2f;
        pathCanvas = new Canvas(image);
            ((MainActivity)c).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            });
        /*
        catch (Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }*/

        /*
        try {
            invalidate();
        }
        catch (Exception e)
        {
            throw  new RuntimeException(e.getMessage());
        }
        */
    }


    void receive_move_line(int col, float stroke_witdh, float start_pos_x, float start_pos_y, float end_pos_x, float end_pos_y)
    {
        other_p.setColor(col);
        other_p.setStrokeWidth(stroke_witdh);
        pathCanvas.drawLine(start_pos_x, start_pos_y, end_pos_x, end_pos_y, other_p);
        ((MainActivity)c).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }


    void init(Context c)
    {
        Log.e("test", "INIT");
        manager = new Manager(this);

        //Bitmap im = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        //image = im.copy(Bitmap.Config.ARGB_8888, true);
        image = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
        pathCanvas = new Canvas(image);

        Log.e("test", "image created");

        pointers = new Pointers(this);

        p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeJoin(Paint.Join.ROUND);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(5);
        p.setColor(Color.GREEN);

        other_p = new Paint();
        other_p.setAntiAlias(true);
        other_p.setDither(true);
        other_p.setStyle(Paint.Style.STROKE);
        other_p.setStrokeJoin(Paint.Join.ROUND);
        other_p.setStrokeCap(Paint.Cap.ROUND);

        overlay_p = new Paint();
        overlay_p.setAntiAlias(true);
        overlay_p.setDither(true);
        overlay_p.setStyle(Paint.Style.STROKE);
        overlay_p.setStrokeJoin(Paint.Join.ROUND);
        overlay_p.setStrokeCap(Paint.Cap.ROUND);
        overlay_p.setStrokeWidth(1);
        p.setColor(Color.GREEN);


    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //((MainActivity) c).tv2.setText(String.valueOf(System.currentTimeMillis()));
        //tv2.setText(String.valueOf(scaleFactor));

        canvas.save();

        canvas.scale((float) scaleFactor, (float) scaleFactor, zoom_x, zoom_y);
        canvas.translate(-(c_x - (this.getWidth()) / 2), -(c_y - (this.getHeight()) / 2));
        canvas.drawBitmap(image, 0, 0, null);
        //canvas.translate(-1, -1);

        //canvas.drawPath(mPath,  p);
        canvas.restore();
        pathCanvas.drawColor(0x00000000);
        canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, initialStrokeWidth * (float)scaleFactor / 2, overlay_p);
        //canvas.drawLine(0, 0, 200, 200, p);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //scaleFactor = 1f / ((double) image.getWidth() / (double) w);
        scaleFactor = (float)w / (float)(image.getWidth());
        min_scale = scaleFactor * 0.1;
        max_scale = scaleFactor * 100.0;
        zoom_x = (float) w / 2f;
        zoom_y = (float) h / 2f;
        c_x = (float)(image.getWidth()) / 2f;
        c_y = (float)(image.getHeight()) / 2f;
        pointers.middle_x = zoom_x;
        pointers.middle_y = zoom_y;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
                int pointerIndex = event.getActionIndex();
                // get pointer ID
                int pointerId = event.getPointerId(pointerIndex);
                // get masked (not specific to a pointer) action
                int maskedAction = event.getActionMasked();
                // ((MainActivity)getContext()).tv1.setText(String.format("%3.3f %3.3f", event.getX(), event.getY()));
                switch (maskedAction) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN: {
                        Log.e("test", String.format("start %d", pointerId));
                        // We have a new pointer. Lets add it to the list of pointers
                        pointers.add(event.getX(pointerIndex), event.getY(pointerIndex), pointerId);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: { // a pointer was moved
                        Log.e("test", String.format("move %d", pointerId));
                        try {
                            pointers.proceed(event);
                        } catch (Exception e) {
                            Log.e("test", e.getMessage() + " " + e.getStackTrace());
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        Log.e("test", String.format("remove %d", pointerId));
                        pointers.remove(pointerId);
                        break;
                    }
                }

        invalidate();
        return true;
    }

    public void translate(float x, float y) {
        c_x += (x * (1 / scaleFactor));
        c_y += (y * (1 / scaleFactor));
        if(c_x < 0)
            c_x = 0;
        else if(c_x > image.getWidth())
            c_x = image.getWidth();
        if(c_y < 0)
            c_y = 0;
        else if(c_y > image.getHeight())
            c_y = image.getHeight();
    }

    public void scale(double newscale) {
        scaleFactor *= newscale;
        if(scaleFactor < min_scale)
            scaleFactor = min_scale;
        else if(scaleFactor > max_scale)
            scaleFactor = max_scale;
    }

    public void pipette(float digit_x, float digit_y)
    {
        float px = c_x + (digit_x * (1f / (float) scaleFactor));
        float py = c_y + (digit_y * (1f / (float) scaleFactor));
        int pix = image.getPixel((int)px, (int)py);
        ((MainActivity)c).modifyColor(Color.red(pix), Color.green(pix), Color.blue(pix));
    }

    public void touch_start(float digit_x, float digit_y, long startTime) {
        ((MainActivity)c).startdraw();
        float px = c_x + (digit_x * (1f / (float) scaleFactor)); // transposer les coordonénes du canvas en coordonnées de l'image
        float py = c_y + (digit_y * (1f / (float) scaleFactor));
        //pathCanvas.drawCircle(px, py, initialStrokeWidth, p);
        last = new Point(px, py, digit_x, digit_y, startTime);
        lastSpeed = 1;
        lastVelocity = 1;
        lastVelocity = initialVelocity;
        n = 0;
        mX = px;
        mY = py;
        lastmidx = px;
        lastmidy = py;
        Log.e("test", String.format(""));
    }

    public void touch_move(float digit_x, float digit_y, long moveTime) {
        //((MainActivity)getContext()).tv1.setText(String.valueOf(pressure));

        float px = c_x + (digit_x * (1f / (float) scaleFactor));
        float py = c_y + (digit_y * (1f / (float) scaleFactor));

        float dx = (digit_x - last.canvas_x);
        float dy = (digit_y - last.canvas_y);

        Log.e("test", String.format("dx dy %3.3f %3.3f", dx, dy));
        if(dx == 0 && dy == 0)
            return;

        velocity = (float)Math.sqrt((dx * dx) + (dy * dy)) / (float)(moveTime- last.time);
        //Log.e("test", String.format("delta : %4d, distance : %3.3f", moveTime - last.time, (float)Math.sqrt((dx * dx) + (dy * dy))));
        //velocity *= lastVelocity;
        velocity = (VELOCITY_FILTER_WEIGHT * velocity) + ((1 - VELOCITY_FILTER_WEIGHT) * lastVelocity);
        width = Math.min(initialStrokeWidth, Math.max(initialStrokeWidth / (velocity +1), 2));
        lastVelocity = velocity;
        last = new Point(px, py, digit_x, digit_y, moveTime);

        float midx = (px + mX) / 2;
        float midy = (py + mY) / 2;
        boolean drawlast = true;
        PointF mid = new PointF(mX, mY);
        if(n == 0)
        {
            mid.x = midx;
            mid.y = midy;
            ++n;
            lastWidth = width;
            //drawlast = false;

        }
        bez(lastWidth, width, new PointF(lastmidx, lastmidy), mid, new PointF(midx, midy), true);

        lastWidth = width;

        lastmidx = midx;
        lastmidy = midy;

        mX = px;
        mY = py;
    }

    public void touch_up() {
        ((MainActivity)c).enddraw();
        pathCanvas.drawLine(lastmidx, lastmidy, mX, mY, p);
        manager.send(p.getColor(), p.getStrokeWidth(), lastmidx, lastmidy, mX, mY);
    }

    public void bez(float startWidth, float endWidth, PointF p1, PointF p2, PointF p3, boolean drawlast) {
        float widthDelta = endWidth - startWidth;

        float distancetotal = 0;
        float dx = (p2.x - p1.x);
        float dy = (p2.y - p1.y);
        distancetotal += Math.sqrt((dx * dx) + (dy * dy));
        dy = (p3.y - p2.y);
        distancetotal += Math.sqrt((dx * dx) + (dy * dy));
        int steps = (int)Math.ceil(distancetotal / 10.0f);
        float iterWidth = (endWidth - startWidth) / steps;
        float lastx = p1.x, lasty = p1.y;
        Log.e("test", String.format("p1 %3.3f %3.3f \np2 %3.3f %3.3f \np3 %3.3f %3.3f",p1.x, p1.y, p2.x, p2.y, p3.x, p3.y));
        float x = 0, y = 0;
        for (int i = 0; i < steps; i++) {
            float t = ((float) i) / steps;
            float tt = t * t;
            float ttt = tt * t;
            float u = 1 - t;
            float uu = u * u;
            float uuu = uu * u;

            x = uuu * p1.x;
            x += 3 * u * t * p2.x;
            //x += 2 * u * tt * p3.x;
            x += ttt * p3.x;

            y = uuu * p1.y;
            y += 3 * u * t * p2.y;
            //y += 2 * u * tt * p3.y;
            y += ttt * p3.y;
            float newstroke =startWidth + (iterWidth * (float)i);//startWidth + ttt * widthDelta;// startWidth + (iterWidth * (float)i);
            p.setStrokeWidth(newstroke);
            //p.setStrokeWidth(startWidth + ttt * widthDelta);
            //log += String.format("getstrokewidth : %3.3f | newstroke : %3.3f\n", p.getStrokeWidth(), newstroke);
            //pathCanvas.drawPoint(x, y, p);
            pathCanvas.drawLine(lastx, lasty, x, y, p);
            manager.send(p.getColor(), p.getStrokeWidth(), lastx, lasty, x, y);
            lastx = x;
            lasty = y;
        }
        pathCanvas.drawLine(x, y, p3.x, p3.y, p);
        manager.send(p.getColor(), p.getStrokeWidth(), x, y, p3.x, p3.y);
        Log.e("test", String.format("x, y %3.3f %3.3f",x, y));
    }
}