package yp970814.model.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author pingge814@proton.me
 * date 2026/9/9 16:08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="orderDetail实体对象", description="订单明细")
public class OrderDetail extends BasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty("物料编码")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String materialCode;

    @ApiModelProperty("物料名称")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String materialName;

    @ApiModelProperty("数量")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private BigDecimal quantity;

}
