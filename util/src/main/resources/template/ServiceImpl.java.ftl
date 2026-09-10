
package ${package.Service}.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jgdt.core.helper.LoginHelper;
import com.sungrow.mysql.utils.PageUtil;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${package.Mapper}.${table.entityName}Mapper;
import ${package.Service}.${table.entityName}Service;
import ${cfg.converter}.${table.entityName}Converter;
import com.sungrow.mysql.idgenerator.BasicEntityIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ${cfg.po}.${table.entityName};

import ${cfg.vo}.${table.entityName}VO;
import ${cfg.dto}.${table.entityName}DTO;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ${table.comment!}实现类
 * @author ${author}
 * @date ${date}
 */
@Service
@Slf4j
public class ${table.entityName}ServiceImpl extends ServiceImpl<${table.entityName}Mapper, ${table.entityName}> implements ${table.entityName}Service {
     @Autowired
     public ${table.entityName}Mapper ${table.entityPath}Mapper;

     @Autowired
     private ${table.entityName}Converter ${table.entityPath}Converter;

     @Resource
     private LoginHelper loginHelper;

     @Override
     public IPage<${table.entityName}VO> pageList(${table.entityName}DTO dto) {
          // 数据权限 删除标记 默认排序 查询条件
          Page<${table.entityName}> page = PageHelper.startPage(dto.getCurrentPage(), dto.getPageSize());
          List<${table.entityName}> ${table.entityPath}s = ${table.entityPath}Mapper.pageList(dto);
          return PageUtil.build(page, ${table.entityPath}s, (list) -> list.stream().map(p -> ${table.entityPath}Converter.po2Vo(p)).collect(Collectors.toList()));
     }

      @Override
      @Transactional(rollbackFor = Exception.class)
      public void saveOrUpdateDTO(${table.entityName}DTO dto) throws Exception{
          // 字段判断
          ${table.entityName} ${table.entityPath} = ${table.entityPath}Converter.dto2Po(dto);
          if (null != dto.getId()) {
              ${table.entityPath}.setOperator(loginHelper.getUserId());
              ${table.entityPath}Mapper.updateById(${table.entityPath});
          } else {
              ${table.entityPath}.setId(BasicEntityIdGenerator.getInstance().generateLongId());
              Long userId = loginHelper.getUserId();
              ${table.entityPath}.setCreator(userId);
              ${table.entityPath}.setOperator(userId);
              ${table.entityPath}Mapper.insert(${table.entityPath});
          }
      }

      @Override
      public void deleteById(Long id) {
          // 更新删除标记

          ${table.entityPath}Mapper.deleteById(id);
      }

      @Override
      public ${table.entityName}VO selectById(Long id) {
          ${table.entityName} ${table.entityPath} = ${table.entityPath}Mapper.selectById(id);

          return ${table.entityPath}Converter.po2Vo(${table.entityPath});
      }
}
