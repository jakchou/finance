package com.defaulttenant.hotswap.domain.ui;

public class FocusEvent {
    public Boolean cancelBubble;
    public String detail;
    public Long layerX;
    public Long layerY;
    public Long pageX;
    public Long pageY;
    public Long which;

    public Boolean getCancelBubble() {
        return cancelBubble;
    }

    public void setCancelBubble(Boolean cancelBubble) {
        this.cancelBubble = cancelBubble;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Long getLayerX() {
        return layerX;
    }

    public void setLayerX(Long layerX) {
        this.layerX = layerX;
    }

    public Long getLayerY() {
        return layerY;
    }

    public void setLayerY(Long layerY) {
        this.layerY = layerY;
    }

    public Long getPageX() {
        return pageX;
    }

    public void setPageX(Long pageX) {
        this.pageX = pageX;
    }

    public Long getPageY() {
        return pageY;
    }

    public void setPageY(Long pageY) {
        this.pageY = pageY;
    }

    public Long getWhich() {
        return which;
    }

    public void setWhich(Long which) {
        this.which = which;
    }
}
