package yp970814.excel.export;

import cn.hutool.core.map.MapUtil;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author pingge814@proton.me
 * date 2026/9/9 16:49
 */
public class SelectSheetWriteHandler implements SheetWriteHandler {

    private final Map<Integer, List<String>> selectMap;

    private final List<Integer> dataFormatList;

    private Integer totleHeadRowNum;

    public SelectSheetWriteHandler(List<String[]> list, List<Integer> dataFormatList, Map<Integer, List<String>> selectMap) {
        String[] maxLengthArray = list.stream()
                .max(Comparator.comparingInt(a -> a.length))
                .orElse(new String[0]);
        this.totleHeadRowNum = maxLengthArray.length;
        this.dataFormatList = dataFormatList;
        this.selectMap = selectMap;
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        // 需要设置下拉框的sheet页
        Sheet curSheet = writeSheetHolder.getSheet();
        Workbook workbook = writeWorkbookHolder.getWorkbook();
        // 设置所有列宽
        curSheet.setDefaultColumnWidth(25);
//        curSheet.setDefaultRowHeight((short) 400);
        if (MapUtil.isNotEmpty(selectMap)) {
            DataValidationHelper helper = curSheet.getDataValidationHelper();
            for (Map.Entry<Integer, List<String>> entry : selectMap.entrySet()) {
                List<String> list = entry.getValue();
                DataValidationConstraint constraint;
                // 下拉选超过30个，就新建隐藏sheet页来引用下拉
                if (list.size() > 30) {
                    String hiddenSheetName = "DropDownOptions_" + System.currentTimeMillis();
                    Sheet hiddenSheet = workbook.createSheet(hiddenSheetName);
                    // 隐藏sheet
                    workbook.setSheetHidden(workbook.getSheetIndex(hiddenSheet), true);
                    for (int i = 0; i < list.size(); i++) {
                        Row row = hiddenSheet.createRow(i);
                        Cell cell = row.createCell(0);
                        cell.setCellValue(list.get(i));
                    }
                    Name name = workbook.createName();
                    name.setNameName(hiddenSheetName);
                    name.setRefersToFormula(hiddenSheetName + "!$A$1:$A$" + list.size());
                    constraint = helper.createFormulaListConstraint(hiddenSheet.getSheetName());
                } else {
                    // 设置引用约束
                    constraint = helper.createExplicitListConstraint(list.toArray(new String[] {}));
                }
                // 设置下拉单元格的首行、末行、首列、末列
                CellRangeAddressList rangeAddressList = new CellRangeAddressList(totleHeadRowNum, 65535, entry.getKey(), entry.getKey());
                // 设置约束
                DataValidation validation = helper.createValidation(constraint, rangeAddressList);
                if (validation instanceof HSSFDataValidation) {
                    validation.setSuppressDropDownArrow(false);
                } else {
                    validation.setSuppressDropDownArrow(true);
                    validation.setShowErrorBox(true);
                }
                validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                validation.createErrorBox("提示", "此值与单元格定义格式不一致！");
                curSheet.addValidationData(validation);
            }
        }
        CellStyle textStyle = workbook.createCellStyle();
        textStyle.setDataFormat((short) 49);
        // 设置日期输入为文本
        if (CollectionUtils.isNotEmpty(dataFormatList)) {
            dataFormatList.forEach(s -> curSheet.setDefaultColumnStyle(s, textStyle));
        }
    }

//    private void dropDownStyle(Workbook workbook) {
//        CellStyle dropDown = workbook.createCellStyle();
//        dropDown.setLocked(false);
//    }
//
//    private void textStyle(Workbook workbook) {
//        CellStyle text = workbook.createCellStyle();
//        DataFormat df = workbook.createDataFormat();
//        text.setDataFormat(df.getFormat("@"));
//        text.setLocked(false);
//    }
//
//    private void decimalStyle(Workbook workbook) {
//        CellStyle decimal = workbook.createCellStyle();
//        decimal.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
//        decimal.setLocked(false);
//    }
//
//    private void numberStyle(Workbook workbook) {
//        CellStyle number = workbook.createCellStyle();
//        DataFormat df = workbook.createDataFormat();
//        number.setDataFormat(df.getFormat("#"));
//        number.setLocked(false);
//    }

}
