package com.tiksem.media.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;
import com.tiksem.media.playback.AudioPlayerService;

/**
 * User: Tikhonenko.S
 * Date: 07.08.14
 * Time: 15:39
 */
public class AudioPlaybackSeekBar extends SeekBar {
    private static final int SEEK_BAR_COEFFICIENT = 1000;

    private AudioPlayerService.PlayerBinder playerBinder;
    private AudioPlayerService.PlayBackListener playBackListener;

    public AudioPlaybackSeekBar(Context context) {
        super(context);
    }

    public AudioPlaybackSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AudioPlaybackSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AudioPlayerService.PlayerBinder getPlayerBinder() {
        return playerBinder;
    }

    public void setPlayerBinder(final AudioPlayerService.PlayerBinder playerBinder) {
        if(playerBinder == null){
            throw new NullPointerException();
        }

        if(this.playerBinder != null){
            throw new IllegalStateException("Unable to setPlayerBinder twice");
        }

        this.playerBinder = playerBinder;
        setVisibility(GONE);

        playBackListener = new AudioPlayerService.PlayBackListener() {
            @Override
            public void onAudioPlayingStarted() {
                setProgress(0);
                setMax(playerBinder.getDuration());
                setVisibility(VISIBLE);
            }

            @Override
            public void onAudioPlayingComplete() {
                setVisibility(GONE);
            }

            @Override
            public void onAudioPlayingPaused() {
            }

            @Override
            public void onAudioPlayingResumed() {
            }

            @Override
            public void onProgressChanged(long progress, long max) {
                setMax(Math.round(max / SEEK_BAR_COEFFICIENT));
                setProgress(Math.round(progress / SEEK_BAR_COEFFICIENT));
            }
        };
        playerBinder.addPlayBackListener(playBackListener);

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
            playerBinder.removePlayBackListener(playBackListener);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }
}
