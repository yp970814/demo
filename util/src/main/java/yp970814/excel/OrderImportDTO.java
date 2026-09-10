package yp970814.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import yp970814.excel.converter.DateConverter;
import yp970814.excel.importExcel.BaseExcelImport;
import yp970814.excel.importExcel.Group;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.util.Date;

/**
 * @author pingge814@proton.me
 * date 2026/9/10 09:58
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OrderImportDTO extends BaseExcelImport {

    /**
     * 可以不写 Default.class
     * validation校验默认是 Default.class
     */
    @NotBlank(message = "订单单号不能为空！")
    @ExcelProperty(value = "*订单单号")
    private String sequenceNo;

    @NotNull(message = "订单类型不能为空！", groups = {Default.class})
    @ExcelProperty(value = "*订单类型")
    private String orderType;

    @NotNull(message = "订单日期不能为空！", groups = {Default.class})
    @ExcelProperty(value = "*订单日期", converter = DateConverter.class)
    private Date orderDate;

    @NotBlank(message = "物料编码不能为空！", groups = {Default.class})
    @ExcelProperty(value = {"*订单明细", "*物料编码"})
    private String materialCode;

    @ExcelProperty(value = {"*订单明细", "采购件名称"})
    private String materialName;

    @NotNull(message = "数量不能为空！", groups = {Default.class, Group.class})
    @DecimalMin(value = "-0.0000001", message = "数量不能小于0！", groups = {Default.class, Group.class})
    @ExcelProperty(value = {"*订单明细", "*数量"})
    private Double detailQuantity;

}
