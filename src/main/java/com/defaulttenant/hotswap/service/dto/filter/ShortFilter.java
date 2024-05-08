package com.defaulttenant.hotswap.service.dto.filter;

public class ShortFilter extends RangeFilter<Short> {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for ShortFilter.</p>
     */
    public ShortFilter() {
    }

    /**
     * <p>Constructor for ShortFilter.</p>
     *
     * @param filter a ShortFilter object.
     */
    public ShortFilter(final ShortFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a ShortFilter object.
     */
    @Override
    public ShortFilter copy() {
        return new ShortFilter(this);
    }

}
