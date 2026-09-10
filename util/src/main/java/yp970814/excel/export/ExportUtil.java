package yp970814.excel.export;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.util.DateUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import yp970814.annotation.excel.ExcelNestedList;
import yp970814.annotation.excel.ExcelSelect;
import yp970814.excel.converter.BooleanConverter;
import yp970814.excel.converter.DateConverter;
import yp970814.excel.converter.LocalDateConverter;
import yp970814.excel.converter.LocalDateTimeConverter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author pingge814@proton.me
 * date 2026/9/9 16:20
 */

@Slf4j
public class ExportUtil {

    private static final String ENCODING_UTF_8 = "UTF-8";

    private static final String FILE_END = ".xlsx";

    private static final String FILE_END1 = ".xls";

    private static final String CONTENT_DISPOSITION = "Content-Disposition";

    private static final String CONTENT_DISPOSITION_VALUE = "attachment;filename=";

    private static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

    private static final String CONTENT_TYPE = "application/octet-stream";

    private static final String USER_NAME = "用户名";

    /**
     * 导出excel，并追加sheet页
     * @param response
     * @param fileName
     * @param list
     * @param clazz
     */
    public static void exportExcel(HttpServletResponse response, String fileName, List<?> list, Class<?> clazz) {
        exportExcel(response, fileName, list, clazz, null);
    }

    public static void exportExcel(HttpServletResponse response, String fileName, List<?> list, Class<?> clazz, Consumer<ExcelWriter> consumer) {
        try {
            String sheetName = createExcelFile(response, fileName);
            ExcelWriter writer = createExcelWriter(response.getOutputStream());
            writerExcel(writer, sheetName, list, clazz);
            if (consumer != null) {
                consumer.accept(writer);
            }
            writer.finish();
        } catch (IOException e) {
            log.error("导出异常", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("导出失败-关闭stream异常", e);
            }
        }
    }

    public static String createExcelFile(HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
        // 文件名
        String sheetName = "sheet1";
        if (!fileName.endsWith(FILE_END) && !fileName.endsWith(FILE_END1)) {
            sheetName = fileName;
            fileName = fileName + "_" + USER_NAME + "_" + DateUtils.format(new Date()) + FILE_END;
        }
        // 设置响应头和类型
        response.setContentType(CONTENT_TYPE);
        String fileNameEncoder = URLEncoder.encode(fileName, ENCODING_UTF_8).replaceAll("\\+", "%20");
        response.setHeader(CONTENT_DISPOSITION, CONTENT_DISPOSITION_VALUE + fileNameEncoder);
        response.setHeader(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION);
        return sheetName;
    }

    public static void writerExcel(ExcelWriter writer, String sheetName, List<?> list, Class<?> clazz) {
        WriteSheet sheet = createSheet(sheetName, list, clazz);
        writer.write(NestedDataConverter.convertToNestedListWithNesting(list), sheet);
    }

    public static ExcelWriter createExcelWriter(OutputStream outputStream) {
        ExcelWriter writer = EasyExcel.write(outputStream).build();
        return writer;
    }

    public static WriteSheet createSheet(String sheetName, List<?> list, Class<?> clazz) {
        // excel写入数据
        Triple<List, List, Map> triple = extractParameterNames(clazz, false);
        return EasyExcel.writerSheet(sheetName)
                .head(createHead(triple.getLeft()))
                .registerConverter(new BooleanConverter())
                .registerConverter(new DateConverter())
                .registerConverter(new LocalDateConverter())
                .registerConverter(new LocalDateTimeConverter())
                .registerWriteHandler(new SmartMergeStrategy(list, clazz))
                .registerWriteHandler(new SelectSheetWriteHandler(triple.getLeft(), triple.getMiddle(), triple.getRight()))
                .registerWriteHandler(ExcelStyleTool.getStyleStrategy())
                .build();
    }

    public static void exportExcel(OutputStream outputStream, String sheetName, List<?> list, Class<?> clazz) {
        try {
            ExcelWriter writer = createExcelWriter(outputStream);
            WriteSheet sheet = createSheet(sheetName, list, clazz);
            writer.write(NestedDataConverter.convertToNestedListWithNesting(list), sheet);
            writer.finish();
        } catch (Exception e) {
            log.error("导出异常", e);
            throw new RuntimeException(e);
        }
    }

    public static List<List<String>> createHead(List<String[]> list) {
        return list.stream().map(arr -> {
            List<String> headx = new ArrayList<>();
            Collections.addAll(headx, arr);
            return headx;
        }).collect(Collectors.toList());
    }

    public static Triple<List, List, Map> extractParameterNames(Class<?> clazz, boolean isHead) {
        return extractParameterNames(clazz, null, isHead);
    }

    private static Triple<List, List, Map> extractParameterNames(Class<?> clazz, List<String[]> list, boolean isHead) {
        if (list == null) {
            list = new ArrayList<>();
        }
        List<Integer> dataFormatList = new ArrayList<>();
        Map<Integer, List<String>> map = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            ExcelNestedList nestedList = field.getAnnotation(ExcelNestedList.class);
            ExcelSelect excelSelect = field.getAnnotation(ExcelSelect.class);
            if (excelProperty != null && nestedList == null) {
                list.add(excelProperty.value());
                if (!isHead) {
                    if (excelProperty.converter().isAssignableFrom(DateConverter.class)) {
                        dataFormatList.add(list.size() - 1);
                    }
                    if (excelSelect != null) {
                        String dictCode = excelSelect.dictCode();
                        if (StringUtils.isNotBlank(dictCode)) {
                            // 根据字典code，获取字典，用作excel下拉选
                            Map<String, String> dictMap = getDictValueKeyByCode(dictCode);
                            map.put(list.size() - 1, new ArrayList<>(dictMap.keySet()));
                        }
                    }
                }
            }
            if (nestedList != null) {
                Class<?> nestedClass = nestedList.value();
                Triple<List, List, Map> triple = extractParameterNames(nestedClass, list, isHead);
                dataFormatList.addAll(triple.getMiddle());
                map.putAll(triple.getRight());
            }
        }
        return Triple.of(list, dataFormatList, map);
    }

    public static Map<String, String> getDictValueKeyByCode(String dictCode) {
        return new HashMap<String, String>(){{
            put("1", "自营订单");
            put("2", "协销订单");
            put("3", "联营订单");
            put("4", "电商");
        }};
    }

}
