package com.tiksem.media.search.parsers.checkers;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 03.03.13
 * Time: 17:40
 * To change this template use File | Settings | File Templates.
 */
public class VkUrlReceivedTitleHasArtistNameChecker extends VkUrlChecker{
    public VkUrlReceivedTitleHasArtistNameChecker(Params params) {
        super(params);
    }

    @Override
    public boolean elementSatisfyCondition(String object, int index) {
        String requestedArtist = params.requestedArtist.toLowerCase();
        String receivedTitle = params.receivedTitle.toLowerCase();
        return receivedTitle.indexOf(requestedArtist) >= 0;
    }
}
