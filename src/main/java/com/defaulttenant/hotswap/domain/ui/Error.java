package com.defaulttenant.hotswap.domain.ui;

public class Error {
    public String errorType;
    public String errorMsg;

    public Error() {
    }

    public Error(String errorType, String errorMsg) {
        this.errorMsg = errorMsg;
        this.errorType = errorType;
    }

    public String getErrorType() {
        return this.errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
