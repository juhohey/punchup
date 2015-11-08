package com.drejkim.androidwearmotionsensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import java.sql.Time;
import java.util.LinkedList;

/**
 * Created by medical on 08/11/15.
 */
public class PunchSwingDetector {
    final int windowSize = 20;

    LinkedList<SensorEvent> eventSamples = new LinkedList<SensorEvent>();
    //float eventSamples[][] = new eventSamples[windowSize][3];

    public double timeOflastEventDetected = 0.;

    public enum Event { PUNCH, SWING, NONE };

    public Event addEvent(SensorEvent event)
    {
        if (eventSamples.size() > windowSize) {
            eventSamples.remove();
        }

        double x = 0, y = 0, z = 0;

        eventSamples.add(event);
        for (SensorEvent e: eventSamples) {
            x += e.values[0];
            y += e.values[1];
            z += e.values[2];
        }

        x /= windowSize;
        y /= windowSize;
        z /= windowSize;

        double amp = Math.sqrt(x * x + y * y + z * z);

        // dot 1, 0, 0
        // dot 0, 0, 1

        if (System.currentTimeMillis() - timeOflastEventDetected < 250) {
            return Event.NONE;
        }

        if (amp > 15 && Math.abs(y)/amp < .65 && Math.abs(z)/amp < .65) {
            timeOflastEventDetected = System.currentTimeMillis();
            return Event.PUNCH;

        }

        if (amp > 15 && Math.abs(x) / amp < 0.65) {
            timeOflastEventDetected = System.currentTimeMillis();
            return Event.SWING;
        }

        return Event.NONE;
    }
}
