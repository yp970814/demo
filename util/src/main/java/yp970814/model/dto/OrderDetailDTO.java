package yp970814.model.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author pingge814@proton.me
 * date 2026/9/9 15:32
 */
@Data
public class OrderDetailDTO {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty("物料编码")
    private String materialCode;

    @ApiModelProperty("物料名称")
    private String materialName;

    @ApiModelProperty("数量")
    private BigDecimal quantity;

}
