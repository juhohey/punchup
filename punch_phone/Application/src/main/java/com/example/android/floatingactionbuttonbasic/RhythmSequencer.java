package com.example.android.floatingactionbuttonbasic;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;

import com.example.android.common.logger.Log;
/**
 * Created by medical on 07/11/15.
 */
public class RhythmSequencer extends TimerTask
{
    private static RhythmSequencer ourInstance = new RhythmSequencer();
    public static RhythmSequencer getInstance() {
        return ourInstance;
    }

    enum SeqState { TOGO_3, TOGO_2, TOGO_1, RUNNING, STOPPED };
    public SeqState state;

    enum SeqEvent { STARTED, PUNCH, SWING, NONE, HIT, MISS, STOPPED, FAULT };
    SeqEvent[] sequence;

    public int currentTick = 0;

    public SeqEvent currentItem() {
        return sequence[currentTick];
    }

    public SeqEvent[] nextItems(int count)
    {
        return Arrays.copyOfRange(
                sequence,
                currentTick,
                Math.min(sequence.length - currentTick, count));
    }

    // use this sequence listener to act on seq events
    public interface SeqListener
    {
        public void tick(SeqEvent item);
    }

    public SeqListener listener = null;

    private void broadcast(SeqEvent event)
    {
        if (event == SeqEvent.NONE) {
            //System.out.println("tick");
            return;
        }

        if (listener != null) {
            listener.tick(event);
        }

        String eventStr = "";
        switch (event) {
            case STARTED: eventStr = "STARTED"; break;
            case PUNCH: eventStr = "PUNCH"; break;
            case SWING: eventStr = "SWING"; break;
            case STOPPED: eventStr = "ENDED"; break;
        }
        System.out.println(eventStr);
    }

    public boolean hitTest(long millis, SeqEvent type) {
        final int gracePeriod = 250;
        for (int i = 0; i < sequence.length; i++) {
            SeqEvent current = sequence[i];
            long testTime = tickLengthMs * i;
            if (Math.abs(millis - testTime) < gracePeriod) {
                String eventStr = "";
                switch (current) {
                    case STARTED:
                        eventStr = "STARTED";
                        break;
                    case PUNCH:
                        eventStr = "PUNCH";
                        break;
                    case SWING:
                        eventStr = "SWING";
                        break;
                    case STOPPED:
                        eventStr = "ENDED";
                        break;
                    case NONE:
                        eventStr = "NONE";
                        break;
                    case HIT:
                        eventStr = "HIT";
                        break;
                    case MISS:
                        eventStr = "MISS";
                        break;
                }
                Log.i(MainActivity.TAG, "found sth.: " + eventStr + ", " + i);
                if (current == type) {
                    sequence[i] = SeqEvent.HIT;
                    return true;
                }
            }
        }
        return false;
    }

    static int ticksPerBeat = 4;
    static int bpm = 120;
    static int tickLengthMs = (60*1000) / (ticksPerBeat*bpm);

    private Timer tickTimer = new Timer();

    private RhythmSequencer() {
        String seq =
                "p---p-s-p---p-s-p---" +
                "p---p-s-p---p-s-p---" +
                "p---p-s-p---p-s-p---" +
                "p---p-s-p---p-s-p---" +
                "p---p-s-p---p-s-p---" +
                "p---p-s-p---p-s-p---" +
                "p---p-s-p---p-s-p---" +
                "p---p-s-p---p-s-p---" +
                "p---p-s-p---p-s-p---" +
                "p---p-s-p---p-s-p---" +
                "p---p-s-p---p-s-p---" +
                "p---p-s-p---p-s-p---" +
                "p---p-s-p---p-s-p---" +
                "p---p-s-p---p-s-p---" +
                "p---p-s-p---p-s-p---" +
                "p---p-s-p---p-s-p---";

        sequence = new SeqEvent[seq.length()];
        for (int i = 0; i < seq.length(); i++) {
            switch (seq.charAt(i)) {
                case 'p': sequence[i] = SeqEvent.PUNCH; break;
                case 's': sequence[i] = SeqEvent.SWING; break;
                case '-': sequence[i] = SeqEvent.NONE; break;
            }
        }
    }

    public void run() {
        currentTick++;
        if (currentTick >= sequence.length)
        {
            this.stop();
        }
        else
        {
            broadcast(this.currentItem());
        }
    }

    public void start() {
        state = SeqState.RUNNING;
        tickTimer.scheduleAtFixedRate(this, 0, tickLengthMs);
        this.broadcast(SeqEvent.STARTED);


        audioPlayer.start();
    }

    public void stop() {
        state = SeqState.STOPPED;
        this.broadcast(SeqEvent.STOPPED);
        tickTimer.cancel();
        currentTick = 0;

        audioPlayer.stop();
    }

    public void restart() {
        this.stop();
        this.start();
    }

    /// mediaplayer stuffffffff

    public MediaPlayer audioPlayer;
    public void createAudioPlayer(Context ctx) {
        audioPlayer = MediaPlayer.create(ctx, R.raw.saxguy);
    }

}
