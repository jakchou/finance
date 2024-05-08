package com.defaulttenant.hotswap.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.defaulttenant.hotswap.config.DateTimeFormatConfiguration;
import com.defaulttenant.hotswap.config.JacksonConfiguration;
import com.defaulttenant.hotswap.exception.HttpCodeException;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sys
 * @date 2021-06-17
 * @since
 */
public class JacksonUtils {
    private JacksonUtils() {}
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final Map<ZoneId, ObjectMapper> zoneObjectMapperMap = new ConcurrentHashMap<>();
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
    }

    public static void setMapper(ObjectMapper mapper) {
        objectMapper = mapper;
    }

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new HttpCodeException(500, e);
        }
    }

    public static JsonNode toJsonNode(Object object) {
        return objectMapper.convertValue(object, JsonNode.class);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new HttpCodeException(500, e);
        }
    }

    public static <T> T fromJson(String json, TypeReference<?> typeRef) {
        try {
            ObjectReader reader = objectMapper.readerFor(typeRef);
            return reader.readValue(json);
        } catch (IOException e) {
            throw new HttpCodeException(500, e);
        }
    }

    public static <T> T fromJson(Object o, Class<T> clazz) {
        try {
            return objectMapper.convertValue(o, clazz);
        } catch (IllegalArgumentException e) {
            throw new HttpCodeException(500, e);
        }
    }

    public static <T> T fromJson(Map json, Class<T> clazz) {
        return objectMapper.convertValue(json, clazz);
    }

    public static <T> T fromJson(Map json, TypeReference<T> typeRef) {
        return objectMapper.convertValue(json, typeRef);
    }

    public static <T> List<T> fromJsonArray(String json, Class<T> clazz) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new HttpCodeException(500, e);
        }
    }

    public static <T> List<T> fromJsonArray(List<Map> jsonArray, Class<T> clazz) {
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
        return objectMapper.convertValue(jsonArray, javaType);
    }

    public static String toJson(Object object, String zoneIdStr) {
        try {
            return getZoneIdObjectMapper(zoneIdStr).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new HttpCodeException(500, e);
        }
    }

    public static JsonNode toJsonNode(Object object, String zoneIdStr) {
        return getZoneIdObjectMapper(zoneIdStr).convertValue(object, JsonNode.class);
    }

    public static <T> T fromJson(String json, Class<T> clazz, String zoneIdStr) {
        try {
            return getZoneIdObjectMapper(zoneIdStr).readValue(json, clazz);
        } catch (IOException e) {
            throw new HttpCodeException(500, e);
        }
    }

    public static <T> T fromJson(String json, TypeReference<?> typeRef, String zoneIdStr) {
        try {
            ObjectReader reader = getZoneIdObjectMapper(zoneIdStr).readerFor(typeRef);
            return reader.readValue(json);
        } catch (IOException e) {
            throw new HttpCodeException(500, e);
        }
    }

    public static <T> T fromJson(Object o, Class<T> clazz, String zoneIdStr) {
        try {
            return getZoneIdObjectMapper(zoneIdStr).convertValue(o, clazz);
        } catch (IllegalArgumentException e) {
            throw new HttpCodeException(500, e);
        }
    }

    public static <T> T fromJson(Map json, Class<T> clazz, String zoneIdStr) {
        return getZoneIdObjectMapper(zoneIdStr).convertValue(json, clazz);
    }

    public static <T> T fromJson(Map json, TypeReference<T> typeRef, String zoneIdStr) {
        return getZoneIdObjectMapper(zoneIdStr).convertValue(json, typeRef);
    }

    public static <T> List<T> fromJsonArray(String json, Class<T> clazz, String zoneIdStr) {
        try {
            JavaType javaType = getZoneIdObjectMapper(zoneIdStr).getTypeFactory().constructCollectionType(List.class, clazz);
            return getZoneIdObjectMapper(zoneIdStr).readValue(json, javaType);
        } catch (IOException e) {
            throw new HttpCodeException(500, e);
        }
    }

    public static <T> List<T> fromJsonArray(List<Map> jsonArray, Class<T> clazz, String zoneIdStr) {
        JavaType javaType = getZoneIdObjectMapper(zoneIdStr).getTypeFactory().constructCollectionType(List.class, clazz);
        return getZoneIdObjectMapper(zoneIdStr).convertValue(jsonArray, javaType);
    }

    public static ObjectMapper getZoneIdObjectMapper(String zoneIdStr) {
        ZoneId zoneId = CommonFunctionUtil.getZoneFromGlobalOrDefault(zoneIdStr);
        ObjectMapper objectMapper = zoneObjectMapperMap.get(zoneId);
        if (objectMapper == null) {
            Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder
                    .json()
                    .indentOutput(false)
                    .failOnEmptyBeans(false)
                    .failOnUnknownProperties(false)
                    .serializerByType(LocalTime.class,new JacksonConfiguration.LcpLocalTimeSerializer())
                    .serializerByType(ZonedDateTime.class,new SpecificZonedDateTimeSerializer(zoneId))
                    .serializerByType(LocalDate.class,new JacksonConfiguration.LcpLocalDateSerializer())
                    .deserializerByType(LocalTime.class, new JacksonConfiguration.LcpLocalTimeDeserializer())
                    .deserializerByType(ZonedDateTime.class,new SpecificZonedDateTimeDeserializer(zoneId))
                    .deserializerByType(LocalDate.class,new JacksonConfiguration.LcpLocalDateDeserializer());
            objectMapper = builder.build();
            zoneObjectMapperMap.put(zoneId, objectMapper);
            return objectMapper;
        } else {
            return objectMapper;
        }
    }

    public static class SpecificZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {

        private ZoneId zoneId;

        public SpecificZonedDateTimeSerializer(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

        @Override
        public void serialize(ZonedDateTime arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException {
            arg1.writeString(arg0
                    .withZoneSameInstant(zoneId)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'" + zoneId.getRules().getOffset(Instant.now()) + "[" + zoneId.getId() + "]'")));
        }
    }
    public static class SpecificZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

        private ZoneId zoneId;

        public SpecificZonedDateTimeDeserializer(ZoneId zoneId) {
            this.zoneId = zoneId;
        }

        @Override
        public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException, JsonProcessingException {
            String value = jsonParser.getText();
            //兼容时间戳类型的txt
            try {
                //1560924503.445000000
                Timestamp timestamp = Timestamp.from(Instant.ofEpochMilli((long) (Double.parseDouble(jsonParser.getText()) * 1000)));
                return ZonedDateTime.ofInstant(timestamp.toInstant(), zoneId);
            } catch (NumberFormatException e) {
                //2019-06-19T06:08:23.445Z
                return ZonedDateTime
                        .parse(value, DateTimeFormatter.ISO_DATE_TIME)
                        .withZoneSameInstant(zoneId);
            }
        }
    }
}
