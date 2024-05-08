package com.defaulttenant.hotswap.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

import com.defaulttenant.hotswap.exception.HttpCodeException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;

/**
 * @author sys
 * @date 2023/5/8 17:10
 */
public class XmlUtils {

    private XmlUtils() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtils.class);

    /**
     * convert xml to json
     *
     * @param xmlString xml
     * @return json
     */
    public static String xmlToJson(String xmlString) {
        JSONObject jObject = XML.toJSONObject(xmlString);
        ObjectMapper mapper = new ObjectMapper();
        Object json;
        String output;
        try {
            json = mapper.readValue(jObject.toString(), Object.class);
            output = mapper.writeValueAsString(json);
            return output;
        } catch (IOException e) {
            LOGGER.warn("xml incorrect format, xmlString: {}", xmlString);
            throw new HttpCodeException(HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    /**
     * convert json to xml
     *
     * @param jsonString json
     * @param charset    charset
     * @return xml
     */
    public static String jsonToXml(String jsonString, Charset charset) {
        JSONObject jsonObj = new JSONObject(jsonString);
        String xmlString = XML.toString(jsonObj);
        return buildXmlFragment(charset) + xmlString;
    }

    /**
     * convert bean to xml
     *
     * @param bean JavaBean
     * @return xml
     */
    public static String beanToXml(Object bean) {
        JSONObject json = new JSONObject(bean);
        return XML.toString(json);
    }

    /**
     * convert bean to xml with charset
     *
     * @param bean    JavaBean
     * @param charset Charset
     * @return xml
     */
    public static String beanToXmlWithCharset(Object bean, Charset charset) {
        JSONObject json = new JSONObject(bean);
        String xmlString = XML.toString(json);
        return buildXmlFragment(charset) + xmlString;
    }

    /**
     * judge is xml type request according to ContentType of HttpHeaders
     *
     * @param contentType contentType
     * @return is xml type
     */
    public static boolean isXmlType(MediaType contentType) {
        if (contentType != null) {
            return contentType.toString().contains(MediaType.APPLICATION_XML_VALUE) || contentType.toString().contains(MediaType.TEXT_XML_VALUE);
        }
        return false;
    }

    /**
     * get charset from contentType, default GBK
     *
     * @param contentType contentType
     * @return charset
     */
    public static Charset getXmlCharset(MediaType contentType) {
        if (contentType != null) {
            String charsetStr = contentType.getParameter("charset");
            if (!StringUtils.isEmpty(charsetStr)) {
                return Charset.forName(charsetStr);
            }
        }
        return Charset.forName("GBK");
    }

    /**
     * build xml header，like <?xml version="1.0" encoding="gbk"?>
     * @param charset xml encoding
     * @return <?xml version="1.0" encoding="charset"?>
     */
    private static String buildXmlFragment(Charset charset) {
        return "<?xml version=\"1.0\" encoding=\"" + charset + "\"?>";
    }
}
