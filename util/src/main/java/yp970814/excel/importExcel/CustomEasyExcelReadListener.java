package yp970814.excel.importExcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ConverterUtils;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.http.util.Asserts;
import org.springframework.util.Assert;
import yp970814.excel.export.ExportUtil;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author pingge814@proton.me
 * date 2026/9/10 09:44
 */
@Slf4j
@Data
public class CustomEasyExcelReadListener<T extends BaseExcelImport> implements ReadListener<T> {

    // 保存读取的对象
    private List<T> rows = new ArrayList<>();

    // 此集合用来存储错误信息
    private List<String> errorMessage = new ArrayList<>();

    private List<Map<Integer, String>> head = new ArrayList<>();

    private Integer totleHeadRowNum;

    private Class<?> headClazz;

    private List<Integer> dataFormatList;

    public CustomEasyExcelReadListener(Class<?> headClazz) {
        Triple<List, List, Map> triple = ExportUtil.extractParameterNames(headClazz, false);
        List<String[]> headArr = triple.getLeft();
        String[] maxLengthArray = headArr.stream()
                .max(Comparator.comparingInt(a -> a.length))
                .orElse(new String[0]);
        totleHeadRowNum = maxLengthArray.length;
        this.createHead(headArr);
        this.headClazz = headClazz;
        this.dataFormatList = triple.getMiddle();
    }

