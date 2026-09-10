package yp970814.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import yp970814.model.dto.OrderDTO;
import yp970814.model.po.Order;
import yp970814.model.vo.OrderVO;

/**
 * @author pingge814@proton.me
 * date 2026/9/9 15:29
 */
@Mapper(componentModel = "spring")
public interface OrderConverter {

    OrderConverter converter = Mappers.getMapper(OrderConverter.class);

    OrderVO po2Vo(Order po);

    Order dto2Po(OrderDTO dto);

}
