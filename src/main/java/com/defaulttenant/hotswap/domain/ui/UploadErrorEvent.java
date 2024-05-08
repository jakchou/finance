package com.defaulttenant.hotswap.domain.ui;

public class UploadErrorEvent {
    public String name;
    public String message;
    public String extensions;
    public String maxSize;
    public String size;
    public Long count;
    public Long limit;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExtensions() {
        return extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    public String getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(String maxSize) {
        this.maxSize = maxSize;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }
}