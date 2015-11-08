package com.example.android.floatingactionbuttonbasic;
import processing.core.*;

import com.example.android.common.logger.Log;
import com.example.android.floatingactionbuttonbasic.*;


import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.os.Bundle;
import android.view.MotionEvent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Administratör on 2015-11-07.
 */
public class Sketch extends PApplet implements RhythmSequencer.SeqListener {

    public static final int size = 1500;
    public static int r = 0, g = 0, b = 0, tickEventIndex = 0;
    int fps = 50, msPerFrame = 1000 / fps, barGap = 20, presentLineOffset = 120;
    static long startMs, msPassed, lastTouchEventMs = 0, tickEventMs = 0;
    static RhythmSequencer.SeqEvent lastEvent;
    static ArrayList<RhythmSequencer.SeqEvent> allItems;

    PImage punch, swing;

    GestureDetector mGestureDetector;
    Context caller;

    String eventStr = "";

    public Sketch(Context caller) {
        this.caller = caller;
    }

    @Override
    public void settings() {
        size(1000, 1500, P2D);
    }

    @Override
    public void setup() {
        //Initialize one time stuffs here
        frameRate(fps);
        RhythmSequencer.getInstance().listener = this;
        RhythmSequencer.getInstance().createAudioPlayer(caller);
        RhythmSequencer.getInstance().start();
        //allItems = RhythmSequencer.getInstance().nextItems(Integer.MAX_VALUE);

        allItems = new ArrayList<RhythmSequencer.SeqEvent>(Arrays.asList(RhythmSequencer.getInstance().nextItems(Integer.MAX_VALUE)));
        startMs = System.currentTimeMillis();

        try {
            Bitmap bmp;
            bmp = BitmapFactory.decodeStream(getResources().openRawResource(R.raw.punch));

            punch = new PImage(bmp.getWidth(), bmp.getHeight(), PConstants.RGB);
            for (int i = 0; i < bmp.getWidth(); i++) {
                for (int j = 0; j < bmp.getHeight(); j++) {
                    //punch.pixels[i] = bmp.getPixel(i, j);
                    punch.set(i, j, bmp.getPixel(i, j));
                }
            }
            punch.updatePixels();


            bmp = BitmapFactory.decodeStream(getResources().openRawResource(R.raw.swing));

            swing = new PImage(bmp.getWidth(), bmp.getHeight(), PConstants.RGB);
            for (int i = 0; i < bmp.getWidth(); i++) {
                for (int j = 0; j < bmp.getHeight(); j++) {
                    //punch.pixels[i] = bmp.getPixel(i, j);
                    swing.set(i, j, bmp.getPixel(i, j));
                }
            }
            swing.updatePixels();
        }
        catch(Exception e) {
            System.err.println("Can't create image from buffer");
            e.printStackTrace();
        }

    }

    @Override
    public void draw() {
        background(255);
        msPassed  = System.currentTimeMillis() - startMs;
        rect(0, height - presentLineOffset, width, 2);

        for (int i = 0; i < allItems.size(); i++) {
            RhythmSequencer.SeqEvent current = allItems.get(i);
            int tickMsPosition = RhythmSequencer.tickLengthMs * i;
            long tickOffset = tickMsPosition - msPassed;

            PImage toDraw = punch;
            long yPos = height - presentLineOffset - tickOffset * 4 / 7 - toDraw.height / 2;
            switch (current) {
                case STARTED:
                    r = 255;
                    g = 255;
                    b = 255;
                    break;
                case PUNCH:
                    r = 255;
                    g = 0;
                    b = 0;
                    toDraw = punch;
                    break;
                case SWING:
                    r = 0;
                    g = 0;
                    b = 255;
                    toDraw = swing;
                    break;
                case STOPPED:
                    r = 0;
                    g = 0;
                    b = 0;
                    break;
                case HIT:
                    r = g = b = 255;
                    textSize(72);
                    fill(255, 0, 0);
                    text("YEAH", width / 2 + 150, yPos + toDraw.height / 2 - 200);
                    break;
                case MISS:
                    r = g = b = 0;
                    textSize(72);
                    fill(255, 0, 0);
                    text("X", width / 2 + 150, yPos + toDraw.height / 2 - 200);
                    break;

                case FAULT:
                    Log.i(MainActivity.TAG, "FAULT");
                    textSize(52);
                    fill(255, 0, 0);
                    text("FAULT", width / 2, yPos + toDraw.height / 2);
                    continue;

                case NONE:
                    continue;
            }
            fill(r, g, b);

            blendMode(MULTIPLY);
            image(toDraw, width / 2 - 100, yPos);

            if (yPos > height - presentLineOffset + toDraw.height / 2 && current != RhythmSequencer.SeqEvent.HIT) {
                //Log.i(MainActivity.TAG, "miss");
                allItems.set(i, RhythmSequencer.SeqEvent.MISS);
            }
            //rect(width / 2 - 100, height - presentLineOffset - tickOffset / 5, 200, 15);
        }

        for (int i=0; i< MainActivity.foo.size(); i++) {
            Foo f = (Foo) MainActivity.foo.get(i);
            f.moveOn();
            f.display();
        }
    }

