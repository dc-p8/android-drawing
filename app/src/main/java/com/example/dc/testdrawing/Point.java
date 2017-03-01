package com.example.dc.testdrawing;

public class Point {
    public float image_x;
    public float image_y;
    public float canvas_x;
    public float canvas_y;
    public final long time;


    public Point(float ix, float iy, float cx, float cy, long time){
        this.image_x = ix;
        this.image_y = iy;
        this.canvas_x = cx;
        this.canvas_y = cy;
        this.time = time;
        //this.speed =
    }
}