package com.tiksem.media.search.parsers.checkers;

/**
 * Created by stykhonenko on 26.10.15.
 */
public class VkITunesSessionChecker extends VkUrlChecker {
    public VkITunesSessionChecker(Params params) {
        super(params);
    }

    @Override
    public boolean elementSatisfyCondition(String object, int index) {
        makeLowerCase();

        if (!params.receivedArtist.contains(params.requestedArtist)) {
            return false;
        }

        if (!params.receivedTitle.contains(params.requestedTitle)) {
            return false;
        }

        return (params.receivedArtist + " " + params.receivedTitle).contains("itunes session");
    }
}
