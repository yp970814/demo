
package ${package.Service};

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import ${cfg.po}.${table.entityName};

import ${cfg.vo}.${table.entityName}VO;
import ${cfg.dto}.${table.entityName}DTO;

import java.util.List;

/**
 * ${table.comment!}接口
 * @author ${author}
 * @date ${date}
 */
public interface ${table.entityName}Service extends IService<${table.entityName}> {

    IPage<${table.entityName}VO> pageList(${table.entityName}DTO dto);

    void saveOrUpdateDTO(${table.entityName}DTO dto) throws Exception;

    void deleteById(Long id);

    ${table.entityName}VO selectById(Long id);

}
