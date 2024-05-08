package com.defaulttenant.hotswap.service.dto.filters.atomic;

import com.defaulttenant.hotswap.service.dto.filters.AbstractQueryFilter;
import com.defaulttenant.hotswap.util.CommonFunctionUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.defaulttenant.hotswap.config.DateTimeFormatConfiguration.*;

/**
 * @Author: sys
 */
public class StringLiteralQueryFilter extends AbstractQueryFilter {

    private String value;
    private Object preparedValue;

    public StringLiteralQueryFilter() {
        this.concept = "StringLiteral";
    }

    public StringLiteralQueryFilter(String value) {
        this();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object getPreparedValue() {
        return preparedValue;
    }

    public void setPreparedValue(Object preparedValue) {
        this.preparedValue = preparedValue;
    }

    @Override
    public String sql(String dbType) {

        if (null == value) {
            value = NULL_STRING;
            preparedValue = value;
            return QUESTION_MARK;
        }


        if (CommonFunctionUtil.DATETIME_FORMAT_PATTERN.matcher(value).find()) {
            preparedValue = ZonedDateTime.parse(value, DateTimeFormatter.ofPattern(ZONED_DATETIME_FORMAT)
                    .withZone(ZoneId.of(DEFAULT_TIMEZONE)));
        } else if (CommonFunctionUtil.DATE_FORMAT_PATTERN.matcher(value).find()) {
            preparedValue =  LocalDateTime.parse(value, DateTimeFormatter.ofPattern(LOCAL_DATE_FORMAT));
        } else if (CommonFunctionUtil.TIME_FORMAT_PATTERN.matcher(value).find()) {
            preparedValue =  LocalDateTime.parse(value, DateTimeFormatter.ofPattern(LOCAL_TIME_FORMAT));
        } else {
            preparedValue = value;
        }

        return QUESTION_MARK;
    }
}
