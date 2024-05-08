package com.defaulttenant.hotswap.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.defaulttenant.hotswap.config.DateTimeFormatConfiguration;
import com.defaulttenant.hotswap.config.JacksonConfiguration;
import com.defaulttenant.hotswap.domain.ApiReturnOf;
import com.defaulttenant.hotswap.domain.PageOf;
import com.defaulttenant.hotswap.domain.enumeration.BaseEnum;
import com.defaulttenant.hotswap.domain.enumeration.ErrorCodeEnum;
import com.defaulttenant.hotswap.domain.structure.anonymous.AnonymousEnumStructure;
import com.defaulttenant.hotswap.domain.structure.anonymous.AnonymousLongEnumStructure;
import com.defaulttenant.hotswap.exception.HttpCodeException;
import com.defaulttenant.hotswap.service.dto.filters.AbstractQueryFilter;
import com.defaulttenant.hotswap.service.dto.filters.atomic.ColumnQueryFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import java.lang.reflect.Modifier;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static com.defaulttenant.hotswap.config.Constants.USER_APP_PROPERTY_KEY_PREFIX;

public class CommonFunctionUtil {
    private static final String DOMAIN_PACKAGE = ApiReturnOf.class.getPackage().getName();
    private static final String CALC_TYPE_DAY = "d";
    private static final String CALC_TYPE_SECOND = "s";
    private static final String CALC_TYPE_MINUTE = "m";
    private static final String CALC_TYPE_HOUR = "h";

    private static final String CALC_TYPE_YEAR = "y";

    private static final String CALC_TYPE_MONTH = "M";

    private static final String CALC_TYPE_WEEK = "w";

    private static final String CALC_TYPE_QUARTER = "q";

    private static final String DATETIME_UNIT_DAYS = "days";
    private static final String DATETIME_UNIT_MONTHS = "months";
    private static final String DATETIME_UNIT_YEARS = "years";
    private static final String DATETIME_UNIT_SECONDS = "seconds";
    private static final String DATETIME_UNIT_MINUTES = "minutes";
    private static final String DATETIME_UNIT_HOURS = "hours";

    private static final String DATETIME_FORMAT_YYYYMMDD = "yyyy-MM-dd";
    private static final String DATETIME_FORMAT_YYYYMMDD2 = "yyyy/MM/dd";
    private static final String DATETIME_FORMAT_YYYYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
    private static final String DATETIME_FORMAT_YYYYMMDD2_HHMMSS = "yyyy/MM/dd HH:mm:ss";
    private static final String DATETIME_FORMAT_YYYYMMDD3_HHMMSS = "yyyy.MM.dd HH:mm:ss";
    private static final String DATETIME_FORMAT_HHMMSS = "HH:mm:ss";
    private static final BigDecimal TWO = BigDecimal.valueOf(2);

    // 2022-10-04T16:00:00.000Z
    private static final String DATETIME_REGEX = "^\\d{4}-\\d{1,2}-\\d{1,2}T([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]).\\d{3}Z$";
    private static final String DATE_REGEX = "^\\d{4}-\\d{1,2}-\\d{1,2}$";
    private static final String TIME_REGEX = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";

    public static final Pattern DATETIME_FORMAT_PATTERN = Pattern.compile(DATETIME_REGEX);
    public static final Pattern DATE_FORMAT_PATTERN = Pattern.compile(DATE_REGEX);
    public static final Pattern TIME_FORMAT_PATTERN = Pattern.compile(TIME_REGEX);

    private static ObjectMapper oj = new ObjectMapper();
    private static final Map<Class, Object> DEFAULT_VALUE = new HashMap<>();
    private static final String[] ESCAPE_CHARACTERS = {"\\", "+", "-", "!", "(", ")", ":", "^", "[", "]", "\"", "{", "}", "~", "*", "?", "|", "&", ";", "/", ".", "$"};

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.####################E0");

    private static final String ABSOLUTE = "absolute";

    private static final Map<String, Function<TemporalAccessor, Long>> DATE_COUNT_FUNCTION = new HashMap<>();

