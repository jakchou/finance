package com.defaulttenant.hotswap.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.defaulttenant.hotswap.exception.HttpCodeException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter;


import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlMessageConverter<T> extends AbstractXmlHttpMessageConverter<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlMessageConverter.class);

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

    public XmlMessageConverter() {
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_XML);
        mediaTypes.add(MediaType.APPLICATION_XML);
        setSupportedMediaTypes(mediaTypes);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    /**
     * because low code generate logic has extra parameter, e.g <Structure><Structure/>,need customize xml deserializer to deal with it.
     * @param clazz the type of object to return
     * @param headers the HTTP input headers
     * @param source the HTTP input body
     * @return
     */
    @Override
    protected T readFromSource(Class<? extends T> clazz, HttpHeaders headers, Source source) {
        String xmlString = processSource(source);
        try {
            JSONObject jsonObject = XML.toJSONObject(xmlString);
            return objectMapper.readValue(jsonObject.toString(), clazz);
        } catch (IOException e) {
            LOGGER.warn("xml deserializer to bean incorrect, xmlString: {}", xmlString);
            HttpCodeException xmlConvertException = new HttpCodeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
            xmlConvertException.setMessage(e.getLocalizedMessage());
            throw xmlConvertException;
        }
    }

    protected String processSource(Source source) {
        if (source instanceof StreamSource) {
            StreamSource streamSource = (StreamSource) source;
            InputStream inputStream = streamSource.getInputStream();
            try {
                byte[] buffer = new byte[1024];
                int length;
                StringBuilder sb = new StringBuilder();
                while ((length = inputStream.read(buffer)) != -1) {
                    String data = new String(buffer, 0, length);
                    sb.append(data);
                }
                return sb.toString();
            } catch (IOException e) {
                LOGGER.warn("xml processSource to string incorrect, source: {}", source);
                throw new HttpCodeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
            }
        }
        return "";
    }

    @Override
    protected void writeToResult(T t, HttpHeaders headers, Result result) throws Exception {
        // empty
    }
}