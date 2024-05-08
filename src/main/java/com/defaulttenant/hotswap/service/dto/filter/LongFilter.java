package com.defaulttenant.hotswap.service.dto.filter;

public class LongFilter extends RangeFilter<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for LongFilter.</p>
     */
    public LongFilter() {
    }

    /**
     * <p>Constructor for LongFilter.</p>
     *
     * @param filter a LongFilter object.
     */
    public LongFilter(final LongFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a LongFilter object.
     */
    @Override
    public LongFilter copy() {
        return new LongFilter(this);
    }

}
