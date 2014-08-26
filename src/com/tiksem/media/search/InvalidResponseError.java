package com.tiksem.media.search;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: CM
 * Date: 02.01.13
 * Time: 2:55
 * To change this template use File | Settings | File Templates.
 */
public class InvalidResponseError extends IOException {
    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public InvalidResponseError() {
    }

    public InvalidResponseError(String detailMessage) {
        super(detailMessage);
    }

    public InvalidResponseError(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidResponseError(Throwable cause) {
        super(cause);
    }
}