    /**
     * 读取表头数据存在headMap中。如果你校验表头格式时可以使用。
     */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        Map<Integer, String> newHeadMap = ConverterUtils.convertToStringMap(headMap, context);
        // excel当前行
        int rowIndex = context.readRowHolder().getRowIndex();
        // excel表头行数
//        int currentHeadRowNumber = context.readSheetHolder().getHeadRowNumber();
        log.info("导入的excel表头数据:{}", JSON.toJSONString(newHeadMap));
        Map<Integer, String> modelMap = head.get(rowIndex);
        log.info("模板的excel表头数据:{}", JSON.toJSONString(modelMap));
        // 解析到的excel表头和实体配置的进行比对
        newHeadMap.forEach((i, value) -> {
            if (StringUtils.isEmpty(value)) {
                errorMessage.add(
                        String.format("您上传的文件第%s行第%s列表头为空，请按照模板检查后重新上传", rowIndex + 1, i + 1));
            } else if (!value.equals(modelMap.get(i))) {
                errorMessage.add(
                        String.format("您上传的文件第%s行第%s列表头与模板表头不一致，请按照模板检查后重新上传", rowIndex + 1, i + 1));
            }
        });
    }

    /**
     * 读取一行一行数据到object
     */
    @Override
    public void invoke(T object, AnalysisContext context) {
        // 实际数据量比较大时，rows里的数据可以存到一定量之后进行批量处理（比如存到数据库），
        // 然后清空列表，以防止内存占用过多造成OOM
        rows.add(object);
    }

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        if (extra.getType() == CellExtraTypeEnum.MERGE) {
            int startRowIndex = extra.getFirstRowIndex() - totleHeadRowNum;
            int endRowIndex = extra.getLastRowIndex() - totleHeadRowNum;
            if (startRowIndex >= 0) {
                for (int i = startRowIndex; i <= endRowIndex; i++) {
                    rows.get(i).setStartRowNum(startRowIndex);
                    rows.get(i).setEndRowNum(endRowIndex);
                }
            }
        }
    }

    /**
     * 在完成数据解析后进行的操作。AOP思想。
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 当前sheet的名称 编码获取类似
        log.info("sheetName = {} -> 所有数据解析完成, read {} rows", context.readSheetHolder().getSheetName(), rows.size());
    }

    /**
     * 在转换异常 获取其他异常下会调用本接口。抛出异常则停止读取。如果这里不抛出异常则 继续读取下一行。
     *
     * @param exception 抛出异常
     * @param context   解析内容
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("解析失败，但是继续解析下一行:{}", exception.getMessage());
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) exception;

            Integer rowIndex = excelDataConvertException.getRowIndex();
            Integer columnIndex = excelDataConvertException.getColumnIndex();
            if (dataFormatList.contains(columnIndex)) {
                errorMessage.add("第" + (rowIndex + 1) + "行，第" + (columnIndex + 1) + "列数据格式解析异常，正确格式为[1970-01-01][19700101]，数据为:" + getCellData(excelDataConvertException.getCellData()));
            } else {
                errorMessage.add("第" + (rowIndex + 1) + "行，第" + (columnIndex + 1) + "列数据类型解析异常，数据为:" + getCellData(excelDataConvertException.getCellData()));
            }
            log.error("第{}行，第{}列数据类型解析异常，数据为:{}", rowIndex + 1, columnIndex + 1, excelDataConvertException.getCellData());
        }
    }

    private String getCellData(CellData<?> cellData) {
        String value = "";
        CellDataTypeEnum type = cellData.getType();
        if (type != null) {
            switch (type) {
                case STRING:
                    value = cellData.getStringValue();
                    break;
                case BOOLEAN:
                    value = Objects.toString(cellData.getBooleanValue(), "");
                    break;
                case NUMBER:
                    value = Objects.toString(cellData.getNumberValue(), "");
                    break;
                default:
                    break;
            }
        }
        return value;
    }

    private void createHead(List<String[]> list) {
        int depth = 0;
        while (depth < totleHeadRowNum) {
            Map<Integer, String> headMap = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                String[] arr = list.get(i);
                if (arr.length - 1 < depth) {
                    headMap.put(i, arr[arr.length - 1]);
                } else {
                    headMap.put(i, arr[depth]);
                }
            }
            head.add(headMap);
            depth++;
        }
    }

    public void validate() {
        Map<Integer, List<String>> errMap = this.getValidateMessage();
        if (MapUtils.isNotEmpty(errMap)) {
            StringBuilder sb = new StringBuilder();
            errMap.forEach((key, value) -> {
                StringBuilder minSb = new StringBuilder();
                value.forEach(s -> minSb.append(s));
                sb.append("[第" + (key + 1 + totleHeadRowNum) + "行：").append(minSb).append("]");
            });
            throw new ImportExcelException(sb.toString());
        }
    }

    /**
     * 校验数据
     * 1.校验导入excel和模板是否一致
     * 2.数据转换是否异常，是否空数据
     * 3.validation校验，@NotBlank、@NotNull
     * 因为导入合并行只在当前第一行展示，所以合并数据每一行都标注起始行、结束行
     * 例如：一个订单三个物料，表头合并，实际在excel导入中，表头只有一行
     * xsdd202609100001	自营订单	20200910	a001	西瓜	1
     * 			                            a002	菠萝	2
     * 			                            a003	香蕉	3
     *
     * 第一行既有表头又有表体，而第二行只有表体
     * 所以校验时，第一行使用 Default.class ，第二行使用自定义的 Group.class
     * @return
     */
    public Map<Integer, List<String>> getValidateMessage() {
        if (CollectionUtils.isNotEmpty(errorMessage)) {
            String jsonStr = JSON.toJSONString(errorMessage);
            log.info("导入excel，解析错误信息为：{}", jsonStr);
            throw new ImportExcelException(jsonStr);
        }
        Assert.notEmpty(rows, "导入数据不能为空！");
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Map<Integer, List<String>> errMap = new HashMap<>();
        for (int i = 0; i < rows.size(); i++) {
            T clazz = rows.get(i);
            Set<ConstraintViolation<T>> violations;
            if (clazz.getStartRowNum() == null) {
                clazz.setStartRowNum(i);
            }
            if (clazz.getStartRowNum().equals(i)) {
                violations = validator.validate(clazz, Default.class);
            } else {
                violations = validator.validate(clazz, Group.class);
            }
            List<String> errList = violations.stream().map(s -> s.getMessage()).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(errList)) {
                errMap.put(i, errList);
            }
        }
        return errMap;
    }

}
