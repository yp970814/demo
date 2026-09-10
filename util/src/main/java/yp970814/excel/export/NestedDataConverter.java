package yp970814.excel.export;

import com.alibaba.excel.annotation.ExcelProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Triple;
import yp970814.annotation.excel.ExcelNestedList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author pingge814@proton.me
 * date 2026/9/9 16:31
 */
public class NestedDataConverter {

    public static List<List<Object>> convertToNestedListWithNesting(List<?> dataList) {
        List<List<Object>> result = new ArrayList<>();
        if (dataList.isEmpty()) {
            return result;
        }
        Class<?> clazz = dataList.get(0).getClass();
        Triple<List, List, Map> triple = ExportUtil.extractParameterNames(clazz, true);
        List<String[]> parameterNames = triple.getLeft();

        List<Field> nestedFields = Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(ExcelNestedList.class))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(nestedFields)) {
            for (Object data : dataList) {
                Map<String, Object> fieldValues = new HashMap<>();
                populateFieldValues(data, fieldValues);
                result.add(buildRowFromFieldValues(fieldValues, parameterNames));
            }
            return result;
        }
        for (Object data : dataList) {
            Map<String, Object> fieldValues = new HashMap<>();
            populateFieldValues(data, fieldValues);
            List<Map<String, Object>> list = new ArrayList<>();
            for (Field nestedField : nestedFields) {
                try {
                    nestedField.setAccessible(true);
                    List<?> nestedList = (List<?>) nestedField.get(data);
                    if (CollectionUtils.isNotEmpty(nestedList)) {
                        for (int i = 0; i < nestedList.size(); i++) {
                            if (i > list.size() - 1) {
                                Map<String, Object> nestedFieldValues = new HashMap<>(fieldValues);
                                list.add(nestedFieldValues);
                            }
                            populateFieldValues(nestedList.get(i), list.get(i));
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to access nested data", e);
                }
            }
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(nestedFieldValues -> result.add(buildRowFromFieldValues(nestedFieldValues, parameterNames)));
            } else {
                result.add(buildRowFromFieldValues(fieldValues, parameterNames));
            }
        }
        return result;
    }

    private static void populateFieldValues(Object data, Map<String, Object> fieldValues) {
        for (Field field : data.getClass().getDeclaredFields()) {
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            if (excelProperty != null) {
                String paramName = Arrays.toString(excelProperty.value());
                try {
                    field.setAccessible(true);
                    Object value = field.get(data);
                    fieldValues.put(paramName, value != null ? value : "");
                } catch (IllegalAccessException e) {
                    fieldValues.put(paramName, "");
                }
            }
        }
    }

    private static List<Object> buildRowFromFieldValues(Map<String, Object> fieldValues, List<String[]> parameterNames) {
        List<Object> row = new ArrayList<>();
        for (String[] arr : parameterNames) {
            String paramName = Arrays.toString(arr);
            Object value = fieldValues.get(paramName);
            row.add(value != null ? value : "");
        }
        return row;
    }

}
