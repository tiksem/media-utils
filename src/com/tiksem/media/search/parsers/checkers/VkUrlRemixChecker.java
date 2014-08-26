package com.tiksem.media.search.parsers.checkers;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 03.03.13
 * Time: 21:47
 * To change this template use File | Settings | File Templates.
 */
public class VkUrlRemixChecker extends VkUrlTitlePartNotExistsChecker{
    public VkUrlRemixChecker(Params params) {
        super(params, "remix");
    }
}