    public void tick(RhythmSequencer.SeqEvent event) {
        switch (event) {
            case STARTED:
                eventStr = "STARTED";
                break;
            case PUNCH:
                eventStr = "PUNCH";
                tickEventMs = msPassed;
                tickEventIndex = RhythmSequencer.getInstance().currentTick;
                lastEvent = event;
                break;
            case SWING:
                eventStr = "SWING";
                tickEventMs = msPassed;
                tickEventIndex = RhythmSequencer.getInstance().currentTick;
                lastEvent = event;
                break;
            case STOPPED:
                eventStr = "ENDED";
                break;
            case NONE:
                tickEventMs = msPassed;
                tickEventIndex = RhythmSequencer.getInstance().currentTick;
                lastEvent = event;
                break;
        }

        //Log.i(MainActivity.TAG, eventStr);
        if (msPassed - lastTouchEventMs < 200 && event  == RhythmSequencer.SeqEvent.PUNCH) {
            Log.i(MainActivity.TAG, "HIT from tick (before): " + Long.toString(msPassed - lastTouchEventMs));
            allItems.set(tickEventIndex - 1, RhythmSequencer.SeqEvent.HIT);
            tickEventMs = 0;
            lastTouchEventMs = 0;
            //Log.i(MainActivity.TAG, Boolean.toString(RhythmSequencer.getInstance().hitTest(lastTouchEventMs, RhythmSequencer.SeqEvent.PUNCH)) + ", " + Long.toString(msPassed));
        } else if (lastTouchEventMs != 0 && msPassed - lastTouchEventMs >= 200 && event == RhythmSequencer.SeqEvent.PUNCH) {
            Log.i(MainActivity.TAG, "miss in tick: " + eventStr);
            allItems.set(tickEventIndex - 1, RhythmSequencer.SeqEvent.MISS);
            lastTouchEventMs = 0;
            tickEventMs = 0;
        }
    }

    // new
    public static void birth(float _x, float _y) {
        MainActivity.foo.add(new Foo(_x, _y));

        lastTouchEventMs = msPassed;
        Log.i(MainActivity.TAG, "touch: " + Long.toString(lastTouchEventMs));
        if (msPassed - tickEventMs < 200 && lastEvent == RhythmSequencer.SeqEvent.PUNCH) {
            allItems.set(tickEventIndex - 1, RhythmSequencer.SeqEvent.HIT);
            Log.i(MainActivity.TAG, "HIT from touch (after)" + Long.toString(msPassed - tickEventMs));
            tickEventMs = 0;
            lastTouchEventMs = 0;
        }
        /*else if (tickEventMs - lastTouchEventMs < 500 && RhythmSequencer.getInstance().currentItem() == RhythmSequencer.SeqEvent.NONE) {
            Log.i(MainActivity.TAG, "FAULT" + Long.toString(msPassed - tickEventMs));
            allItems.set(tickEventIndex, RhythmSequencer.SeqEvent.FAULT);
            tickEventMs = 0;
            lastTouchEventMs = 0;
        }*/
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGestureDetector = new GestureDetector(caller, new LearnGestureListener());
    }
    public boolean surfaceTouchEvent(MotionEvent me) {
        if (mGestureDetector.onTouchEvent(me)) {
            //return true;
            return super.surfaceTouchEvent(me);
        }
        else {
            return false;
        }
    }
}
