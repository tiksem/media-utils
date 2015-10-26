package com.tiksem.media.search;

import java.io.IOException;

/**
 * Created by stykhonenko on 26.10.15.
 */
public class RequestJsonException extends IOException {
    public RequestJsonException() {
    }

    public RequestJsonException(String detailMessage) {
        super(detailMessage);
    }

    public RequestJsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestJsonException(Throwable cause) {
        super(cause);
    }
}
