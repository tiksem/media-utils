package com.tiksem.media.playback;

/**
 * Created by stykhonenko on 20.10.15.
 */
public interface StateChangedListener {
    void onStateChanged(Status status, Status lastStatus);
}
