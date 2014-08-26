package com.tiksem.media.search.parsers;

import com.tiksem.media.data.Audio;
import com.tiksem.media.search.parsers.checkers.VkUrlChecker;
import com.tiksem.media.search.parsers.checkers.VkUrlCheckerFactory;
import com.tiksem.media.search.parsers.checkers.VkUrlDurationChecker;
import com.utils.framework.collections.checkers.ElementChecker;
import com.utils.framework.collections.checkers.ElementCheckerListPriorityProvider;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class VkAudioUrlPriorityProvider extends ElementCheckerListPriorityProvider {
    private VkUrlChecker.Params vkUrlCheckerParams = new VkUrlChecker.Params();
    private JSONArray tracks;

    public VkAudioUrlPriorityProvider(JSONArray tracks, Audio audio) {
        vkUrlCheckerParams.requestedArtist = audio.getArtistName();
        vkUrlCheckerParams.requestedDuration = Math.round((float)audio.getDuration() / 1000.0f);
        vkUrlCheckerParams.requestTitle = audio.getName();
        this.tracks = tracks;
    }

    @Override
    protected List getElementCheckers() {
        return Arrays.asList(VkUrlCheckerFactory.artistNameTitleAndDuration(vkUrlCheckerParams),
                VkUrlCheckerFactory.receivedTitleHasArtistNameAndTitleAndDuration(vkUrlCheckerParams),
                VkUrlCheckerFactory.artistNameOrTitleAndDuration(vkUrlCheckerParams),
                VkUrlCheckerFactory.artistNameAndTitle(vkUrlCheckerParams),
                VkUrlCheckerFactory.receivedTitleHasArtistNameOrTitleAndDuration(vkUrlCheckerParams),
                VkUrlCheckerFactory.receivedTitleHasArtistNameAndTitle(vkUrlCheckerParams),
                new VkUrlDurationChecker(vkUrlCheckerParams),
                VkUrlCheckerFactory.artistNameOrTitle(vkUrlCheckerParams),
                VkUrlCheckerFactory.receivedTitleHasArtistNameOrTitle(vkUrlCheckerParams));
    }

    @Override
    public int getPriorityOf(Object object, int index) {
        JSONObject track = tracks.optJSONObject(index + 1);
        if(track == null){
            return getPrioritiesCount() - 1;
        }

        vkUrlCheckerParams.receivedArtist = track.optString("artist");
        vkUrlCheckerParams.receivedTitle = track.optString("title");
        vkUrlCheckerParams.receivedDuration = track.optInt("duration",-1);

        return super.getPriorityOf(object, index);
    }
}
