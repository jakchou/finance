package com.defaulttenant.hotswap.service.dto.filter;

public class DoubleFilter extends RangeFilter<Double> {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for DoubleFilter.</p>
     */
    public DoubleFilter() {
    }

    /**
     * <p>Constructor for DoubleFilter.</p>
     *
     * @param filter a DoubleFilter object.
     */
    public DoubleFilter(final DoubleFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a DoubleFilter object.
     */
    @Override
    public DoubleFilter copy() {
        return new DoubleFilter(this);
    }

}
