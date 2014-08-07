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
            public void onProgressChanged(int progress) {
                setProgress(progress);
            }
        };
        playerBinder.addPlayBackListener(playBackListener);

        super.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    playerBinder.seekTo(progress);
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
