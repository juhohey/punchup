package com.example.android.floatingactionbuttonbasic;


import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import com.example.android.common.logger.Log;

class Foo {
    float x, y, taille, easing = 0.05f, targetX, targetY, X, Y;
    float totalAnimDx, totalAnimDy;
    long startTime, endTime;
    Interpolator animateInterpolator;
    //color couleur;
    int colR, colG, colB, colA;
    Foo(float _x, float _y) {
        x = _x;
        y = _y;
        taille = 70;
        animateInterpolator = new OvershootInterpolator();
        //couleur = color(123, 123, 123, 50);
        colR = colG = colB = 123;
    }
    void update(float _x, float _y) {
        x += _x;
        y += _y;
    }
    void moveOn() {
        //    x += _x;
        //    y += _y;
        x = processing.core.PApplet.constrain(x, 0 + taille / 2, Sketch.size - taille / 2);
        y = processing.core.PApplet.constrain(y, 0 + taille / 2, Sketch.size - taille / 2);
        targetX = x;
        targetY = y;
        float dx = targetX - X;
        float dy = targetY - Y;
        if (Math.abs(dx) > 1) X += dx * easing;
        if (Math.abs(dy) > 1) Y += dy * easing;
    }
    void quickly(float dx, float dy, long duration) {
        startTime = System.currentTimeMillis();
        endTime = startTime + duration;
        totalAnimDx = dx;
        totalAnimDy = dy;
        long curTime = System.currentTimeMillis();
        float percentTime = (float) (curTime - startTime) / (float) (endTime - startTime);
        float percentDistance = animateInterpolator.getInterpolation(percentTime);
        float curDx = percentDistance * totalAnimDx;
        float curDy = percentDistance * totalAnimDy;
        update(curDx, curDy);
    }
    void changeColor() {
        //couleur = color(Math.random(255), Math.random(255), Math.random(255), 50);
        colR = (int) (Math.random()* 255);
        colG = (int) (Math.random()* 255);
        colB = (int) (Math.random()* 255);
        colA = 50;
    }
    void grow() {
        if (taille<300) {
            taille+=10;
            System.out.println(taille);
        }
        else {
            taille = 70;
        }
    }
    void display() {
        //fill(colR, colB, colG);
        //MainActivity.sketch.fill(colR, colB, colG);
        //Sketch.r = colR;
        //Sketch.g = colG;
        //Sketch.b = colB;
        //MainActivity.sketch. ellipse(X, Y, taille, taille);
        //Log.i(MainActivity.TAG, colR + ", " + colG + ", " + colB);
        //MainActivity.sketch.fill();
    }
}
