package com.defaulttenant.hotswap.util;

import com.defaulttenant.hotswap.config.Constants;
import com.defaulttenant.hotswap.context.UserContext;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import javax.activation.MimetypesFileTypeMap;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Pattern;
import static com.defaulttenant.hotswap.filestorage.FileStorageClient.DOWNLOAD_FORM_ORIGINAL_S3_CLIENT;

public final class FileUploadUtils {
    public static final String UPLOAD_API_PATH_PREFIX = "/upload";
    /**
     * 加密过后的文件路径
     */
    public static final String ENCRYPT_FILE_PTAH = "_efp";

    private static final int EXPIRATION_START_INDEX = (Constants.UPLOAD_ACCESS_SUFFIX_MAGIC + Constants.UPLOAD_ACCESS_PRIVATE_SUFFIX).length();
    private static final String DEFAULT_MIME_TYPE = "application/octet-stream";
    private static final MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();

    private static final List<String> DOWNLOAD_FILE_PATHS = new ArrayList<>(Arrays.asList("/upload/downloadFile", "/upload/downloadFiles"));

    public static final String ZIP_FILE_SUFFIX = ".zip";

    public static final int CONNECTION_TIME_OUT = 10 * 1000;

    public static final int READ_TIME_OUT = 10 * 1000;

