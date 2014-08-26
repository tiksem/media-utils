package com.tiksem.media.search.parsers.checkers;

import com.utils.framework.collections.checkers.ElementChecker;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 03.03.13
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */
public abstract class VkUrlChecker implements ElementChecker<String> {
    public static class Params{
        public String requestedArtist;
        public String requestTitle;
        public String receivedArtist;
        public String receivedTitle;
        public int requestedDuration;
        public int receivedDuration;
    }

   protected Params params;

    public VkUrlChecker(Params params) {
        this.params = params;
    }
}
