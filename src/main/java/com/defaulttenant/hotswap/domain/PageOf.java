package com.defaulttenant.hotswap.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * System built in generic class
 * PageOf
 *
 * @author sys
 */
public class PageOf<T> {
    public Long number;

    public Boolean last;

    public Long size;

    public Long numberOfElements;

    public Long totalPages;

    public List<T> content = new ArrayList<>();

    public Boolean first;

    public Long totalElements;

    public Boolean empty;

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Boolean getLast() {
        return last;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(Long numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Boolean getFirst() {
        return first;
    }

    public void setFirst(Boolean first) {
        this.first = first;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Boolean getEmpty() {
        return empty;
    }

    public void setEmpty(Boolean empty) {
        this.empty = empty;
    }

    public static <T> PageOf of(List<T> content, Long total, Long page, Long size) {
        return of(content, null == total ? null : total, page, size);
    }

    public static <T> PageOf of(List<T> content, Long total, Integer page, Integer size) {
        return of(content, null == total ? null : total.intValue(), page, size);
    }

    public static <T> PageOf of(List<T> content, Integer total, Integer page, Integer size) {
        PageOf<T> pageOf = new PageOf();
        pageOf.setContent(content);

        content = ( null == content ? Collections.EMPTY_LIST : content );
        pageOf.setNumberOfElements((long) content.size());

        if (total != null) {
            pageOf.setEmpty(total == 0);
            pageOf.setTotalElements((long) total);
        } else {
            total = 0;
        }

        if (null != page && null != size) {
            pageOf.setSize((long) size);
            pageOf.setNumber((long) page);
            pageOf.setTotalPages((long) total / size + (total % size > 0 ? 1 : 0));

            pageOf.setFirst(1 == page);
            pageOf.setLast(page.equals(pageOf.getTotalPages()));
        }
        return pageOf;
    }

    @Override
    public String toString() {
        return (
            "PageOf{" +
            "number=" +
            number +
            ", last=" +
            last +
            ", size=" +
            size +
            ", numberOfElements=" +
            numberOfElements +
            ", totalPages=" +
            totalPages +
            ", content=" +
            content +
            ", first=" +
            first +
            ", totalElements=" +
            totalElements +
            ", empty=" +
            empty +
            '}'
        );
    }
}