    private static final Map<String, ChronoUnit> CHRONOS_UNIT_MAP = Stream.of(ChronoUnit.values()).collect(Collectors.toMap(chronoUnit -> chronoUnit.toString().toLowerCase(), Function.identity()));

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonFunctionUtil.class);

    static {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule
            .addSerializer(LocalDate.class, new JacksonConfiguration.LcpLocalDateSerializer())
            .addDeserializer(LocalDate.class, new JacksonConfiguration.LcpLocalDateDeserializer())
            .addSerializer(ZonedDateTime.class, new JacksonConfiguration.LcpZonedDateTimeSerializer())
            .addDeserializer(ZonedDateTime.class, new JacksonConfiguration.LcpZonedDateTimeDeserializer());

        oj.registerModule(javaTimeModule);

        DEFAULT_VALUE.put(int.class, 0);
        DEFAULT_VALUE.put(byte.class, (byte) 0);
        DEFAULT_VALUE.put(short.class, (short) 0);
        DEFAULT_VALUE.put(float.class, 0f);
        DEFAULT_VALUE.put(boolean.class, false);
        DEFAULT_VALUE.put(double.class, 0D);
        DEFAULT_VALUE.put(char.class, (char) 0);
        DEFAULT_VALUE.put(long.class, 0L);

        // init date_count_function
        for (ChronoField value : ChronoField.values()) {
            DATE_COUNT_FUNCTION.put(value.toString().toLowerCase(), t -> (long)t.get(value));
        }
        DATE_COUNT_FUNCTION.put("dayofquarter", t -> (long)t.get(IsoFields.DAY_OF_QUARTER));
        DATE_COUNT_FUNCTION.put("quarterofyear", t -> (long)t.get(IsoFields.QUARTER_OF_YEAR));
        DATE_COUNT_FUNCTION.put("monthofquarter", t -> {
            int i = t.get(ChronoField.MONTH_OF_YEAR);
            return (long) (i % 3 == 0 ? 3 : i % 3);
        });
    }

    private CommonFunctionUtil() {}

    //*********** 时间 **************
    @SuppressWarnings("unchecked")
    public static <T extends Temporal> T alterDateTime(T temporal, String addOrSub, Long amount, String unit) {
        if (Stream.of(temporal, addOrSub, amount, unit).anyMatch(Objects::isNull)){
            HttpCodeException exception = new HttpCodeException("param is null");
            exception.setMessage("param is null");
            throw exception;
        }
        //前端返回的单位都是不带s的，需要拼接上s
        amount = "Increase".equals(addOrSub) ? amount : -amount;
        if ("quarters".equalsIgnoreCase(unit + "s")) {
            return (T) temporal.plus(amount * 3L, ChronoUnit.MONTHS);
        }

        ChronoUnit chronoUnit = CHRONOS_UNIT_MAP.get(unit.toLowerCase() + "s");
        if (chronoUnit == null) {
            HttpCodeException exception = new HttpCodeException("temporal type do not support");
            exception.setMessage("temporal type do not support");
            throw exception;
        }

        return (T) temporal.plus(amount, chronoUnit);
    }

    public static Long getDateCount(TemporalAccessor temporalAccessor, String type) {
        return getDateCount(temporalAccessor, type, null);
    }

    public static Long getDateCount(TemporalAccessor temporalAccessor, String type, String zoneIdStr) {
        if (temporalAccessor instanceof ZonedDateTime){
            // 时区转换
            ZonedDateTime zonedDateTime = (ZonedDateTime) temporalAccessor;
            temporalAccessor = zonedDateTime.withZoneSameInstant(getZoneFromGlobalOrDefault(zoneIdStr));
        }

        if (Stream.of(temporalAccessor, type).anyMatch(Objects::isNull)){
            HttpCodeException exception = new HttpCodeException("param is null");
            exception.setMessage("param is null");
            throw exception;
        }
        String[] split = type.split("-");
        String leftType = split[0];
        String rightType = split[1];
        // 第一周最小天数 和 每周的第一天 默认都是 1
        if ("week".equals(leftType)){
            WeekFields weekFields = WeekFields.of(DayOfWeek.of(Math.toIntExact(1)), 1);
            switch (rightType) {
                case "month":
                    return (long)temporalAccessor.get(weekFields.weekOfMonth());
                case "quarter":
                    return getWeekOfQuarter(temporalAccessor, 1L);
                case "year":
                    return (long)temporalAccessor.get(weekFields.weekOfYear());
                default:
                    // do nothing
            }
        }

        return DATE_COUNT_FUNCTION.getOrDefault(leftType.toLowerCase() + "of" + rightType.toLowerCase(), t -> {
            HttpCodeException exception = new HttpCodeException("temporal type do not support");
            exception.setMessage("temporal type do not support");
            throw exception;
        }).apply(temporalAccessor);
    }

    public static Long getWeekOfQuarter(TemporalAccessor temporalAccessor, Long fastDayInWeek){
        WeekFields weekFields = WeekFields.of(DayOfWeek.of(Math.toIntExact(fastDayInWeek)), 1);
        // 先获取当前时间所属季度的第一周在当年的周数，再获取当前时间在当年的周数，两者相减就是weekOfQuarter
        int firstMonth = ((temporalAccessor.get(ChronoField.MONTH_OF_YEAR) + 2) / 3 - 1) * 3 + 1;
        String firstMonthStr = firstMonth < 10 ? "0" + firstMonth : "" + firstMonth;
        LocalDate firstWeekInQuarter = LocalDate.parse(temporalAccessor.get(ChronoField.YEAR) + "-" + firstMonthStr + "-" + "01");
        int firstWeekInQuarterOfYear = firstWeekInQuarter.get(weekFields.weekOfYear());
        int currWeekOfYear = temporalAccessor.get(weekFields.weekOfYear());
        return (long)(currWeekOfYear - firstWeekInQuarterOfYear + 1);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Temporal> List<T> getSpecificDaysOfWeek(T start, T end, List<Long> specificDays){
        // 之后date、time作为参数时才会走到这，dateTime一定有时区参数会直接调getSpecificDaysOfWeek(T start, T end, List<Long> specificDays, String zoneIdStr)
        return getSpecificDaysOfWeek(start, end, specificDays, null);
    }


    @SuppressWarnings("unchecked")
    public static <T extends Temporal> List<T> getSpecificDaysOfWeek(T start, T end, List<Long> specificDays, String zoneIdStr){
        if (Stream.of(start, end, specificDays).anyMatch(Objects::isNull)){
            HttpCodeException exception = new HttpCodeException("param is null");
            exception.setMessage("param is null");
            throw exception;
        }
        // specificDays为空，直接返回空列表
        if (specificDays.isEmpty()){
            return Collections.emptyList();
        }
        LocalDate startLocalDate;
        LocalDate endLocalDate;
        if (start instanceof ZonedDateTime){
            // ZonedDateTime转化为指定时区
            ZoneId zoneId = getZoneFromGlobalOrDefault(zoneIdStr);
            startLocalDate = ((ZonedDateTime) start).withZoneSameInstant(zoneId).toLocalDate();
            endLocalDate = ((ZonedDateTime) end).withZoneSameInstant(zoneId).toLocalDate();
        } else if (start instanceof LocalDate){
            // LocalDate没有时区信息，直接计算
            startLocalDate = (LocalDate) start;
            endLocalDate = (LocalDate) end;
        } else {
            HttpCodeException exception = new HttpCodeException("temporal type do not support");
            exception.setMessage("temporal type do not support");
            throw exception;
        }
        List<T> list = new ArrayList<>();
        LocalDate endIndex = endLocalDate.plus(1, ChronoUnit.DAYS);

        for (LocalDate index = startLocalDate; index.isBefore(endIndex); index = index.plus(1, ChronoUnit.DAYS)){
            if (specificDays.contains((long)index.get(ChronoField.DAY_OF_WEEK))){
                if (start instanceof ZonedDateTime){
                    list.add((T) ZonedDateTime.of(index, LocalTime.MIN, getZoneFromGlobalOrDefault(zoneIdStr)));
                } else {
                    list.add((T) index);
                }
            }
        }
        return list;
    }

    private static ZoneId getZoneFromGlobalOrDefault() {
        String timeZone = GlobalContext.getTimeZone();
        return timeZone == null ? ZoneId.systemDefault() : ZoneId.of(GlobalContext.getTimeZone());
    }

    public static ZoneId getZoneFromGlobalOrDefault(String zoneIdStr) {
        if (zoneIdStr != null){
            // zoneIdStr 有三种情况：1. "global" 2. "user" 3. 具体的某个时区
            // global 表示使用系统参数配置中配置的时区，有两种情况：1. user 表示用户时区 2. 具体的某个时区，翻译的时候会将系统参数配置中配置的时区写到配置文件
            if ("global".equals(zoneIdStr)){
                return getTimeZoneFromSystemConfiguration();
            }
            zoneIdStr = "user".equals(zoneIdStr) ? GlobalContext.getTimeZone() : zoneIdStr;
            // 定时任务GlobalContext.getTimeZone()为空，使用系统默认时区
            zoneIdStr = zoneIdStr == null ? ZoneId.systemDefault().toString() : zoneIdStr;
            return ZoneId.of(zoneIdStr);
        } else {
            // zoneIdStr 为空保持和3.3逻辑一致，先获取到系统参数配置的时区，有两种情况1.user 表示用户时区 2. 具体的某个时区
            // user时使用Header中配置的时区
            return getTimeZoneFromSystemConfiguration();
        }
    }

    public static ZoneId getTimeZoneFromSystemConfiguration() {
        String zoneIdStr = environment.getProperty("custom.system.time-zone");
        if ("user".equals(zoneIdStr)){
            // 用户本地时区必须要前端传过来，没有的话就抛出异常，避免运行结果不符合预期
            return ZoneId.of(GlobalContext.getTimeZone() == null ? ZoneId.systemDefault().toString() : GlobalContext.getTimeZone());
        } else {
            return ZoneId.of(zoneIdStr == null ? ZoneId.systemDefault().toString() : zoneIdStr);
        }
    }

    public static ZonedDateTime convertTimezone(ZonedDateTime dateTime, String toTimeZone){
        return dateTime.withZoneSameInstant(ZoneId.of(toTimeZone));
    }

    public static ZonedDateTime currDateTime() {
        return ZonedDateTime.now(getZoneFromGlobalOrDefault(null));
    }

    public static LocalDate currDate() {
        return ZonedDateTime.now(getZoneFromGlobalOrDefault(null)).toLocalDate();
    }

    public static LocalTime currTime() {
        return ZonedDateTime.now(getZoneFromGlobalOrDefault(null)).toLocalTime();
    }

    public static ZonedDateTime currDateTime(String zoneIdStr) {
        return ZonedDateTime.now(getZoneFromGlobalOrDefault(zoneIdStr));
    }

    public static LocalDate currDate(String zoneIdStr) {
        return ZonedDateTime.now(getZoneFromGlobalOrDefault(zoneIdStr)).toLocalDate();
    }

    public static LocalTime currTime(String zoneIdStr) {
        return ZonedDateTime.now(getZoneFromGlobalOrDefault(zoneIdStr)).toLocalTime();
    }

    public static String dateTimeToString(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static ZonedDateTime stringToDateTime(String dateTimeStr) {
        return ZonedDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static String dateToString(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ISO_DATE);
    }

    public static LocalDate stringToDate(String dateStr) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
    }

    public static LocalDate date(Long year, Long month, Long day) {
        return LocalDate.of(year.intValue(), month.intValue(), day.intValue());
    }

    public static LocalDate lastDayOfMonth(LocalDate localDate) {
        return localDate.with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDate lastDayOfMonth() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    public static ZonedDateTime dateToDateTime(LocalDate localDate) {
        return localDate.atStartOfDay(ZoneOffset.UTC);
    }

    public static LocalDate dateTimeToDate(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toLocalDate();
    }

    public static ZonedDateTime plusYears(ZonedDateTime zonedDateTime, Long years) {
        return zonedDateTime.plusYears(years);
    }

    public static ZonedDateTime plusMonths(ZonedDateTime zonedDateTime, Long months) {
        return zonedDateTime.plusMonths(months);
    }

    public static ZonedDateTime plusDays(ZonedDateTime zonedDateTime, Long days) {
        return zonedDateTime.plusDays(days);
    }

    public static ZonedDateTime plusHours(ZonedDateTime zonedDateTime, Long hours) {
        return zonedDateTime.plusHours(hours);
    }

    public static ZonedDateTime plusMinutes(ZonedDateTime zonedDateTime, Long minutes) {
        return zonedDateTime.plusMinutes(minutes);
    }

    public static ZonedDateTime plusSeconds(ZonedDateTime zonedDateTime, Long seconds) {
        return zonedDateTime.plusSeconds(seconds);
    }

    public static LocalDate plusYears(LocalDate localDate, Long years) {
        return localDate.plusYears(years);
    }

    public static LocalDate plusMonths(LocalDate localDate, Long months) {
        return localDate.plusMonths(months);
    }

    public static LocalDate plusDays(LocalDate localDate, Long days) {
        return localDate.plusDays(days);
    }

    public static long periodBetween(LocalDate d1, LocalDate d2, String type) {
        if (DATETIME_UNIT_DAYS.equalsIgnoreCase(type)) {
            return betweenDay(d1, d2);
        } else if (DATETIME_UNIT_MONTHS.equalsIgnoreCase(type)) {
            return betweenMonth(d1, d2);
        } else if (DATETIME_UNIT_YEARS.equalsIgnoreCase(type)) {
            return betweenYear(d1, d2);
        } else if (DATETIME_UNIT_SECONDS.equalsIgnoreCase(type)) {
            return betweenSecond(d1, d2);
        } else if (DATETIME_UNIT_MINUTES.equalsIgnoreCase(type)) {
            return betweenMinute(d1, d2);
        } else if (DATETIME_UNIT_HOURS.equalsIgnoreCase(type)) {
            return betweenHour(d1, d2);
        }
        return 0;
    }

    public static long periodBetween(ZonedDateTime d1, ZonedDateTime d2, String type) {
        if (DATETIME_UNIT_DAYS.equalsIgnoreCase(type)) {
            return betweenDay(d1, d2);
        } else if (DATETIME_UNIT_MONTHS.equalsIgnoreCase(type)) {
            return betweenMonth(d1, d2);
        } else if (DATETIME_UNIT_YEARS.equalsIgnoreCase(type)) {
            return betweenYear(d1, d2);
        } else if (DATETIME_UNIT_SECONDS.equalsIgnoreCase(type)) {
            return betweenSecond(d1, d2);
        } else if (DATETIME_UNIT_MINUTES.equalsIgnoreCase(type)) {
            return betweenMinute(d1, d2);
        } else if (DATETIME_UNIT_HOURS.equalsIgnoreCase(type)) {
            return betweenHour(d1, d2);
        }
        return 0;
    }

    public static long betweenSecond(LocalDate d1, LocalDate d2) {
        return abs(ChronoUnit.SECONDS.between(d1, d2));
    }

    public static long betweenMinute(LocalDate d1, LocalDate d2) {
        return abs(ChronoUnit.MINUTES.between(d1, d2));
    }

    public static long betweenHour(LocalDate d1, LocalDate d2) {
        return abs(ChronoUnit.HOURS.between(d1, d2));
    }

    public static long betweenDay(LocalDate d1, LocalDate d2) {
        return abs(ChronoUnit.DAYS.between(d1, d2));
    }

    public static long betweenMonth(LocalDate d1, LocalDate d2) {
        return abs(ChronoUnit.YEARS.between(d1, d2));
    }

    public static long betweenYear(LocalDate d1, LocalDate d2) {
        return abs(ChronoUnit.YEARS.between(d1, d2));
    }

    public static long betweenSecond(ZonedDateTime d1, ZonedDateTime d2) {
        return abs(ChronoUnit.SECONDS.between(d1, d2));
    }

    public static long betweenMinute(ZonedDateTime d1, ZonedDateTime d2) {
        return abs(ChronoUnit.MINUTES.between(d1, d2));
    }

    public static long betweenHour(ZonedDateTime d1, ZonedDateTime d2) {
        return abs(ChronoUnit.HOURS.between(d1, d2));
    }

    public static long betweenDay(ZonedDateTime d1, ZonedDateTime d2) {
        return abs(ChronoUnit.DAYS.between(d1, d2));
    }

    public static long betweenMonth(ZonedDateTime d1, ZonedDateTime d2) {
        return abs(ChronoUnit.MONTHS.between(d1, d2));
    }

    public static long betweenYear(ZonedDateTime d1, ZonedDateTime d2) {
        return abs(ChronoUnit.YEARS.between(d1, d2));
    }

    public static Long year(ZonedDateTime zonedDateTime) {
        return (long) zonedDateTime.getYear();
    }

    public static Long month(ZonedDateTime zonedDateTime) {
        return (long) zonedDateTime.getMonth().getValue();
    }

    public static Long day(ZonedDateTime zonedDateTime) {
        return (long) zonedDateTime.getDayOfMonth();
    }

    public static Long hour(ZonedDateTime zonedDateTime) {
        return (long) zonedDateTime.getHour();
    }

    public static Long minute(ZonedDateTime zonedDateTime) {
        return (long) zonedDateTime.getMinute();
    }

    public static Long second(ZonedDateTime zonedDateTime) {
        return (long) zonedDateTime.getSecond();
    }

    public static String formatDate(LocalDate date, String formatter) {
        if (formatter == null || "".equals(formatter.trim())) {
            formatter = DATETIME_FORMAT_YYYYMMDD;
        }
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern(formatter);
        return date.format(pattern);
    }

    public static String formatDateTime(ZonedDateTime dateTime, String formatter) {
        if (formatter == null || "".equals(formatter.trim())) {
            formatter = DATETIME_FORMAT_YYYYMMDD_HHMMSS;
        }
        dateTime = dateTime.withZoneSameInstant(getZoneFromGlobalOrDefault(null));
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern(formatter);
        return dateTime.format(pattern);
    }
    public static String formatDateTime(ZonedDateTime dateTime, String formatter, String zoneIdStr) {
        if (formatter == null || "".equals(formatter.trim())) {
            formatter = DATETIME_FORMAT_YYYYMMDD_HHMMSS;
        }
        dateTime = dateTime.withZoneSameInstant(getZoneFromGlobalOrDefault(zoneIdStr));
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern(formatter);
        return dateTime.format(pattern);
    }

    public static LocalDate addDays(LocalDate date, Long amount) {
        return date.plusDays(amount);
    }

    public static ZonedDateTime addDays(ZonedDateTime dateTime, Long amount) {
        return dateTime.plusDays(amount);
    }

    public static LocalDate subDays(LocalDate date, Long amount) {
        return date.minusDays(amount);
    }

    public static ZonedDateTime subDays(ZonedDateTime dateTime, Long amount) {
        return dateTime.minusDays(amount);
    }

    public static LocalDate addMonths(LocalDate date, Long amount) {
        return date.plusMonths(amount);
    }

    public static ZonedDateTime addMonths(ZonedDateTime dateTime, Long amount) {
        return dateTime.plusMonths(amount);
    }

    public static LocalDate subMonths(LocalDate date, Long amount) {
        return date.minusMonths(amount);
    }

    public static ZonedDateTime subMonths(ZonedDateTime dateTime, Long amount) {
        return dateTime.minusMonths(amount);
    }

    //*********** 字符串 **************
    public static String concat(Object... objs) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : objs) {
            String str = null;
            if (obj instanceof BaseEnum) {
                str = String.valueOf((((BaseEnum) obj).getCode()));
            } else if (null != obj) {
                str = obj.toString();
            }
            sb.append(str);
        }

        return sb.toString();
    }

    public static String toLower(String str) {
        return str.toLowerCase();
    }

    public static String toUpper(String str) {
        return str.toUpperCase();
    }

    public static String trim(String str) {
        return str.trim();
    }

    public static Long indexOf(String str, String search, Long fromIndex, boolean ignoreCase) {
        fromIndex = fromIndex < 0 ? 0 : fromIndex;
        if(StringUtils.isEmpty(str)) {
            return -1L;
        }
        if(ignoreCase) {
            str = str.toUpperCase();
            search = search.toUpperCase();
        }
        return (long) str.indexOf(search, fromIndex.intValue());
    }

    public static Long lastIndexOf(String str, String search, boolean ignoreCase) {
        if(StringUtils.isEmpty(str)) {
            return -1L;
        }
        if(ignoreCase) {
            str = str.toUpperCase();
            search = search.toUpperCase();
        }
        return (long) str.lastIndexOf(search);
    }

    public static String replace(String str, String search, String replace) {
        if(StringUtils.isEmpty(str)) {
            return str;
        }
        return str.replace(search, replace);
    }

    public static String subString(String str, Long start, Long length) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        start = start < 0 ? 0 : start;
        length = length < 0 ? 0 : length;

        if (start >= str.length()) {
            return StringUtils.EMPTY;
        }
        Long lastIndex = start + length;
        if (lastIndex > str.length()) {
            lastIndex = (long) str.length();
        }
        return str.substring(start.intValue(), lastIndex.intValue());
    }

    public static String join(List<?> array, String separator) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            Object obj = array.get(i);
            if (obj instanceof BaseEnum) {
                sb.append(((BaseEnum) obj).getCode());
            } else {
                sb.append(null == array.get(i) ? null : array.get(i).toString());
            }
            if (i < array.size() - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    public static List<String> split(String str, String separator) {
        if (str == null) {
            return Collections.emptyList();
        }
        for (String escapeStr : ESCAPE_CHARACTERS) {
            if (escapeStr.equals(separator)) {
                separator = "\\" + separator;
                break;
            }
        }
        return new ArrayList<>(Arrays.asList(str.split(separator)));
    }

    //*********** 运算 **************

    public static Long abs(Long number) {
        return Math.abs(number);
    }

    public static BigDecimal abs(BigDecimal number) {
        return number.abs();
    }

    //幂
    public static BigDecimal power(BigDecimal number, Long count) {
        return number.pow(count.intValue());
    }
    public static BigDecimal round(BigDecimal number) {
        return number.setScale(0, BigDecimal.ROUND_HALF_UP);
    }
    public static BigDecimal sqrt(Long number) {
        return new BigDecimal(Math.sqrt(number));
    }

    public static BigDecimal sqrt(BigDecimal number) {
        int scale = 3;
        // Obtain the first approximation
        BigDecimal x = number.divide(BigDecimal.valueOf(3), scale, BigDecimal.ROUND_DOWN);
        BigDecimal lastX = BigDecimal.valueOf(0);
        // Proceed through 50 iterations
        for (int i = 0; i < 50; i++) {
            x = number.add(x.multiply(x)).divide(x.multiply(TWO), scale, BigDecimal.ROUND_DOWN);
            if (x.compareTo(lastX) == 0) {
                break;
            }
            lastX = x;
        }
        return x;
    }

    public static Long sum(Long... args) {
        Long res = 0L;
        for (Long item : args) {
            res = res + item;
        }
        return res;
    }

    public static BigDecimal sum(BigDecimal... args) {
        BigDecimal bigDecimal = new BigDecimal(0);
        for (BigDecimal item : args) {
            bigDecimal = bigDecimal.add(item);
        }
        return bigDecimal;
    }

    public static BigDecimal avg(Long... args) {
        Long res = 0L;
        for (Long item : args) {
            res = res + item;
        }
        return new BigDecimal(((double) res) / args.length);
    }

    public static BigDecimal avg(BigDecimal... args) {
        BigDecimal bigDecimal = new BigDecimal(0);
        for (BigDecimal item : args) {
            bigDecimal = bigDecimal.add(item);
        }
        return bigDecimal.divide(new BigDecimal(args.length), 2, BigDecimal.ROUND_HALF_UP);
    }

    public static Long count(Object... args) {
        return (long) args.length;
    }

    public static Long count(Collection args) {
        return (long) args.size();
    }

    public static Long max(Long a, Long b) {
        return Math.max(a, b);
    }
    public static BigDecimal max(BigDecimal a, BigDecimal b) {
        return a.max(b);
    }

    public static Long min(Long a, Long b) {
        return Math.min(a, b);
    }
    public static BigDecimal min(BigDecimal a, BigDecimal b) {
        return a.min(b);
    }
    public static BigDecimal sign(BigDecimal a) {
        return new BigDecimal(a.signum());
    }

    //---------- list -------

    public static <T> void add(List<T> list, T object) {
        list.add(object);
    }

    public static <T> T get(List<T> list, Long index) {
        return list.get(index.intValue());
    }

    public static <T> T set(List<T> list, Long index, T t) {
        return list.set(index.intValue(), t);
    }

    public static <T> void insert(List<T> list, Long index, T t) {
        list.add(index.intValue(), t);
    }

    public static <T> T removeAt(List<T> list, Long index) {
        return list.remove(index.intValue());
    }

    public static <T> void remove(List<T> list, T t) {
        list.remove(t);
    }

    public static <T> boolean contains(List<T> list, T t) {
        return list.contains(t);
    }

    public static <T> void listSort(List<T> list, Function<T, ? extends Comparable> bys, Boolean isAsc) {

        if (bys == null) {
            Collections.sort(list,null);
        }else {
            Comparator co = Comparator.comparing(bys);
            list.sort(co);
        }

        if (!Boolean.TRUE.equals(isAsc)) {
            Collections.reverse(list);
        }

    }

    public static <T> Long addAll(List<T> list, List<T> addlist) {
        list.addAll(addlist);
        return (long) list.size();
    }

    // 左闭右开，暂时不考虑索引越界异常处理
    public static <T> List<T> listSlice(List<T> list, Long start, Long end) {
        try {
            return list.subList(start.intValue(), end.intValue());
        } catch (Exception e) {
            throw new HttpCodeException(500, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> PageOf<T> listSliceToPageOf(List<T> list, Long page, Long size) {
        return PageOf.of(listSlice(list, (page - 1) * size, Math.min(size * page, list.size())), (long) list.size(), page, size);
    }

    public static <T> void listReverse(List<T> list) {
        Collections.reverse(list);
    }

    public static <T> void listDistinct(List<T> list) {
        if (list == null) return;
        HashSet<T> set = new HashSet<>(list.size());
        List<T> res = new ArrayList<>(list.size());
        list.forEach(e -> {
            if (set.add(e)) {
                res.add(e);
            }
        });
        list.clear();
        list.addAll(res);
    }

    public static <T> T listFind(List<T> list, Predicate<T> by) {
        if (by == null) return null;
        return list.stream().filter(by).findFirst().orElse(null);
    }

    public static <T> List<T> listFindAll(List<T> list, Predicate<T> by) {
        if (by == null) return null;
        return list.stream().filter(by).collect(Collectors.toList());
    }

    public static <T> Long listFindIndex(List<T> list, Predicate<T> by) {
        if (by == null) {
            return -1L;
        }
        return (long) list.stream().filter(by).findFirst().map(list::indexOf).orElse(-1);
    }

    @SuppressWarnings("all")
    @SafeVarargs
    public static <T> void listSort(List<T> list, String sort, Function<T, ? extends Comparable>... bys) {
        Comparator co = null;
        if (bys != null) {
            Iterator<Function<T, ? extends Comparable>> iterator = Arrays.stream(bys).iterator();
            co = Comparator.comparing(iterator.next());
            while (iterator.hasNext() && iterator.next() != null) {
                co.thenComparing(iterator.next());
            }
        }
        list.sort(co);

        if ("desc".equalsIgnoreCase(sort)) {
            Collections.reverse(list);
        }
    }

    //---------- stay same with frontend -------

    public static <T> T New(Class<T> clazz) {
        try {
            /**
             * @since Feature-16095: 暂时只支持ArrayList & HashMap;
             */
            if(List.class.equals(clazz)) {
                return (T) new ArrayList<>();
            }
            if(Map.class.equals(clazz)) {
                return (T) new HashMap<>();
            }
            return clazz.newInstance();
        } catch (Exception e) {
            throw new HttpCodeException(500, e);
        }
    }

    public static <T> T newWithInitiation(T obj, Consumer<? super T> action) {
        if (null != obj) {
            action.accept(obj);
        }

        return obj;
    }

    public static <T> T clone(T object) {
        if (null == object) {
            return null;
        }

        try {
            String json = oj.writeValueAsString(object);
            Class<T> clazz = (Class<T>) object.getClass();
            return oj.readValue(json, clazz);
        } catch (Exception e) {
            throw new HttpCodeException(500, e);
        }
    }

    public static Object merge(Object o1, Object o2) {
        // TODO 这个方法实现不是merge的语义
        if (null != o1) {
            return o1;
        } else {
            return o2;
        }
    }

    public static <T> T clearObject(T o) {
        return clear(o);
    }

    public static <T> T clear(T o) {
        if (o == null) {
            return o;
        }
        if (o instanceof List) {
            ((List) o).clear();
        } else if (o instanceof Set) {
            ((Set) o).clear();
        } else if (o.getClass().isArray()) {
            clearArray(o);
        } else if (o instanceof Map) {
            ((Map) o).clear();
        } else {
            clearObjectField(o);
        }
        return o;
    }

    private static void clearObjectField(Object o) {
        Field[] fields = o.getClass().getDeclaredFields();
        if (fields.length <= 0) {
            return;
        }
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                field.set(o, DEFAULT_VALUE.get(field.getType()));
            } catch (IllegalAccessException e) {
                throw new HttpCodeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
            }
        }
    }

    private static void clearArray(Object o) {
        Class<?> type = o.getClass().getComponentType();
        for (int i = 0; i < Array.getLength(o); i ++) {
            Array.set(o, i, DEFAULT_VALUE.get(type));
        }
    }

    //----------- 枚举 -----------
    public static String enumValueToText(BaseEnum tEnum, Class<? extends BaseEnum> tClass) {
        if (null == tEnum || null == tClass) {
            return "";
        }

        BaseEnum[] baseEnums = tClass.getEnumConstants();
        for (BaseEnum baseEnum : baseEnums) {
            if (tEnum.getCode().equals(baseEnum.getCode())) {
                return baseEnum.getDesc();
            }
        }

        return "";
    }

    public static <T extends BaseEnum> T toEnumItem(String str, Class<T> enumClass) {
        T[] baseEnums = enumClass.getEnumConstants();

        if (ArrayUtils.isEmpty(baseEnums)) {
            return null;
        }

        for (T tEnum : baseEnums) {
            if (String.valueOf(tEnum.getCode()).equals(str)) {
                return tEnum;
            }
        }

        return null;
    }

    public static <T extends BaseEnum> T toEnumItem(Long code, Class<T> enumClass) {
        T[] baseEnums = enumClass.getEnumConstants();

        if (ArrayUtils.isEmpty(baseEnums)) {
            return null;
        }

        for (T tEnum : baseEnums) {
            if (tEnum.getCode().equals(code)) {
                return tEnum;
            }
        }

        return null;
    }

    public static List<AnonymousEnumStructure> enumToList(Class<? extends BaseEnum> tClass) {
        BaseEnum[] baseEnums = tClass.getEnumConstants();

        if (!ArrayUtils.isEmpty(baseEnums)) {
            List<AnonymousEnumStructure> result = new ArrayList(baseEnums.length);
            for (BaseEnum baseEnum : baseEnums) {
                AnonymousEnumStructure enumStructure = new AnonymousEnumStructure();
                enumStructure.setText(baseEnum.getDesc());
                enumStructure.setValue(null == baseEnum.getCode() ? null : String.valueOf(baseEnum.getCode()));
                result.add(enumStructure);
            }

            return result;
        }

        return Collections.emptyList();
    }

    public static List<AnonymousLongEnumStructure> longEnumToList(Class<? extends BaseEnum> tClass) {
        BaseEnum[] baseEnums = tClass.getEnumConstants();

        if (!ArrayUtils.isEmpty(baseEnums)) {
            List<AnonymousLongEnumStructure> result = new ArrayList(baseEnums.length);
            for (BaseEnum baseEnum : baseEnums) {
                AnonymousLongEnumStructure enumStructure = new AnonymousLongEnumStructure();
                enumStructure.setText(baseEnum.getDesc());
                enumStructure.setValue(null == baseEnum.getCode() ? null : Long.valueOf(String.valueOf(baseEnum.getCode())));
                result.add(enumStructure);
            }

            return result;
        }

        return Collections.emptyList();
    }

    //----------- 类型转换 -----------

    @SuppressWarnings("unchecked")
    public static <T> T convert(Object o, Class<T> clazz) {
        try {
            T result = null;
            if (clazz == Boolean.class) {
                result = clazz.cast(convertBoolean(o));
            } else if (clazz == Integer.class) {
                result = clazz.cast(convertInteger(o));
            } else if (clazz == Long.class) {
                result = clazz.cast(convertLong(o));
            } else if (clazz == String.class) {
                result = clazz.cast(convertString(o));
            } else if (clazz == BigDecimal.class) {
                result = clazz.cast(convertBigDecimal(o));
            } else if (clazz == LocalDate.class) {
                result = clazz.cast(convertLocalDate(o));
            } else if (clazz == ZonedDateTime.class) {
                result = clazz.cast(convertZonedDateTime(o));
            } else if (clazz == LocalTime.class) {
                result = clazz.cast(convertLocalTime(o));
            } else if (clazz == Double.class) {
                result = clazz.cast(convertDouble(o));
            }
            return result;
        } catch (Exception e) {
            throw new HttpCodeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    private static Boolean convertBoolean(Object o) {
        Boolean result = false;
        if (o instanceof Integer) {
            // IntegerToBoolean
            int n = (int) o;
            return n != 0;
        } else if (o instanceof BigDecimal) {
            // BigDecimalToBoolean
            BigDecimal decimal = (BigDecimal) o;
            return decimal.compareTo(BigDecimal.ZERO) != 0;
        } else if (o instanceof Long) {
            // LongToBoolean
            long n = (long) o;
            return n != 0;
        } else if (o instanceof Double) {
            // DoubleToBoolean
            double d = (double) o;
            return d != 0;
        }
        return result;
    }

    private static Integer convertInteger(Object o) {
        Integer result = null;
        if (o instanceof Boolean) {
            // BooleanToInteger
            int temp = (boolean) o ? 1 : 0;
            result = Integer.valueOf(temp);
        } else if (o instanceof Long) {
            // LongToInteger
            result = Integer.valueOf(((Long) o).intValue());
        } else if (o instanceof String) {
            // StringToInteger
            result = Integer.valueOf(Integer.parseInt((String)o));
        } else if (o instanceof BigDecimal) {
            // BigDecimalToInteger
            result = Integer.valueOf(((BigDecimal) o).intValue());
        } else if (o instanceof Double) {
            // DoubleToInteger
            result = Integer.valueOf(((Double) o).intValue());
        }
        return result;
    }

    private static Long convertLong(Object o) {
        Long result = null;
        if (o instanceof Boolean) {
            // BooleanToInteger
            long temp = (boolean) o ? 1 : 0;
            result = Long.valueOf(temp);
        } else if (o instanceof Long) {
            // LongToLong
            result = Long.valueOf(((Long) o).longValue());
        } else if (o instanceof BigDecimal) {
            // BigDecimalToLong
            result = Long.valueOf(((BigDecimal) o).longValue());
        } else if (o instanceof String) {
            // StringToLong
            result = Long.valueOf(Long.parseLong((String) o));
        } else if (o instanceof Integer) {
            // IntegerToLong
            result = Long.valueOf(((Integer) o).longValue());
        } else if (o instanceof Double) {
            // DoubleToLong
            result = Long.valueOf(((Double) o).longValue());
        } else if (o instanceof ZonedDateTime) {
            // ZonedDateTimeToLong
            result = Long.valueOf(((ZonedDateTime) o).toInstant().toEpochMilli());
        }
        return result;
    }

    private static String convertString(Object o) {
        String result = null;
        if (o instanceof Boolean) {
            // BooleanToString
            result = o.toString();
        } else if (o instanceof Integer) {
            // IntegerToString
            result = o.toString();
        } else if (o instanceof Long) {
            // LongToString
            result = o.toString();
        } else if (o instanceof String) {
            // StringToString(TimeToString, StringToTime)
            result = (String) o;
        } else if (o instanceof BigDecimal) {
            // BigDecimalToString
            result = o.toString();
        } else if (o instanceof LocalDate) {
            // LocalDateToString
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern(DATETIME_FORMAT_YYYYMMDD);
            result = ((LocalDate) o).format(pattern);
        } else if (o instanceof ZonedDateTime) {
            // ZonedDateTimeToString
            ZonedDateTime dateTime = ((ZonedDateTime) o).withZoneSameInstant(ZoneId.systemDefault());
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern(DATETIME_FORMAT_YYYYMMDD_HHMMSS);
            result = dateTime.format(pattern);
        } else if (o instanceof LocalTime) {
            // LocalTimeToString
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern(DATETIME_FORMAT_HHMMSS);
            result = ((LocalTime) o).format(pattern);
        } else if (o instanceof Double) {
            // DoubleToString
            result = o.toString();
        }  else if (o instanceof BaseEnum) {
            result = ((BaseEnum<?, String>) o).getCode();
        }
        return result;
    }

    private static BigDecimal convertBigDecimal(Object o) {
        BigDecimal result = null;
        if (o instanceof String) {
            // StringToBigDecimal
            result = new BigDecimal(o.toString());
        }

        if (o instanceof Number) {
            // 数值转BigDecimal
            result = new BigDecimal(o.toString());
        }
        return result;
    }

    private static LocalDate convertLocalDate(Object o) {
        LocalDate result = null;
        if (o instanceof String) {
            // StringToLocalDate
            String s = o.toString();
            String pattern;
            if (s.contains("-")) {
                pattern = DATETIME_FORMAT_YYYYMMDD;
            } else if (s.contains("/")) {
                pattern = DATETIME_FORMAT_YYYYMMDD2;
            } else if (s.contains(".")) {
                pattern = "yyyy.MM.dd";
            } else {
                throw new HttpCodeException(400, ErrorCodeEnum.ILLEGAL_DATE.code, s);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            result = LocalDate.parse(s, formatter);
            if (!formatter.format(result).equals(s)) {
                throw new HttpCodeException(400, ErrorCodeEnum.ILLEGAL_DATE.code, s);
            }
        } else if (o instanceof ZonedDateTime) {
            // ZonedDateTimeToLocalDate
            result = ((ZonedDateTime) o).toLocalDate();
        }
        return result;
    }

    private static ZonedDateTime convertZonedDateTime(Object o) {
        ZonedDateTime result = null;
        if (o instanceof String) {
            // StringToZonedDateTime
            String s = o.toString();
            String pattern;
            if (s.contains("-")) {
                pattern = DATETIME_FORMAT_YYYYMMDD_HHMMSS;
            } else if (s.contains("/")) {
                pattern = DATETIME_FORMAT_YYYYMMDD2_HHMMSS;
            } else if (s.contains(".")) {
                pattern = DATETIME_FORMAT_YYYYMMDD3_HHMMSS;
            } else {
                throw new HttpCodeException(400, ErrorCodeEnum.ILLEGAL_DATETIME.code, s);
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault());
            result = ZonedDateTime.parse(s, formatter);

            if (!formatter.format(result).equals(s)) {
                throw new HttpCodeException(400, ErrorCodeEnum.ILLEGAL_DATETIME.code, s);
            }
        } else if (o instanceof LocalDate) {
            // LocalDateToZonedDateTime
            result = ((LocalDate) o).atStartOfDay(ZoneId.systemDefault());
        } else if (o instanceof Long) {
            // LongToZonedDateTime
            result = ZonedDateTime.ofInstant(Instant.ofEpochMilli((Long) o), ZoneId.systemDefault());
        }
        return result;
    }

    private static LocalTime convertLocalTime(Object o) {
        LocalTime result = null;
        if (o instanceof String) {
            // StringToLocalDate
            String s = o.toString();
            String pattern;
            if (s.contains(":")) {
                pattern = DATETIME_FORMAT_HHMMSS;
            } else {
                throw new HttpCodeException(400, ErrorCodeEnum.ILLEGAL_DATETIME.code, s);
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            result = LocalTime.parse(s, formatter);

            if (!formatter.format(result).equals(s)) {
                throw new HttpCodeException(400, ErrorCodeEnum.ILLEGAL_DATETIME.code, s);
            }
        } else if (o instanceof ZonedDateTime) {
            // ZonedDateTimeToLocalDate
            result = ((ZonedDateTime) o).toLocalTime();
        }
        return result;
    }

    private static Double convertDouble(Object o) {
        Double result = null;
        if (o instanceof String) {
            // StringToDouble
            result = Double.valueOf((String) o);
        } else if (o instanceof Integer) {
            // IntegerToDouble
            result = Double.valueOf(((Integer) o).doubleValue());
        } else if (o instanceof Long) {
            // LongToDouble
            result = Double.valueOf(((Long) o).doubleValue());
        } else if (o instanceof BigDecimal) {
            result = ((BigDecimal) o).doubleValue();
        }
        return result;
    }

    public static Long dateDiff(ZonedDateTime dateTime1, ZonedDateTime dateTime2, String calcType, Boolean needAbs) {
        long calculateAns;
        switch (calcType) {
            case CALC_TYPE_YEAR:
                calculateAns = dateTime1.until(dateTime2, ChronoUnit.YEARS);
                break;
            case CALC_TYPE_MONTH:
                calculateAns = dateTime1.until(dateTime2, ChronoUnit.MONTHS);
                break;
            case CALC_TYPE_DAY:
                calculateAns =  dateTime1.until(dateTime2, ChronoUnit.DAYS);
                break;
            case CALC_TYPE_WEEK:
                calculateAns =  dateTime1.until(dateTime2, ChronoUnit.WEEKS);
                break;
            case CALC_TYPE_QUARTER:
                //先看间隔年
                long gapYear = dateTime1.until(dateTime2, ChronoUnit.YEARS);

                int leftNumber = (dateTime1.getMonthValue() - 1) / 3 + 1;
                int rightNumber = (dateTime2.getMonthValue() - 1) / 3 + 1;
                calculateAns =  gapYear * 4L + (leftNumber - rightNumber);
                break;
            case CALC_TYPE_HOUR:
                calculateAns =  dateTime1.until(dateTime2, ChronoUnit.HOURS);
                break;
            case CALC_TYPE_MINUTE:
                calculateAns =  dateTime1.until(dateTime2, ChronoUnit.MINUTES);
                break;
            case CALC_TYPE_SECOND:
                calculateAns =  dateTime1.until(dateTime2, ChronoUnit.SECONDS);
                break;
            default:
                return 0L;
        }
        return Boolean.TRUE.equals(needAbs) ? Math.abs(calculateAns) : calculateAns;
    }

    /**
     * 计算两个日期的间隔
     * @param left
     * @param right
     * @return 年, 月, 天, 星期, 季度
     */
    public static Long dateDiff(LocalDate left, LocalDate right, String calcType, Boolean needAbs) {
        long calculateAns;
        long leftNumber;
        long rightNumber;
        switch (calcType) {
            case CALC_TYPE_YEAR:
                calculateAns = left.until(right, ChronoUnit.YEARS);
                break;
            case CALC_TYPE_MONTH:
                calculateAns = left.until(right, ChronoUnit.MONTHS);
                break;
            case CALC_TYPE_WEEK:
                calculateAns = left.until(right, ChronoUnit.WEEKS);
                break;
            case CALC_TYPE_DAY:
                calculateAns = left.until(right, ChronoUnit.DAYS);
                break;
            case CALC_TYPE_QUARTER:
                //先看间隔年
                long gapYear = left.until(right, ChronoUnit.YEARS);

                leftNumber = (left.getMonthValue() - 1) / 3 + 1;
                rightNumber = (right.getMonthValue() - 1) / 3 + 1;
                calculateAns = gapYear * 4 + (leftNumber - rightNumber);
                break;
            case CALC_TYPE_HOUR:
                calculateAns = left.until(right, ChronoUnit.DAYS) * 24;
                break;
            case CALC_TYPE_MINUTE:
                calculateAns = left.until(right, ChronoUnit.DAYS) * 24 * 60;
                break;
            case CALC_TYPE_SECOND:
                calculateAns = left.until(right, ChronoUnit.DAYS) * 24 * 60 * 60;
                break;
            default:
                return 0L;
        }
        return Boolean.TRUE.equals(needAbs) ? Math.abs(calculateAns) : calculateAns;
    }

    /**
     * 时间间隔
     * @param left
     * @param right
     * @param calcType
     * @return 小时/分/秒
     */
    public static Long dateDiff(LocalTime left, LocalTime right, String calcType, Boolean needAbs) {
        long calculateAns;
        switch (calcType) {
            case CALC_TYPE_HOUR:
                calculateAns = left.until(right, ChronoUnit.HOURS);
                break;
            case CALC_TYPE_MINUTE:
                calculateAns = left.until(right, ChronoUnit.MINUTES);
                break;
            case CALC_TYPE_SECOND:
                calculateAns = left.until(right, ChronoUnit.SECONDS);
                break;
            default:
                return 0L;
        }
        return Boolean.TRUE.equals(needAbs) ? Math.abs(calculateAns) : calculateAns;
    }

    private static Environment environment;

    public static void registerEnv(Environment _environment) {
        environment = _environment;
    }

    /**
     * 读取yml的配置文件数据
     * @param key
     * @return
     */
    public static String getProperties(String key) {
        return environment.getProperty(USER_APP_PROPERTY_KEY_PREFIX + key);
    }

    public static <L, R> L wrapValue(L left, R right) {

        if(right instanceof Integer) {
            //int -> long
            if(left instanceof Long) {
                return (L) Long.valueOf(String.valueOf(right));
            }
            //int -> double
            if(left instanceof Double) {
                return (L) Double.valueOf(String.valueOf(right));
            }

            if(left instanceof BigDecimal) {
                return (L) new BigDecimal(String.valueOf(right));
            }
            return (L) right;
        }

        if(right instanceof Long) {
            // long -> double
            if(left instanceof Double) {
                return (L) Double.valueOf(String.valueOf(right));
            }

            if(left instanceof BigDecimal) {
                return (L) new BigDecimal(String.valueOf(right));
            }
            return (L) right;
        }
        return (L) right;
    }

    // list 转 PageOf
    public static <T> PageOf<T> createPageOf(List<T> values, Long page, Long size, Long total) {
        return PageOf.of(values, total, page, size);
    }

    // 处理实体字段和数据库字段名可能存在大小写不一致
    public static void preHandleQueryExpression(AbstractQueryFilter filter, Map<String, String> entityFiledColumnNameMap) {
        if (filter == null) {
            return;
        }
        if (filter instanceof ColumnQueryFilter) {
            String propertyName = ((ColumnQueryFilter) filter).getProperty().getName();
            String columnName = entityFiledColumnNameMap.get(propertyName);
            columnName = null == columnName ? propertyName : columnName;
            ((ColumnQueryFilter) filter).setColumnName(columnName);
            return;
        }
        preHandleQueryExpression(filter.getLeft(), entityFiledColumnNameMap);
        preHandleQueryExpression(filter.getRight(), entityFiledColumnNameMap);
    }

    /*********** Map 相关 **************/
    public static <Key, Value> Value mapGet(Map<Key, Value> map, Object key) {
        Class<Key> clazz = getMapKeyClass(map);
        if(null == clazz) {
            return null;
        }
        return map.get(convertType(clazz, key));
    }

    public static <Key, Value> void mapPut(Map<Key, Value> map, Key key, Value value) {
        map.put(key, value);
    }

    public static <Key, Value> void mapRemove(Map<Key, Value> map, Object key) {
        Class<Key> clazz = getMapKeyClass(map);
        if(null == clazz) {
            return;
        }
        map.remove(convertType(clazz, key));
    }

    public static <Key, Value> List<Key> mapKeys(Map<Key, Value> map) {
        return new ArrayList<>(map.keySet());
    }

    public static <Key, Value> List<Value> mapValues(Map<Key, Value> map) {
        return new ArrayList<>(map.values());
    }

    public static <Key, Value> Boolean mapContains(Map<Key, Value> map, Object key) {
        Class<Key> clazz = getMapKeyClass(map);
        if(null == clazz) {
            return Boolean.FALSE;
        }
        return map.containsKey(convertType(clazz, key));
    }

    public static <Key, Value> Map<Key, Value> mapFilter(Map<Key, Value> map, Predicate<Key> keyBy, Predicate<Value> valueBy) {
        Iterator<Map.Entry<Key, Value>> iterator = map.entrySet().iterator();
        Map<Key, Value> resultMap = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<Key, Value> currentEntry = iterator.next();
            Boolean keyPredicate = null == keyBy ? Boolean.TRUE : keyBy.test(currentEntry.getKey());
            Boolean valuePredicate = null == valueBy ? Boolean.TRUE : valueBy.test(currentEntry.getValue());
            if(keyPredicate && valuePredicate) {
                resultMap.put(currentEntry.getKey(), currentEntry.getValue());
            }
        }
        return resultMap;
    }

    public static Long length(Object obj) {
        if (null == obj) {
            throw new NullPointerException();
        }
        if (obj instanceof String) {
            return (long) ((String) obj).length();
        }
        if (obj instanceof Map) {
            return (long) ((Map) obj).size();
        }
        if (obj instanceof List) {
            return (long) ((List) obj).size();
        }
        throw new IllegalArgumentException();
    }

    private static <Key, Value> Class<Key> getMapKeyClass(Map<Key, Value> map) {
        if(null == map || map.isEmpty()) {
            return null;
        }
        return (Class<Key>) map.keySet().stream().collect(Collectors.toList()).get(0).getClass();
    }

    private static <Target, Source> Target convertType(Class<Target> targetClass, Source source) {
        Boolean rightIsString = source instanceof String;
        //需要的是所有String
        if (String.class.equals(targetClass)) {
            if (rightIsString) {
                return (Target) source;
            }
            return (Target) String.valueOf(source);
        }

        if (Long.class.equals(targetClass) || Integer.class.equals(targetClass) || Double.class.equals(targetClass)) {
            return convertNumberType(targetClass, source);
        }

        //需要的是Boolean
        if (Boolean.class.equals(targetClass)) {
            if(rightIsString) {
                return (Target) Boolean.valueOf((String) source);
            }
            return (Target) source;
        }
        return (Target) source;
    }

    private static <Target, Source> Target convertNumberType(Class<Target> targetClass, Source source) {
        Boolean rightIsString = source instanceof String;
        if (Integer.class.equals(targetClass)) {
            //右侧是否为String
            if (rightIsString) {
                return (Target) Integer.valueOf((String) source);
            }
            return (Target) source;
        }

        //需要的是Long
        Boolean rightIsInteger = source instanceof Integer;
        if (Long.class.equals(targetClass)) {
            //右侧为String 或 Integer
            if (rightIsString || rightIsInteger) {
                return (Target) Long.valueOf(String.valueOf(source));
            }
            return (Target) source;
        }

        //需要的是Double
        Boolean rightIsLong = source instanceof Long;
        if (Double.class.equals(targetClass)) {
            //右侧为String 或 Integer 或 Long
            if(rightIsString || rightIsLong || rightIsInteger) {
                return (Target) Double.valueOf(String.valueOf(source));
            }
            return (Target) source;
        }

        return (Target) source;
    }


    /**
     * @Since 2.17 内置函数新增
     */

    /**
     * listTransform
     * @param sourceList
     * @param function
     * @return
     * @param <R>
     * @param <S>
     */
    public static <R, S> List<R> listTransform(List<S> sourceList, Function<S, R> function) {
        if (null == function) {
            return null;
        }
        return sourceList.stream().map(function).collect(Collectors.toList());
    }

    /**
     * listFilter
     * @param sourceList
     * @param predicate
     * @return
     * @param <T>
     */
    public static <T> List<T> listFilter(List<T> sourceList, Predicate<T> predicate) {
        if (null == predicate) {
            return sourceList;
        }
        return sourceList.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * list 求最大值
     * @param sourceList
     * @return
     * @param <T>
     */
    @SuppressWarnings("All")
    public static <T> T listMax(List<T> sourceList) {
        if (null == sourceList) {
            return null;
        }
        List<T> nullRemoved = sourceList.stream().filter(item -> item != null).collect(Collectors.toList());
        return 0 == nullRemoved.size() ? null : (T) nullRemoved.stream().max(buildComparator(nullRemoved.get(0))).get();
    }

    /**
     * list 求最小值
     * @param sourceList
     * @return
     * @param <T>
     */
    @SuppressWarnings("all")
    public static <T> T listMin(List<T> sourceList) {
        if (null == sourceList) {
            return null;
        }
        List<T> nullRemoved = sourceList.stream().filter(item -> item != null).collect(Collectors.toList());
        return 0 == nullRemoved.size() ? null : (T) nullRemoved.stream().min(buildComparator(nullRemoved.get(0))).get();
    }

    /**
     * list求和
     * @param sourceList
     * @return
     * @param <T>
     */
    @SuppressWarnings("all")
    public static <T extends Number> T listSum(List<T> sourceList) {
        if (null == sourceList) {
            return null;
        }
        List<T> nullRemoved = sourceList.stream().filter(item -> item != null).collect(Collectors.toList());

        return 0 == nullRemoved.size() ? null
                : (T) nullRemoved.stream().reduce(buildSumOperator(nullRemoved.get(0))).get();
    }

    /**
     * list乘积
     * @param sourceList
     * @return
     * @param <T>
     */
    @SuppressWarnings("all")
    public static <T extends Number> T listProduct(List<T> sourceList) {
        if (null == sourceList) {
            return null;
        }
        List<T> nullRemoved = sourceList.stream().filter(item -> item != null).collect(Collectors.toList());
        return 0 == nullRemoved.size() ? null
                : (T) nullRemoved.stream().reduce(buildProductOperator(nullRemoved.get(0))).get();
    }

    /**
     * list求平均值
     * @param sourceList
     * @return
     * @param <T>
     */
    public static <T extends Number> BigDecimal listAverage(List<T> sourceList) {
        if (null == sourceList) {
            return null;
        }
        List<T> nullRemoved = sourceList.stream().filter(item -> item != null).collect(Collectors.toList());

        if (0 == nullRemoved.size()) {
            return null;
        } else {
            // 解决 0.1 + 0.2 ≠ 0.3 的精度问题，使其相等
            BigDecimal sumOftheList = new BigDecimal(
                    nullRemoved.stream().reduce(buildSumOperator(nullRemoved.get(0))).get().toString());
            return sumOftheList.divide(new BigDecimal(nullRemoved.size()));
        }
    }

    /**
     * List的去重
     * @param sourceList
     * @param
     * @return
     */
    public static <T> List<T> listDistinctBy(List<T> sourceList, List<Function<T, Object>> fns) {
        if (null == fns) {
            return null;
        }

        Map<String, T> m = new LinkedHashMap<>();
        if (sourceList == null || sourceList.size() == 0) {
            return new ArrayList<>();
        }
        for (T item : sourceList) {
            // 每个属性 toString 后求 HashCode，再拼接起来，再求 HashCode 作为 HashMap 的 key
            List<String> hashArr = fns.stream().map(fn -> fn.apply(item))
                    .map(prop -> "" + String.valueOf(prop).hashCode())
                    .collect(Collectors.toList());
            String hash = "" + String.join("", hashArr);
            if (!m.containsKey(hash)) {
                m.put(hash, item);
            }
        }

        return m.values().stream().collect(Collectors.toList());
    }

    /**
     * list 分组
     * @param sourceList
     * @param function
     * @return
     * @param <T>
     * @param <S>
     */
    @SuppressWarnings("all")
    public static <T, S> Map<S, List<T>> listGroupBy(List<T> sourceList, Function<T, S> function) {
        if (null == function) {
            return null;
        }
        return sourceList.stream().collect(Collectors.groupingBy(function));
    }

    public static <T> T listHead(List<T> sourceList) {
        if (null == sourceList || 0 == sourceList.size()) {
            return null;
        }
        return sourceList.get(0);
    }

    public static <T> T listLast(List<T> sourceList) {
        if (null == sourceList || 0 == sourceList.size()) {
            return null;
        }
        return sourceList.get(sourceList.size() - 1);
    }

    /**
     * 将list打平
     * @param sourceList
     * @return
     * @param <T>
     */
    public static <T> List<T> listFlatten(List<List<T>> sourceList) {
        if (null == sourceList || 0 == sourceList.size()) {
            return null;
        }
        return sourceList.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    /**
     * Map的transform
     * @param sourceMap
     * @param keyFunction
     * @param valueFunction
     * @return
     */
    public static <Key, Value, KeyResult, ValueResult> Map<KeyResult, ValueResult> mapTransform(Map<Key, Value> sourceMap, BiFunction<Key, Value, KeyResult> keyFunction, BiFunction<Key, Value, ValueResult> valueFunction) {
        if (null == keyFunction || null == valueFunction) {
            return null;
        }
        Map<KeyResult, ValueResult> newMap = new HashMap<>();
        sourceMap.entrySet().stream().forEach(e -> applyBiFunction(keyFunction, valueFunction, e, newMap));
        return newMap;
    }

    private static <Key, Value, KeyResult, ValueResult> void applyBiFunction(BiFunction<Key, Value, KeyResult> keyFunction, BiFunction<Key, Value, ValueResult> valueFunction,
                                                                             Map.Entry<Key, Value> entry, Map<KeyResult, ValueResult> newMap) {
        KeyResult key = keyFunction.apply(entry.getKey(), entry.getValue());
        ValueResult value = valueFunction.apply(entry.getKey(), entry.getValue());
        newMap.put(key, value);
    }

    public static <Key, Value> Map<Key, Value> mapFilter(Map<Key, Value> sourceMap, BiPredicate<Key, Value> predicate) {
        if (null == predicate) {
            return sourceMap;
        }

        Map<Key, Value> res = new LinkedHashMap<>();
        if (null == sourceMap) {
            return res;
        }
        for (Map.Entry<Key, Value> entry : sourceMap.entrySet()) {
            if (testBiPredicate(predicate, entry.getKey(), entry.getValue())) {
                res.put(entry.getKey(), entry.getValue());
            }
        }
        return res;
    }

    private static <T, U>  Boolean testBiPredicate(BiPredicate<T, U> predicate, T t, U u) {
        return predicate.test(t, u);
    }

    /**
     * list转map
     * @param sourceList
     * @return
     * @param <Key>
     */
    @SuppressWarnings("all")
    public static <Key, Value, Source> Map<Key, Value> listToMap(List<Source> sourceList, Function<Source, Key> keyFunction, Function<Source, Value> valueFunction) {
        if (null == keyFunction || null == valueFunction) {
            return (Map<Key, Value>) sourceList.stream().collect(Collectors.toMap(item -> item, item -> item, (v1, v2) -> v1));
        }

        HashMap<Key, Value> m = new LinkedHashMap<>();
        if (null == sourceList || 0 == sourceList.size()) {
            return m;
        }
        for (Source item : sourceList) {
            Key k = keyFunction.apply(item);
            if (!m.containsKey(k)) {
                m.put(k, valueFunction.apply(item));
            }
        }
        return m;
    }

    private static <T> Comparator buildComparator(T firstNode) {
        if (firstNode instanceof Integer) {
            return (Comparator<Integer>) Integer::compareTo;
        } else if (firstNode instanceof Long) {
            return (Comparator<Long>) Long::compareTo;
        } else if (firstNode instanceof Double) {
            return (Comparator<Double>) Double::compareTo;
        } else if (firstNode instanceof BigDecimal) {
            return (Comparator<BigDecimal>) BigDecimal::compareTo;
        } else if (firstNode instanceof String) {
            return (Comparator<String>) String::compareTo;
        } else if (firstNode instanceof ZonedDateTime) {
            return (Comparator<ZonedDateTime>) ZonedDateTime::compareTo;
        } else if (firstNode instanceof LocalDate) {
            return (Comparator<LocalDate>) LocalDate::compareTo;
        } else {
            return (Comparator<LocalTime>) LocalTime::compareTo;
        }
    }

    private static <T> BinaryOperator buildSumOperator(T clazz) {
        if (clazz instanceof Integer) {
            return (BinaryOperator<Integer>) Integer::sum;
        } else if (clazz instanceof Long) {
            return (BinaryOperator<Long>) Long::sum;
        } else if (clazz instanceof Double) {
            return (BinaryOperator<Double>) Double::sum;
        } else {
            return (BinaryOperator<BigDecimal>) BigDecimal::add;
        }
    }

    private static <T> BinaryOperator buildProductOperator(T clazz) {
        if (clazz instanceof Integer) {
            return (BinaryOperator<Integer>) (x, y) -> x * y;
        } else if (clazz instanceof Long) {
            return (BinaryOperator<Long>) (x, y) -> x * y;
        } else if (clazz instanceof Double) {
            return (BinaryOperator<Double>) (x, y) -> x * y;
        } else {
            return (BinaryOperator<BigDecimal>) (x, y) -> x.multiply(y);
        }
    }

    public static <T, R>  R createListPage(List<T> list, Long total, Class<R> rClass) {
        try {
            R obj = rClass.newInstance();
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(rClass, "setList", List.class), obj, list);
            ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(rClass, "setTotal", Long.class), obj, total);
            return obj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

        public static <T, R>  R createListPage(List<T> list, Integer total, Class<R> rClass) {
            try {
                R obj = rClass.newInstance();
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(rClass, "setList", List.class), obj, list);
                ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(rClass, "setTotal", Long.class), obj, total.longValue());
                return obj;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    public static <T, R> R sliceToListPage(List<T> list, Long page, Long size, Class<R> rClass) {
        return createListPage(listSlice(list, (page - 1) * size, Math.min(size * page, list.size())), page * size, rClass);
    }



    /**
     * @since v3.6.0 分页组件
     */
    public static <T, R>  R paginateList(List<T> list, Long page, Long size, Class<R> rClass) {
        if (page == null || page < 1) {
            throw new HttpCodeException(500, "分页变量page值必须大于0");
        }
        if (size == null || size < 1) {
            throw new HttpCodeException(500, "分页变量size值必须大于0");
        }
        long offset = size * (page - 1);
        long realOffset = Math.min((offset + size), list.size());
        if (offset > list.size()) {
            return createListPage(new ArrayList<>(), list.size(), rClass);
        }
        List<T> subList = list.subList(Math.toIntExact(offset), Math.toIntExact(realOffset));
        return CommonFunctionUtil.createListPage(subList, list.size(), rClass);
    }

    /**
     * @since v2.19.0
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        return toString(obj, false, 0, new IdentityHashMap<>(), null);
    }

    public static String toString(Object obj, String timeZoneStr) {
        return toString(obj, false, 0, new IdentityHashMap<>(), timeZoneStr);
    }

    private static String toString(Object obj, boolean collapseClass, int depth, IdentityHashMap<Object, Integer> visitedObjects) {
        return toString(obj, collapseClass, depth, visitedObjects, null);
    }

    private static String toString(Object obj, boolean collapseClass, int depth, IdentityHashMap<Object, Integer> visitedObjects, String timeZoneStr) {
        // 工具类不展开
        if(obj instanceof ObjectMapper){
            return toNullString(collapseClass);
        }
        // 有些类型一级toString时不要加引号，在二级展开时需要加引号，比如String、时间类型
        String quot = collapseClass ? "\"" : "";

        if (null == obj) {
            // for ide type: null
            return toNullString(collapseClass);
        } else if (isNumberType(obj)) {
            // for ide type: int、long、Double、Decimal...
            return toNumberString(obj);
        } else if (obj instanceof Boolean) {
            // for ide type: boolean
            return obj.toString();
        } else if (obj instanceof String) {
            // for ide type: string、text、 email
            if (collapseClass && obj.toString().length() > 100) {
                return quot + ((String) obj).substring(0, 100) + "..." + quot;
            }
            return quot + obj + quot;
        } else if (isBinaryType(obj)) {
            // for ide type: binary
            return "Binary";
        } else if (obj instanceof Temporal) {
            // for ide type: datetime/date/time
            return toTemporalString(obj, collapseClass, timeZoneStr);
        } else if (obj instanceof List) {
            // for ide type: List
            return toListString(obj, collapseClass, depth, visitedObjects);
        } else if (obj.getClass().isEnum()) {
            // 其他枚举比如依赖库自带的枚举
            return toEnumString(obj);
        } else if (obj instanceof Map) {
            // for ide type: Map
            return toMapString(obj, collapseClass, depth, visitedObjects);
        } else if (null == obj.getClass().getPackage()) {
            return JacksonUtils.toJson(obj);
        } else {
            // 其他复合对象，比如实体、数据结构等
            return toElseString(obj, collapseClass, depth, visitedObjects);
        }
    }


    private static boolean isBinaryType(Object obj) {
        return obj instanceof byte[] || obj instanceof Byte[]|| obj instanceof InputStream;
    }

    private static String toEnumString(Object obj) {
        if (obj instanceof BaseEnum) {
            // for ide type: 枚举
            return toString(((BaseEnum) obj).getDesc());
        } else if (obj.getClass().isEnum()) {
            // 其他枚举比如依赖库自带的枚举
            return obj.toString();
        }

        return String.valueOf(obj);
    }

    private static boolean isNumberType(Object obj) {
        return obj instanceof Integer || obj instanceof Long ||
                obj instanceof Double || obj instanceof Float || obj instanceof BigDecimal;
    }

    private static String toNumberString(Object obj) {
        if(obj instanceof Integer || obj instanceof Long) {
            // for ide type: int、long
            return obj.toString();
        } else if(obj instanceof Double || obj instanceof Float) {
            // for ide type: double
            return new BigDecimal(obj.toString()).toPlainString();
        } else if (obj instanceof BigDecimal) {
            // for ide type: Decimal
            return ((BigDecimal) obj).toPlainString();
        }

        return String.valueOf(obj);
    }

    private static String toNullString(boolean collapseClass) {
        if (collapseClass) {
            return "(空)";
        } else {
            return "";
        }
    }

    private static String toTemporalString(Object obj, boolean collapseClass, String timeZoneStr) {
        String quot = collapseClass ? "\"" : "";
        if(obj instanceof ZonedDateTime) {
            // for ide type: datetime
            ZonedDateTime zonedDateTime = (ZonedDateTime) obj;
            // 根据时区参数做转化，如果没有时区参数的话使用默认时区保持逻辑和之前一致
            if (StringUtils.isNotBlank(timeZoneStr)) {
                zonedDateTime = zonedDateTime.withZoneSameInstant(getZoneFromGlobalOrDefault(timeZoneStr));
            } else {
                zonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault());
            }
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern(DATETIME_FORMAT_YYYYMMDD_HHMMSS);
            return quot + pattern.format(zonedDateTime) + quot;
        } else if(obj instanceof LocalDateTime) {
            // for ide type: datetime
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern(DATETIME_FORMAT_YYYYMMDD_HHMMSS);
            return quot + pattern.format((LocalDateTime) obj) + quot;
        } else if(obj instanceof LocalDate) {
            // for ide type: date
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern(DATETIME_FORMAT_YYYYMMDD);
            return quot + pattern.format((LocalDate) obj) + quot;
        } else if(obj instanceof LocalTime) {
            // for ide type: time
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern(DATETIME_FORMAT_HHMMSS);
            return quot + pattern.format((LocalTime) obj) + quot;
        }

        return quot + obj.toString() + quot;
    }

    private static String toListString(Object obj, boolean collapseClass, int depth, IdentityHashMap<Object, Integer> visitedObjects) {
        String blank = StringUtils.repeat("    ", depth);
        // for ide type: List
        List list = (List) obj;
        StringBuilder stringBuilder = new StringBuilder();
        if (list.isEmpty()) {
            stringBuilder.append("[]");
        } else {
            stringBuilder.append("[\n");
            String hyper = "  " + blank;
            for (Object item : list) {
                stringBuilder.append(hyper).append(toString(item, true, depth + 1, visitedObjects));
                hyper = ",\n  " + blank;
            }
            stringBuilder.append("\n").append(blank).append("]");
        }

        return stringBuilder.toString();
    }

    private static String toMapString(Object obj, boolean collapseClass, int depth, IdentityHashMap<Object, Integer> visitedObjects) {
        String blank = StringUtils.repeat("    ", depth);
        StringBuilder stringBuilder = new StringBuilder();
        Map<?, ?> map = (Map) obj;
        if (map.isEmpty()) {
            stringBuilder.append("[->]");
        } else {
            stringBuilder.append("[\n");
            String hyper = "  " + blank;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                stringBuilder.append(hyper);
                if (visitedObjects.containsKey(entry.getKey())) {
                    stringBuilder.append(entry.getKey().getClass().getSimpleName()).append(" {...}");
                } else {
                    stringBuilder.append(toString(entry.getKey(), true, depth + 1, visitedObjects));
                }
                stringBuilder.append(" -> ");
                if (visitedObjects.containsKey(entry.getValue())) {
                    stringBuilder.append(entry.getValue().getClass().getSimpleName()).append(" {...}");
                } else {
                    stringBuilder.append(toString(entry.getValue(), true, depth + 1, visitedObjects));
                }
                hyper = ",\n  " + blank;
            }
            stringBuilder.append("\n").append(blank).append("]");

        }

        return stringBuilder.toString();
    }

    private static String toElseString(Object obj, boolean collapseClass, int depth, IdentityHashMap<Object, Integer> visitedObjects) {
        String blank = StringUtils.repeat("    ", depth);
        String objPackageName = obj.getClass().getPackage().getName();
        StringBuilder stringBuilder = new StringBuilder();
        // 展示类名，一些nasl生成的类toString后需要和nasl里的名字保持一致
        if (objPackageName.startsWith(DOMAIN_PACKAGE) && objPackageName.endsWith("anonymous")) {
            // 匿名数据结构的展示不太一样
            stringBuilder.append("{");
        } else {
            stringBuilder.append(getToStringClassName(obj.getClass())).append(" {");
        }
        // 循环引用处理
        if (!visitedObjects.containsKey(obj)) {
            visitedObjects.put(obj, 1);
        } else {
            return obj.getClass().getSimpleName() + " {...}";
        }

        Field[] fields = obj.getClass().getDeclaredFields();
        String hyper = "\n  " + blank;
        for (Field field : fields) {
            field.setAccessible(true);
            if(Modifier.isStatic(field.getModifiers())){
                continue;
            }
            stringBuilder.append(hyper).append(field.getName()).append(": ");
            stringBuilder.append(toString(ReflectionUtils.getField(field,obj), true, depth + 1, visitedObjects));
            hyper = ",\n  " + blank;
        }

        stringBuilder.append("\n");
        stringBuilder.append(blank).append("}");
        visitedObjects.remove(obj);
        return stringBuilder.toString();
    }

    private static String getToStringClassName(Class clazz) {
        String simpleClassName = clazz.getSimpleName();
        String objPackageName = clazz.getPackage().getName();
        boolean isDomainClass = objPackageName.startsWith(DOMAIN_PACKAGE);
        boolean isAnonymousClass = isDomainClass && objPackageName.endsWith("anonymous");

        if (isAnonymousClass) {
            return "";
        } else if(!isDomainClass) {
            return simpleClassName;
        } else if (simpleClassName.endsWith("Entity")) {
            return simpleClassName.substring(0, simpleClassName.length() - "Entity".length());
        } else if (simpleClassName.endsWith("Structure")) {
            return simpleClassName.substring(0, simpleClassName.length() - "Structure".length());
        } else if (simpleClassName.endsWith("Enum")) {
            return simpleClassName.substring(0, simpleClassName.length() - "Enum".length());
        } else {
            return simpleClassName;
        }
    }

    public static <T> T fromString(String str, Class<T> tClass) {
        if (Boolean.class.isAssignableFrom(tClass)) {
            // 旧版本的string转boolean没实现
            if (Boolean.TRUE.toString().toLowerCase().equals(str)) {
                return (T) Boolean.TRUE;
            } else if (Boolean.FALSE.toString().toLowerCase().equals(str)) {
                return (T) Boolean.FALSE;
            } else {
                throw new HttpCodeException(400, String.format("%s cannot convert to boolean", str));
            }
        } else {
            // 其他的和旧版本的保持一致
            return convert(str, tClass);
        }
    }

    public static Long randomInt(Long start, Long end) {
        return RandomUtils.nextLong(start, end);
    }

    public static Long round(BigDecimal value, RoundingMode mode) {
        if (null == value) {
            throw new NullPointerException();
        }

        BigDecimal res = value.setScale(0, mode);
        return res.longValue();
    }

    public static boolean hasValue(Object... objects) {
        // 多个对象都有值才返回true，任一对象没有值返回false
        for (Object object : objects) {
            boolean hasValueResult = true;
            // 为空直接返回
            if (null == object) {
                return false;
            }

            if (object instanceof String) {
                String s = ((String) object).trim();
                // 字符串为空，直接返回false
                hasValueResult = s.length() != 0;
            } else if (object instanceof Collection) {
                // list对象为空，直接返回false
                hasValueResult = !((Collection<?>) object).isEmpty();
            } else if (object instanceof Map) {
                // map对象为空，直接返回false
                hasValueResult = !((Map<?, ?>) object).isEmpty();
            } else if (!object.getClass().isPrimitive() && !(object instanceof Number)
                    && !(object instanceof Boolean) && !(object instanceof TemporalAccessor)) {
                // 其他类型的对象判断对象属性是否是否为空
                // 传入多个对象调用hasValue函数，只要有一个对象为空就返回false，全部不为空才返回true
                hasValueResult = singleObjectHasValue(object);
            }

            if (!hasValueResult) {
                return false;
            }
        }
        return true;
    }

    private static boolean singleObjectHasValue(Object object){
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(object.getClass(), field.getName());
                if (null == propertyDescriptor || propertyDescriptor.getReadMethod() == null) {
                    continue;
                }
                Object value = propertyDescriptor.getReadMethod().invoke(object);
                if (null != value) {
                    // 对象只要有一个属性有值就返回true
                    return true;
                }
            }
        } catch (Exception e) {
            throw new HttpCodeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
        return false;
    }

    /**
     * @since 3.7 格式化百分比
     * @return
     */
    public static String formatPercent(Number value, Long digits, Boolean omit, Boolean showGroups) {
        if (value instanceof BigDecimal) {
            String decimalTag = Boolean.TRUE.equals(omit) ? /*隐藏0*/ "#" : "0";
            String prefix = Boolean.TRUE.equals(showGroups) ? /*显示千分位*/ "#,##0." : "#0.";
            DecimalFormat decimalFormat = new DecimalFormat(prefix + StringUtils.repeat(decimalTag, digits.intValue()) + "%");
            return decimalFormat.format(value);
        }
        /**
         * 整数的百分比处理
         */
        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMaximumFractionDigits(digits.intValue());
        if (Boolean.FALSE.equals(omit)) {
            percentFormat.setMinimumFractionDigits(digits.intValue());
        }
        percentFormat.setGroupingUsed(showGroups);
        return percentFormat.format(value);
    }

    /**
     * @since 3.7 格式化数字
     * @param value
     * @param digits
     * @param omit
     * @param showGroup
     * @param fix
     * @param unit
     * @return
     */
    public static String formatNumber(Number value, Long digits, Boolean omit, Boolean showGroup, String fix, String unit) {
        String result;
        if (value instanceof BigDecimal) {
            String decimalTag = Boolean.TRUE.equals(omit) ? /*隐藏0*/ "#" : "0";
            String prefix = Boolean.TRUE.equals(showGroup) ? /*显示千分位*/ "#,##0." : "#0.";
            DecimalFormat decimalFormat = new DecimalFormat(prefix + StringUtils.repeat(decimalTag, digits.intValue()));
            decimalFormat.setGroupingUsed(showGroup);
            result = decimalFormat.format(value);
        } else {
            //整数无需截取小数
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            numberFormat.setMaximumFractionDigits(digits.intValue());
            if (Boolean.FALSE.equals(omit)) {
                numberFormat.setMinimumFractionDigits(digits.intValue());
            }
            numberFormat.setGroupingUsed(showGroup);
            result = numberFormat.format(value);
        }
        if (StringUtils.isNotBlank(unit)) {
            if ("prefix".equals(fix)) {
                result = unit + result;
            } else {
                result = result + unit;
            }
        }
        return result;
    }

    /**
     * @since 3.7 时间格式化
     * @param date
     * @param formatter
     * @return
     */
    public static String formatTime(LocalTime date, String formatter) {
        if (formatter == null || "".equals(formatter.trim())) {
            formatter = DATETIME_FORMAT_HHMMSS;
        }
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern(formatter);
        return date.format(pattern);
    }

    public static <T> Boolean equals(T left, T right) {
        if (left == right) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }

        if (left instanceof List || right instanceof List || left instanceof Map || right instanceof Map) {
            return false;
        }

        return left.equals(right);
    }

    public static <T> Boolean notEquals(T left, T right) {
        return !equals(left, right);
    }

    public static <T> Boolean referenceEquals(T left, T right) {
        return left == right;
    }
}
