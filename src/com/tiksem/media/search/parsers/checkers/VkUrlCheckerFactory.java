package com.tiksem.media.search.parsers.checkers;

import com.utils.framework.collections.checkers.ElementChecker;
import com.utils.framework.collections.checkers.ElementCheckersAnd;
import com.utils.framework.collections.checkers.ElementCheckersOr;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 03.03.13
 * Time: 17:20
 * To change this template use File | Settings | File Templates.
 */
public final class VkUrlCheckerFactory {
    public static ElementChecker artistNameAndTitle(VkUrlChecker.Params params){
        return new ElementCheckersAnd(new VkUrlArtistNameEqualsChecker(params),
                new VkUrlTitleEqualsChecker(params));
    }

    public static ElementChecker artistNameOrTitle(VkUrlChecker.Params params){
        return new ElementCheckersOr(new VkUrlArtistNameEqualsChecker(params),
                new VkUrlTitleEqualsChecker(params));
    }

    public static ElementChecker receivedTitleHasArtistNameAndTitle(VkUrlChecker.Params params){
        return new ElementCheckersAnd(
            new VkUrlRemixChecker(params),
            new ElementCheckersAnd(
                new VkUrlReceivedTitleHasArtistNameChecker(params),
                new VkUrlReceivedTitleHasTitleChecker(params)
            )
        );
    }

    public static ElementChecker receivedTitleHasArtistNameOrTitle(VkUrlChecker.Params params){
        return new ElementCheckersAnd(
                new VkUrlRemixChecker(params),
                new ElementCheckersOr(
                        new VkUrlReceivedTitleHasArtistNameChecker(params),
                        new VkUrlReceivedTitleHasTitleChecker(params)
                )
        );
    }

    public static ElementChecker receivedTitleHasArtistNameOrTitleAndDuration(VkUrlChecker.Params params){
        return new ElementCheckersAnd(receivedTitleHasArtistNameOrTitle(params),
                new VkUrlDurationChecker(params));
    }

    public static ElementChecker receivedTitleHasArtistNameAndTitleAndDuration(VkUrlChecker.Params params){
        return new ElementCheckersAnd(receivedTitleHasArtistNameAndTitle(params),
                new VkUrlDurationChecker(params));
    }

    public static ElementChecker artistNameTitleAndDuration(VkUrlChecker.Params params){
        return new ElementCheckersAnd(artistNameAndTitle(params),
                new VkUrlDurationChecker(params));
    }

    public static ElementChecker artistNameOrTitleAndDuration(VkUrlChecker.Params params){
        return new ElementCheckersAnd(artistNameOrTitle(params),
                new VkUrlDurationChecker(params));
    }
}
