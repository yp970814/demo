package yp970814.model.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author pingge814@proton.me
 * date 2026/9/9 15:32
 */
@Data
public class OrderDTO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty("订单单号")
    private String sequenceNo;

    @ApiModelProperty("订单类型")
    private String orderType;

    @ApiModelProperty("订单日期")
    private Date orderDate;

    @ApiModelProperty("订单明细")
    private List<OrderDetailDTO> orderDetailDTOList;

}
