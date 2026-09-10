package yp970814.excel.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import yp970814.annotation.excel.ExcelMerge;
import yp970814.annotation.excel.ExcelNestedList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author pingge814@proton.me
 * date 2026/9/9 16:48
 */
@Slf4j
public class SmartMergeStrategy extends AbstractMergeStrategy {

    private List<?> dataList;

    private Class<?> clazz;

    private List<String[]> head;

    private Integer totleHeadRowNum;

    private Map<Integer, List<int[]>> mergeInfo = new HashMap<>();

    public SmartMergeStrategy(List<?> dataList, Class<?> clazz) {
        this.dataList = dataList;
        this.clazz = clazz;
        Triple<List, List, Map> triple = ExportUtil.extractParameterNames(clazz, true);
        this.head = triple.getLeft();
        String[] maxLengthArray = this.head.stream()
                .max(Comparator.comparingInt(a -> a.length))
                .orElse(new String[0]);
        this.totleHeadRowNum = maxLengthArray.length;
        this.prepareMergeInfo();
    }

    private void prepareMergeInfo() {
        List<Field> mergeFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ExcelMerge.class))
                .collect(Collectors.toList());
        List<Field> nestedFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ExcelNestedList.class))
                .collect(Collectors.toList());
        int currentRow = this.totleHeadRowNum;
        for (Object data : dataList) {
            try {
                int nestedSize = 0;
                for (Field field : nestedFields) {
                    Field nestedField = clazz.getDeclaredField(field.getName());
                    nestedField.setAccessible(true);
                    List<?> nestedList = (List<?>) nestedField.get(data);
                    if (CollectionUtils.isEmpty(nestedList)) {
                        continue;
                    }
                    if (nestedList.size() > nestedSize) {
                        nestedSize = nestedList.size();
                    }
                }
                int startRow = currentRow;
                int endRow = (nestedSize > 0) ? (currentRow + nestedSize - 1) : currentRow;
                for (Field field : mergeFields) {
                    int columnIndex = this.getColumnIndex(field);
                    if (columnIndex >= 0 && startRow != endRow) {
                        mergeInfo.computeIfAbsent(startRow, k -> new ArrayList<>())
                                .add(new int[]{columnIndex, endRow});
//                        mergeInfo.computeIfAbsent(columnIndex, k -> new ArrayList<>())
//                                .add(new int[]{startRow, endRow});
                    }
                }
                currentRow = endRow + 1;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error("SmartMergeStrategy_Error processing data: {}", e.getMessage());
            }
        }
    }

    private int getColumnIndex(Field field) {
        ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
        if (excelProperty != null) {
            String[] value = excelProperty.value();
            if (value.length > 0) {
                String valueStr = StringUtils.join(value, ",");
                for (int i = 0; i < this.head.size(); i++) {
                    String[] headValue = this.head.get(i);
                    if (headValue.length > 0) {
                        String headValueStr = StringUtils.join(headValue, ",");
                        if (valueStr.equals(headValueStr)) {
                            return i;
                        }
                    }
                }
            }
        }
        return -1;
    }

    /**
     * 这个格式必须复用，excel导出格式超过64000个就报错
     */
    private CellStyle textStyle;

    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        if (textStyle == null) {
            Workbook workbook = sheet.getWorkbook();
            textStyle = workbook.createCellStyle();
            textStyle.setDataFormat((short) 49);
//        textStyle.setDataFormat(workbook.createDataFormat().getFormat("@"));
//        textStyle.setLocked(true);
        }
        int columnIndex = cell.getColumnIndex();
        int rowIndex = cell.getRowIndex();
        if (mergeInfo.containsKey(rowIndex)) {
            List<int[]> ranges = mergeInfo.get(rowIndex);
            for (int[] range : ranges) {
                int column = range[0];
                int endRow = range[1];
                if (columnIndex == column) {
                    CellRangeAddress region = new CellRangeAddress(rowIndex, endRow, columnIndex, columnIndex);
                    sheet.addMergedRegion(region);

                    cell.setCellStyle(textStyle);
                }
            }
        }

//        if (mergeInfo.containsKey(columnIndex)) {
//            List<int[]> ranges = mergeInfo.get(columnIndex);
//            for (int[] range : ranges) {
//                int startRow = range[0];
//                int endRow = range[1];
//                if (rowIndex == startRow) {
//                    CellRangeAddress region = new CellRangeAddress(startRow, endRow, columnIndex, columnIndex);
//                    sheet.addMergedRegion(region);
//                }
//            }
//        }
    }

}
