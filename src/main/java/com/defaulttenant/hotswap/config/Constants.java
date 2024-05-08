package com.defaulttenant.hotswap.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.defaulttenant.hotswap.Application;

import java.time.format.DateTimeFormatter;

/**
 * Application constants.
 */
public final class Constants {
    public static final String SYSTEM_ACCOUNT = "system";
    public static final String BASE_PACKAGE = Application.class.getPackage().getName();
    public static final String LIGHT_GW_TYPE = "light";
    public static final String LCAP_CUSTOMIZE_LOGGER = "LCAP_CUSTOMIZE_LOGGER";

    /**
     * AuthFilter里面的一些常量
     **/
    public static final String AUTH_FILTER_HEADER_CONTENT_TYPE = "application/json; charset=UTF-8";
    public static final String AUTH_FILTER_HEADER_AUTHORIZATION = "Authorization";
    public static final String THIRD_PARTY_COOKIE_BODY_KEY = "ThirdPartyCookie";
    public static final String AUTH_FILTER_HEADER_HOST = "host";
    public static final String AUTH_FILTER_HEADER_USERNAME = "UserName";
    public static final String AUTH_FILTER_HEADER_DOMAINNAME = "DomainName";
    public static final String AUTH_API_PREFIX = "/api/";
    public static final String AUTH_FILTER_VERIFY_TOKEN = "Token";
    public static final String AUTH_FILTER_ID_SUFFIX = "/id:";
    public static final String AUTH_FILTER_API_NUIMS = "/nuims";
    public static final String AUTH_FILTER_API_PROCESS = "/api/system/process";
    public static final String AUTH_FILTER_API_USER_LIST = "/api/system/user/userListFromNuimOrApp";
    public static final String AUTH_FILTER_API_ANNOTATION_LIST = "/api/system/annotation";

    /**
     * AuthFilter里面token鉴权返回结果的key
     **/
    public static final String AUTH_FILTER_SUCCESS_STR = "Success";
    public static final String AUTH_FILTER_FAIL_STR = "Fail";
    public static final String AUTH_FILTER_CODE_STR = "Code";
    public static final String AUTH_FILTER_DATA_STR = "Data";
    public static final String AUTH_FILTER_RESULT_STR = "Result";
    public static final String AUTH_FILTER_USERID_STR = "UserId";
    public static final String AUTH_FILTER_USERNAME_STR = "UserName";
    public static final String AUTH_FILTER_PHONE_STR = "Phone";
    public static final String AUTH_FILTER_EMAIL_STR = "Email";
    public static final String AUTH_FILTER_STATUS_STR = "Status";
    public static final String AUTH_FILTER_CREATETIME_STR = "CreateTime";
    public static final String AUTH_FILTER_UPDATETIME_STR = "UpdateTime";
    public static final String AUTH_FILTER_NICKNAME_STR = "NickName";
    public static final String AUTH_FILTER_SOURCE_STR = "Source";

    /**
     * 文件上传相关的一些常量
     **/
    public static final String UPLOAD_TYPE_GATEWAY = "gateway";
    public static final String UPLOAD_TYPE_LOCAL = "local";

    public static final String UPLOAD_CONFIG_KEY_FILE_STORAGE_TYPE = "fileStorageType";
    public static final String UPLOAD_CONFIG_LOCAL_BASE_PATH = "localFsBasePath";
    public static final String UPLOAD_CONFIG_KEY_S3_ADDRESS = "address";
    public static final String UPLOAD_CONFIG_KEY_S3_ACCESS_KEY = "accessKey";
    public static final String UPLOAD_CONFIG_KEY_S3_SECRET_KEY = "secretKey";
    public static final String UPLOAD_CONFIG_KEY_S3_BUCKET = "bucket";
    public static final String UPLOAD_CONFIG_KEY_CDN_ADDRESS = "cdnAddress";
    public static final String UPLOAD_PARAMETER_FS_IS_COMPRESS = "lcapIsCompress";
    public static final String UPLOAD_PARAMETER_FS_VIA_ORIGIN_URL = "viaOriginURL";
    public static final String UPLOAD_PARAMETER_STORAGE_PATH = "storagePath";

    public static final String UPLOAD_PARAMETER_PATH = "lcapPath";
    public static final String UPLOAD_PARAMETER_FS_TYPE = "lcapFsType";
    public static final String UPLOAD_PARAMETER_FILENAME = "filename";
    public static final String UPLOAD_HEADER_ACCESS = "lcap-access";
    public static final String UPLOAD_HEADER_TTL = "lcap-ttl";

    public static final Double UPLOAD_ACCESS_NONE_EXPIRATION = -1D;
    public static final String UPLOAD_ACCESS_PUBLIC = "public";
    public static final String UPLOAD_ACCESS_PUBLIC_SUFFIX = "1";
    public static final String UPLOAD_ACCESS_PRIVATE = "private";
    public static final String UPLOAD_ACCESS_PRIVATE_SUFFIX = "2";
    public static final String UPLOAD_ACCESS_SUFFIX_JOINER = "_";
    public static final String UPLOAD_ACCESS_SUFFIX_MAGIC = "pms";

    /**
     * 一些拓展模块相关常量
     **/
    public static final String EXTENSION_PROPERTY_KEY = "extension";

    /**
     * httpUrlConnection 里面的一些常量
     **/
    public static final int HTTP_TIMEOUT_CONNECT_MILLIS = 5000;
    public static final int HTTP_TIMEOUT_READ_MILLIS = 30000;
    public static final int HTTP_DEFAULT_PORT = 80;

    /**
     * 一些错误响应常量
     **/
    public static final String ERROR_ATTR_PIC = "@@ERROR_PIC";

    private Constants() {
    }

    public static final String DB_MYSQL = "mysql";
    public static final String DB_ORACLE = "oracle";
    public static final String DB_DAMENG = "dameng";
    public static final String DB_DB2 = "db2";

    public static final String ORACLE_DATETIME = "to_timestamp('%s', 'yyyy-MM-dd hh24:mi:ss')";
    public static final String ORACLE_DATE = "to_timestamp('%s', 'yyyy-MM-dd')";
    public static final String ORACLE_TIME = "to_timestamp('%s', 'hh24:mi:ss')";

    public static final String TRUE_TO_ONE = "1";
    public static final String FALSE_TO_ZERO = "0";

    public static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static final String ORACLE_CONCAT_LIKE = " CONCAT(CONCAT('%%',%s),'%%')";
    public static final String MYSQL_CONCAT_LIKE = "concat('%%', %s ,'%%')";

    public static final String USER_APP_PROPERTY_KEY_PREFIX = "custom.";
    public static final List<String> RESERVED_PROPERTY_KEY_PREFIX = Collections.unmodifiableList(Arrays.asList("custom", "extensions"));
    public static final String LCAP_INFER_BASEPATH = "@@INFER_BASEPATH";
    public static final String DEFAULT_ROLE_NAME = "DEV-AdminRole";
}
