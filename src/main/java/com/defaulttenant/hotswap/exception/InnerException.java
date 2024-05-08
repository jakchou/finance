package com.defaulttenant.hotswap.exception;

/**
 * common business exception for inner call
 *
 * @author sys
 * @date 2022-04-10 20:52
 */
public class InnerException extends RuntimeException {
    public InnerException(String msg) {
        super(msg);
    }

    public InnerException(Throwable e) {
        super(e);
    }

    public InnerException(String msg, Throwable e) {
        super(msg, e);
    }
}
