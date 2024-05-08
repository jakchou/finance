package com.defaulttenant.hotswap.service.dto.filter;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class LocalTimeFilter extends RangeFilter<LocalTime> {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for LocalDateFilter.</p>
     */
    public LocalTimeFilter() {
    }

    /**
     * <p>Constructor for LocalDateFilter.</p>
     *
     * @param filter a LocalDateFilter object.
     */
    public LocalTimeFilter(final LocalTimeFilter filter) {
        super(filter);
    }

    /** {@inheritDoc} */
    @Override
    public LocalTimeFilter copy() {
        return new LocalTimeFilter(this);
    }

    /** {@inheritDoc} */
    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    public LocalTimeFilter setEquals(LocalTime equals) {
        super.setEquals(equals);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    public LocalTimeFilter setNotEquals(LocalTime equals) {
        super.setNotEquals(equals);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    public LocalTimeFilter setIn(List<LocalTime> in) {
        super.setIn(in);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    public LocalTimeFilter setNotIn(List<LocalTime> notIn) {
        super.setNotIn(notIn);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    public LocalTimeFilter setGreaterThan(LocalTime equals) {
        super.setGreaterThan(equals);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    public LocalTimeFilter setLessThan(LocalTime equals) {
        super.setLessThan(equals);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    public LocalTimeFilter setGreaterThanOrEqual(LocalTime equals) {
        super.setGreaterThanOrEqual(equals);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    public LocalTimeFilter setLessThanOrEqual(LocalTime equals) {
        super.setLessThanOrEqual(equals);
        return this;
    }

}

