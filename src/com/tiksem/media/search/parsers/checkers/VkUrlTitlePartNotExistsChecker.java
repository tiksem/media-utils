package com.tiksem.media.search.parsers.checkers;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 03.03.13
 * Time: 21:42
 * To change this template use File | Settings | File Templates.
 */
public class VkUrlTitlePartNotExistsChecker extends VkUrlChecker{
    private String part;

    public VkUrlTitlePartNotExistsChecker(Params params, String part) {
        super(params);
        this.part = part;
    }

    @Override
    public boolean elementSatisfyCondition(String object, int index) {
        String requestedTitle = params.requestTitle.toLowerCase();
        String receivedTitle = params.receivedTitle.toLowerCase();
        return requestedTitle.indexOf(part) >= 0 || receivedTitle.indexOf(part) < 0;
    }
}
