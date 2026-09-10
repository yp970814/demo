package ${package.Mapper};

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;

import ${cfg.po}.${table.entityName};

import ${cfg.vo}.${table.entityName}VO;
import ${cfg.dto}.${table.entityName}DTO;

import java.util.List;
/**
* ${table.comment!}Mapper
* @author ${author}
* @date ${date}
*/
@Mapper
public interface ${table.entityName}Mapper extends BaseMapper<${table.entityName}> {

    //IPage<${table.entityName}> get${table.entityName}List(Page<${table.entityName}> page);

    List<${table.entityName}> pageList(${table.entityName}DTO dto);

    void deleteById(Long id);
}
