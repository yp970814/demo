package yp970814.model.po;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author pingge814@proton.me
 * date 2026/9/9 15:31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="order实体对象", description="订单")
public class Order extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty("订单单号")
    @TableField(fill = FieldFill.INSERT)
    private String sequenceNo;

    @ApiModelProperty("订单类型")
    @TableField(fill = FieldFill.INSERT)
    private String orderType;

    @ApiModelProperty("订单日期")
    @TableField(fill = FieldFill.INSERT)
    private Date orderDate;

}
