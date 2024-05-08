package com.defaulttenant.hotswap.domain.enumeration;

public enum ErrorCodeEnum {
    UNKNOWN("error.unknown", "unknown error"),

    PARAM_REQUIRED("param.required", "param is required"),
    PARAM_NOTHING_TODO("param.nothing.todo", "param is miss, nothing to update or delete"),
    PARAM_INVALID("param.invalid", "param is invalid"),

    RELATION_PROTECT("entity.relation.protect", "relation protect"),
    DATA_NOT_EXIST("entity.notExist", "data not exist"),
    PARAM_PRIMARY_KEY_REQUIRED("param.primary.key.required", "param primary key is required"),

    FILE_NOT_EXIST("upload.file.not.exist", "文件不存在"),
    FILE_TYPE_NOT_ALLOWED("upload.file.type.not.allowed", "上传文件类型不允许"),
    FILENAME_TOO_LONG("upload.filename.too.long", "上传文件名长度超过255"),

    FILENAME_NOT_ALLOWED("upload.filename.not.allowed", "上传文件名非法"),
    FILESYSTEM_NOT_SUPPORT("upload.filesystem.not.support", "file not exist"),
    NO_PERMISSION_ACCESS_RESOURCE("no.permission.access.resource","no auth api has no permission to access this resource"),

    FILEPATH_NOT_ALLOWED("upload.filepath.not.allowed", "上传文件路径非法"),

    FILEPATH_TOO_LONG("upload.filepath.too.long", "上传文件路径长度超过10"),

    DB_TYPE_NOT_SUPPORT("db.type.not.support", "db type not support"),

    OPERATOR_NOT_SUPPORT("operator.not.support", "operator not support"),

    FILE_DOWNLOAD_FAILED("file.download.failed", "file download failed"),

    TOKEN_INVALID("token.is.invalid", "token is invalid"),

    ILLEGAL_DATE("convert.illegal.date", "非法的日期值"),
    ILLEGAL_TIME("convert.illegal.time", "非法的时间值"),
    ILLEGAL_DATETIME("convert.illegal.datetime", "非法的日期时间值"),

    INDEX_OUT_OF_BOUNDS("index.out.of.bounds", "index out of bounds"),
    ;

    public final String code;
    public final String desc;

    ErrorCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
