package com.example.android.floatingactionbuttonbasic;

import android.view.GestureDetector;
import android.view.MotionEvent;

import android.view.MotionEvent.*;

/**
 * Created by Administratör on 2015-11-07.
 */
public class LearnGestureListener extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onSingleTapUp(MotionEvent ev) {
        System.out.println("onSingleTapUp");
        Sketch.birth(ev.getX(), ev.getY());
        return true;
    }
    @Override
    public boolean onDoubleTap(MotionEvent ev) {
        //System.out.println("onDoubleTap");
        for (int i=0; i < MainActivity.foo.size(); i++) {
            Foo f = (Foo) MainActivity.foo.get(i);
            f.changeColor();
        }
        return true;
    }
    @Override
    public void onShowPress(MotionEvent ev) {
        //System.out.println("onShowPress");
    }
    @Override
    public void onLongPress(MotionEvent ev) {
        for (int i=0; i< MainActivity.foo.size(); i++) {
            Foo f = (Foo) MainActivity.foo.get(i);
            f.grow();
        }
        //System.out.println("onLongPress");
    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //System.out.println("onScroll");
        for (int i=0; i< MainActivity.foo.size(); i++) {
            Foo f = (Foo) MainActivity.foo.get(i);
            f.update(distanceX, distanceY);
        }
        return true;
    }
    @Override
    public boolean onDown(MotionEvent ev) {
        //System.out.println("onDown");
        return true;
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //System.out.println("onFling");
        final float distanceTimeFactor = 0.4f;
        final float totalDx = (distanceTimeFactor * velocityX / 2);
        final float totalDy = (distanceTimeFactor * velocityY / 2);
        for (int i=0; i< MainActivity.foo.size(); i++) {
            Foo f = (Foo) MainActivity.foo.get(i);
            f.quickly(totalDx, totalDy, (long) (1000 * distanceTimeFactor));
        }
        return true;
    }
}
