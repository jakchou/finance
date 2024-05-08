package com.defaulttenant.hotswap.service.dto.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Filter<FIELD_TYPE> {

    private static final long serialVersionUID = 1L;
    private FIELD_TYPE equals;
    private FIELD_TYPE notEquals;
    private Boolean specified;
    private List<FIELD_TYPE> in;
    private List<FIELD_TYPE> notIn;

    /**
     * <p>Constructor for Filter.</p>
     */
    public Filter() {
    }

    /**
     * <p>Constructor for Filter.</p>
     *
     * @param filter a object.
     */
    public Filter(Filter<FIELD_TYPE> filter) {
        this.equals = filter.equals;
        this.notEquals = filter.notEquals;
        this.specified = filter.specified;
        this.in = filter.in == null ? null : new ArrayList<>(filter.in);
        this.notIn = filter.notIn == null ? null : new ArrayList<>(filter.notIn);
    }

    /**
     * <p>copy.</p>
     *
     * @return a {@link } object.
     */
    public Filter<FIELD_TYPE> copy() {
        return new Filter<>(this);
    }

    /**
     * <p>Getter for the field <code>equals</code>.</p>
     *
     * @return a FIELD_TYPE object.
     */
    public FIELD_TYPE getEquals() {
        return equals;
    }

    /**
     * <p>Setter for the field <code>equals</code>.</p>
     *
     * @param equals a FIELD_TYPE object.
     * @return a Filter<FIELD_TYPE>.
     */
    public Filter<FIELD_TYPE> setEquals(FIELD_TYPE equals) {
        this.equals = equals;
        return this;
    }

    /**
     * <p>Getter for the field <code>notEquals</code>.</p>
     *
     * @return a FIELD_TYPE object.
     */
    public FIELD_TYPE getNotEquals() {
        return notEquals;
    }

    /**
     * <p>Setter for the field <code>notEquals</code>.</p>
     *
     * @param notEquals a FIELD_TYPE object.
     * @return a Filter<FIELD_TYPE>.
     */
    public Filter<FIELD_TYPE> setNotEquals(FIELD_TYPE notEquals) {
        this.notEquals = notEquals;
        return this;
    }

    /**
     * <p>Getter for the field <code>specified</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getSpecified() {
        return specified;
    }

    /**
     * <p>Setter for the field <code>specified</code>.</p>
     *
     * @param specified a {@link java.lang.Boolean} object.
     * @return a Filter<FIELD_TYPE>.
     */
    public Filter<FIELD_TYPE> setSpecified(Boolean specified) {
        this.specified = specified;
        return this;
    }

    /**
     * <p>Getter for the field <code>in</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<FIELD_TYPE> getIn() {
        return in;
    }

    /**
     * <p>Getter for the field <code>in</code>.</p>
     * <p><code>in</code> is key word for mybatis, so this method for mybatis to access getter</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<FIELD_TYPE> getInCondition() {
        return in;
    }


    /**
     * <p>Setter for the field <code>in</code>.</p>
     *
     * @param in a {@link java.util.List} object.
     * @return a Filter<FIELD_TYPE>.
     */
    public Filter<FIELD_TYPE> setIn(List<FIELD_TYPE> in) {
        this.in = in;
        return this;
    }

    /**
     * <p>Getter for the field <code>notIn</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<FIELD_TYPE> getNotIn() {
        return notIn;
    }

    /**
     * <p>Setter for the field <code>notIn</code>.</p>
     *
     * @param notIn a {@link java.util.List} object.
     * @return a Filter<FIELD_TYPE>.
     */
    public Filter<FIELD_TYPE> setNotIn(List<FIELD_TYPE> notIn) {
        this.notIn = notIn;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Filter<?> filter = (Filter<?>) o;
        return Objects.equals(equals, filter.equals) &&
                Objects.equals(notEquals, filter.notEquals) &&
                Objects.equals(specified, filter.specified) &&
                Objects.equals(in, filter.in) &&
                Objects.equals(notIn, filter.notIn);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(equals, notEquals, specified, in, notIn);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getFilterName() + " ["
                + (getEquals() != null ? "equals=" + getEquals() + ", " : "")
                + (getNotEquals() != null ? "notEquals=" + getNotEquals() + ", " : "")
                + (getSpecified() != null ? "specified=" + getSpecified() + ", " : "")
                + (getIn() != null ? "in=" + getIn() + ", " : "")
                + (getNotIn() != null ? "notIn=" + getNotIn() : "")
                + "]";
    }

    /**
     * <p>getFilterName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    protected String getFilterName() {
        return this.getClass().getSimpleName();
    }
}
