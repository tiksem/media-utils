package com.tiksem.media.search.parsers.checkers;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 03.03.13
 * Time: 17:32
 * To change this template use File | Settings | File Templates.
 */
public class VkUrlArtistNameEqualsChecker extends VkUrlChecker{
    public VkUrlArtistNameEqualsChecker(Params params) {
        super(params);
    }

    @Override
    public boolean elementSatisfyCondition(String object, int index) {
        return params.requestedArtist.equalsIgnoreCase(params.receivedArtist);
    }
}
