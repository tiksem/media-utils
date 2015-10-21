package com.tiksem.media.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;
import com.tiksem.media.playback.AudioPlayerService;
import com.tiksem.media.playback.ProgressChangedListener;
import com.tiksem.media.playback.StateChangedListener;
import com.tiksem.media.playback.Status;

/**
 * User: Tikhonenko.S
 * Date: 07.08.14
 * Time: 15:39
 */
public class AudioPlaybackSeekBar extends SeekBar {
    private static final int SEEK_BAR_COEFFICIENT = 1000;

    private AudioPlayerService.Binder playerBinder;
    private ProgressChangedListener progressChangedListener;
    private StateChangedListener stateChangedListener;

    public AudioPlaybackSeekBar(Context context) {
        super(context);
    }

    public AudioPlaybackSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AudioPlaybackSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPlayerBinder(final AudioPlayerService.Binder playerBinder) {
        if(playerBinder == null){
            throw new NullPointerException();
        }

        if(this.playerBinder != null){
            throw new IllegalStateException("Unable to setPlayerBinder twice");
        }

        this.playerBinder = playerBinder;
        setVisibility(GONE);

        progressChangedListener = new ProgressChangedListener() {
            @Override
            public void onProgressChanged(long progress, long max) {
                setMax(Math.round(max / SEEK_BAR_COEFFICIENT));
                setProgress(Math.round(progress / SEEK_BAR_COEFFICIENT));
            }
        };
        playerBinder.addProgressChangedListener(progressChangedListener);

        stateChangedListener = new StateChangedListener() {
            @Override
            public void onStateChanged(Status status) {
                if (status == Status.PLAYING || status == Status.PAUSED) {
                    setVisibility(VISIBLE);
                    setEnabled(true);
                } else {
                    setEnabled(false);
                }
            }
        };
        playerBinder.addStateChangedListener(stateChangedListener);
        stateChangedListener.onStateChanged(playerBinder.getStatus());

        super.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    playerBinder.seekTo(progress * SEEK_BAR_COEFFICIENT);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(playerBinder != null){
            playerBinder.removeProgressChangedListener(progressChangedListener);
            playerBinder.removeStateChangedListener(stateChangedListener);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }
}
