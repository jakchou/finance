package com.defaulttenant.hotswap.domain.ui;

public class UploadEvent {
    public File item;
    public String data;
    public String file;
    public String xhr;
    public String formData;
    public String xml;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getXhr() {
        return xhr;
    }

    public void setXhr(String xhr) {
        this.xhr = xhr;
    }

    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }
}
