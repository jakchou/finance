package com.defaulttenant.hotswap.domain.ui;


public class PaginationEvent {
    public Long page;
    public Long oldPage;
    public Long pageSize;
    public Long oldPageSize;
    public Long size;
    public Long oldSize;
    public Long number;
    public Long oldNumber;

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getOldPage() {
        return oldPage;
    }

    public void setOldPage(Long oldPage) {
        this.oldPage = oldPage;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getOldPageSize() {
        return oldPageSize;
    }

    public void setOldPageSize(Long oldPageSize) {
        this.oldPageSize = oldPageSize;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getOldSize() {
        return oldSize;
    }

    public void setOldSize(Long oldSize) {
        this.oldSize = oldSize;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Long getOldNumber() {
        return oldNumber;
    }

    public void setOldNumber(Long oldNumber) {
        this.oldNumber = oldNumber;
    }
}
