package com.tiksem.media.search.parsers.checkers;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 03.03.13
 * Time: 17:43
 * To change this template use File | Settings | File Templates.
 */
public class VkUrlReceivedTitleHasTitleChecker extends VkUrlChecker{
    public VkUrlReceivedTitleHasTitleChecker(VkUrlChecker.Params params) {
        super(params);
    }

    @Override
    public boolean elementSatisfyCondition(String object, int index) {
        String requestTitle = params.requestedTitle.toLowerCase();
        String receivedTitle = params.receivedTitle.toLowerCase();
        return receivedTitle.indexOf(requestTitle) >= 0;
    }
}
