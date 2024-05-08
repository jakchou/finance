package com.defaulttenant.hotswap.service.dto.filter;

import java.util.UUID;

public class UUIDFilter extends Filter<UUID> {

    private static final long serialVersionUID = 1L;

    /**
     * <p>Constructor for UUIDFilter.</p>
     */
    public UUIDFilter() {
    }

    /**
     * <p>Constructor for UUIDFilter.</p>
     *
     * @param filter a UUIDFilter object.
     */
    public UUIDFilter(final UUIDFilter filter) {
        super(filter);
    }

    /**
     * <p>copy.</p>
     *
     * @return a UUIDFilter object.
     */
    @Override
    public UUIDFilter copy() {
        return new UUIDFilter(this);
    }

}
