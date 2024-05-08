package com.defaulttenant.hotswap.service.dto.filters.atomic;

import com.defaulttenant.hotswap.service.dto.filters.AbstractQueryFilter;
import com.defaulttenant.hotswap.util.CommonFunctionUtil;
import org.apache.commons.lang3.StringUtils;
import com.defaulttenant.hotswap.domain.enumeration.BaseEnum;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.defaulttenant.hotswap.config.DateTimeFormatConfiguration.*;

/**
 * @Author: sys
 */
public class ListLiteralQueryFilter extends AbstractQueryFilter {

    private String value;
    private List listValue;

    private List<Object> listValues = new ArrayList<>();

    public ListLiteralQueryFilter() {
        this.concept = "ListLiteral";
    }

    public ListLiteralQueryFilter(String value) {
        this();
        this.value = value;
    }

    public ListLiteralQueryFilter(List listValue) {
        this();
        this.listValue = listValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List getListValue() {
        return listValue;
    }

    public void setListValue(List listValue) {
        this.listValue = listValue;
    }

    public List<Object> getListValues() {
        return listValues;
    }

    public void setListValues(List<Object> listValues) {
        this.listValues = listValues;
    }

    @Override
    public String sql(String dbType) {
        dbType = StringUtils.defaultString(dbType, "");
        if (null == value && null == listValue) {
            listValues.add(NULL_STRING);
            return QUESTION_MARK;
        } else if(null != listValue) {
            return sqlByValueList(dbType);
        }

        List<String> valueList = new ArrayList<>(Arrays.asList(value.split(SPLIT_COMMA)));
        StringBuilder placeholder = new StringBuilder();
        String split = "";
        for (int i = 0; i < valueList.size(); i++) {
            placeholder.append(split);
            placeholder.append(QUESTION_MARK);
            split = SPLIT_COMMA;
            String item = valueList.get(i);

            if (Boolean.TRUE.toString().equals(item) || Boolean.FALSE.toString().equals(item)) {
                listValues.add(Boolean.valueOf(item));
            } else if (CommonFunctionUtil.DATETIME_FORMAT_PATTERN.matcher(value).find()) {
                listValues.add(ZonedDateTime.parse(value, DateTimeFormatter.ofPattern(ZONED_DATETIME_FORMAT)
                        .withZone(ZoneId.of(DEFAULT_TIMEZONE))));
            } else if (CommonFunctionUtil.DATE_FORMAT_PATTERN.matcher(value).find()) {
                listValues.add(LocalDateTime.parse(value, DateTimeFormatter.ofPattern(LOCAL_DATE_FORMAT)));
            } else if (CommonFunctionUtil.TIME_FORMAT_PATTERN.matcher(value).find()) {
                listValues.add(LocalDateTime.parse(value, DateTimeFormatter.ofPattern(LOCAL_TIME_FORMAT)));
            } else {
                if (null != item && item.startsWith(SINGLE_QUOT) && item.endsWith(SINGLE_QUOT)) {
                    item = item.substring(1, item.length() - 1);
                }
                listValues.add(item);
            }
        }

        return LEFT_PARENTHESES + placeholder + RIGHT_PARENTHESES;
    }

    private String sqlByValueList(String dbType) {
        StringBuilder placeholder = new StringBuilder();
        String split = "";
        for (Object item : listValue) {
            placeholder.append(split);
            placeholder.append(QUESTION_MARK);
            split = SPLIT_COMMA;
            if (item instanceof BaseEnum) {
                listValues.add(((BaseEnum)item).getCode());
            } else {
                listValues.add(item);
            }
        }
        return LEFT_PARENTHESES + placeholder + RIGHT_PARENTHESES;
    }
}

