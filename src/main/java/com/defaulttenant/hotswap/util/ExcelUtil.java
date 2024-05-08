package com.defaulttenant.hotswap.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.defaulttenant.hotswap.config.DateTimeFormatConfiguration;
import com.defaulttenant.hotswap.exception.HttpCodeException;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * TODO change to EasyExcel
 */
public class ExcelUtil {
    private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);
    private static Map<String, Map<String, PropertyDescriptor>> classPropertyDescriptorMap = new HashMap<>();
    private static final DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter dateFormatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter dateFormatter2 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static String enumPrefix = "FIELD_";

    private ExcelUtil() {}
    /**
     * register pd, for easy read or write
     * @param domainClass
     */
    public static Map<String, PropertyDescriptor> registerDomainClass(Class domainClass) {
        if (null != domainClass) {
            Map<String, PropertyDescriptor> propertyDescriptorMap = classPropertyDescriptorMap.get(domainClass.getName());
            if (null == propertyDescriptorMap) {
                propertyDescriptorMap = new HashMap<>();
                classPropertyDescriptorMap.put(domainClass.getName(), propertyDescriptorMap);
            }

            PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(domainClass);
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                propertyDescriptorMap.put(propertyDescriptor.getName(), propertyDescriptor);
            }

            return propertyDescriptorMap;
        }

        return null;
    }

    public static <T> List<T> read(File excelFile, String fileType, Class<T> clazz,
                                   Map<String, String> entityTitleNameMap)
            throws InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        try (InputStream inputStream = new FileInputStream(excelFile)) {
            return read(inputStream, fileType, clazz, entityTitleNameMap);
        }
    }

    public static <T> List<T> read(InputStream inputStream, String fileType, Class<T> clazz,
                                   Map<String, String> entityTitleNameMap)
            throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Workbook workbook = getWorkBook(inputStream, fileType);

        List<T> resultDataList = new ArrayList<>();
        // 解析sheet
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            Sheet sheet = workbook.getSheetAt(sheetNum);
            // 校验sheet是否合法
            if (sheet == null) {
                continue;
            }
            // 获取第一行数据
            int firstRowNum = sheet.getFirstRowNum();
            Row firstRow = sheet.getRow(firstRowNum);
            if (null == firstRow) {
                log.warn("解析Excel失败，在第一行没有属性信息!");
                return resultDataList;
            }

            List<String> fields = parseField(firstRow, entityTitleNameMap);
            // 解析每一行的数据，构造数据对象
            int rowStart = firstRowNum + 1;
            int rowEnd = sheet.getPhysicalNumberOfRows();
            for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (null == row) {
                    continue;
                }

                T data = convertRowToData(row, fields, clazz);
                if (null == data) {
                    log.warn("第{}行数据不合法，已忽略！", row.getRowNum());
                    continue;
                }
                resultDataList.add(data);
            }
        }
        return resultDataList;
    }

    private static Workbook getWorkBook(InputStream inputStream, String fileType) throws IOException {
        if ("XLS".equalsIgnoreCase(fileType)) {
            return new HSSFWorkbook(inputStream);
        } else if ("XLSX".equalsIgnoreCase(fileType)) {
            return new XSSFWorkbook(inputStream);
        } else {
            throw new HttpCodeException(HttpStatus.BAD_REQUEST.value(), "unknown file type");
        }
    }

    public static <T> String write(List<T> data, Class<T> clazz, Map<String, String> targetFields) throws IOException {
        try (Workbook workbook = new SXSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();

            List<PropertyDescriptor> propertyDescriptors = buildDataSheet(sheet, clazz, targetFields);
            int rowNum = 1;
            for (T t : data) {
                if (t == null) {
                    continue;
                }
                //输出行数据
                Row row = sheet.createRow(rowNum++);
                convertDataToRow(t, row, propertyDescriptors);
            }
            String tempDir = System.getProperty("java.io.tmpdir");
            String uniqueId = UUID.randomUUID().toString();
            String storeFileName = tempDir + /* File.separator +*/uniqueId + ".XLSX";
            workbook.write(new FileOutputStream(storeFileName));
            return storeFileName;
        }
    }

    private static <T> void convertDataToRow(T data, Row row, List<PropertyDescriptor> propertyDescriptors) {
        int cellNum = 0;
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Cell cell = row.createCell(cellNum++);
            cell.setCellValue(getDataValueStr(propertyDescriptor, data));
        }
    }

    private static List<PropertyDescriptor> buildDataSheet(Sheet sheet, Class clazz, Map<String, String> targetFields) {
        List<PropertyDescriptor> propertyDescriptors = new ArrayList<>();
        Map<String, PropertyDescriptor> propertyDescriptorMap = classPropertyDescriptorMap.get(clazz.getName());
        if (null == propertyDescriptorMap) {
            propertyDescriptorMap = registerDomainClass(clazz);
        }

        if (CollectionUtils.isEmpty(targetFields)) {
            propertyDescriptors = new ArrayList<>(propertyDescriptorMap.values());
        } else {
            for (String fieldName : targetFields.keySet()) {
                if (propertyDescriptorMap.containsKey(fieldName)) {
                    propertyDescriptors.add(propertyDescriptorMap.get(fieldName));
                }
            }
        }

        // 设置列头宽度
        for (int i = 0; i < propertyDescriptors.size(); i++) {
            sheet.setColumnWidth(i, 4000);
        }
        // 设置默认行高
        sheet.setDefaultRowHeight((short) 400);
        // 构建头单元格样式
        CellStyle cellStyle = buildHeadCellStyle(sheet.getWorkbook());
        // 写入第一行各列的数据
        Row head = sheet.createRow(0);

        for (int i = 0; i < propertyDescriptors.size(); i++) {
            Cell cell = head.createCell(i);
            String fieldName = propertyDescriptors.get(i).getName();
            String titleName = (targetFields == null ? "" : targetFields.get(fieldName));
            if (!StringUtils.isEmpty(titleName)) {
                cell.setCellValue(titleName);
            } else {
                cell.setCellValue(fieldName);
            }
            cell.setCellStyle(cellStyle);
        }
        return propertyDescriptors;
    }

    private static String getDataValueStr(PropertyDescriptor propertyDescriptor, Object data) {
        Object value;
        try {
            value = propertyDescriptor.getReadMethod().invoke(data);
            return value == null ? "" : value.toString();
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.warn("get filed value error", e);
            return null;
        }
    }

    private static CellStyle buildHeadCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        //对齐方式设置
        style.setAlignment(HorizontalAlignment.CENTER);
        //边框颜色和宽度设置
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex()); // 下边框
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex()); // 左边框
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex()); // 右边框
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex()); // 上边框
        //设置背景颜色
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //粗体字设置
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private static List<String> parseField(Row firstRow, Map<String, String> entityTitleNameMap) {
        List<String> fields = new ArrayList<>();
        int cellNum = firstRow.getLastCellNum();
        for (int i = 0; i < cellNum; i++) {
            String titleName = firstRow.getCell(i).getStringCellValue().trim();
            String field = ( entityTitleNameMap == null ? "" : entityTitleNameMap.get(titleName) );
            if (!StringUtils.isEmpty(field)) {
                fields.add(field);
            } else {
                fields.add(titleName);
            }
        }
        return fields;
    }

    private static Object handleCellValueType(PropertyDescriptor propertyDescriptor, Cell cell) {
        if (cell == null || null == propertyDescriptor) {
            return null;
        }

        Class clazz = propertyDescriptor.getPropertyType();
        String cellValue = "";
        switch (cell.getCellType()) {
            case NUMERIC: //数字，日期也被处理成数字
                Double doubleValue = cell.getNumericCellValue();
                if (DateUtil.isCellDateFormatted(cell) && LocalTime.class.isAssignableFrom(clazz)) {
                    return cell.getLocalDateTimeCellValue().toLocalTime();
                } else if (DateUtil.isCellDateFormatted(cell) && LocalDate.class.isAssignableFrom(clazz)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate();
                } else if (DateUtil.isCellDateFormatted(cell) && ZonedDateTime.class.isAssignableFrom(clazz)) {
                    return cell.getLocalDateTimeCellValue().atZone(ZoneId.systemDefault())
                            .withZoneSameInstant(ZoneId.of(DateTimeFormatConfiguration.DEFAULT_TIMEZONE));
                } else {
                    cellValue = NumberToTextConverter.toText(doubleValue);
                }
                break;
            case STRING: //字符串
                cellValue = cell.getStringCellValue();
                break;
            case BOOLEAN: //布尔
                Boolean booleanValue = cell.getBooleanCellValue();
                cellValue = booleanValue.toString();
                break;
            case BLANK: // 空值
                break;
            case FORMULA: // 公式
                break;
            case ERROR: // 故障
                break;
            default:
                break;
        }
        return convertCellValueByClass(cellValue, clazz);
    }

    private static Object convertCellValueByClass(String cellValue, Class clazz) {
        if (StringUtils.isEmpty(cellValue)) {
            return null;
        }
        if (clazz.isEnum()) {
            return Enum.valueOf(clazz, enumPrefix + cellValue);
        }
        String name = clazz.getSimpleName();
        switch (name) {
            case "String":
                return cellValue;
            case "Long":
                return (long) (Double.parseDouble(cellValue));
            case "Integer":
                return (int) (Double.parseDouble(cellValue));
            case "Double":
                return Double.parseDouble(cellValue);
            case "Float":
                return (float) Double.parseDouble(cellValue);
            case "Boolean":
                return Boolean.valueOf(cellValue) || "1".equals(cellValue) || "是".equals(cellValue);
            case "BigDecimal":
                return new BigDecimal(cellValue);
            case "LocalDate":
                return cellValue.indexOf('/') > -1 ?
                        LocalDate.parse(cellValue, dateFormatter2) :
                        LocalDate.parse(cellValue, dateFormatter1);
            case "LocalTime":
                return LocalTime.parse(cellValue);
            case "ZonedDateTime":
                return cellValue.indexOf('/') > -1 ?
                        ZonedDateTime.parse(cellValue, dateTimeFormatter2) :
                        ZonedDateTime.parse(cellValue, dateTimeFormatter1);
            case "Instant":
                return Instant.parse(cellValue);
            case "Duration":
                return Duration.parse(cellValue);
            case "UUID":
                return UUID.fromString(cellValue);
            default:
                return null;
        }
    }

    private static <T> T convertRowToData(Row row, List<String> fields, Class<T> clazz)
            throws InvocationTargetException, IllegalAccessException, InstantiationException {
        T obj = clazz.newInstance();
        Map<String, PropertyDescriptor> propertyDescriptorMap = classPropertyDescriptorMap.get(clazz.getName());
        if (CollectionUtils.isEmpty(propertyDescriptorMap)) {
            propertyDescriptorMap = registerDomainClass(clazz);
        }

        for (int i = 0; i < fields.size(); i++) {
            Cell cell = row.getCell(i);
            String fileName = fields.get(i);
            PropertyDescriptor propertyDescriptor = propertyDescriptorMap.get(fileName) == null ? propertyDescriptorMap.get(StringUtils.capitalize(fileName)): propertyDescriptorMap.get(fileName);
            if (null == propertyDescriptor) {
                continue;
            }

            propertyDescriptor.getWriteMethod().invoke(obj,
                    propertyDescriptor.getPropertyType().cast(handleCellValueType(propertyDescriptor, cell)));
        }
        return obj;
    }
}