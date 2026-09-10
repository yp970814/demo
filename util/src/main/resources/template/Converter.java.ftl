package ${cfg.converter};



import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import ${cfg.po}.${table.entityName};
import ${cfg.vo}.${table.entityName}VO;
import ${cfg.dto}.${table.entityName}DTO;



/**
 * <p>
 *  ${table.comment!} 前端控制器
 * </p>
 * @author ${author}
 * @since ${date}
 */
@Mapper(componentModel = "spring")
public interface ${entity}Converter {

    ${entity}Converter converter = Mappers.getMapper(${entity}Converter.class);

    ${entity}VO po2Vo(${entity} basicArea);

    ${entity} dto2Po(${entity}DTO dto);
}
