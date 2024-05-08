package com.defaulttenant.hotswap.exception;

/**
 * 中止异常
 *
 * @author sys
 * @date 2023-05-11 15:12
 */
public class LcapAbortException extends HttpCodeException {

    public LcapAbortException() {
        super(501, "abort");
    }

    public LcapAbortException(Throwable t) {
        super(501, t.getMessage(), t);
    }
}
