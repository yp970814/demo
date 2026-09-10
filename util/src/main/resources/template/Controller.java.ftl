package ${package.Controller};


import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;
</#if>
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.Api;
import io.swagger.annotations.*;

import  ${package.Service}.${table.serviceName};
import  ${package.Entity}.${table.entityName};

import ${cfg.vo}.${table.entityName}VO;
import ${cfg.dto}.${table.entityName}DTO;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.ModelMap;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.jgdt.util.JsonResult;
import com.jgdt.core.BaseController;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
/**
 * <p>
 *  ${table.comment!} 前端控制器
 * </p>
 * @author ${author}
 * @since ${date}
 */
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@Api(tags = "${table.comment}相关接口")
@RequestMapping("<#if package.ModuleName??>${package.ModuleName}</#if>/<#if controllerMappingHyphenStyle??>${controllerMappingHyphen?replace("-","")}<#else>${table.entityPath?replace("-","")}</#if>")
@Slf4j
<#if kotlin>
class ${table.controllerName}<#if superControllerClass??> : ${superControllerClass}()</#if>
<#else>
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {
</#if>

    @Autowired
    private ${table.serviceName} ${table.entityPath}Service;

	/**
     * 分页列表
     */
    @ApiOperation(value = "分页${table.comment}",notes="分页${table.comment}")
    @PostMapping("/pageList")
    public JsonResult pageList(@RequestBody ${table.entityName}DTO dto) {
        log.info("分页${table.comment} 参数：query:{}", JSONObject.toJSONString(dto));
        IPage<${table.entityName}VO> result = ${table.entityPath}Service.pageList(dto);
        return success(MESSAGE_OK,result);
    }

    /**
     * 保存或更新
     */
    @ApiOperation(value = "保存或更新${table.comment}",notes="保存或更新${table.comment}")
    @PostMapping("/saveOrUpdate")
    public JsonResult saveOrUpdate(@RequestBody ${table.entityName}DTO dto) throws Exception {
        log.info("保存或更新${table.comment} 参数：query:{}", JSONObject.toJSONString(dto));
        ${table.entityPath}Service.saveOrUpdateDTO(dto);
        return success(MESSAGE_OK);
    }
    /**
     * 删除
     */
    @ApiOperation(value = "删除${table.comment}",notes="删除${table.comment}")
    @PostMapping("/deleteById")
    public JsonResult deleteById(@RequestBody ${table.entityName}DTO dto) {
        log.info("删除${table.comment} 参数:{}",JSONObject.toJSONString(dto));
        if(dto == null || dto.getId() == null) {
            return JsonResult.ERROR().failed("参数缺失");
        }
        ${table.entityPath}Service.deleteById(dto.getId());
        return success(MESSAGE_OK);
    }
    /**
    * 详情
    */
    @ApiOperation(value = "详情${table.comment}",notes="详情${table.comment}")
    @PostMapping("detail")
    public JsonResult detail(@RequestBody ${table.entityName}DTO dto) {
        log.info("详情${table.comment} 参数:{}",JSONObject.toJSONString(dto));
        if(dto == null || dto.getId() == null) {
            return JsonResult.ERROR().failed("参数缺失");
        }
        ${table.entityName}VO dataVo = ${table.entityPath}Service.selectById(dto.getId());
        return success(MESSAGE_OK,dataVo);
    }
}
</#if>