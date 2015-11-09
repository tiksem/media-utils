package com.tiksem.media.search.parsers;

import com.tiksem.media.data.Audio;
import com.utils.framework.Primitive;

import java.util.Comparator;

/**
 * Created by stykhonenko on 09.11.15.
 */
public class VkUrlDataComparator implements Comparator<UrlQueryData> {
    private final VkAudioUrlPriorityProvider priorityProvider;
    private Audio audio;

    public VkUrlDataComparator(Audio audio) {
        this.audio = audio;
        priorityProvider = new VkAudioUrlPriorityProvider(audio);
    }

    @Override
    public int compare(UrlQueryData a, UrlQueryData b) {
        int aPriority = priorityProvider.getPriorityOf(a, -1);
        int bPriority = priorityProvider.getPriorityOf(b, -1);
        int priorityCompare = Primitive.compare(aPriority, bPriority);
        if (priorityCompare != 0) {
            return priorityCompare;
        }

        int inputDuration = audio.getDuration();
        if (inputDuration > 0) {
            return Integer.compare(Math.abs(inputDuration - a.getDuration()),
                    Math.abs(inputDuration - b.getDuration()));
        }

        return 0;
    }
}
