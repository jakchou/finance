package com.defaulttenant.hotswap.web.controller;


import com.defaulttenant.hotswap.config.Constants;
import com.defaulttenant.hotswap.config.LcpProperties;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BaseUiController {
    private final Logger log = LoggerFactory.getLogger(BaseUiController.class);

    @Resource
    protected LcpProperties lcpProperties;
    @Value("${custom.api.context-path:/}")
    protected String uiBasePath;

    private static final String INDEX_HTML_PATH = "/static/index.html";
    private static final String ERROR_HTML_PATH = "/static/error.html";
    private static final String NOFOUND_HTML_PATH = "/static/404.html";
    protected static final String JS_FILE_NAME = "client.js";
    protected static final String PATH_SPLIT = "/";

    private Map<String, String> indexHtmlMap = new HashMap<>();

    private String[] errorHtmlParts;

    private String uiResourceAddress;
    private Map<String, LcpProperties.FrontendDTO> basePath2Frontends = Collections.emptyMap();

    @PostConstruct
    public void init() {
        uiResourceAddress = lcpProperties.getUiResourceAddress();
        uiResourceAddress = uiResourceAddress.endsWith(JS_FILE_NAME) ?
                uiResourceAddress.substring(0, uiResourceAddress.length() - JS_FILE_NAME.length()) : uiResourceAddress;

        if (null != lcpProperties.getFrontends() && lcpProperties.getFrontends().size() > 0) {
            basePath2Frontends = new HashMap<>(lcpProperties.getFrontends().size());

            for (LcpProperties.FrontendDTO frontendDTO : lcpProperties.getFrontends().values()) {
                basePath2Frontends.put(frontendDTO.getPath(), frontendDTO);
            }
        }
    }

    protected String invalidPathError(HttpServletRequest request, String basePath, String uiResourceAddress) {
        if (null != request.getAttribute(Constants.ERROR_ATTR_PIC)) {
            String imagePath = (String) request.getAttribute(Constants.ERROR_ATTR_PIC);
            return getErrorHtml(imagePath);
        } else if (ObjectUtils.isNotEmpty(basePath2Frontends)) {
            // 请求的basePath 和现有的端不匹配
            if (!basePath2Frontends.containsKey(basePath) && !basePath2Frontends.containsKey(PATH_SPLIT)) {
                return getNoFoundHtml();
            }
        }
        if (!basePath2Frontends.containsKey(basePath)) {
            basePath = "";
        }
        return getIndexHtml(basePath, concatPath(uiResourceAddress, concatPath(basePath, JS_FILE_NAME)));
    }

    protected String concatPath(String pre, String post) {
        if (pre.endsWith(JS_FILE_NAME) && post.endsWith(JS_FILE_NAME)) {
            return pre;
        }
        if (pre.endsWith(PATH_SPLIT) && post.startsWith(PATH_SPLIT)) {
            // 多了 /
            if (post.equals(PATH_SPLIT)) {
                return pre;
            }
            return pre + post.substring(1);
        } else if (!pre.endsWith(PATH_SPLIT) && !post.startsWith(PATH_SPLIT)) {
            // 少了 /
            return pre + PATH_SPLIT + post;
        } else {
            return pre + post;
        }
    }

    protected synchronized String getIndexHtml(String basePath, String uiResourceAddress) {
        if (indexHtmlMap.containsKey(basePath)) {
            return indexHtmlMap.get(basePath);
        }

        try (InputStream resourceAsStream = this.getClass().getResourceAsStream(INDEX_HTML_PATH);
             BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream))) {
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            String indexHtml = sb.toString();
            indexHtml = indexHtml.replace("${uiResourceAddress}", uiResourceAddress);
            indexHtml = indexHtml.replace("${uiBasePath}", "/".equals(uiBasePath) ? "" : uiBasePath);
            indexHtmlMap.put(basePath, indexHtml);
            return indexHtml;
        } catch (Exception e) {
            log.error("Read index html file error, ", e);
            return null;
        }
    }

    protected synchronized String getErrorHtml(String errorShowImage) {
        if (ArrayUtils.isEmpty(errorHtmlParts)) {
            try (InputStream resourceAsStream = this.getClass().getResourceAsStream(ERROR_HTML_PATH)) {
                String errorHtml = IOUtils.toString(resourceAsStream, "UTF-8");
                errorHtmlParts = StringUtils.splitByWholeSeparator(errorHtml, "${src}", 2);
            } catch (Exception e) {
                log.error("Read error html file error, ", e);
                return null;
            }
        }

        if (null != errorHtmlParts && errorHtmlParts.length == 2) {
            StringBuilder errorHtmlBuilder = new StringBuilder("");
            errorHtmlBuilder.append(errorHtmlParts[0]);
            errorHtmlBuilder.append(errorShowImage);
            errorHtmlBuilder.append(errorHtmlParts[1]);
            return errorHtmlBuilder.toString();
        }

        return null;
    }
    protected String handleErrorRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpStatus.OK.value());
        String basePath = "";
        if (null != request.getAttribute(Constants.LCAP_INFER_BASEPATH)) {
            basePath = (String) request.getAttribute(Constants.LCAP_INFER_BASEPATH);
        }

        return invalidPathError(request, basePath, this.uiResourceAddress);
    }

    protected String getNoFoundHtml() {
        try (InputStream resourceAsStream = this.getClass().getResourceAsStream(NOFOUND_HTML_PATH)) {
            String noFoundHtml = IOUtils.toString(resourceAsStream, "UTF-8");
            return noFoundHtml;
        } catch (Exception e) {
            log.error("Read error html file error, ", e);
            return null;
        }
    }
}
