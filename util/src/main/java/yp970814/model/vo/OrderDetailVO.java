package yp970814.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author pingge814@proton.me
 * date 2026/9/9 15:32
 */
@Data
public class OrderDetailVO {

    private Long id;

    @ExcelProperty("物料编码")
    private String materialCode;

    @ExcelProperty("物料名称")
    private String materialName;

    @ExcelProperty("数量")
    private BigDecimal quantity;

}
