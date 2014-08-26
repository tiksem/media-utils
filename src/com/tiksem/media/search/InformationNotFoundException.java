package com.tiksem.media.search;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 02.01.13
 * Time: 3:02
 * To change this template use File | Settings | File Templates.
 */
public class InformationNotFoundException extends Exception{
    public InformationNotFoundException() {
        super();
    }

    public InformationNotFoundException(String detailMessage) {
        super(detailMessage);
    }

    public InformationNotFoundException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public InformationNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
