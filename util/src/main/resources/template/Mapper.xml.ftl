<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="${package.Mapper}.${table.entityName}Mapper">
    <!-- 通用查询映射结果 -->
    <resultMap id="BaseResultMap" type="${package.Entity}.${entity}">
        <#list table.fields as field>
            <#if field.keyIdentityFlag>
                <id column="${field.name}" property="${field.propertyName}" />
            <#else>
                <result column="${field.name}" property="${field.propertyName}" />
            </#if>
        </#list>
    </resultMap>

    <!-- 通用查询结果列 -->
    <sql id="Base_Column_List">
        <#list table.fields as field>${field.columnName}<#sep>,<#if field_index%5==2>${"\n        "}</#if></#list>
    </sql>

    <!-- 列表查询pageList-->
    <select id="pageList" resultMap="BaseResultMap">
        SELECT
        <include refid="Base_Column_List"></include>
        FROM
        ${table.name}
        WHERE deleted = 0
        <#list table.fields as field>
            <#if field.propertyType == "String">
                <if test="${field.propertyName} != null and ${field.propertyName} !=''">
                    and ${field.name}=<#noparse>#{</#noparse>${field.propertyName}<#noparse>}</#noparse>
                </if>
            <#else>
                <if test="${field.propertyName} != null">
                    and ${field.name}=<#noparse>#{</#noparse>${field.propertyName}<#noparse>}</#noparse>
                </if>
            </#if>
        </#list>
        order by id desc
    </select>

    <!--删除-->
    <update id="deleteById">
        update ${table.name} set deleted = 1
        where id = <#noparse>#{</#noparse>id<#noparse>}</#noparse>
    </update>
</mapper>