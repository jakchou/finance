package com.defaulttenant.hotswap.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Configure the converters to use the ISO format for dates by default.
 */
@Configuration
public class DateTimeFormatConfiguration implements WebMvcConfigurer {
    public static final String DEFAULT_TIMEZONE = "UTC";
    public static final String ZONED_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String LOCAL_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String LOCAL_DATE_FORMAT = "yyyy-MM-dd";
    public static final String LOCAL_TIME_FORMAT = "HH:mm:ss";

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(registry);

        registry.addConverter(new StringToZonedDateTimeConverter());
        registry.addConverter(new StringToLocalDateTimeConverter());
        registry.addConverter(new StringToLocalDateConverter());
        registry.addConverter(new StringToLocalTimeConverter());
    }

    class StringToZonedDateTimeConverter  implements Converter<String, ZonedDateTime> {

        @Override
        public ZonedDateTime convert(String s) {
            return ZonedDateTime.parse(s, DateTimeFormatter.ofPattern(ZONED_DATETIME_FORMAT)
                    .withZone(ZoneId.of(DEFAULT_TIMEZONE)));
        }
    }

    class StringToLocalDateTimeConverter  implements Converter<String, LocalDateTime> {

        @Override
        public LocalDateTime convert(String s) {
            return LocalDateTime.parse(s, DateTimeFormatter.ofPattern(LOCAL_DATETIME_FORMAT));
        }
    }

    class StringToLocalDateConverter  implements Converter<String, LocalDate> {

        @Override
        public LocalDate convert(String s) {
            return LocalDate.parse(s, DateTimeFormatter.ofPattern(LOCAL_DATE_FORMAT));
        }
    }

    class StringToLocalTimeConverter  implements Converter<String, LocalTime> {

        @Override
        public LocalTime convert(String s) {
            return LocalTime.parse(s, DateTimeFormatter.ofPattern(LOCAL_TIME_FORMAT));
        }
    }
}
