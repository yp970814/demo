package yp970814.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import yp970814.annotation.excel.ExcelMerge;
import yp970814.annotation.excel.ExcelNestedList;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @author pingge814@proton.me
 * date 2026/9/9 15:32
 */
@Data
public class OrderVO {

    private Long id;

    @ExcelProperty("订单单号")
    @ExcelMerge
    private String sequenceNo;

    @ExcelProperty("订单类型")
    @ExcelMerge
    private String orderType;

    @ExcelProperty("订单日期")
    @ExcelMerge
    private LocalDate orderDate;

    @ExcelProperty("订单明细")
    @ExcelNestedList(OrderDetailVO.class)
    private List<OrderDetailVO> orderDetailDTOList;

}
