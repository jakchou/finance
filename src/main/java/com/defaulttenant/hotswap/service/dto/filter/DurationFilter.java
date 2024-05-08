package com.defaulttenant.hotswap.service.dto.filter;

import java.time.Duration;

public class DurationFilter extends RangeFilter<Duration> {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for DurationFilter.</p>
     */
    public DurationFilter() {
    }

    /**
     * <p>Constructor for DurationFilter.</p>
     *
     * @param filter a DurationFilter object.
     */
    public DurationFilter(final DurationFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a DurationFilter object.
     */
    @Override
    public DurationFilter copy() {
        return new DurationFilter(this);
    }

}

