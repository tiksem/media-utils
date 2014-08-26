package com.tiksem.media.search.parsers.checkers;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 03.03.13
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class VkUrlDurationChecker extends VkUrlChecker {
    public VkUrlDurationChecker(Params params) {
        super(params);
    }

    @Override
    public boolean elementSatisfyCondition(String object, int index) {
        return params.requestedDuration == params.receivedDuration;
    }
}