    // 生成压缩包时, File.createTempFile 方法要求文件夹名字长度必须大于等于3
    public static final int FOLDER_NAME_MINI_LENGTH = 3;

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtils.class);

    /**
     * 针对文件名做一些特殊处理
     * @param filePath
     * @param hashFileName
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static Triple<String/*原始文件名称*/, String/*处理过后的文件名*/, String/*处理过后的文件名 + 后缀*/> handlerFileName(String filePath, Boolean hashFileName, String appId, Boolean needEncrypt) throws Exception {
        if (StringUtils.isBlank(filePath)) {
            //如果为空就作为默认值处理
            filePath = "default_original_file";
        }

        //对象存储中的文件名需要和返回的不一致
        filePath = FileUploadUtils.replaceEncryptEndTag(filePath);

        int indexOfExt = FilenameUtils.indexOfExtension(filePath);
        String originalFileName = indexOfExt > 0 ? filePath.substring(0, indexOfExt) : filePath;
        //后缀
        String ext = FilenameUtils.getExtension(filePath);
        ext = StringUtils.isEmpty(ext) ? "" : "." + ext;

        //处理随机名
        if (hashFileName) {
            originalFileName = hashFileName(originalFileName);
        }

        //加密前, 处理下特殊符号的文件名
        originalFileName = originalFileName.replaceAll("[^\\w\\u4E00-\\u9FA5.-]", "");

        //非加密时, 直接返回结果
        if (!needEncrypt) {
            return new ImmutableTriple<>(originalFileName, originalFileName, originalFileName + ext);
        }

        //拼接上租户ID 以及  appId
        if (null != UserContext.getCurrentUser() && StringUtils.isNotBlank(UserContext.getCurrentUser().userId)) {
            originalFileName += "_" + UserContext.getCurrentUser().userId;
        }

        if (StringUtils.isNotBlank(appId)) {
            originalFileName += "_" + appId;
        }

        //对文件名加密
        String encodeFileName = FileUploadUtils.encryptFilePath(URLEncoder.encode(originalFileName, "UTF-8"));

        return new ImmutableTriple<>(originalFileName, encodeFileName, encodeFileName + ext);
    }

    public static String generateSavePath(String originPath, String basePath, String access, Double ttl,Boolean viaOriginUrl) {
        // 1. 添加时间戳避免重名
        String fileName = FilenameUtils.getName(originPath);
        String ext = FilenameUtils.getExtension(fileName);
        // 某些文件没有扩展名，不需要额外加 .
        ext = StringUtils.isEmpty(ext) ? "" : "." + ext;
        String filenameWithoutExt = FilenameUtils.removeExtension(fileName);
        String curTime = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");

        // 2. 添加权限后缀
        String permissionSuffix = FileUploadUtils.getPathPermission(access, ttl);

        // 3. 添加CDN加速后缀
        String cdnSuffix = BooleanUtils.isTrue(viaOriginUrl)? "_ori" : "";

        String saveFilename = filenameWithoutExt + Constants.UPLOAD_ACCESS_SUFFIX_JOINER + curTime + cdnSuffix+ permissionSuffix + ext;

        // 5. 拼接上基础路径前缀
        return FilenameUtils.concat(FilenameUtils.concat(File.separator, basePath), saveFilename);
    }


    public static String hashFileName(String fileName) throws NoSuchAlgorithmException {

        int length = 32;

        // 生成随机字符串
        String uuid = UUID.randomUUID().toString();
        String randomString = uuid.replaceAll("-", "");

        // 计算文件名的散列值
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(fileName.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        // 截取散列值和随机字符串结合后的前32个字符
        String hashString = hexString.toString() + randomString;
        byte[] newHash = md.digest(hashString.getBytes(StandardCharsets.UTF_8));

        StringBuilder newHexString = new StringBuilder();
        for (byte b : newHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) newHexString.append('0');
            newHexString.append(hex);
        }

        String newFileName = newHexString.toString().substring(0, length);

        return newFileName;
    }

    public static String convertToUnifiedPath(String url) {
        String path = url.replaceAll("\\\\", "/"); // 将Windows路径分隔符替换为Linux路径分隔符
        return path;
    }

    public static String getPathPermission(String access, Double ttl) {
        // 公开且不过期的文件不添加额外的后缀
        if (Constants.UPLOAD_ACCESS_PUBLIC.equalsIgnoreCase(access) && Constants.UPLOAD_ACCESS_NONE_EXPIRATION.equals(ttl)) {
            return "";
        }

        // 指定了文件访问权限信息，将在保存的文件名上添加权限后缀信息,生成规则: pms1/2[0时区到期时间戳]
        String accessPart = Constants.UPLOAD_ACCESS_PUBLIC.equalsIgnoreCase(access) ?
                Constants.UPLOAD_ACCESS_PUBLIC_SUFFIX : Constants.UPLOAD_ACCESS_PRIVATE_SUFFIX;
        String ttlPart = "";
        if (!Constants.UPLOAD_ACCESS_NONE_EXPIRATION.equals(ttl) && ttl > 0) {
            ttlPart = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC"))
                    .plusMinutes((long) (ttl * 24 * 60)).toInstant().toEpochMilli() + "";
        }

        return Constants.UPLOAD_ACCESS_SUFFIX_JOINER + Constants.UPLOAD_ACCESS_SUFFIX_MAGIC + accessPart + ttlPart;
    }

    public static boolean isUploadApiPath(String apiPath, String method) {
        return "GET".equalsIgnoreCase(method) &&
                StringUtils.isNotBlank(apiPath) && ( apiPath.contains("/upload/") || apiPath.contains("/download/"));
    }

    public static boolean shouldAccessControl(String apiPath) {
        return StringUtils.isNotBlank(apiPath) && getPathPermissionSuffix(apiPath)
                .startsWith(Constants.UPLOAD_ACCESS_SUFFIX_MAGIC + Constants.UPLOAD_ACCESS_PRIVATE_SUFFIX);
    }

    public static boolean isPathExpiration(String apiPath) {
        String permissionSuffix = getPathPermissionSuffix(apiPath);
        if (StringUtils.isNotBlank(permissionSuffix) &&
                permissionSuffix.startsWith(Constants.UPLOAD_ACCESS_SUFFIX_MAGIC) &&
                permissionSuffix.length() > EXPIRATION_START_INDEX) {
            String expirationTimeStr = permissionSuffix.substring(EXPIRATION_START_INDEX);
            try {
                long expirationTime = Long.parseLong(expirationTimeStr);
                long now = ZonedDateTime.now().withZoneSameInstant(ZoneId.of("UTC")).toInstant().toEpochMilli() ;
                return now >= expirationTime;
            } catch (NumberFormatException e) {
                LOGGER.info("parse upload path: {} expiration time error", apiPath);
            }
        }

        return false;
    }

    public static String getPathPermissionSuffix(String apiPath) {
        String fileName = FilenameUtils.getName(apiPath);
        String nameWithoutExt = FilenameUtils.removeExtension(fileName);
        String[] parts = nameWithoutExt.split(Constants.UPLOAD_ACCESS_SUFFIX_JOINER);
        String permissionPart = parts[parts.length - 1];

        return permissionPart;
    }

    public static String getFileMimeType(String fileName) {
        String mimeType = mimetypesFileTypeMap.getContentType(fileName.toLowerCase(Locale.ROOT));
        return StringUtils.defaultIfBlank(mimeType, DEFAULT_MIME_TYPE);
    }

    public static String getFileName(String url, String fileName) {
        // 处理文件名
        String originFileName = FilenameUtils.getName(url);
        // 文件后缀名
        String ext = FilenameUtils.getExtension(originFileName);
        if (StringUtils.isBlank(fileName)) {
            fileName = originFileName;
            // 处理仅有后缀名的文件，.log  --->  20221115151108735.log
            if (fileName.equals("." + ext)) {
                fileName = getCurrentTimestamp() + "." + ext;
            }
        } else if (StringUtils.isNotBlank(ext)) {
            fileName = fileName + "." + ext;
        }
        return fileName;
    }

    /** 获取多文件下载压缩包名字 **/
    public static String getCompressName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            fileName = getCurrentTimestamp();
        } else if (fileName.length() < FOLDER_NAME_MINI_LENGTH) {
            fileName = fileName + "_" + getCurrentTimestamp();
        }
        return fileName;
    }

    /** 获取压缩包下的文件名称 **/
    public static String getCompressEntryName(String url, Map<String, Integer> compressEntryNameMap) {
        String compressEntryName = FilenameUtils.getName(url);
        // 处理重名文件
        if (compressEntryNameMap.containsKey(compressEntryName)) {
            compressEntryNameMap.put(compressEntryName, compressEntryNameMap.get(compressEntryName) + 1);
            String ext = FilenameUtils.getExtension(compressEntryName);
            // 某些文件没有扩展名，不需要额外加 .
            ext = StringUtils.isEmpty(ext) ? "" : "." + ext;
            String filenameWithoutExt = FilenameUtils.removeExtension(compressEntryName);
            compressEntryName = filenameWithoutExt + "-(" + compressEntryNameMap.get(compressEntryName) + ")" + ext;
        } else {
            compressEntryNameMap.put(compressEntryName, 1);
        }
        return compressEntryName;
    }

    public static String getCurrentTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
        Date date = new Date();
        return dateFormat.format(date);
    }


    /**
     根据 HttpServletRequest 对象和上传文件路径前缀获取上传文件的路径
     *
     @param request HttpServletRequest 对象
     @param uploadPrefix 上传文件路径前缀
     @return 上传文件的路径，如果获取失败则返回 null
     */
    //获取upload 后续路径
    public static String getUploadPath(HttpServletRequest request, String uploadPrefix) {
        StringBuffer requestURL = request.getRequestURL();
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        int index = requestURL.indexOf(requestURI);
        if (index != -1) {
            String prefix = requestURL.substring(0, index + contextPath.length());
            String path = requestURI.substring(contextPath.length());
            if (path.startsWith(uploadPrefix)) {
                return path.substring(uploadPrefix.length());
            }else if (path.equals(uploadPrefix)) {
                return "";
            }
        }
        return "";
    }

    /***
     * 这里是处理文件名称的逻辑
     */
    private static final String ALGORITHM = "AES";
    private static final String MODE = "AES/ECB/PKCS5Padding";
    private static final String KEY = "f3a9c2e5d48b7610"; // 16字节的密钥

    public static String replaceEncryptEndTag(String fileName) {
        if (!fileName.contains(FileUploadUtils.ENCRYPT_FILE_PTAH)) {
            return fileName;
        }
        fileName = fileName.replace(FileUploadUtils.ENCRYPT_FILE_PTAH, "");
        if (fileName.equals("")) {
            //这里应该不允许仅有_efp的文件名
            fileName = "default_replaced_file";
        }
        return fileName;
    }

    public static String encryptFilePath(final String originPath) {
        try {
            if (StringUtils.isBlank(originPath)) {
                return originPath;
            }
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(MODE);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal((originPath).getBytes(StandardCharsets.UTF_8));
            String encryptedText = Base64.getUrlEncoder().encodeToString(encryptedBytes);
            return encryptedText.replace("=", "");
        } catch (Exception e) {
            return originPath;
        }
    }

    public static String replaceTargetFileSeg(final String filePath, final String originSeg, final String targetSeg, Boolean needEncrypt) {
        if (!needEncrypt) {
            return filePath;
        }
        return filePath.replace(originSeg, targetSeg + FileUploadUtils.ENCRYPT_FILE_PTAH + getRandomTag());
    }

    /**
     * 加密标识匹配
     */
    private final static Pattern pattern = Pattern.compile(FileUploadUtils.ENCRYPT_FILE_PTAH + "\\d{1}\\w{1}\\w{1}\\w{1}\\d{1}");

    public static String handlerEncryptFilePath(String filePath, Map<String, String> urlParams) throws Exception {
        //因为是最后拼接的, 所以拿到最后一个_efp的位置
        int lastIndex = filePath.lastIndexOf(FileUploadUtils.ENCRYPT_FILE_PTAH);
        //_efpxxxx_{随机时间}
        if (lastIndex < 0 || lastIndex + 10 > filePath.length()) {
            //脏数据
            urlParams.put(DOWNLOAD_FORM_ORIGINAL_S3_CLIENT, String.valueOf(true));
            return filePath;
        }
        //需要拿到_efpxxxxx
        String tag = filePath.substring(lastIndex, lastIndex + 9);

        //原始地址
        if (!pattern.matcher(tag).matches()) {
            //需要从原始的s3Client下载
            urlParams.put(DOWNLOAD_FORM_ORIGINAL_S3_CLIENT, String.valueOf(true));
            return filePath;
        }

        String[] paths = filePath.split("/");
        String targetFileName = "";
        String encryptFileName = "";
        for (String pathTemp : paths) {
            if (!pathTemp.contains(tag)) {
                continue;
            }
            String[] fileNameSegs = pathTemp.split(tag);
            if (fileNameSegs.length != 2) {
                //这里异常数据
                continue;
            }
            targetFileName = fileNameSegs[0];
            encryptFileName = encryptFilePath(URLEncoder.encode(targetFileName, "UTF-8"));
            break;
        }

        return filePath.replace(targetFileName + tag, encryptFileName);
    }

    /**
     * 获取随机标识: 数字, 字符, 字符, 字符, 数字
     * @return
     */
    private static String getRandomTag() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        // 第一位是数字
        int randomNumber = random.nextInt(10);
        stringBuilder.append(randomNumber);

        // 第二位是字符
        char firstChar = (char) (random.nextInt(26) + 'A');
        stringBuilder.append(firstChar);

        // 第三位是字符
        char secondChar = (char) (random.nextInt(26) + 'A');
        stringBuilder.append(secondChar);

        // 第四位是字符
        char thirdChar = (char) (random.nextInt(26) + 'A');
        stringBuilder.append(thirdChar);

        // 第五位是数字
        int secondNumber = random.nextInt(10);
        stringBuilder.append(secondNumber);

        return stringBuilder.toString();
    }

}